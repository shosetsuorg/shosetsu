package app.shosetsu.android.domain.model.local

import app.shosetsu.android.common.ext.deserializeString
import app.shosetsu.android.common.ext.serializeToString
import app.shosetsu.android.view.uimodels.model.ColorChoiceUI

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
 * shosetsu
 * 17 / 09 / 2020
 * Choices for colors
 * @param name Name of the color choice
 * @param textColor Color of the text
 * @param backgroundColor Color of the background
 */
data class ColorChoiceData(
	val identifier: Long,
	val name: String,
	val textColor: Int,
	val backgroundColor: Int,
) {

	override fun toString(): String = try {
		"$identifier,${name.serializeToString()},$textColor,$backgroundColor"
	} catch (e: Exception) {
		"$identifier,FAILED,$textColor,$backgroundColor"
	}

	companion object {
		/**
		 * Converts a string into a [ColorChoiceUI]
		 */
		fun fromString(string: String): ColorChoiceData = string.split(",").let {
			ColorChoiceData(
				it[0].toLong(),
				try {
					it[1].deserializeString()
				} catch (e: Exception) {
					null
				} ?: "UNKNOWN",
				it[2].toInt(),
				it[3].toInt()
			)
		}
	}

}