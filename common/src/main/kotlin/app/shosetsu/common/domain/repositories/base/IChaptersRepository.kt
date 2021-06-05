package app.shosetsu.common.domain.repositories.base

import app.shosetsu.common.domain.model.local.ChapterEntity
import app.shosetsu.common.domain.model.local.ReaderChapterEntity
import app.shosetsu.common.dto.HResult
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
 * 30 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
interface IChaptersRepository {
	/**
	 * Loads a [ChapterEntity]s text
	 * First checks memory
	 * Then checks storage
	 * Then checks network
	 *
	 * Saves successful passages into caches
	 *
	 * @return
	 * [HResult.Success] Chapter successfully retrieved
	 *
	 * [HResult.Error] Something went wrong loading the chapter
	 *
	 * [HResult.Empty] Nothing was found for the chapter
	 *
	 * [HResult.Loading] never
	 */
	suspend fun getChapterPassage(
		formatter: IExtension,
		entity: ChapterEntity,
	): HResult<ByteArray>

	/**
	 * Save the [ChapterEntity] [passage] to storage
	 *
	 * Will not save into any caches, as is assuming content retrieved from [getChapterPassage]
	 *
	 * @return
	 * [HResult.Success] Chapter saved to storage scornfully
	 *
	 * [HResult.Error] Something went wrong saving to storage
	 *
	 * [HResult.Empty] never
	 *
	 * [HResult.Loading] never
	 */
	suspend fun saveChapterPassageToStorage(
		entity: ChapterEntity,
		chapterType: Novel.ChapterType,
		passage: ByteArray
	): HResult<*>

	/**
	 * Handles chapters for ze novel
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] TODO RETURN DESCRIPTION
	 */
	suspend fun handleChapters(
		novelID: Int,
		extensionID: Int,
		list: List<Novel.Chapter>
	): HResult<*>

	/**
	 * Handles chapters return, but returns the chapters that are new
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] TODO RETURN DESCRIPTION
	 */
	suspend fun handleChaptersReturn(
		novelID: Int,
		extensionID: Int,
		list: List<Novel.Chapter>,
	): HResult<List<ChapterEntity>>

	/**
	 * Loads [ChapterEntity]s matching [novelID] in a [Flow] of [HResult]
	 *
	 * @return [Flow] of
	 *
	 * [HResult.Success] Chapters found and returned
	 *
	 * [HResult.Error] Something went wrong loading the chapters
	 *
	 * [HResult.Empty] never?
	 *
	 * [HResult.Loading] Initial
	 */
	suspend fun getChaptersLive(novelID: Int): Flow<HResult<List<ChapterEntity>>>

	suspend fun getChapters(novelID: Int): HResult<List<ChapterEntity>>

	/**
	 * Loads a [ChapterEntity] by its [chapterID]
	 *
	 * @return
	 * [HResult.Success] Chapter successfully loaded
	 *
	 * [HResult.Error] Exception occurred loading entity
	 *
	 * [HResult.Empty] No such chapter exists
	 *
	 * [HResult.Loading] never
	 */
	suspend fun getChapter(chapterID: Int): HResult<ChapterEntity>

	/**
	 * Update [chapterEntity] in database
	 *
	 * @return
	 * [HResult.Success] [ChapterEntity] updated properly
	 *
	 * [HResult.Error] Exception occurred when saving
	 *
	 * [HResult.Empty] never
	 *
	 * [HResult.Loading] never
	 */
	suspend fun updateChapter(chapterEntity: ChapterEntity): HResult<*>

	/**
	 * Loads [ReaderChapterEntity]s by it's [novelID]
	 *
	 * @return
	 * [HResult.Success] [List] of [ReaderChapterEntity]
	 *
	 * [HResult.Error] Exception occurred loading
	 *
	 * [HResult.Empty] No entities found
	 *
	 * [HResult.Loading] Initial value
	 */
	suspend fun getReaderChaptersFlow(novelID: Int): Flow<HResult<List<ReaderChapterEntity>>>

	/**
	 * Update [readerChapterEntity] in database
	 *
	 * @return
	 * [HResult.Success] [readerChapterEntity] successfully updated
	 *
	 * [HResult.Error] Something went wrong
	 *
	 * [HResult.Empty] never
	 *
	 * [HResult.Loading] never
	 */
	suspend fun updateReaderChapter(readerChapterEntity: ReaderChapterEntity): HResult<*>

	/**
	 * Delete the chapter passage from storage
	 *
	 * Also deletes from memory and cache
	 * @return
	 * [HResult.Success] [chapterEntity]s passage deleted
	 *
	 * [HResult.Error] Something went wrong attempting to save
	 *
	 * [HResult.Empty] never
	 *
	 * [HResult.Loading] never
	 */
	suspend fun deleteChapterPassage(
		chapterEntity: ChapterEntity,
		chapterType: Novel.ChapterType
	): HResult<*>
}