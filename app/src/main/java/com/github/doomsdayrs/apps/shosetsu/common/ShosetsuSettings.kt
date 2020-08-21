package com.github.doomsdayrs.apps.shosetsu.common

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import com.github.doomsdayrs.apps.shosetsu.common.consts.settings.*
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchIO

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
 */

/**
 * Shosetsu
 * 14 / June / 2019
 *
 * Setting variables to work with
 */
class ShosetsuSettings(
		/** Application context for internal use */
		val context: Context,
		val settings: SharedPreferences = context.getSharedPreferences("view", 0),
		private val readerSettings: SharedPreferences = context.getSharedPreferences("reader", 0)

) {

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


	val readerTextSizeLive: MutableLiveData<Float> by lazy {
		MutableLiveData(readerTextSize)
	}

	val readerParagraphSpacingLive: MutableLiveData<Int> by lazy {
		MutableLiveData(readerParagraphSpacing)
	}

	val readerIndentSizeLive: MutableLiveData<Int> by lazy {
		MutableLiveData(readerIndentSize)
	}

	// Real data


	// Reader

	var readerTextSize: Float
		set(value) = readerSettings.edit { putFloat(READER_TEXT_SIZE, value) }.also {
			launchIO { readerTextSizeLive.postValue(value) }
		}
		get() = readerSettings.getFloat(READER_TEXT_SIZE, 14f)

	var readerIndentSize: Int
		set(value) = settings.edit { putInt(READER_TEXT_INDENT, value) }.also {
			launchIO { readerIndentSizeLive.postValue(value) }
		}
		get() = settings.getInt(READER_TEXT_INDENT, 1)

	var readerParagraphSpacing: Int
		set(value) = readerSettings.edit { putInt(READER_TEXT_SPACING, value) }.also {
			launchIO { readerParagraphSpacingLive.postValue(value) }
		}
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

	var resumeOpenFirstUnread: Boolean
		set(value) = readerSettings.edit { putBoolean(READER_RESUME_FIRST_UNREAD, value) }
		get() = readerSettings.getBoolean(READER_RESUME_FIRST_UNREAD, false)

	// View Settings

	var columnsInNovelsViewP
		set(value) = settings.edit { putInt(C_IN_NOVELS_P, value) }
		get() = settings.getInt(C_IN_NOVELS_P, -1)

	var columnsInNovelsViewH
		set(value) = settings.edit { putInt(C_IN_NOVELS_H, value) }
		get() = settings.getInt(C_IN_NOVELS_H, 0)

	var novelCardType
		set(value) = settings.edit { putInt(NOVEL_CARD_TYPE, value) }
		get() = settings.getInt(NOVEL_CARD_TYPE, 0)


	// Update Settings

	/**
	 * How many hours between each update check
	 */
	var updateCycle: Int
		set(value) = settings.edit { putInt(UPDATE_CYCLE, value) }
		get() = settings.getInt(UPDATE_CYCLE, 1)

	var updateOnLowBattery: Boolean
		set(value) = settings.edit { putBoolean(UPDATE_LOW_BATTERY, value) }
		get() = settings.getBoolean(UPDATE_LOW_BATTERY, false)

	var updateOnLowStorage: Boolean
		set(value) = settings.edit { putBoolean(UPDATE_LOW_STORAGE, value) }
		get() = settings.getBoolean(UPDATE_LOW_STORAGE, false)

	var updateOnMetered: Boolean
		set(value) = settings.edit { putBoolean(UPDATE_METERED, value) }
		get() = settings.getBoolean(UPDATE_METERED, false)

	var updateOnlyIdle: Boolean
		set(value) = settings.edit { putBoolean(UPDATE_IDLE, value) }
		get() = settings.getBoolean(UPDATE_IDLE, false)


	var downloadOnUpdate: Boolean
		set(value) = settings.edit { putBoolean(IS_DOWNLOAD_ON_UPDATE, value) }
		get() = settings.getBoolean(IS_DOWNLOAD_ON_UPDATE, false)

	var onlyUpdateOngoing: Boolean
		set(value) = settings.edit { putBoolean(ONLY_UPDATE_ONGOING, value) }
		get() = settings.getBoolean(ONLY_UPDATE_ONGOING, false)

	// Advanced Settings
	var showIntro: Boolean
		set(value) = settings.edit { putBoolean(FIRST_TIME, value) }
		get() = settings.getBoolean(FIRST_TIME, true)

	// Download Settings

	var downloadDirectory: String
		set(value) = settings.edit { putString(DOWNLOAD_DIRECTORY, value) }
		get() = settings.getString(DOWNLOAD_DIRECTORY, "/Shosetsu/")!!

	var downloadOnLowBattery: Boolean
		set(value) = settings.edit { putBoolean(DOWNLOAD_LOW_BATTERY, value) }
		get() = settings.getBoolean(DOWNLOAD_LOW_BATTERY, false)

	var downloadOnLowStorage: Boolean
		set(value) = settings.edit { putBoolean(DOWNLOAD_LOW_STORAGE, value) }
		get() = settings.getBoolean(DOWNLOAD_LOW_STORAGE, false)

	var downloadOnMetered: Boolean
		set(value) = settings.edit { putBoolean(DOWNLOAD_METERED, value) }
		get() = settings.getBoolean(DOWNLOAD_METERED, false)

	var downloadOnlyIdle: Boolean
		set(value) = settings.edit { putBoolean(DOWNLOAD_IDLE, value) }
		get() = settings.getBoolean(DOWNLOAD_IDLE, false)

	/** If download manager is paused */
	var isDownloadPaused: Boolean
		set(value) = settings.edit { putBoolean(IS_DOWNLOAD_PAUSED, value) }
		get() = settings.getBoolean(IS_DOWNLOAD_PAUSED, false)

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
