package com.github.doomsdayrs.apps.shosetsu.backend.database

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
 * ====================================================================
 */

/**
 * shosetsu
 * 18 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
enum class Tables(private val key: String) {
	NOVEL_IDENTIFICATION("novel_identification"),
	CHAPTER_IDENTIFICATION("chapter_identification"),
	NOVELS("novels"),
	CHAPTERS("chapters"),

	@Deprecated("ROOM")
	UPDATES("updates"),

	@Deprecated("ROOM")
	DOWNLOADS("downloads"), ;

	override fun toString(): String {
		return key
	}
}