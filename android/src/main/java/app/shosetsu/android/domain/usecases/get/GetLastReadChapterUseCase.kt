package app.shosetsu.android.domain.usecases.get

import app.shosetsu.common.domain.model.local.ChapterHistoryEntity
import app.shosetsu.common.domain.repositories.base.IChapterHistoryRepository

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
 * @since 13 / 11 / 2021
 * @author Doomsdayrs
 *
 * Get the last read chapter of a novel
 *
 * This is useful when you need to act upon a certain chapter or its neighbors
 */
class GetLastReadChapterUseCase(
	private val chapterHistory: IChapterHistoryRepository
) {
	operator fun invoke(novelId: Int): ChapterHistoryEntity? {
		return chapterHistory.getLastRead(novelId)
	}
}