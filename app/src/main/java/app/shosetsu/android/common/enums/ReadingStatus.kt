package app.shosetsu.android.common.enums

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
 * Shosetsu
 * 20 / 06 / 2019
 * Status of a [com.github.doomsdayrs.apps.shosetsu.domain.model.local.ChapterEntity]
 */
enum class ReadingStatus(val a: Int, val status: String) {
	// Novels and chapters
	UNREAD(0, "Unread"), READING(1, "Reading"), READ(2, "Read"),

	UNKNOWN(-1, "Unknown");

	override fun toString(): String = a.toString()

	companion object {
		fun getStatus(a: Int): ReadingStatus {
			return when (a) {
				0 -> UNREAD
				1 -> READING
				2 -> READ
				else -> UNKNOWN
			}
		}
	}

}