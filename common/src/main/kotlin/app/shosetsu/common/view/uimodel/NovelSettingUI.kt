package app.shosetsu.common.view.uimodel

import app.shosetsu.common.domain.model.local.NovelSettingEntity
import app.shosetsu.common.dto.Convertible
import app.shosetsu.common.enums.ChapterSortType
import app.shosetsu.common.enums.ReaderType
import app.shosetsu.common.enums.ReadingStatus

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
data class NovelSettingUI(
	val novelID: Int,

	// how chapters are sorted
	var sortType: ChapterSortType = ChapterSortType.SOURCE,
	var showOnlyReadingStatusOf: ReadingStatus? = null,
	var showOnlyBookmarked: Boolean = false,
	var showOnlyDownloaded: Boolean = false,
	var reverseOrder: Boolean = false,

	// how the reader is set-up
	var readerType: ReaderType
) : Convertible<NovelSettingEntity> {
	override fun convertTo(): NovelSettingEntity = NovelSettingEntity(
		novelID,
		sortType,
		showOnlyReadingStatusOf,
		showOnlyBookmarked,
		showOnlyDownloaded,
		reverseOrder,
		readerType
	)
}