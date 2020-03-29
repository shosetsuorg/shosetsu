package com.github.doomsdayrs.apps.shosetsu.backend

import android.content.SharedPreferences
import android.graphics.Color
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

	enum class TextSizes(val i: Float) {
		SMALL(14F),
		MEDIUM(17F),
		LARGE(20F)
	}

	enum class ReaderThemes(val i: Int) {
		NIGHT(0),
		LIGHT(1),
		SEPIA(2),
		DARK(3),
		DARKI(4),
		CUSTOM(5);
	}

	lateinit var settings: SharedPreferences
	lateinit var readerSettings: SharedPreferences
	lateinit var formatterSettings: SharedPreferences


	// Constant keys
	const val LISTING_KEY = "listing"

	// READER
	/**
	 * How to mark a chapter as reading
	 */
	var readerMarkingType: Int
		set(value) = readerSettings.edit().putInt("readerMarkingType", value).apply()
		get() = readerSettings.getInt("readerMarkingType", MarkingTypes.ONVIEW.i)

	var readerTextSize: Float
		set(value) = readerSettings.edit().putFloat("readerTextSize", value).apply()
		get() = readerSettings.getFloat("readerTextSize", 14f)

	var readerParagraphSpacing: Int
		set(value) = readerSettings.edit().putInt("readerParagraphSpacing", value).apply()
		get() = readerSettings.getInt("readerParagraphSpacing", 1)


	var readerTheme: Int
		set(value) = readerSettings.edit().putInt("readerTheme", value).apply()
		get() = readerSettings.getInt("readerTheme", ReaderThemes.SEPIA.i)

	var readerCustomBack: Int
		set(value) = readerSettings.edit().putInt("readerCustomBack", value).apply()
		get() = readerSettings.getInt("readerCustomBack", Color.WHITE)

	var readerCustomFront: Int
		set(value) = readerSettings.edit().putInt("readerCustomFront", value).apply()
		get() = readerSettings.getInt("readerCustomFront", Color.BLACK)


	var isTapToScroll: Boolean
		set(value) = readerSettings.edit().putBoolean("invertedSwipe", false).apply()
		get() = readerSettings.getBoolean("tapToScroll", false)

	var isInvertedSwipe: Boolean
		get() = readerSettings.getBoolean("invertedSwipe", false)
		set(value) = readerSettings.edit().putBoolean("invertedSwipe", value).apply()

	// View Settings
	/**
	 * If download manager is paused
	 */
	var downloadPaused: Boolean
		set(value) = settings.edit().putBoolean("paused", value).apply()
		get() = settings.getBoolean("paused", false)

	var isDownloadOnUpdateEnabled: Boolean
		set(value) = settings.edit().putBoolean("downloadOnUpdate", value).apply()
		get() = settings.getBoolean("downloadOnUpdate", false)


	var disabledFormatters: JSONArray
		set(value) = settings.edit().putString("disabledFormatters", value.toString()).apply()
		get() = JSONArray(settings.getString("disabledFormatters", "[]"))

	var ReaderIndentSize
		set(value) = settings.edit().putInt("indentSize", value).apply()
		get() = settings.getInt("indentSize", 1)

	var columnsInNovelsViewP
		set(value) = settings.edit().putInt("columnsInNovelsViewP", value).apply()
		get() = settings.getInt("columnsInNovelsViewP", -1)

	var columnsInNovelsViewH
		set(value) = settings.edit().putInt("columnsInNovelsViewH", value).apply()
		get() = settings.getInt("columnsInNovelsViewH", -1)

	var novelCardType
		set(value) = settings.edit().putInt("novelCardType", value).apply()
		get() = settings.getInt("novelCardType", 0)


	// Advanced Settings
	var showIntro: Boolean
		set(value) = settings.edit().putBoolean("first_time", value).apply()
		get() = settings.getBoolean("first_time", true)

	// Download Settings

	// Formatter Settings
}

