package app.shosetsu.android.common

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.shosetsu.android.common.consts.settings.*
import app.shosetsu.android.common.enums.MarkingTypes
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.view.uimodels.model.ColorChoiceUI
import com.github.doomsdayrs.apps.shosetsu.R

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
@Deprecated("Integrating into MVVM")
class ShosetsuSettings(
		/** Application context for internal use */
		val context: Context,
		val settings: SharedPreferences = context.getSharedPreferences("view", 0),
		private val readerSettings: SharedPreferences = settings,
) {

	/** LiveData of [readerTextSize] */
	val readerTextSizeLive: LiveData<Float>
		get() = _readerTextSizeLive

	/** LiveData of [readerParagraphSpacing] */
	val readerParagraphSpacingLive: LiveData<Int>
		get() = _readerParagraphSpacingLive

	/** LiveData of [readerIndentSize] */
	val readerIndentSizeLive: LiveData<Int>
		get() = _readerIndentSizeLive

	/** LiveData of [selectedReaderTheme] */
	val readerUserThemeSelectionLive: LiveData<Int>
		get() = _readerUserThemeSelectionLive

	/** LiveData of [readerUserThemes] */
	val readerUserThemesLive: LiveData<List<ColorChoiceUI>>
		get() = _readerUserThemesLive

	/** Live data of [getReaderTextColor] to [getReaderBackgroundColor]*/
	val readerUserThemeSelectionColorLive: LiveData<Pair<Int, Int>>
		get() = _readerUserThemeSelectionColorLive

	/** LiveData of [isDownloadPaused] */
	val isDownloadPausedLive: LiveData<Boolean>
		get() = _isDownloadPausedLive

	private val _readerTextSizeLive: MutableLiveData<Float> by lazy {
		MutableLiveData(readerTextSize)
	}

	private val _readerParagraphSpacingLive: MutableLiveData<Int> by lazy {
		MutableLiveData(readerParagraphSpacing)
	}

	private val _readerIndentSizeLive: MutableLiveData<Int> by lazy {
		MutableLiveData(readerIndentSize)
	}

	private val _readerUserThemeSelectionLive: MutableLiveData<Int> by lazy {
		MutableLiveData(selectedReaderTheme)
	}

	private val _readerUserThemeSelectionColorLive: MutableLiveData<Pair<Int, Int>> by lazy {
		MutableLiveData(getReaderTextColor() to getReaderBackgroundColor())
	}

	private val _readerUserThemesLive: MutableLiveData<List<ColorChoiceUI>> by lazy {
		MutableLiveData(readerUserThemes)
	}

	private val _isDownloadPausedLive: MutableLiveData<Boolean> by lazy {
		MutableLiveData(isDownloadPaused)
	}

	//## Real data

	// READER

	var readerTextSize: Float
		set(value) = readerSettings.edit { putFloat(READER_TEXT_SIZE, value) }.also {
			launchIO { _readerTextSizeLive.postValue(value) }
		}
		get() = readerSettings.getFloat(READER_TEXT_SIZE, 14f)

	var readerIndentSize: Int
		set(value) = settings.edit { putInt(READER_TEXT_INDENT, value) }.also {
			launchIO { _readerIndentSizeLive.postValue(value) }
		}
		get() = settings.getInt(READER_TEXT_INDENT, 1)

	var readerParagraphSpacing: Int
		set(value) = readerSettings.edit { putInt(READER_TEXT_SPACING, value) }.also {
			launchIO { _readerParagraphSpacingLive.postValue(value) }
		}
		get() = readerSettings.getInt(READER_TEXT_SPACING, 1)

	/**
	 * Value is an identifier of [ColorChoiceUI]
	 */
	var selectedReaderTheme: Int
		set(value) = readerSettings.edit { putInt(READER_THEME, value) }.also {
			launchIO {
				_readerUserThemeSelectionLive.postValue(value)
				_readerUserThemeSelectionColorLive.postValue(getReaderTextColor() to getReaderBackgroundColor())
			}
		}
		get() = readerSettings.getInt(READER_THEME, -1)

	/**
	 * These represent choices
	 */
	var readerUserThemes: List<ColorChoiceUI>
		get() = readerSettings.getStringSet(READER_USER_THEMES, null)?.let { set ->
			set.map { ColorChoiceUI.fromString(it) }
		} ?: listOf(
				ColorChoiceUI(
						-1,
						context.getString(R.string.light),
						-0x1000000,
						-0x1
				),
				ColorChoiceUI(
						-2,
						context.getString(R.string.light_dark),
						-0x333334,
						-0xbbbbbc
				),
				ColorChoiceUI(
						-3,
						context.getString(R.string.sepia),
						-0x1000000,
						getColor(context, R.color.wheat)
				),
				ColorChoiceUI(
						-4,
						context.getString(R.string.amoled),
						-0x777778,
						-0x1000000
				)
		).also {
			readerUserThemes = it
		}
		set(value) {
			readerSettings.edit {
				putStringSet(READER_USER_THEMES, value.map { it.toString() }.toSet())
			}.also { launchIO { _readerUserThemesLive.postValue(value) } }
		}

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
	private var deletePreviousChapter: Int
		set(value) = readerSettings.edit { putInt(DELETE_READ_CHAPTER, value) }
		get() = readerSettings.getInt(DELETE_READ_CHAPTER, -1)

	var resumeOpenFirstUnread: Boolean
		set(value) = readerSettings.edit { putBoolean(READER_RESUME_FIRST_UNREAD, value) }
		get() = readerSettings.getBoolean(READER_RESUME_FIRST_UNREAD, false)

	// View Settings

	var columnsInNovelsViewP: Int
		set(value) = settings.edit { putInt(C_IN_NOVELS_P, value) }
		get() = settings.getInt(C_IN_NOVELS_P, 3)

	var columnsInNovelsViewH: Int
		set(value) = settings.edit { putInt(C_IN_NOVELS_H, value) }
		get() = settings.getInt(C_IN_NOVELS_H, 6)

	var novelCardType: Int
		set(value) = settings.edit { putInt(NOVEL_CARD_TYPE, value) }
		get() = settings.getInt(NOVEL_CARD_TYPE, 0)

	var navigationStyle: Int
		set(value) = settings.edit { putInt(NAVIGATION_STYLE, value) }
		get() = settings.getInt(NAVIGATION_STYLE, 0)


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
		set(value) = settings.edit { putBoolean(IS_DOWNLOAD_PAUSED, value) }.also {
			_isDownloadPausedLive.postValue(value)
		}
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


	@ColorInt
	fun getReaderBackgroundColor(readerTheme: Long = this.selectedReaderTheme.toLong()): Int {
		return readerUserThemes.find {
			it.identifier == readerTheme
		}?.backgroundColor ?: Color.WHITE
	}

	@ColorInt
	fun getReaderTextColor(readerTheme: Long = this.selectedReaderTheme.toLong()): Int {
		return readerUserThemes.find {
			it.identifier == readerTheme
		}?.textColor ?: Color.BLACK
	}

	fun calculateColumnCount(context: Context, columnWidthDp: Float): Int {
		// For example columnWidthdp=180
		val c = if (context.resources.configuration.orientation == 1)
			columnsInNovelsViewP
		else columnsInNovelsViewH

		val displayMetrics = context.resources.displayMetrics
		val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density

		return if (c <= 0) (screenWidthDp / columnWidthDp + 0.5).toInt()
		else (screenWidthDp / (screenWidthDp / c) + 0.5).toInt()
	}
}
