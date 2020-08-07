package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.annotation.SuppressLint
import android.util.Log
import android.widget.ArrayAdapter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings
import com.github.doomsdayrs.apps.shosetsu.common.ext.context
import com.github.doomsdayrs.apps.shosetsu.ui.settings.SettingsSubController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.settings.base.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.settings.dsl.*
import org.kodein.di.generic.instance
import java.util.*

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
 * 28 / 03 / 2020
 *
 * @author github.com/doomsdayrs
 */
@SuppressLint("LogConditional")
class ReaderSettings : SettingsSubController() {
	val S: ShosetsuSettings by instance()

	override val settings: ArrayList<SettingsItemData> by settingsList {
		spinnerSettingData(0) {
			title { R.string.paragraph_spacing }
			arrayAdapter = ArrayAdapter(
					context!!,
					android.R.layout.simple_spinner_dropdown_item,
					resources!!.getStringArray(R.array.sizes_with_none)
			)
			spinnerField { S::readerParagraphSpacing }
		}
		spinnerSettingData(1) {
			title { R.string.text_size }
			spinnerSelection = when (S.readerTextSize.toInt()) {
				14 -> 0
				17 -> 1
				20 -> 2
				else -> 0
			}
			arrayAdapter = ArrayAdapter(
					context!!,
					android.R.layout.simple_spinner_dropdown_item,
					resources!!.getStringArray(R.array.sizes_no_none)
			)
			onSpinnerItemSelected { adapterView, _, i, _ ->
				Log.d("TextSizeSelection", i.toString())
				if (i in 0..2) {
					var size = 14
					when (i) {
						0 -> {
						}
						1 -> size = 17
						2 -> size = 20
					}
					S.readerTextSize = (size.toFloat())
					adapterView?.setSelection(i)
				}
			}
		}
		spinnerSettingData(2) {
			title { R.string.paragraph_indent }
			spinnerField { S::readerIndentSize }
			arrayAdapter = ArrayAdapter(
					context!!,
					android.R.layout.simple_spinner_dropdown_item,
					resources!!.getStringArray(R.array.sizes_with_none)
			)
		}
		spinnerSettingData(3) {
			title { R.string.reader_theme }
			spinnerField { S::readerTheme }
			arrayAdapter = ArrayAdapter(
					context!!,
					android.R.layout.simple_spinner_dropdown_item,
					resources!!.getStringArray(R.array.reader_themes)
			)
		}
		colorPickerSettingData(1) {
			title { R.string.text_custom_color }
			colorName { "Text" }
			description { R.string.custom_theme_warn }
			colorValue { S::readerCustomBackColor }
		}
		colorPickerSettingData(1) {
			title { R.string.text_custom_background_color }
			colorName { "back" }
			description { R.string.custom_theme_warn }
			colorValue { S::readerCustomTextColor }
		}
		switchSettingData(1) {
			title { R.string.inverted_swipe }
			checker { S::isInvertedSwipe }
		}
		switchSettingData(1) {
			title { R.string.tap_to_scroll }
			checker { S::isTapToScroll }
		}
		switchSettingData(4) {
			title { "Resume first unread" }
			description { "Instead of resuming the first chapter that is not read(can be reading), the app will open the first unread chapter" }
			checker { S::resumeOpenFirstUnread }
		}
	}
}