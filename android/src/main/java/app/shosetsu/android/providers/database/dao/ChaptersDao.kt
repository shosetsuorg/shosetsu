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
 * ====================================================================
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

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM chapters")
	fun loadAllChapters(): Array<DBChapterEntity>

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM chapters WHERE novelID = :novelID")
	fun getChaptersFlow(novelID: Int): Flow<List<DBChapterEntity>>

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM chapters WHERE novelID = :novelID")
	suspend fun getChapters(novelID: Int): List<DBChapterEntity>

	@Throws(SQLiteException::class)
	@Query("SELECT id, url, title, readingPosition, readingStatus, bookmarked FROM chapters WHERE novelID = :novelID")
	fun getReaderChaptersFlow(novelID: Int): Flow<List<ReaderChapterEntity>>


	//## Single result queries

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM chapters WHERE id = :chapterID LIMIT 1")
	suspend fun getChapter(chapterID: Int): DBChapterEntity

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM chapters WHERE _rowid_ = :rowID LIMIT 1")
	suspend fun getChapter(rowID: Long): DBChapterEntity


	@Query("SELECT COUNT(*) FROM chapters WHERE readingStatus != 2")
	@Throws(SQLiteException::class)
	suspend fun loadChapterUnreadCount(): Int

	//# Transactions

	@Transaction
	@Throws(SQLiteException::class)
	suspend fun updateReaderChapter(readerDBChapter: ReaderChapterEntity): Unit =
		getChapter(readerDBChapter.id).copy(
			readingPosition = readerDBChapter.readingPosition,
			readingStatus = readerDBChapter.readingStatus,
			bookmarked = readerDBChapter.bookmarked
		).let { update(it) }

	@Transaction
	@Throws(SQLiteException::class)
	suspend fun handleChapters(
		novelID: Int,
		extensionID: Int, list: List<Novel.Chapter>
	) {
		val databaseChapterEntities: List<DBChapterEntity> = getChapters(novelID)
		list.forEach { novelChapter ->
			databaseChapterEntities.find { it.url == novelChapter.link }?.let { dbChapterEntity ->
				handleUpdate(
					DBChapterEntity = dbChapterEntity,
					novelChapter = novelChapter
				)
			} ?: handleAbortInsert(
				novelChapter = novelChapter,
				novelID = novelID,
				extensionID = extensionID
			)
		}
	}

	@Throws(SQLiteException::class, IndexOutOfBoundsException::class)
	@Transaction
	suspend fun handleChaptersReturnNew(
		novelID: Int,
		extensionID: Int,
		list: List<Novel.Chapter>,
	): List<DBChapterEntity> {
		val newChapters = ArrayList<DBChapterEntity>()
		val databaseChapterEntities: List<DBChapterEntity> = getChapters(novelID)
		list.forEach { novelChapter ->
			databaseChapterEntities.find { it.url == novelChapter.link }?.let { dbChapterEntity ->
				handleUpdate(
					DBChapterEntity = dbChapterEntity,
					novelChapter = novelChapter
				)
			} ?: newChapters.add(
				insertReturn(
					novelID = novelID,
					extensionID = extensionID,
					novelChapter = novelChapter
				)
			)
		}
		return newChapters
	}

	@Throws(SQLiteException::class)
	@Transaction
	suspend fun insertAndReturnDBChapter(DBChapterEntity: DBChapterEntity): DBChapterEntity =
		getChapter(insertReplace(DBChapterEntity))


	@Throws(IndexOutOfBoundsException::class, SQLiteException::class)
	private suspend fun insertReturn(
		novelID: Int,
		extensionID: Int,
		novelChapter: Novel.Chapter,
	): DBChapterEntity =
		handleAbortInsert(
			novelChapter = novelChapter,
			novelID = novelID,
			extensionID = extensionID
		).let { rowID ->
			if (rowID < 0) throw IndexOutOfBoundsException("Insertion aborted")
			getChapter(rowID)
		}

	@Throws(SQLiteException::class)
	private suspend fun handleAbortInsert(
		novelChapter: Novel.Chapter,
		novelID: Int,
		extensionID: Int
	) =
		insertAbort(
			novelChapter.entity(
				novelID = novelID,
				extensionID = extensionID
			).toDB()
		)

	@Throws(SQLiteException::class)
	private suspend fun handleUpdate(
		DBChapterEntity: DBChapterEntity,
		novelChapter: Novel.Chapter
	) {
		update(
			DBChapterEntity.copy(
				title = novelChapter.title,
				releaseDate = novelChapter.release,
				order = novelChapter.order
			)
		)
	}
}