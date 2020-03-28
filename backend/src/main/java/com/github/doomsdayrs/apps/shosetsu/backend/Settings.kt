package com.github.doomsdayrs.apps.shosetsu.backend

import android.graphics.Color
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities.FIRST_TIME_KEY
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities.advancedPreferences
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities.downloadPreferences
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities.viewPreferences
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
			viewPreferences.edit().putInt("markingType", value).apply()
		}
		get() = viewPreferences.getInt("markingType", MarkingTypes.ONVIEW.i)


	/**
	 * Reader text size
	 */
	var ReaderTextSize: Float = TextSizes.SMALL.i.toFloat()
		get() = viewPreferences.getInt("ReaderTextSize", 14).toFloat()
		set(value) {
			field = value
			viewPreferences.edit().putInt("ReaderTextSize", value.toInt()).apply()
		}

	/**
	 * Get's the reader color
	 */
	var ReaderTheme: Int = 0
		get() = viewPreferences.getInt("ReaderTheme", 0)
		set(value) {
			field = value
			viewPreferences.edit().putInt("ReaderTheme", value).apply()
		}

	/**
	 * Reader text color
	 */
	@Deprecated("Bad practice", replaceWith = ReplaceWith("ReaderTheme"), level = DeprecationLevel.ERROR)
	var ReaderTextColor = Color.BLACK
		set(value) {
			field = value
			viewPreferences.edit().putInt("ReaderTextColor", value).apply()
		}
		get() = viewPreferences.getInt("ReaderTextColor", Color.BLACK)

	/**
	 * Reader background color
	 */
    @Deprecated("Bad practice", replaceWith = ReplaceWith("ReaderTheme"), level = DeprecationLevel.ERROR)
	var ReaderTextBackgroundColor = Color.WHITE
		set(value) {
			field = value
			viewPreferences.edit().putInt("ReaderBackgroundColor", value).apply()
		}
		get() = viewPreferences.getInt("ReaderBackgroundColor", Color.WHITE)


	/**
	 * If download manager is paused
	 */
	var downloadPaused: Boolean = false
		set(value) {
			field = value
			downloadPreferences.edit().putBoolean("paused", field).apply()
		}
		get() = downloadPreferences.getBoolean("paused", false)

	var isDownloadOnUpdateEnabled: Boolean = false
		set(value) {
			field = value
			viewPreferences.edit().putBoolean("downloadOnUpdate", value).apply()
		}
		get() = viewPreferences.getBoolean("downloadOnUpdate", false)

	var paragraphSpacing: Int = 0
		set(value) {
			field = value
			viewPreferences.edit().putInt("paragraphSpacing", value).apply()
		}
		get() = viewPreferences.getInt("paragraphSpacing", 1)

	var disabledFormatters: JSONArray = JSONArray()
		set(value) {
			field = value
			advancedPreferences.edit().putString("disabledFormatters", value.toString()).apply()
		}
		get() = JSONArray(advancedPreferences.getString("disabledFormatters", "[]"))

	var indentSize = 0
		set(value) {
			field = value
			viewPreferences.edit().putInt("indentSize", value).apply()
		}
		get() = viewPreferences.getInt("indentSize", 1)

	var columnsInNovelsViewP = -1
		set(value) {
			field = value
			viewPreferences.edit().putInt("columnsInNovelsViewP", value).apply()
		}
		get() = viewPreferences.getInt("columnsInNovelsViewP", -1)

	var columnsInNovelsViewH = -1
		set(value) {
			field = value
			viewPreferences.edit().putInt("columnsInNovelsViewH", value).apply()
		}
		get() = viewPreferences.getInt("columnsInNovelsViewH", -1)

	var novelCardType = 0
		set(value) {
			field = value
			viewPreferences.edit().putInt("novelCardType", value).apply()
		}
		get() = viewPreferences.getInt("novelCardType", 0)

	var showIntro: Boolean = false
		set(value) {
			field = value
			advancedPreferences.edit().putBoolean(FIRST_TIME_KEY, field).apply()
		}
		get() = advancedPreferences.getBoolean(FIRST_TIME_KEY, true)
}