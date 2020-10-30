package app.shosetsu.android.common

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.edit
import app.shosetsu.android.common.consts.settings.*
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

	/**
	 * Value is an identifier of [ColorChoiceUI]
	 */
	var selectedReaderTheme: Int
		set(value) = readerSettings.edit { putInt(READER_THEME, value) }.also {
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
			}.also { }
		}


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



	// Advanced Settings

	var showIntro: Boolean
		set(value) = settings.edit { putBoolean(FIRST_TIME, value) }
		get() = settings.getBoolean(FIRST_TIME, true)
	// Formatter Settings



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
