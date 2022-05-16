package app.shosetsu.android.common.utils

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
 * Converts a string passage into an HTML passage.
 * Used with the option for converting string sources to html
 */
fun asHtml(
	passage: String,
	title: String = "Converted from string",
	separator: String = ""
): String =
	"""
	<!DOCTYPE html>
	<html>
		<header>
			<h1>$title</h1>
		</header>
		<body>
			${
		passage.split("\n").joinToString(separator = separator) { "<p>$it</p>" }
	}
		</body>
	</html> 
	"""
