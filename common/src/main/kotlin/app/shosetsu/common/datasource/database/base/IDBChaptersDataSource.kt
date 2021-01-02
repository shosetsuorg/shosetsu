package app.shosetsu.common.datasource.database.base

import app.shosetsu.common.domain.model.local.ReaderChapterEntity
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.domain.model.local.ChapterEntity
import app.shosetsu.common.domain.model.local.NovelEntity
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
 * 04 / 05 / 2020
 */
interface IDBChaptersDataSource {
	/** Get the chapters of a novel */
	suspend fun loadChapters(novelID: Int): Flow<HResult<List<ChapterEntity>>>

	/** Loads a chapter by its ID */
	suspend fun loadChapter(chapterID: Int): HResult<ChapterEntity>

	/** Loads chapters by novelID */
	suspend fun loadReaderChapters(novelID: Int): Flow<HResult<List<ReaderChapterEntity>>>

	/** Handles chapters from a remote source */
	suspend fun handleChapters(novelEntity: NovelEntity, list: List<Novel.Chapter>): HResult<*>

	/** Handles chapters from a remote source, then returns the new chapters */
	suspend fun handleChapterReturn(
			novelEntity: NovelEntity,
			list: List<Novel.Chapter>,
	): HResult<List<ChapterEntity>>

	/** Updates a [chapterEntity] */
	suspend fun updateChapter(chapterEntity: ChapterEntity): HResult<*>

	/** Updates a [readerChapterEntity], a cut down version [updateChapter] */
	suspend fun updateReaderChapter(readerChapterEntity: ReaderChapterEntity): HResult<*>
}