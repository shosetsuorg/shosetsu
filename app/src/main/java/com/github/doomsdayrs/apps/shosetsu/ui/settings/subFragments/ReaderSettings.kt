package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.annotation.SuppressLint
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.Settings
import com.github.doomsdayrs.apps.shosetsu.common.ext.context
import com.github.doomsdayrs.apps.shosetsu.ui.settings.*
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData.SettingsType.*
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
	override val settings: ArrayList<SettingsItemData> by lazy {
		arrayListOf(
				settingsItemData(0, SPINNER) {
					title { R.string.paragraph_spacing }
					spinnerSelection = Settings.readerParagraphSpacing
					arrayAdapter = ArrayAdapter(
							context!!,
							android.R.layout.simple_spinner_item,
							resources!!.getStringArray(R.array.sizes_with_none)
					)
					onSpinnerItemSelected { adapterView, view, position, id ->
						Log.d("SpaceSelection", position.toString())
						if (position in 0..3) {
							Settings.readerParagraphSpacing = position
							adapterView?.setSelection(position)
						}
					}
				},
				settingsItemData(1, SPINNER) {
					title { R.string.text_size }
					spinnerSelection = when (Settings.readerTextSize.toInt()) {
						14 -> 0
						17 -> 1
						20 -> 2
						else -> 0
					}
					arrayAdapter = ArrayAdapter(
							context!!,
							android.R.layout.simple_spinner_item,
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
							Settings.readerTextSize = (size.toFloat())
							adapterView?.setSelection(i)
						}
					}
				},
				settingsItemData(2, SPINNER) {
					title { R.string.paragraph_indent }
					spinnerSelection = Settings.readerIndentSize
					arrayAdapter = ArrayAdapter(
							context!!,
							android.R.layout.simple_spinner_item,
							resources!!.getStringArray(R.array.sizes_with_none)
					)
					onSpinnerItemSelected { adapterView, view, i, id ->
						Log.d("IndentSizeSelection", i.toString())
						if (i in 0..3) {
							Settings.readerIndentSize = i
							adapterView?.setSelection(i)
						}
					}
				},
				settingsItemData(3, SPINNER) {
					title { R.string.reader_theme }
					spinnerSelection = Settings.readerTheme
					arrayAdapter = ArrayAdapter(
							context!!,
							android.R.layout.simple_spinner_item,
							resources!!.getStringArray(R.array.reader_themes)
					)
					onSpinnerItemSelected { adapterView, view, position, id ->
						Log.d("NightMode", view.toString())
						Settings.readerTheme = position
					}
				},
				SettingsItemData(COLOR_PICKER, 1)
						.setTitle(R.string.text_custom_color)
						.setDescription(R.string.custom_theme_warn)
						.setPickerColor(Settings.readerCustomBackColor)
						.setColorPreference("text")
						.setOnColorChosen { Settings.readerCustomBackColor = it },

				SettingsItemData(COLOR_PICKER, 1)
						.setTitle(R.string.text_custom_background_color)
						.setColorPreference("back")
						.setDescription(R.string.custom_theme_warn)
						.setPickerColor(Settings.readerCustomTextColor)
						.setOnColorChosen { Settings.readerCustomTextColor = it },

				SettingsItemData(SWITCH, 1)
						.setTitle(R.string.inverted_swipe)
						.setIsChecked(Settings.isInvertedSwipe)
						.setOnCheckedListener(CompoundButton.OnCheckedChangeListener { _, _ ->
							toggleInvertedSwipe()
						}),

				SettingsItemData(SWITCH, 1)
						.setTitle(R.string.tap_to_scroll)
						.setIsChecked(Settings.isTapToScroll)
						.setOnCheckedListener(CompoundButton.OnCheckedChangeListener { _, p1 ->
							Log.d("Tap to scroll", p1.toString())
							toggleTapToScroll()
						}),
				settingsItemData(4, SWITCH) {
					title { "Resume first unread" }
					description { "Instead of resuming the first chapter that is not read(can be reading), the app will open the first unread chapter" }
					isChecked = Settings.resumeOpenFirstUnread
					setOnCheckedListener { _, isChecked ->
						Settings.resumeOpenFirstUnread = isChecked
					}
				}
		)
	}

	private fun toggleTapToScroll(): Boolean {
		val b = Settings.isTapToScroll
		Settings.isTapToScroll = !b
		return !b
	}

	private fun toggleInvertedSwipe(): Boolean {
		val b = Settings.isInvertedSwipe
		Settings.isInvertedSwipe = !b
		return !b
	}
}