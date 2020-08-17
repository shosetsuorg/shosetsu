package com.github.doomsdayrs.apps.shosetsu.common.consts

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
 * 12 / May / 2020
 */
object ErrorKeys {
	/** When something is wrong generally */
	const val ERROR_GENERAL = 0

	/** When something is wrong in lua generally */
	const val ERROR_LUA_GENERAL = 1

	/** When there is a network error*/
	const val ERROR_NETWORK = 2

	/** When there is a not found error */
	const val ERROR_NOT_FOUND = 3

	/** When the lua script is broken */
	const val ERROR_LUA_BROKEN = 4

	/** When the extension has no search functionality */
	const val ERROR_NO_SEARCH = 5
}