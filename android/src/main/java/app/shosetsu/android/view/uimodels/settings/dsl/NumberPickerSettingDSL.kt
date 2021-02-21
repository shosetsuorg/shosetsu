package app.shosetsu.android.view.uimodels.settings.dsl

import android.widget.NumberPicker
import app.shosetsu.android.view.uimodels.settings.NumberPickerSettingData
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.base.SettingsListBuilder
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
inline fun numberPickerSettingData(
	id: Int,
	action: NumberPickerSettingData.() -> Unit,
): SettingsItemData = NumberPickerSettingData(id).also(action)

@SettingsItemDSL
inline fun SettingsListBuilder.numberPickerSettingData(
	id: Int,
	action: NumberPickerSettingData.() -> Unit,
): Unit = this.let { list.add(NumberPickerSettingData(id).also(action)) }


@SettingsItemDSL
inline fun NumberPickerSettingData.range(
	crossinline value: NumberPickerSettingData.() -> Pair<
			@ParameterName("lowerBound") Int,
			@ParameterName("upperBound") Int
			>,
): Unit = value().let { lowerBound = it.first;upperBound = it.second }

@SettingsItemDSL
inline fun NumberPickerSettingData.onValueSelected(
	crossinline action: NumberPickerSettingData.(
		@ParameterName("picker") NumberPicker?,
		@ParameterName("oldVal") Int,
		@ParameterName("newVal") Int,
	) -> Unit,
) {
	numberPickerOnValueChangedListener = { picker, oldVal, newVal ->
		action(picker, oldVal, newVal)
	}
}

@SettingsItemDSL
inline fun NumberPickerSettingData.initalValue(
	value: NumberPickerSettingData.() -> Int,
): Unit = value().let { numberPickerValue = it }

@SettingsItemDSL
inline fun NumberPickerSettingData.numberValue(
	crossinline action: NumberPickerSettingData.() -> KMutableProperty0<Int>,
) {
	val property = action()
	initalValue { property.get() }
	onValueSelected { _, _, newVal: Int -> property.set(newVal) }
}