package com.github.doomsdayrs.apps.shosetsu.ui.settings.data.dsl

import com.github.doomsdayrs.apps.shosetsu.ui.settings.data.*
import com.github.doomsdayrs.apps.shosetsu.ui.settings.data.base.SettingsItemData

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

// - Builders

@SettingsItemDSL
inline fun buttonSettingData(
		id: Int,
		action: ButtonSettingData.() -> Unit
): SettingsItemData = ButtonSettingData(id).also(action)

@SettingsItemDSL
inline fun checkBoxSettingData(
		id: Int,
		action: CheckBoxSettingData.() -> Unit
): SettingsItemData = CheckBoxSettingData(id).also(action)

@SettingsItemDSL
inline fun seekBarSettingData(
		id: Int,
		action: SeekBarSettingData.() -> Unit
): SettingsItemData = SeekBarSettingData(id).also(action)

@SettingsItemDSL
inline fun switchSettingData(
		id: Int,
		action: SwitchSettingData.() -> Unit
): SettingsItemData = SwitchSettingData(id).also(action)

@SettingsItemDSL
inline fun textSettingData(
		id: Int,
		action: TextSettingData.() -> Unit
): SettingsItemData = TextSettingData(id).also(action)
// - Functions

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
inline fun SettingsItemData.requiredVersion(value: SettingsItemData.() -> Int) {
	minVersionCode = value()
}