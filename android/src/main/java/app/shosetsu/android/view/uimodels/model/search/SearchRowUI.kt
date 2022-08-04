package app.shosetsu.android.view.uimodels.model.search

import androidx.compose.runtime.Immutable

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
 * 09 / 09 / 2020
 * @param name Name displayed of this data
 */
@Immutable
data class SearchRowUI(
	val extensionID: Int,
	val name: String,
	val imageURL: String?,
	val hasError: Boolean = false
)