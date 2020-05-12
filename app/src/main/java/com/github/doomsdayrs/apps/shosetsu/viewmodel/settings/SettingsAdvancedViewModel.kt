package com.github.doomsdayrs.apps.shosetsu.viewmodel.settings

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.database.SQLException
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ext.toast
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ISettingsAdvancedViewModel

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
class SettingsAdvancedViewModel(
		val activity: Activity,
		val context: Context,
		val resources: Resources = context.resources
) : ISettingsAdvancedViewModel() {
	override val settings: ArrayList<SettingsItem.SettingsItemData> by lazy {
		arrayListOf(
				SettingsItem.SettingsItemData(SettingsItem.SettingsItemData.SettingsType.SPINNER, 1)
						.setTitle(R.string.theme)
						.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
							override fun onNothingSelected(parent: AdapterView<*>?) {
							}

							override fun onItemSelected(
									parent: AdapterView<*>?,
									view: View?,
									position: Int,
									id: Long
							) {
								if (position in 0..1) {
									val delegate =
											(activity as AppCompatActivity).delegate
									when (position) {
										0 -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
										1 -> delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
									}
									val theme = delegate.localNightMode
									parent?.setSelection(if (
											theme == AppCompatDelegate.MODE_NIGHT_YES ||
											theme == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM ||
											theme == AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
									) 1 else 0)
								}
							}
						})
						.setArrayAdapter(ArrayAdapter(
								context,
								android.R.layout.simple_spinner_item,
								resources.getStringArray(R.array.application_themes)
						)),
				SettingsItem.SettingsItemData(SettingsItem.SettingsItemData.SettingsType.BUTTON, 2)
						.setTitle(R.string.remove_novel_cache)
						.setOnClickListenerButton {
							try {
								// TODO purge
							} catch (e: SQLException) {
								context.toast("SQLITE Error")
								Log.e("AdvancedSettings", "DatabaseError", e)
							}
						}
		)
	}
}