package app.shosetsu.android.common.consts

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
	/** An impossibility, This could NOT have happened and yet you have done it. */
	const val ERROR_IMPOSSIBLE: Int = -1

	/** When something is wrong generally */
	const val ERROR_GENERAL: Int = 0

	/** When something is wrong in lua generally */
	const val ERROR_LUA_GENERAL: Int = 1

	/** When there is a network error*/
	const val ERROR_NETWORK: Int = 2

	/** When there is a not found error */
	const val ERROR_NOT_FOUND: Int = 3

	/** When the lua script is broken */
	const val ERROR_LUA_BROKEN: Int = 4

	/** When HTTP returns a non 200 code */
	const val ERROR_HTTP_ERROR: Int = 6

	/** An SQL Exception occurred */
	const val ERROR_HTTP_SQL: Int = 7

	/** An JSON Exception occurred */
	const val ERROR_JSON: Int = 8

	/** An IO Exception occurred */
	const val ERROR_IO: Int = 9

	/** When something is duplicated but shouldn't be */
	const val ERROR_DUPLICATE: Int = 10

	/** Lack permissions to do something */
	const val ERROR_LACK_PERM: Int = 11

	/**
	 * Null pointer occurred
	 */
	const val ERROR_NPE: Int = 12

	/**
	 * The extension is incompatible, Please update or remove
	 */
	const val ERROR_INCOMPATIBLE: Int = 13

	const val ERROR_FILE_IO: Int = 14

	const val ERROR_NETWORK_IO: Int = 15

	/** Timeout exception */
	const val ERROR_TIMEOUT: Int = 16

	const val ERROR_HOST_UNKNOWN: Int = 17
}