package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.util.Log
import android.widget.ArrayAdapter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.Settings
import com.github.doomsdayrs.apps.shosetsu.common.ext.context
import com.github.doomsdayrs.apps.shosetsu.ui.settings.SettingsSubController
import com.github.doomsdayrs.apps.shosetsu.ui.settings.data.base.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.ui.settings.data.dsl.*

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
				spinnerSettingData(0) {
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
				numberPickerSettingData(1) {
					title { R.string.columns_of_novel_listing_p }
					description { (R.string.columns_zero_automatic) }
					numberValue { Settings::columnsInNovelsViewP }
					range { 0 to 10 }
				},
				numberPickerSettingData(2) {
					title { R.string.columns_of_novel_listing_h }
					description { (R.string.columns_zero_automatic) }
					numberValue { Settings::columnsInNovelsViewH }
					range { 0 to 10 }
				},
				spinnerSettingData(3) {
					title { R.string.novel_card_type_selector_title }
					description { R.string.novel_card_type_selector_desc }
					spinnerField { Settings::novelCardType }
					arrayAdapter = ArrayAdapter(
							context!!,
							android.R.layout.simple_spinner_item,
							resources!!.getStringArray(R.array.novel_card_types)
					)
				}
		)
	}

	fun setReaderMarkingType(markingType: Settings.MarkingTypes) {
		Settings.readerMarkingType = markingType
	}
}