package app.shosetsu.common.domain.repositories.base

import app.shosetsu.common.domain.model.local.ChapterEntity
import app.shosetsu.common.domain.model.local.NovelEntity
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
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] TODO RETURN DESCRIPTION
	 */
	suspend fun loadChapterPassage(
		formatter: IExtension,
		chapterEntity: ChapterEntity,
	): HResult<String>

	/**
	 * Save the [ChapterEntity] [passage] to memory
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
	suspend fun saveChapterPassageToMemory(
		chapterEntity: ChapterEntity,
		passage: String
	): HResult<*>

	/**
	 * Save the [ChapterEntity] [passage] to storage
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
	suspend fun saveChapterPassageToStorage(
		chapterEntity: ChapterEntity,
		passage: String
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
	suspend fun handleChapters(novelEntity: NovelEntity, list: List<Novel.Chapter>): HResult<*>

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
		novelEntity: NovelEntity,
		list: List<Novel.Chapter>,
	): HResult<List<ChapterEntity>>

	/**
	 * Loads [ChapterEntity]s matching [novelID]
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
	suspend fun loadChapters(novelID: Int): Flow<HResult<List<ChapterEntity>>>

	/**
	 * Loads a [ChapterEntity] by its [chapterID]
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
	suspend fun loadChapter(chapterID: Int): HResult<ChapterEntity>

	/**
	 * Update [chapterEntity] in database
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
	suspend fun updateChapter(chapterEntity: ChapterEntity): HResult<*>

	/**
	 * Loads [ReaderChapterEntity]s by it's [novelID]
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
	suspend fun loadReaderChapters(novelID: Int): Flow<HResult<List<ReaderChapterEntity>>>

	/**
	 * Update [readerChapterEntity] in database
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
	suspend fun updateReaderChapter(readerChapterEntity: ReaderChapterEntity): HResult<*>

	/**
	 * Delete the chapter passage from storage
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
	suspend fun deleteChapterPassage(chapterEntity: ChapterEntity): HResult<*>
}