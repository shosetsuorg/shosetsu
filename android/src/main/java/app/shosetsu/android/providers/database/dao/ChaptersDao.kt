package app.shosetsu.android.providers.database.dao

import android.database.sqlite.SQLiteException
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import app.shosetsu.android.common.ext.entity
import app.shosetsu.android.common.ext.toDB
import app.shosetsu.android.domain.model.database.DBChapterEntity
import app.shosetsu.android.providers.database.dao.base.BaseDao
import app.shosetsu.common.domain.model.local.ReaderChapterEntity
import app.shosetsu.lib.Novel
import kotlinx.coroutines.flow.Flow

/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * shosetsu
 * 23 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
@Dao
interface ChaptersDao : BaseDao<DBChapterEntity> {

	//# Queries

	/**
	 * Gets a flow of the chapters corresponding to the novel
	 */
	@Throws(SQLiteException::class)
	@Query("SELECT * FROM chapters WHERE novelID = :novelID")
	fun getChaptersFlow(novelID: Int): Flow<List<DBChapterEntity>>

	/**
	 * Get the current chapters of a novel
	 */
	@Throws(SQLiteException::class)
	@Query("SELECT * FROM chapters WHERE novelID = :novelID")
	suspend fun getChapters(novelID: Int): List<DBChapterEntity>

	/**
	 * Gets a flow of chapters as [ReaderChapterEntity]
	 */
	@Throws(SQLiteException::class)
	@Query("SELECT id, url, title, readingPosition, readingStatus, bookmarked FROM chapters WHERE novelID = :novelID")
	fun getReaderChaptersFlow(novelID: Int): Flow<List<ReaderChapterEntity>>


	//## Single result queries

	/**
	 * Get a chapter by its id
	 */
	@Throws(SQLiteException::class)
	@Query("SELECT * FROM chapters WHERE id = :chapterID LIMIT 1")
	suspend fun getChapter(chapterID: Int): DBChapterEntity

	/**
	 * Get a chapter by its rowId
	 */
	@Throws(SQLiteException::class)
	@Query("SELECT * FROM chapters WHERE _rowid_ = :rowId LIMIT 1")
	suspend fun getChapter(rowId: Long): DBChapterEntity

	//# Transactions

	/**
	 * Updates a [DBChapterEntity] via its [ReaderChapterEntity]
	 */
	@Transaction
	@Throws(SQLiteException::class)
	suspend fun update(entity: ReaderChapterEntity) {
		val updatedChapter: DBChapterEntity =
			getChapter(entity.id)
				.copy(
					readingPosition = entity.readingPosition,
					readingStatus = entity.readingStatus,
					bookmarked = entity.bookmarked
				)
		update(updatedChapter)
	}

	/**
	 * Handle new data. Update's chapters that already exist, and insert any new chapters.
	 *
	 * @param novelId Id of the novel to work on
	 * @param extensionId Id of the extension to work with
	 * @param list List of new data to work on
	 */
	@Transaction
	@Throws(SQLiteException::class)
	suspend fun handleNewData(
		novelId: Int,
		extensionId: Int,
		list: List<Novel.Chapter>
	) {
		val databaseChapterEntities: List<DBChapterEntity> = getChapters(novelId)
		list.forEach { novelChapter ->
			databaseChapterEntities.find { it.url == novelChapter.link }?.let { dbChapterEntity ->
				update(
					chapterEntity = dbChapterEntity,
					newData = novelChapter
				)
			} ?: insertAbort(
				novelChapter = novelChapter,
				novelID = novelId,
				extensionID = extensionId
			)
		}
	}

	/**
	 * Handle new data. Update's chapters that already exist, and insert any new chapters.
	 *
	 * @param novelId Id of the novel to work on
	 * @param extensionId Id of the extension to work with
	 * @param list List of new data to work on
	 *
	 * @return list of chapters that were newly inserted
	 */
	@Throws(SQLiteException::class, IndexOutOfBoundsException::class)
	@Transaction
	suspend fun handleNewDataReturn(
		novelId: Int,
		extensionId: Int,
		list: List<Novel.Chapter>,
	): List<DBChapterEntity> {
		val newChapters = ArrayList<DBChapterEntity>()
		val databaseChapterEntities: List<DBChapterEntity> = getChapters(novelId)
		list.forEach { novelChapter ->
			databaseChapterEntities.find { it.url == novelChapter.link }?.let { dbChapterEntity ->
				update(
					chapterEntity = dbChapterEntity,
					newData = novelChapter
				)
			} ?: newChapters.add(
				insertReturn(
					novelID = novelId,
					extensionID = extensionId,
					novelChapter = novelChapter
				)
			)
		}
		return newChapters
	}

	/**
	 * Insert a new [DBChapterEntity] using [novelChapter] as the data
	 *
	 * @return the new [DBChapterEntity]
	 */
	@Transaction
	@Throws(IndexOutOfBoundsException::class, SQLiteException::class)
	private suspend fun insertReturn(
		novelID: Int,
		extensionID: Int,
		novelChapter: Novel.Chapter,
	): DBChapterEntity =
		insertAbort(
			novelChapter = novelChapter,
			novelID = novelID,
			extensionID = extensionID
		).let { rowId ->
			if (rowId < 0) throw IndexOutOfBoundsException("Invalid rowId")
			getChapter(rowId)
		}

	/**
	 * Inserts a new [DBChapterEntity] using [novelChapter] as the data
	 *
	 * @return the rowId of the new row
	 */
	@Throws(SQLiteException::class)
	private suspend fun insertAbort(
		novelChapter: Novel.Chapter,
		novelID: Int,
		extensionID: Int
	): Long =
		insertAbort(
			novelChapter.entity(
				novelID = novelID,
				extensionID = extensionID
			).toDB()
		)

	/**
	 * Update's [chapterEntity] with [newData]
	 */
	@Throws(SQLiteException::class)
	private suspend fun update(
		chapterEntity: DBChapterEntity,
		newData: Novel.Chapter
	) {
		update(
			chapterEntity.copy(
				title = newData.title,
				releaseDate = newData.release,
				order = newData.order
			)
		)
	}
}