package com.github.doomsdayrs.apps.shosetsu.variables

import android.graphics.Color
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import org.json.JSONArray

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
    enum class MarkingTypes(val i: Int) {
        ONVIEW(0),
        ONSCROLL(1)
    }

    @Suppress("unused")
    //TODO Use this
    enum class TextSizes(val i: Int) {
        SMALL(14),
        MEDIUM(17),
        LARGE(20)
    }

    /**
     * How to mark a chapter as reading
     */
    var ReaderMarkingType: Int = MarkingTypes.ONVIEW.i
        set(value) {
            field = value
            Utilities.viewPreferences.edit().putInt("markingType", value).apply()
        }
        get() = Utilities.viewPreferences.getInt("markingType", MarkingTypes.ONVIEW.i)


    /**
     * Reader text size
     */
    var ReaderTextSize: Float = TextSizes.SMALL.i.toFloat()
        get() = Utilities.viewPreferences.getInt("ReaderTextSize", 14).toFloat()
        set(value) {
            field = value
            Utilities.viewPreferences.edit().putInt("ReaderTextSize", value.toInt()).apply()
        }

    /**
     * Reader text color
     */
    var ReaderTextColor = Color.BLACK
        set(value) {
            field = value
            Utilities.viewPreferences.edit().putInt("ReaderTextColor", value).apply()
        }
        get() = Utilities.viewPreferences.getInt("ReaderTextColor", Color.BLACK)

    /**
     * Reader background color
     */
    var ReaderTextBackgroundColor = Color.WHITE
        set(value) {
            field = value
            Utilities.viewPreferences.edit().putInt("ReaderBackgroundColor", value).apply()
        }
        get() = Utilities.viewPreferences.getInt("ReaderBackgroundColor", Color.WHITE)

    /**
     * If download manager is paused
     */
    var downloadPaused: Boolean = false
        set(value) {
            field = value
            Utilities.viewPreferences.edit().putBoolean("paused", value).apply()
        }
        get() = Utilities.downloadPreferences.getBoolean("paused", false)

    var isDownloadOnUpdateEnabled: Boolean = false
        set(value) {
            field = value
            Utilities.viewPreferences.edit().putBoolean("downloadOnUpdate", value).apply()
        }
        get() = Utilities.viewPreferences.getBoolean("downloadOnUpdate", false)

    var paragraphSpacing: Int = 0
        set(value) {
            field = value
            Utilities.viewPreferences.edit().putInt("paragraphSpacing", value).apply()
        }
        get() = Utilities.viewPreferences.getInt("paragraphSpacing", 1)

    var disabledFormatters: JSONArray = JSONArray()
        set(value) {
            field = value
            Utilities.advancedPreferences.edit().putString("disabledFormatters", value.toString()).apply()
        }
        get() = JSONArray(Utilities.advancedPreferences.getString("disabledFormatters", "[]"))

    var indentSize = 0
        set(value) {
            field = value
            Utilities.viewPreferences.edit().putInt("indentSize", value).apply()
        }
        get() = Utilities.viewPreferences.getInt("indentSize", 1)

    var columnsInNovelsViewP = -1
        set(value) {
            field = value
            Utilities.viewPreferences.edit().putInt("columnsInNovelsViewP", value).apply()
        }
        get() = Utilities.viewPreferences.getInt("columnsInNovelsViewP", -1)

    var columnsInNovelsViewH = -1
        set(value) {
            field = value
            Utilities.viewPreferences.edit().putInt("columnsInNovelsViewH", value).apply()
        }
        get() = Utilities.viewPreferences.getInt("columnsInNovelsViewH", -1)

    var novelCardType = 0
        set(value) {
            field = value
            Utilities.viewPreferences.edit().putInt("novelCardType", value).apply()
        }
        get() = Utilities.viewPreferences.getInt("novelCardType", 0)

}