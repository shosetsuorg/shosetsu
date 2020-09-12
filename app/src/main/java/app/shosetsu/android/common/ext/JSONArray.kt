package app.shosetsu.android.common.ext

import org.json.JSONArray

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
 * 20 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */

/**
 * Performs the given [action] on each element.
 */
inline fun JSONArray.forEach(action: (Any) -> Unit) {
	for (index in 0 until length())
		action(this[index])
}

inline fun <T> JSONArray.forEachTyped(action: (T) -> Unit) {
	for (index in 0 until length())
		action(this[index] as T)
}

inline fun JSONArray.forEachIndexed(action: (index: Int, Any) -> Unit) {
	for (index in 0 until length())
		action(index, this[index])
}
