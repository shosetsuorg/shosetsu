package com.github.doomsdayrs.apps.shosetsu.common.consts

import android.view.View

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

const val SHOSETSU_UPDATE_URL: String =
		"https://raw.githubusercontent.com/Doomsdayrs/shosetsu/master/app/src/main/assets/update.xml"

val SHOSETSU_DEV_UPDATE_URL: String =
		SHOSETSU_UPDATE_URL.replace("master", "developmment")

const val selectedStrokeWidth = 8

const val scriptDirectory = "/scripts/"
const val libraryDirectory = "/libraries/"
const val sourceFolder = "/src/"
const val repoFolderStruct = "/src/main/resources/"

/** @see View.VISIBLE */
const val VISIBLE = View.VISIBLE

/** @see View.GONE */
const val GONE = View.GONE
