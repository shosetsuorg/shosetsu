package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.util.Log
import android.widget.ArrayAdapter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings.MarkingTypes
import com.github.doomsdayrs.apps.shosetsu.common.ext.context
import com.github.doomsdayrs.apps.shosetsu.ui.settings.SettingsSubController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.settings.base.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.settings.dsl.*
import org.kodein.di.generic.instance

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
	private val s: ShosetsuSettings by instance()

	override val settings: List<SettingsItemData> by settingsList {
		spinnerSettingData(0) {
			title { R.string.marking_mode }
			onSpinnerItemSelected { _, _, position, _ ->
				when (position) {
					0 -> setReaderMarkingType(MarkingTypes.ONVIEW)
					1 -> setReaderMarkingType(MarkingTypes.ONSCROLL)
					else -> Log.e("MarkingMode", "UnknownType")
				}
			}
			spinnerSelection = s.readerMarkingType.i
			arrayAdapter = ArrayAdapter(
					context!!,
					android.R.layout.simple_spinner_dropdown_item,
					resources!!.getStringArray(R.array.marking_names)
			)
		}
		numberPickerSettingData(1) {
			title { R.string.columns_of_novel_listing_p }
			description { (R.string.columns_zero_automatic) }
			numberValue { s::columnsInNovelsViewP }
			range { 0 to 10 }
		}
		numberPickerSettingData(2) {
			title { R.string.columns_of_novel_listing_h }
			description { (R.string.columns_zero_automatic) }
			numberValue { s::columnsInNovelsViewH }
			range { 0 to 10 }
		}
		spinnerSettingData(3) {
			title { R.string.novel_card_type_selector_title }
			description { R.string.novel_card_type_selector_desc }
			spinnerField { s::novelCardType }
			arrayAdapter = ArrayAdapter(
					context!!,
					android.R.layout.simple_spinner_dropdown_item,
					resources!!.getStringArray(R.array.novel_card_types)
			)
		}
	}

	private fun setReaderMarkingType(markingType: MarkingTypes) {
		s.readerMarkingType = markingType
	}
}