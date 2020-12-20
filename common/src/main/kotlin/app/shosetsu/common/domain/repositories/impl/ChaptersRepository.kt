package app.shosetsu.common.domain.repositories.impl

import app.shosetsu.common.datasource.database.base.ILocalChaptersDataSource
import app.shosetsu.common.datasource.file.base.IFileCachedChapterDataSource
import app.shosetsu.common.datasource.file.base.IFileChapterDataSource
import app.shosetsu.common.datasource.memory.base.IMemChaptersDataSource
import app.shosetsu.common.datasource.remote.base.IRemoteChaptersDataSource
import app.shosetsu.common.domain.model.local.ChapterEntity
import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.common.domain.model.local.ReaderChapterEntity
import app.shosetsu.common.domain.repositories.base.IChaptersRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.and
import app.shosetsu.lib.IExtension
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

	private suspend fun placeIntoCache(chapterEntity: ChapterEntity, value: HResult<String>) {
		if (value is HResult.Success)
			saveChapterPassageToMemory(chapterEntity, value.data)
	}

	override suspend fun getChapterPassage(
		formatter: IExtension,
		chapterEntity: ChapterEntity,
	): HResult<String> =
		memorySource.loadChapterFromCache(chapterEntity.id!!)
			.takeIf { it is HResult.Success }
			?: cacheSource.loadChapterPassage(chapterEntity.id!!)
				.takeIf { it is HResult.Success }
			?: fileSource.loadChapterPassageFromStorage(chapterEntity)
				.takeIf { it is HResult.Success }?.also { placeIntoCache(chapterEntity, it) }
			?: remoteSource.loadChapterPassage(
				formatter,
				chapterEntity.url
			).also { placeIntoCache(chapterEntity, it) }

	suspend fun saveChapterPassageToMemory(
		chapterEntity: ChapterEntity,
		passage: String,
	): HResult<*> = memorySource.saveChapterInCache(chapterEntity.id!!, passage) and
			cacheSource.saveChapterInCache(chapterEntity.id!!, passage)

	override suspend fun saveChapterPassageToStorage(
		chapterEntity: ChapterEntity,
		passage: String,
	): HResult<*> = saveChapterPassageToMemory(chapterEntity, passage) and
			fileSource.saveChapterPassageToStorage(chapterEntity, passage) and
			dbSource.updateChapter(chapterEntity.copy(isSaved = true))


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

	override suspend fun getChapters(novelID: Int): Flow<HResult<List<ChapterEntity>>> =
		dbSource.loadChapters(novelID)

	override suspend fun updateChapter(chapterEntity: ChapterEntity): HResult<*> =
		dbSource.updateChapter(chapterEntity)

	override suspend fun getChapter(chapterID: Int): HResult<ChapterEntity> =
		dbSource.loadChapter(chapterID)

	override suspend fun getReaderChaptersFlow(
		novelID: Int,
	): Flow<HResult<List<ReaderChapterEntity>>> = dbSource.loadReaderChapters(novelID)

	override suspend fun updateReaderChapter(readerChapterEntity: ReaderChapterEntity): HResult<*> =
		dbSource.updateReaderChapter(readerChapterEntity)

	override suspend fun deleteChapterPassage(chapterEntity: ChapterEntity): HResult<*> =
		dbSource.updateChapter(
			chapterEntity.copy(
				isSaved = false
			)
		) and fileSource.deleteChapter(chapterEntity)

}