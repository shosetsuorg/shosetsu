package com.github.doomsdayrs.apps.shosetsu.variables.ext

import org.luaj.vm2.LuaError

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
 * 04 / 03 / 2020
 *
 * @author github.com/doomsdayrs
 */

fun LuaError.smallMessage(): String {
	return this.message?.let { it ->
		return it.substring(it.lastIndexOf("}").let {
			return@let when {
				it > 0 -> it
				else -> 0
			}
		})
	} ?: "UNKNOWN ERROR"
}
