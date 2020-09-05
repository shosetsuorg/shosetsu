package com.github.doomsdayrs.apps.shosetsu.domain.model.local

import com.github.doomsdayrs.apps.shosetsu.domain.model.base.Convertible
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.UpdateChapterUI

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
 * 07 / 05 / 2020
 */
data class UpdateChapterSubEntity(
		val id: Int,
		val url: String,
		val title: String,
		val novelID: Int,
		val bookmarked: Boolean,
		val formatterID: Int,
		val isSaved: Boolean,
) : Convertible<UpdateChapterUI> {
	override fun convertTo(): UpdateChapterUI =
			UpdateChapterUI(id, url, title, novelID, bookmarked, formatterID, isSaved)
}