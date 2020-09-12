package app.shosetsu.android.common.utils


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
 * 18 / 01 / 2020
 * [FormatterUtils] provides a class that handles all basic needs for extensions
 */
class FormatterUtils {
	companion object {
		private fun splitVersion(version: String): Array<String> =
				version.split(".").toTypedArray()

		/**
		 * @return [Boolean] true if version difference
		 */
		fun compareVersions(ver1: String, ver2: String): Boolean {
			if (ver1 == ver2)
				return false

			val version1 = splitVersion(ver1)
			val version2 = splitVersion(ver2)

			if (version1.size != version2.size)
				return false

			version1.forEachIndexed { index, s ->
				if (version2[index] != s)
					return true
			}
			return false
		}
	}

}