package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.util.Log
import android.widget.ArrayAdapter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.Settings
import com.github.doomsdayrs.apps.shosetsu.common.ext.context
import com.github.doomsdayrs.apps.shosetsu.ui.settings.*
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData.SettingsType
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData.SettingsType.SPINNER

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
 * 13 / 07 / 2019
 */
class ViewSettings : SettingsSubController() {
	override val settings: List<SettingsItemData> by lazy {
		listOf(
				settingsItemData(0, SPINNER) {
					title { R.string.marking_mode }
					onSpinnerItemSelected { adapterView, view, position, id ->
						when (position) {
							0 -> setReaderMarkingType(Settings.MarkingTypes.ONVIEW)
							1 -> setReaderMarkingType(Settings.MarkingTypes.ONSCROLL)
							else -> Log.e("MarkingMode", "UnknownType")
						}
					}
					spinnerSelection = Settings.readerMarkingType.i
					arrayAdapter = ArrayAdapter(
							context!!,
							android.R.layout.simple_spinner_item,
							resources!!.getStringArray(R.array.marking_names)
					)
				},
				SettingsItemData(SettingsType.NUMBER_PICKER, 1)
						.setTitle(R.string.columns_of_novel_listing_p)
						.setDescription((R.string.columns_zero_automatic))
						.setNumberValue(Settings.columnsInNovelsViewP.let {
							if (it == -1) 0 else it
						})
						.setNumberLowerBound(0)
						.setNumberUpperBound(10)
						.setNumberPickerOnValueChangedListener { _, _, newVal ->
							when (newVal) {
								0 -> Settings.columnsInNovelsViewP = -1
								else -> Settings.columnsInNovelsViewP = newVal
							}
						},
				SettingsItemData(SettingsType.NUMBER_PICKER, 2)
						.setTitle(R.string.columns_of_novel_listing_h)
						.setDescription(R.string.columns_zero_automatic)
						.setNumberValue(Settings.columnsInNovelsViewH.let {
							if (it == -1) 0 else it
						})
						.setNumberLowerBound(0)
						.setNumberUpperBound(10)
						.setNumberPickerOnValueChangedListener { _, _, newVal ->
							when (newVal) {
								0 -> Settings.columnsInNovelsViewH = -1
								else -> Settings.columnsInNovelsViewH = newVal
							}
						},
				settingsItemData(3, SPINNER) {
					title { R.string.novel_card_type_selector_title }
					description { R.string.novel_card_type_selector_desc }
					arrayAdapter = ArrayAdapter(
							context!!,
							android.R.layout.simple_spinner_item,
							resources!!.getStringArray(R.array.novel_card_types)
					)
					onSpinnerItemSelected { _, _, position, _ ->
						Settings.novelCardType = position
					}
				}
		)
	}

	fun setReaderMarkingType(markingType: Settings.MarkingTypes) {
		Settings.readerMarkingType = markingType
	}
}