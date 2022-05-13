package app.shosetsu.android.datasource.local.database.base

import app.shosetsu.android.common.GenericSQLiteException
import app.shosetsu.common.domain.model.local.ChapterEntity
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
 * 04 / 05 / 2020
 */
interface IDBChaptersDataSource {
	/** Get the chapters of a novel */
	suspend fun getChaptersFlow(novelID: Int): Flow<List<ChapterEntity>>

	@Throws(GenericSQLiteException::class)
	suspend fun getChapters(novelID: Int): List<ChapterEntity>

	@Throws(GenericSQLiteException::class)
	suspend fun getChaptersByExtension(extensionId: Int): List<ChapterEntity>

	/** Loads a chapter by its ID */
	@Throws(GenericSQLiteException::class)
	suspend fun getChapter(chapterID: Int): ChapterEntity?

	/** Loads chapters by novelID */
	suspend fun getReaderChapters(novelID: Int): Flow<List<ReaderChapterEntity>>

	/** Handles chapters from a remote source */
	@Throws(GenericSQLiteException::class)
	suspend fun handleChapters(
		novelID: Int,
		extensionID: Int,
		list: List<Novel.Chapter>
	)

	/** Handles chapters from a remote source, then returns the new chapters */
	@Throws(IndexOutOfBoundsException::class, GenericSQLiteException::class)
	suspend fun handleChapterReturn(
		novelID: Int,
		extensionID: Int,
		list: List<Novel.Chapter>,
	): List<ChapterEntity>

	/** Updates a [chapterEntity] */
	@Throws(GenericSQLiteException::class)
	suspend fun updateChapter(chapterEntity: ChapterEntity)

	/** Updates a [readerChapterEntity], a cut down version [updateChapter] */
	@Throws(GenericSQLiteException::class)
	suspend fun updateReaderChapter(readerChapterEntity: ReaderChapterEntity)

	@Throws(GenericSQLiteException::class)
	suspend fun delete(entity: ChapterEntity)
}