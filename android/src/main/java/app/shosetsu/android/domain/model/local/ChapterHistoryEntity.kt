package app.shosetsu.android.domain.model.local

import app.shosetsu.android.common.enums.ReadingStatus
import app.shosetsu.common.enums.ReadingStatus

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
 * @since 11 / 11 / 2021
 * @author Doomsdayrs
 *
 */
sealed class ChapterHistoryEntity {
	abstract val novelId: Int
	abstract val chapterId: Int

	/**
	 * Date this update occurred
	 */
	abstract val asOf: Long

	data class StatusUpdate(
		override val novelId: Int,
		override val chapterId: Int,
		override val asOf: Long,
		val status: ReadingStatus,
	) : ChapterHistoryEntity()
}