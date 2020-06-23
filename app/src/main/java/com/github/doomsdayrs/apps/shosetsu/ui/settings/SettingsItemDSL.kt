package com.github.doomsdayrs.apps.shosetsu.ui.settings

import android.view.View
import android.widget.AdapterView
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData.SettingsType

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
 * 20 / 06 / 2020
 */
@DslMarker
annotation class SettingsItemDSL

@SettingsItemDSL
inline fun settingsItemData(
		id: Int,
		type: SettingsType,
		action: SettingsItemData.() -> Unit
): SettingsItemData = SettingsItemData(type, id).also(action)

@SettingsItemDSL
inline fun SettingsItemData.title(value: SettingsItemData.() -> Any): Unit =
		value().let {
			when (it) {
				is String -> titleText = it
				is Int -> titleID = it
				else -> throw IllegalArgumentException("Input must be either an int or string")
			}
		}

@SettingsItemDSL
inline fun SettingsItemData.description(value: SettingsItemData.() -> Any): Unit =
		value().let {
			when (it) {
				is String -> descText = it
				is Int -> descID = it
				else -> throw IllegalArgumentException("Input must be either an int or string")
			}
		}

@SettingsItemDSL
inline fun SettingsItemData.onSpinnerItemSelected(crossinline value: (
		AdapterView<*>?,
		View?,
		@ParameterName("position") Int,
		@ParameterName("id") Long
) -> Unit) {
	spinnerOnItemSelectedListener = object : AdapterView.OnItemSelectedListener {
		override fun onNothingSelected(parent: AdapterView<*>?) {}

		override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
			value(parent, view, position, id)
		}
	}
}

@SettingsItemDSL
inline fun SettingsItemData.requiredVersion(value: SettingsItemData.() -> Int) {
	minVersionCode = value()
}