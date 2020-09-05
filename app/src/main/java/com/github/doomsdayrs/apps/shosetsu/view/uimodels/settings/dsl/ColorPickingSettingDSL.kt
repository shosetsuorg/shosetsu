package com.github.doomsdayrs.apps.shosetsu.view.uimodels.settings.dsl

import com.github.doomsdayrs.apps.shosetsu.view.uimodels.settings.ColorPickerSettingData
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.settings.base.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.settings.base.SettingsListBuilder
import kotlin.reflect.KMutableProperty0

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
 * 25 / 06 / 2020
 */
@SettingsItemDSL
inline fun colorPickerSettingData(
		id: Int,
		action: ColorPickerSettingData.() -> Unit,
): SettingsItemData = ColorPickerSettingData(id).also(action)

@SettingsItemDSL
inline fun SettingsListBuilder.colorPickerSettingData(
		id: Int,
		action: ColorPickerSettingData.() -> Unit,
): Unit = this.let { list.add(ColorPickerSettingData(id).also(action)) }

@SettingsItemDSL
inline fun ColorPickerSettingData.onColorPicked(
		crossinline action: ColorPickerSettingData.(
				@ParameterName("color") Int,
		) -> Unit,
) {
	colorFunction = {
		action(it)
	}
}

@SettingsItemDSL
inline fun ColorPickerSettingData.colorName(
		crossinline value: ColorPickerSettingData.() -> String,
) = value().let { colorPreferenceName = it }

@SettingsItemDSL
inline fun ColorPickerSettingData.itemColor(
		crossinline value: ColorPickerSettingData.() -> Int,
) = value().let { itemColor = it }

@SettingsItemDSL
inline fun ColorPickerSettingData.colorValue(
		crossinline action: ColorPickerSettingData.() -> KMutableProperty0<Int>,
) {
	val property = action()
	itemColor { property.get() }
	onColorPicked { property.set(it) }
}