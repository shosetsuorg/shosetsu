package app.shosetsu.android.datasource.local.database.impl

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.enums.ReadingStatus
import app.shosetsu.android.common.ext.toDB
import app.shosetsu.android.datasource.local.database.base.IDBChaptersDataSource
import app.shosetsu.android.domain.model.local.ChapterEntity
import app.shosetsu.android.domain.model.local.ReaderChapterEntity
import app.shosetsu.android.dto.convertList
import app.shosetsu.android.providers.database.dao.ChaptersDao
import app.shosetsu.lib.Novel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * Shosetsu
 * 10 / May / 2020
 *
 * @author github.com/doomsdayrs
 */
class DBChaptersDataSource(
	private val chaptersDao: ChaptersDao,
) : IDBChaptersDataSource {

	override suspend fun getChaptersFlow(
		novelID: Int,
	): Flow<List<ChapterEntity>> =
		chaptersDao.getChaptersFlow(novelID).map { it.convertList() }

	@Throws(SQLiteException::class)
	override suspend fun getChapters(novelID: Int): List<ChapterEntity> =
		(chaptersDao.getChapters(novelID).convertList())

	@Throws(SQLiteException::class)
	override suspend fun getChaptersByExtension(extensionId: Int): List<ChapterEntity> =
		(chaptersDao.getChaptersByExtension(extensionId).convertList())

	@Throws(SQLiteException::class)
	override suspend fun getChapter(chapterID: Int): ChapterEntity? =
		chaptersDao.getChapter(chapterID)?.convertTo()

	override fun getReaderChapters(
		novelID: Int,
	): Flow<List<ReaderChapterEntity>> =
		chaptersDao.getReaderChaptersFlow(novelID)

	@Throws(SQLiteException::class)
	override suspend fun handleChapters(
		novelID: Int,
		extensionID: Int,
		list: List<Novel.Chapter>,
	): Unit =
		chaptersDao.handleNewData(novelID, extensionID, list)


	@Throws(IndexOutOfBoundsException::class, SQLiteException::class)
	override suspend fun handleChapterReturn(
		novelID: Int,
		extensionID: Int,
		list: List<Novel.Chapter>,
	): List<ChapterEntity> =
		chaptersDao.handleNewDataReturn(novelID, extensionID, list).convertList()

	@Throws(SQLiteException::class)
	override suspend fun updateChapter(chapterEntity: ChapterEntity): Unit =
		chaptersDao.update(chapterEntity.toDB())

	@Throws(SQLiteException::class)
	override suspend fun delete(entity: ChapterEntity): Unit =
		chaptersDao.delete(entity.toDB())

	@Throws(SQLiteException::class)
	override suspend fun delete(entity: List<ChapterEntity>) {
		chaptersDao.delete(entity.toDB())
	}

	override fun getChapterProgress(chapterId: Int): Flow<Double> =
		chaptersDao.getChapterProgress(chapterId)

	override fun getChapterBookmarkedFlow(id: Int): Flow<Boolean?> =
		chaptersDao.getChapterBookmarkedFlow(id)

	override suspend fun updateChapterReadingStatus(
		chapterIds: List<Int>,
		readingStatus: ReadingStatus
	) {
		chaptersDao.updateChapterReadingStatusBulk(chapterIds, readingStatus)
	}

	override suspend fun updateChapterBookmark(chapterIds: List<Int>, bookmarked: Boolean) {
		chaptersDao.updateChapterBookmarkBulk(chapterIds, bookmarked)
	}

	override suspend fun markChaptersDeleted(chapterIds: List<Int>) {
		chaptersDao.markChaptersDeletedBulk(chapterIds)
	}
}