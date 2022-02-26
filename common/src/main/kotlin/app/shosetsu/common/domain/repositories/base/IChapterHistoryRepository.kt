package app.shosetsu.common.domain.repositories.base

import app.shosetsu.common.domain.model.local.ChapterEntity
import app.shosetsu.common.domain.model.local.ChapterHistoryEntity
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
 * Shosetsu
 *
 * Contain a history of chapters.
 *
 * @since 11 / 11 / 2021
 * @author Doomsdayrs
 */
interface IChapterHistoryRepository {

	/**
	 * Mark a chapter as having been read
	 */
	fun markChapterAsRead(chapter: ChapterEntity)

	/**
	 * Mark a chapter as being read
	 */
	fun markChapterAsReading(chapter: ChapterEntity)

	/**
	 * Get the last read chapter for a novel
	 *
	 * @return a chapter is found, empty if nothing is there
	 */
	fun getLastRead(novelId: Int): ChapterHistoryEntity?

	/**
	 * Live view of the history
	 */
	val history: Flow<List<ChapterHistoryEntity>>
}