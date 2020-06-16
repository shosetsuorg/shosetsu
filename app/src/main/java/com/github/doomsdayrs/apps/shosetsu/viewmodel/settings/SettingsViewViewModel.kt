package com.github.doomsdayrs.apps.shosetsu.viewmodel.settings

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.Settings
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData.SettingsType.NUMBER_PICKER
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData.SettingsType.SPINNER
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ISettingsViewViewModel

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
class SettingsViewViewModel(
		val context: Context,
		val resources: Resources = context.resources
) : ISettingsViewViewModel() {
	override val settings: ArrayList<SettingsItem.SettingsItemData> by lazy {
		arrayListOf(
				SettingsItem.SettingsItemData(SPINNER, 0)
						.setTitle(R.string.marking_mode)
						.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
							override fun onNothingSelected(p0: AdapterView<*>?) {}
							override fun onItemSelected(
									p0: AdapterView<*>?,
									p1: View?,
									p2: Int,
									p3: Long
							) {
								Log.d("MarkingMode", p2.toString())
								when (p2) {
									0 -> setReaderMarkingType(Settings.MarkingTypes.ONVIEW)
									1 -> setReaderMarkingType(Settings.MarkingTypes.ONSCROLL)
									else -> Log.e("MarkingMode", "UnknownType")
								}
							}
						})
						.setArrayAdapter(ArrayAdapter(
								context,
								android.R.layout.simple_spinner_item,
								resources.getStringArray(R.array.marking_names)
						))
						.setSpinnerSelection(Settings.readerMarkingType.i),
				SettingsItem.SettingsItemData(NUMBER_PICKER, 1)
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
				SettingsItem.SettingsItemData(NUMBER_PICKER, 2)
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
				SettingsItem.SettingsItemData(SPINNER, 3)
						.setTitle((R.string.novel_card_type_selector_title))
						.setDescription((R.string.novel_card_type_selector_desc))
						.setArrayAdapter(ArrayAdapter(
								context,
								android.R.layout.simple_spinner_item,
								resources.getStringArray(R.array.novel_card_types)
						))
						.setSpinnerSelection(Settings.novelCardType)
						.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
							override fun onNothingSelected(parent: AdapterView<*>?) {
							}

							override fun onItemSelected(
									parent: AdapterView<*>?,
									view: View?,
									position: Int,
									id: Long
							) {
								Settings.novelCardType = position
							}

						})
		)
	}

	fun setReaderMarkingType(markingType: Settings.MarkingTypes) {
		Settings.readerMarkingType = markingType
	}
}