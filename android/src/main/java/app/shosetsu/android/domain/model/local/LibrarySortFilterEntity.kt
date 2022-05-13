package app.shosetsu.android.domain.model.local

import app.shosetsu.android.common.enums.InclusionState
import app.shosetsu.android.common.enums.NovelSortType
import kotlinx.serialization.Serializable

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
 * 03 / 01 / 2021
 */
@Serializable
data class LibrarySortFilterEntity(
	var sortType: NovelSortType = NovelSortType.BY_TITLE,
	var reversedSort: Boolean = false,
	var unreadInclusion: InclusionState? = null,

	var genreFilter: Map<String, InclusionState> = mapOf(),
	var authorFilter: Map<String, InclusionState> = mapOf(),
	var artistFilter: Map<String, InclusionState> = mapOf(),
	var tagFilter: Map<String, InclusionState> = mapOf(),
)
