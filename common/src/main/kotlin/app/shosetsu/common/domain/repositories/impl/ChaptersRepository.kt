package app.shosetsu.common.domain.repositories.impl

import app.shosetsu.common.datasource.database.base.IDBChaptersDataSource
import app.shosetsu.common.datasource.file.base.IFileCachedChapterDataSource
import app.shosetsu.common.datasource.file.base.IFileChapterDataSource
import app.shosetsu.common.datasource.memory.base.IMemChaptersDataSource
import app.shosetsu.common.datasource.remote.base.IRemoteChaptersDataSource
import app.shosetsu.common.domain.model.local.ChapterEntity
import app.shosetsu.common.domain.model.local.ReaderChapterEntity
import app.shosetsu.common.domain.repositories.base.IChaptersRepository
import app.shosetsu.common.dto.*
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
	private val dbSource: IDBChaptersDataSource,
	private val fileSource: IFileChapterDataSource,
	private val remoteSource: IRemoteChaptersDataSource,
) : IChaptersRepository {

	private suspend inline fun placeIntoCache(
		entity: ChapterEntity,
		chapterType: Novel.ChapterType,
		result: HResult<ByteArray>
	) = result.handle { saveChapterPassageToMemory(entity, chapterType, it) }

	override suspend fun getChapterPassage(
		formatter: IExtension,
		entity: ChapterEntity,
	): HResult<ByteArray> =
		memorySource.loadChapterFromCache(entity.id!!)
			.catch {
				cacheSource.loadChapterPassage(entity.id!!, formatter.chapterType).also { result ->
					result.handle {
						memorySource.saveChapterInCache(entity.id!!, it)
					}
				}
			}.catch {
				fileSource.load(entity, formatter.chapterType)
					.also { result ->
						placeIntoCache(entity, formatter.chapterType, result)
					}
			}.catch {
				remoteSource.loadChapterPassage(
					formatter,
					entity.url
				).also { placeIntoCache(entity, formatter.chapterType, it) }
			}

	suspend fun saveChapterPassageToMemory(
		chapterEntity: ChapterEntity,
		chapterType: Novel.ChapterType,
		passage: ByteArray,
	): HResult<*> = memorySource.saveChapterInCache(chapterEntity.id!!, passage) thenAlso
			cacheSource.saveChapterInCache(chapterEntity.id!!, chapterType, passage)


	/**
	 *
	 * 1. save to memory
	 * 2. save to filesystem
	 * 3. if filesystem save was a success, then update the chapter
	 * 4. finally evaluate with and between 1&&(2||3)
	 */

	/**
	 * We want to ensure that the [passage] is saved either to [memorySource] or [fileSource] first off
	 * After that, then we can update the [entity] to say [passage] was properly saved
	 */
	override suspend fun saveChapterPassageToStorage(
		entity: ChapterEntity,
		chapterType: Novel.ChapterType,
		passage: ByteArray,
	): HResult<*> =
		saveChapterPassageToMemory(entity, chapterType, passage) thenAlso (
				fileSource.save(entity, chapterType, passage) ifSo {
					dbSource.updateChapter(entity.copy(isSaved = true))
				})


	override suspend fun handleChapters(
		novelID: Int,
		extensionID: Int, list: List<Novel.Chapter>,
	): HResult<*> =
		dbSource.handleChapters(novelID, extensionID, list)

	override suspend fun handleChaptersReturn(
		novelID: Int,
		extensionID: Int, list: List<Novel.Chapter>,
	): HResult<List<ChapterEntity>> =
		dbSource.handleChapterReturn(novelID, extensionID, list)

	override suspend fun getChaptersLive(novelID: Int): Flow<HResult<List<ChapterEntity>>> =
		dbSource.getChaptersFlow(novelID)

	override suspend fun getChapters(novelID: Int): HResult<List<ChapterEntity>> =
		dbSource.getChapters(novelID)

	override suspend fun updateChapter(chapterEntity: ChapterEntity): HResult<*> =
		dbSource.updateChapter(chapterEntity)

	override suspend fun getChapter(chapterID: Int): HResult<ChapterEntity> =
		dbSource.getChapter(chapterID)

	override suspend fun getReaderChaptersFlow(
		novelID: Int,
	): Flow<HResult<List<ReaderChapterEntity>>> = dbSource.getReaderChapters(novelID)

	override suspend fun updateReaderChapter(readerChapterEntity: ReaderChapterEntity): HResult<*> =
		dbSource.updateReaderChapter(readerChapterEntity)

	override suspend fun deleteChapterPassage(
		chapterEntity: ChapterEntity,
		chapterType: Novel.ChapterType
	): HResult<*> =
		dbSource.updateChapter(
			chapterEntity.copy(
				isSaved = false
			)
		) ifSo { fileSource.delete(chapterEntity, chapterType) }

}