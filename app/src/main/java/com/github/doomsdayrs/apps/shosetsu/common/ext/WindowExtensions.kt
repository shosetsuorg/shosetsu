package com.github.doomsdayrs.apps.shosetsu.common.ext

import android.view.View
import android.view.View.*
import android.view.Window

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
 * 19 / 08 / 2020
 *
 * Taken from tachiyomi
 */


fun Window.showBar() {
	val uiFlags = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
			SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
			View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	decorView.systemUiVisibility = uiFlags
}

fun Window.hideBar() {
	val uiFlags = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
			SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
			SYSTEM_UI_FLAG_FULLSCREEN or
			SYSTEM_UI_FLAG_HIDE_NAVIGATION or
			SYSTEM_UI_FLAG_IMMERSIVE_STICKY
	decorView.systemUiVisibility = uiFlags
}

fun Window.defaultBar() {
	decorView.systemUiVisibility = SYSTEM_UI_FLAG_VISIBLE
}

fun Window.isDefaultBar() = decorView.systemUiVisibility == SYSTEM_UI_FLAG_VISIBLE
