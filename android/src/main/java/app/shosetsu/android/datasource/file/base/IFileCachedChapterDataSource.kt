package app.shosetsu.android.datasource.file.base

import app.shosetsu.android.common.FileNotFoundException
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
 * 17 / 08 / 2020
 *
 * This is the secondary cache system for the app, where chapters are saved to the applications
 * cache directory.
 *
 * Files should not be stored for more then 10 minutes.
 */
interface IFileCachedChapterDataSource {
	/**
	 * Puts a chapter passage into cache, if cache exists this overwrites
	 *
	 * Will launch a second coroutine that will clear out old content
	 */
	suspend fun saveChapterInCache(
		chapterID: Int,
		chapterType: Novel.ChapterType,
		passage: ByteArray
	)

	/**
	 * Gets chapter passage via it's ID
	 */
	@Throws(FileNotFoundException::class)
	suspend fun loadChapterPassage(
		chapterID: Int,
		chapterType: Novel.ChapterType
	): ByteArray
}