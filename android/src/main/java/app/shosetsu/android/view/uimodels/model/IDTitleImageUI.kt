package app.shosetsu.android.view.uimodels.model

import app.shosetsu.android.domain.model.local.IDTitleImage
import app.shosetsu.common.com.dto.Convertible

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
 * 08 / 05 / 2020
 */
data class IDTitleImageUI(
		val id: Int,
		val title: String,
		val imageURL: String,
) : Convertible<IDTitleImage> {
	override fun convertTo(): IDTitleImage = IDTitleImage(id, title, imageURL)
}