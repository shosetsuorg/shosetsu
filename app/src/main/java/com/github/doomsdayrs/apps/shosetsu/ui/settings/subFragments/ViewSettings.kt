package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.ui.settings.SettingsSubController
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData.SettingsType
import com.github.doomsdayrs.apps.shosetsu.variables.ext.context

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
 * ====================================================================
 */
/**
 * Shosetsu
 * 13 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */ // TODO: Migrate to using PreferenceScreen and PreferenceGroup. Maybe
class ViewSettings : SettingsSubController() {

	override val settings by lazy {
		arrayListOf(
				SettingsItemData(SettingsType.SPINNER)
						.setTitle(R.string.marking_mode)
						.setOnItemSelectedListener(object : OnItemSelectedListener {
							override fun onNothingSelected(p0: AdapterView<*>?) {}
							override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
								Log.d("MarkingMode", p2.toString())
								when (p2) {
									0 -> Utilities.setReaderMarkingType(Settings.MarkingTypes.ONVIEW)
									1 -> Utilities.setReaderMarkingType(Settings.MarkingTypes.ONSCROLL)
									else -> Log.e("MarkingMode", "UnknownType")
								}
							}
						}),
				SettingsItemData(SettingsType.NUMBER_PICKER)
						.setTitle(R.string.columns_of_novel_listing_p)
						.setDescription((R.string.columns_zero_automatic))
						.setNumberValue(Settings.columnsInNovelsViewP.let { if (it == -1) 0 else it })
						.setNumberLowerBound(0)
						.setNumberUpperBound(10)
						.setNumberPickerOnValueChangedListener { _, _, newVal ->
							when (newVal) {
								0 -> Settings.columnsInNovelsViewP = -1
								else -> Settings.columnsInNovelsViewP = newVal
							}
						},
				SettingsItemData(SettingsType.NUMBER_PICKER)
						.setTitle(R.string.columns_of_novel_listing_h)
						.setDescription(R.string.columns_zero_automatic)
						.setNumberValue(Settings.columnsInNovelsViewH.let { if (it == -1) 0 else it })
						.setNumberLowerBound(0)
						.setNumberUpperBound(10)
						.setNumberPickerOnValueChangedListener { _, _, newVal ->
							when (newVal) {
								0 -> Settings.columnsInNovelsViewH = -1
								else -> Settings.columnsInNovelsViewH = newVal
							}
						},
				SettingsItemData(SettingsType.SPINNER)
						.setTitle((R.string.novel_card_type_selector_title))
						.setDescription((R.string.novel_card_type_selector_desc))
						.setSpinnerSelection(Settings.novelCardType)
						.setOnItemSelectedListener(object : OnItemSelectedListener {
							override fun onNothingSelected(parent: AdapterView<*>?) {
							}

							override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
								Settings.novelCardType = position
							}

						})
		)
	}

	override fun onViewCreated(view: View) {
		super.onViewCreated(view)
		run {
			val x = findDataByID(R.string.marking_mode)
			settings[x].adapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, resources!!.getStringArray(R.array.marking_names))
			settings[x].spinnerSelection = Settings.readerMarkingType
		}

		run {
			val x = findDataByID(R.string.novel_card_type_selector_title)
			settings[x].adapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_item, resources!!.getStringArray(R.array.novel_card_types))
		}
		Log.i("onViewCreated", "Finished creation")
		recyclerView?.layoutManager = LinearLayoutManager(context)
		recyclerView?.adapter = adapter
	}
}