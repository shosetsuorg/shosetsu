package app.shosetsu.common.com.enums

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
 * 08 / 12 / 2020
 */
enum class NovelUIType(
		private val code: Int
) {
	NORMAL(0),
	COMPRESSED(1),
	COZY(2), ;

	fun toInt(): Int = code

	companion object {
		fun fromInt(code: Int): NovelUIType = values().find { it.code == code }!!
	}
}