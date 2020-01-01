package com.github.doomsdayrs.apps.shosetsu.variables

import android.graphics.Color
import android.net.ConnectivityManager

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
 * ====================================================================
 * Shosetsu
 * 14 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
/**
 * Setting variables to work with
 */
object Settings {
    /**
     * How to mark a chapter as reading
     */
    @JvmField
    var ReaderMarkingType: Int = MarkingTypes.ONVIEW.i

    enum class MarkingTypes(val i: Int) {
        ONVIEW(0),
        ONSCROLL(1)
    }


    /**
     * Reader text size
     */
    @JvmField
    var ReaderTextSize: Float = TextSizes.SMALL.i.toFloat()

    @Suppress("unused")

    //TODO Use this
    enum class TextSizes(val i: Int) {
        SMALL(14),
        MEDIUM(17),
        LARGE(20)
    }

    /**
     * Reader text color
     */
    @JvmField
    var ReaderTextColor = Color.BLACK

    /**
     * Reader background color
     */
    @JvmField
    var ReaderTextBackgroundColor = Color.WHITE

    /**
     * global connectivity manager variable
     */
    @JvmField
    var connectivityManager: ConnectivityManager? = null

    /**
     * If download manager is paused
     */
    @JvmField
    var downloadPaused = false

    /**
     * Current theme to use
     */
    @JvmField
    var themeMode = Themes.LIGHT.id

    //TODO Use this
    enum class Themes(val id: Int) {
        LIGHT(0),
        NIGHT(1),
        DARK(2)
    }

    @JvmField
    var paragraphSpacing = 0

    @JvmField
    var indentSize = 0
}