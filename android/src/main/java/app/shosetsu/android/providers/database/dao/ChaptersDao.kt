package app.shosetsu.android.providers.database.dao

import android.database.sqlite.SQLiteException
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import app.shosetsu.android.common.ext.entity
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.common.ext.toDB
import app.shosetsu.android.domain.model.database.DBChapterEntity
import app.shosetsu.android.providers.database.dao.base.BaseDao
import app.shosetsu.common.consts.ErrorKeys.ERROR_GENERAL
import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.common.domain.model.local.ReaderChapterEntity
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.errorResult
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.successResult
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
	fun loadLiveChapters(novelID: Int): Flow<List<DBChapterEntity>>

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM chapters WHERE novelID = :novelID")
	suspend fun loadChapters(novelID: Int): List<DBChapterEntity>

	@Throws(SQLiteException::class)
	@Query("SELECT id, url, title, readingPosition, readingStatus, bookmarked FROM chapters WHERE novelID = :novelID")
	fun loadLiveReaderChapters(novelID: Int): Flow<List<ReaderChapterEntity>>


	//## Single result queries

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM chapters WHERE id = :chapterID LIMIT 1")
	suspend fun loadChapter(chapterID: Int): DBChapterEntity

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM chapters WHERE _rowid_ = :rowID LIMIT 1")
	suspend fun loadChapter(rowID: Long): DBChapterEntity


	@Query("SELECT COUNT(*) FROM chapters WHERE readingStatus != 2")
	@Throws(SQLiteException::class)
	suspend fun loadChapterUnreadCount(): Int

	//# Transactions

	@Transaction
	@Throws(SQLiteException::class)
	suspend fun updateReaderChapter(readerDBChapter: ReaderChapterEntity): Unit =
			loadChapter(readerDBChapter.id).copy(
					readingPosition = readerDBChapter.readingPosition,
					readingStatus = readerDBChapter.readingStatus,
					bookmarked = readerDBChapter.bookmarked
			).let { suspendedUpdate(it) }

	@Transaction
	@Throws(SQLiteException::class)
	suspend fun handleChapters(novelEntity: NovelEntity, list: List<Novel.Chapter>) {
		val databaseChapterEntities: List<DBChapterEntity> = loadChapters(novelEntity.id!!)
		list.forEach { novelChapter: Novel.Chapter ->
			databaseChapterEntities.find { it.url == novelChapter.link }?.let {
				handleUpdate(it, novelChapter)
			} ?: handleAbortInsert(novelChapter, novelEntity)
		}
	}

	@Throws(SQLiteException::class)
	@Transaction
	suspend fun handleChaptersReturnNew(
			novelEntity: NovelEntity,
			list: List<Novel.Chapter>,
	): HResult<List<DBChapterEntity>> {
		val newChapters = ArrayList<DBChapterEntity>()
		val databaseChapterEntities: List<DBChapterEntity> = loadChapters(novelEntity.id!!)
		list.forEach { novelChapter: Novel.Chapter ->
			databaseChapterEntities.find { it.url == novelChapter.link }?.let {
				handleUpdate(it, novelChapter)
			} ?: run {
				insertReturn(novelEntity, novelChapter).handle(
						onError = {
							logE(it.toString())
						}
				) {
					newChapters.add(it)
				}
			}
		}
		return successResult(newChapters)
	}

	@Throws(SQLiteException::class)
	@Transaction
	suspend fun insertAndReturnDBChapter(DBChapterEntity: DBChapterEntity): DBChapterEntity =
			loadChapter(insertReplace(DBChapterEntity))


	private suspend fun insertReturn(
			novelEntity: NovelEntity,
			novelChapter: Novel.Chapter,
	): HResult<DBChapterEntity> {
		try {
			val row = handleAbortInsert(novelChapter, novelEntity)
			if (row < 0) return errorResult(ERROR_GENERAL, "Aborted")
			val c = loadChapter(row)
			return successResult(c)
		} catch (e: Exception) {
			return errorResult(ERROR_GENERAL, "Unprecedented error")
		}
	}

	@Throws(SQLiteException::class)
	private suspend fun handleAbortInsert(novelChapter: Novel.Chapter, novelEntity: NovelEntity) =
			insertAbort(novelChapter.entity(novelEntity).toDB())

	@Throws(SQLiteException::class)
	private suspend fun handleUpdate(DBChapterEntity: DBChapterEntity, novelChapter: Novel.Chapter) {
		suspendedUpdate(DBChapterEntity.copy(
				title = novelChapter.title,
				releaseDate = novelChapter.release,
				order = novelChapter.order
		))
	}
}