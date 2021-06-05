package app.shosetsu.common.datasource.file.base

import app.shosetsu.common.domain.model.local.ChapterEntity
import app.shosetsu.common.dto.HResult
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
 * 12 / 05 / 2020
 */
interface IFileChapterDataSource {
	/**
	 * Save the chapter passage to storage
	 */
	suspend fun save(
		chapterEntity: ChapterEntity,
		chapterType: Novel.ChapterType,
		passage: ByteArray
	): HResult<*>

	/**
	 * Gets chapter passage via it's ID
	 * @return [HResult.Empty] if passage not found, [HResult.Success] if found
	 */
	suspend fun load(
		chapterEntity: ChapterEntity,
		chapterType: Novel.ChapterType
	): HResult<ByteArray>

	/** Deletes a chapter from the filesystem */
	suspend fun delete(
		chapterEntity: ChapterEntity,
		chapterType: Novel.ChapterType
	): HResult<*>
}