package app.shosetsu.android.common.consts

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

const val SELECTED_STROKE_WIDTH: Int = 8

const val FLING_SPEED = 10
const val FLING_DIVIDE = 100
const val FLING_THRESHOLD = 15000

const val SCRIPT_DIR: String = "/scripts/"
const val LIBRARY_DIR: String = "/libraries/"
const val SOURCE_DIR: String = "/src/"
const val REPO_DIR_STRUCT: String = "/src/main/resources/"

@Deprecated("Just use View", replaceWith = ReplaceWith("VISIBLE", "import android.view.View.VISIBLE"), DeprecationLevel.WARNING)
const val VISIBLE: Int = View.VISIBLE

@Deprecated("Just use View", replaceWith = ReplaceWith("GONE", "import android.view.View.GONE"), DeprecationLevel.WARNING)
const val GONE: Int = View.GONE
