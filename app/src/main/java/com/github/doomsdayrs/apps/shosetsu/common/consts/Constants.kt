package com.github.doomsdayrs.apps.shosetsu.common.consts

import android.view.View
import com.github.doomsdayrs.apps.shosetsu.BuildConfig

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
 * 04 / 05 / 2020
 */

val SHOSETSU_UPDATE_URL: String =
		"https://raw.githubusercontent.com/Doomsdayrs/shosetsu/${
			if (BuildConfig.DEBUG)
				"development"
			else "master"
		}/app/src/${
			if (BuildConfig.DEBUG)
				"debug"
			else "master"
		}/assets/update.${
			if (BuildConfig.DEBUG)
				"json"
			else "xml"
		}"

const val selectedStrokeWidth: Int = 8

const val scriptDirectory: String = "/scripts/"
const val libraryDirectory: String = "/libraries/"
const val sourceFolder: String = "/src/"
const val repoFolderStruct: String = "/src/main/resources/"

/** @see View.VISIBLE */
const val VISIBLE: Int = View.VISIBLE

/** @see View.GONE */
const val GONE: Int = View.GONE
