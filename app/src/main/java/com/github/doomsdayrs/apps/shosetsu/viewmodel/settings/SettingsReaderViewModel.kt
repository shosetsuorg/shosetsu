package com.github.doomsdayrs.apps.shosetsu.viewmodel.settings

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData.SettingsType.*
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ISettingsReaderViewModel

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
 * shosetsu
 * 12 / May / 2020
 */
class SettingsReaderViewModel(
		val context: Context,
		val resources: Resources = context.resources
) : ISettingsReaderViewModel() {
	override val settings: List<SettingsItemData> by lazy {
		arrayListOf(
				SettingsItemData(SPINNER, 0)
						.setTitle(R.string.spacing)
						.setSpinnerSelection(Settings.readerParagraphSpacing)
						.setArrayAdapter(ArrayAdapter(
								context,
								android.R.layout.simple_spinner_item,
								resources.getStringArray(R.array.sizes_with_none)
						))
						.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
							override fun onItemSelected(
									adapterView: AdapterView<*>,
									view: View,
									i: Int,
									l: Long
							) {
								Log.d("SpaceSelection", i.toString())
								if (i in 0..3) {
									Settings.readerParagraphSpacing = (i)
									adapterView.setSelection(i)
								}
							}

							override fun onNothingSelected(adapterView: AdapterView<*>?) {}
						}),

				SettingsItemData(SPINNER, 1)
						.setTitle(R.string.text_size)
						.setArrayAdapter(ArrayAdapter(
								context,
								android.R.layout.simple_spinner_item,
								resources.getStringArray(R.array.sizes_no_none)
						))
						.setSpinnerSelection(when (Settings.readerTextSize.toInt()) {
							14 -> 0
							17 -> 1
							20 -> 2
							else -> 0
						})
						.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
							override fun onItemSelected(
									adapterView: AdapterView<*>,
									view: View,
									i: Int,
									l: Long
							) {
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
									adapterView.setSelection(i)
								}
							}

							override fun onNothingSelected(adapterView: AdapterView<*>?) {}
						}),

				SettingsItemData(SPINNER, 1)
						.setTitle(R.string.indent_size)
						.setArrayAdapter(ArrayAdapter(
								context,
								android.R.layout.simple_spinner_item,
								resources.getStringArray(R.array.sizes_with_none)
						))
						.setSpinnerSelection(Settings.ReaderIndentSize)
						.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
							override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
								Log.d("IndentSizeSelection", i.toString())
								if (i in 0..3) {
									Settings.ReaderIndentSize = i
									adapterView.setSelection(i)
								}
							}

							override fun onNothingSelected(adapterView: AdapterView<*>?) {}
						}),

				SettingsItemData(SPINNER, 1)
						.setTitle(R.string.reader_theme)
						.setSpinnerSelection(Settings.readerTheme)
						.setArrayAdapter(ArrayAdapter(
								context,
								android.R.layout.simple_spinner_item,
								resources.getStringArray(R.array.reader_themes)
						))
						.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
							override fun onNothingSelected(p0: AdapterView<*>?) {}
							override fun onItemSelected(
									p0: AdapterView<*>?,
									p1: View?,
									p2: Int,
									p3: Long
							) {
								Log.d("NightMode", p1.toString())
								Settings.readerTheme = p2
							}
						}),

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
						.setOnCheckedListner(CompoundButton.OnCheckedChangeListener { _, _ ->
							toggleInvertedSwipe()
						}),

				SettingsItemData(SWITCH, 1)
						.setTitle(R.string.tap_to_scroll)
						.setIsChecked(Settings.isTapToScroll)
						.setOnCheckedListner(CompoundButton.OnCheckedChangeListener { _, p1 ->
							Log.d("Tap to scroll", p1.toString())
							toggleTapToScroll()
						})
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