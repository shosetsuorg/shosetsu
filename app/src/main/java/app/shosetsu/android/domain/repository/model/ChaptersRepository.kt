package app.shosetsu.android.domain.repository.model

import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.and
import app.shosetsu.android.datasource.database.base.ILocalChaptersDataSource
import app.shosetsu.android.datasource.file.base.IFileCachedChapterDataSource
import app.shosetsu.android.datasource.file.base.IFileChapterDataSource
import app.shosetsu.android.datasource.memory.base.IMemChaptersDataSource
import app.shosetsu.android.datasource.remote.base.IRemoteChaptersDataSource
import app.shosetsu.android.domain.model.local.ChapterEntity
import app.shosetsu.android.domain.model.local.NovelEntity
import app.shosetsu.android.domain.model.local.ReaderChapterEntity
import app.shosetsu.android.domain.repository.base.IChaptersRepository
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Novel

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
 * 02 / 05 / 2020
 * @param memorySource Source from memory
 * @param dbSource Source from db
 * @param remoteSource Source from online
 * @param fileSource Source from storage
 */
class ChaptersRepository(
		private val memorySource: IMemChaptersDataSource,
		private val cacheSource: IFileCachedChapterDataSource,
		private val dbSource: ILocalChaptersDataSource,
		private val fileSource: IFileChapterDataSource,
		private val remoteSource: IRemoteChaptersDataSource,
) : IChaptersRepository {

	private suspend fun handleReturn(chapterEntity: ChapterEntity, value: HResult<String>) {
		if (value is HResult.Success)
			saveChapterPassageToMemory(chapterEntity, value.data)
	}

	override suspend fun loadChapterPassage(
			formatter: IExtension,
			chapterEntity: ChapterEntity,
	): HResult<String> = memorySource.loadChapterFromCache(chapterEntity.id!!)
			.takeIf { it is HResult.Success }
			?: cacheSource.loadChapterPassage(chapterEntity.id!!)
					.takeIf { it is HResult.Success }
			?: fileSource.loadChapterPassageFromStorage(chapterEntity)
					.takeIf { it is HResult.Success }?.also { handleReturn(chapterEntity, it) }
			?: remoteSource.loadChapterPassage(
					formatter,
					chapterEntity.url
			).also { handleReturn(chapterEntity, it) }

	override suspend fun saveChapterPassageToMemory(
			chapterEntity: ChapterEntity,
			passage: String,
	): HResult<*> = memorySource.saveChapterInCache(chapterEntity.id!!, passage) and
			cacheSource.saveChapterInCache(chapterEntity.id!!, passage)

	@Throws(SQLiteException::class)
	override suspend fun saveChapterPassageToStorage(
			chapterEntity: ChapterEntity,
			passage: String,
	): HResult<*> = saveChapterPassageToMemory(chapterEntity, passage) and
			fileSource.saveChapterPassageToStorage(chapterEntity, passage) and
			dbSource.updateChapter(chapterEntity.copy(isSaved = true))


	@Throws(SQLiteException::class)
	override suspend fun handleChapters(
			novelEntity: NovelEntity,
			list: List<Novel.Chapter>,
	): HResult<*> =
			dbSource.handleChapters(novelEntity, list)

	override suspend fun handleChaptersReturn(
			novelEntity: NovelEntity,
			list: List<Novel.Chapter>,
	): HResult<List<ChapterEntity>> =
			dbSource.handleChapterReturn(novelEntity, list)

	override suspend fun loadChapters(novelID: Int): LiveData<HResult<List<ChapterEntity>>> =
			dbSource.loadChapters(novelID)

	@Throws(SQLiteException::class)
	override suspend fun updateChapter(chapterEntity: ChapterEntity): HResult<*> =
			dbSource.updateChapter(chapterEntity)

	override suspend fun loadChapter(chapterID: Int): HResult<ChapterEntity> =
			dbSource.loadChapter(chapterID)

	override suspend fun loadReaderChapters(
			novelID: Int,
	): LiveData<HResult<List<ReaderChapterEntity>>> = dbSource.loadReaderChapters(novelID)

	@Throws(SQLiteException::class)
	override suspend fun updateReaderChapter(readerChapterEntity: ReaderChapterEntity): HResult<*> =
			dbSource.updateReaderChapter(readerChapterEntity)

	@Throws(SQLiteException::class)
	override suspend fun deleteChapterPassage(chapterEntity: ChapterEntity): HResult<*> =
			dbSource.updateChapter(chapterEntity.copy(
					isSaved = false
			)) and fileSource.deleteChapter(chapterEntity)

}