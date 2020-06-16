package com.github.doomsdayrs.apps.shosetsu.common

import android.content.SharedPreferences
import android.graphics.Color
import androidx.core.content.edit

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
		ONSCROLL(1);
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
	private const val FIRST_TIME = "first_time"


	// How things look in Reader
	const val READER_THEME = "readerTheme"
	const val READER_TEXT_C_COLOR = "readerCustomTextColor"
	const val READER_BACK_C_COLOR = "readerCustomBackColor"

	const val READER_TEXT_SIZE = "readerTextSize"
	const val READER_TEXT_SPACING = "readerParagraphSpacing"
	const val READER_TEXT_INDENT = "readerIndentSize"

	//- How things act in Reader
	const val READER_IS_TAP_TO_SCROLL = "tapToScroll"
	const val READER_IS_INVERTED_SWIPE = "invertedSwipe"
	const val READER_MARKING_TYPE = "readerMarkingType"

	// Download options
	const val IS_DOWNLOAD_PAUSED = "isDownloadPaused"
	const val IS_DOWNLOAD_ON_UPDATE = "isDownloadOnUpdate"

	const val DISABLED_FORMATTERS = "disabledFormatters"
	const val DELETE_READ_CHAPTER = "deleteReadChapter"

	// View options
	const val C_IN_NOVELS_P = "columnsInNovelsViewP"
	const val C_IN_NOVELS_H = "columnsInNovelsViewH"
	const val NOVEL_CARD_TYPE = "novelCardType"

	// Backup Options
	const val BACKUP_CHAPTERS = "backupChapters"
	const val BACKUP_SETTINGS = "backupSettings"
	const val BACKUP_QUICK = "backupQuick"

	// Download Options
	private const val DOWNLOAD_DIRECTORY = "downloadDirectory"

	// READER
	/**
	 * How to mark a chapter as reading
	 */
	var readerMarkingType: MarkingTypes
		set(value) {
			readerSettings.edit { putInt(READER_MARKING_TYPE, value.i) }
		}
		get() = readerSettings.getInt(READER_MARKING_TYPE, MarkingTypes.ONVIEW.i).let {
			when (it) {
				0 -> MarkingTypes.ONVIEW
				1 -> MarkingTypes.ONSCROLL
				else -> MarkingTypes.ONSCROLL
			}
		}

	var readerTextSize: Float
		set(value) = readerSettings.edit { putFloat(READER_TEXT_SIZE, value) }
		get() = readerSettings.getFloat(READER_TEXT_SIZE, 14f)

	var readerParagraphSpacing: Int
		set(value) = readerSettings.edit { putInt(READER_TEXT_SPACING, value) }
		get() = readerSettings.getInt(READER_TEXT_SPACING, 1)


	var readerTheme: Int
		set(value) = readerSettings.edit { putInt(READER_THEME, value) }
		get() = readerSettings.getInt(READER_THEME, ReaderThemes.SEPIA.i)

	var readerCustomTextColor: Int
		set(value) = readerSettings.edit { putInt(READER_TEXT_C_COLOR, value) }
		get() = readerSettings.getInt(READER_TEXT_C_COLOR, Color.WHITE)

	var readerCustomBackColor: Int
		set(value) = readerSettings.edit { putInt(READER_BACK_C_COLOR, value) }
		get() = readerSettings.getInt(READER_BACK_C_COLOR, Color.BLACK)


	var isTapToScroll: Boolean
		set(value) = readerSettings.edit { putBoolean(READER_IS_TAP_TO_SCROLL, value) }
		get() = readerSettings.getBoolean(READER_IS_TAP_TO_SCROLL, false)

	var isInvertedSwipe: Boolean
		set(value) = readerSettings.edit { putBoolean(READER_IS_INVERTED_SWIPE, value) }
		get() = readerSettings.getBoolean(READER_IS_INVERTED_SWIPE, false)

	/**
	 * Which chapter to delete after reading
	 * If -1, then does nothing
	 * If 0, then deletes the read chapter
	 * If 1+, deletes the chapter of READ CHAPTER - [deletePreviousChapter]
	 */
	var deletePreviousChapter: Int
		set(value) = readerSettings.edit { putInt(DELETE_READ_CHAPTER, value) }
		get() = readerSettings.getInt(DELETE_READ_CHAPTER, -1)

	// View Settings
	/**
	 * If download manager is paused
	 */
	var isDownloadPaused: Boolean
		set(value) = settings.edit { putBoolean(IS_DOWNLOAD_PAUSED, value) }
		get() = settings.getBoolean(IS_DOWNLOAD_PAUSED, false)

	var isDownloadOnUpdateEnabled: Boolean
		set(value) = settings.edit { putBoolean(IS_DOWNLOAD_ON_UPDATE, value) }
		get() = settings.getBoolean(IS_DOWNLOAD_ON_UPDATE, false)

	var ReaderIndentSize
		set(value) = settings.edit { putInt(READER_TEXT_INDENT, value) }
		get() = settings.getInt(READER_TEXT_INDENT, 1)

	var columnsInNovelsViewP
		set(value) = settings.edit { putInt(C_IN_NOVELS_P, value) }
		get() = settings.getInt(C_IN_NOVELS_P, -1)

	var columnsInNovelsViewH
		set(value) = settings.edit { putInt(C_IN_NOVELS_H, value) }
		get() = settings.getInt(C_IN_NOVELS_H, -1)

	var novelCardType
		set(value) = settings.edit { putInt(NOVEL_CARD_TYPE, value) }
		get() = settings.getInt(NOVEL_CARD_TYPE, 0)


	// Advanced Settings
	var showIntro: Boolean
		set(value) = settings.edit { putBoolean(FIRST_TIME, value) }
		get() = settings.getBoolean(FIRST_TIME, true)

	// Download Settings
	var downloadDirectory: String
		set(value) = settings.edit { putString(DOWNLOAD_DIRECTORY, value) }
		get() = settings.getString(DOWNLOAD_DIRECTORY, "/Shosetsu/")!!

	// Formatter Settings

	// Backup Settings
	var backupChapters: Boolean
		set(value) = settings.edit { putBoolean(BACKUP_CHAPTERS, value) }
		get() = settings.getBoolean(BACKUP_CHAPTERS, true)

	var backupSettings: Boolean
		set(value) = settings.edit { putBoolean(BACKUP_SETTINGS, value) }
		get() = settings.getBoolean(BACKUP_SETTINGS, false)


	var backupQuick: Boolean
		set(value) = settings.edit { putBoolean(BACKUP_QUICK, value) }
		get() = settings.getBoolean(BACKUP_QUICK, false)
}

