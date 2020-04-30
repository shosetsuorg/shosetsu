package com.github.doomsdayrs.apps.shosetsu.view.uimodels

import app.shosetsu.lib.Formatter
import app.shosetsu.lib.Novel

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
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
data class NovelUI(
		val id: Int = -1,

		val novelURL: String,

		val formatter: Formatter,

		var bookmarked: Boolean,

		var readerType: Int,

		var title: String,

		var imageURL: String,

		var description: String?,

		var language: String,

		var genres: Array<String>,
		var authors: Array<String>,
		var artists: Array<String>,
		var tags: Array<String>,

		var status: Novel.Status
)