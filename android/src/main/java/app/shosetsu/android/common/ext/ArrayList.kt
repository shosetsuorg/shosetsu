package app.shosetsu.android.common.ext

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
 *
 * 14 / 12 / 2020
 */

/**
 * 14 / 12 / 2020
 *
 * Removes the first element matching the [predicate]
 */
fun <T> ArrayList<T>.removeFirst(predicate: (T) -> Boolean): Boolean {
	for (i in size - 1 downTo 0)
		if (predicate(get(i))) {
			removeAt(i)
			return true
		}
	return false
}