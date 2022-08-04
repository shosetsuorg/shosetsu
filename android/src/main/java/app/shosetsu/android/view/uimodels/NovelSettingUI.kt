package app.shosetsu.android.view.uimodels

import androidx.compose.runtime.Immutable
import app.shosetsu.android.common.enums.ChapterSortType
import app.shosetsu.android.common.enums.ReadingStatus
import app.shosetsu.android.domain.model.local.NovelSettingEntity
import app.shosetsu.android.dto.Convertible

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * 30 / 12 / 2020
 */
@Immutable
data class NovelSettingUI(
	val novelID: Int,

	// how chapters are sorted
	val sortType: ChapterSortType = ChapterSortType.SOURCE,
	val showOnlyReadingStatusOf: ReadingStatus? = null,
	val showOnlyBookmarked: Boolean = false,
	val showOnlyDownloaded: Boolean = false,
	val reverseOrder: Boolean = false
) : Convertible<NovelSettingEntity> {
	override fun convertTo(): NovelSettingEntity = NovelSettingEntity(
		novelID,
		sortType,
		showOnlyReadingStatusOf,
		showOnlyBookmarked,
		showOnlyDownloaded,
		reverseOrder,
	)
}