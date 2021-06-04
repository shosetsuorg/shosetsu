package app.shosetsu.android.view.uimodels.settings.dsl

import android.view.View
import android.widget.AdapterView
import app.shosetsu.android.view.uimodels.settings.SpinnerSettingData
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
inline fun spinnerSettingData(
	id: Int,
	action: SpinnerSettingData.() -> Unit,
): SettingsItemData = SpinnerSettingData(id).also(action)

@SettingsItemDSL
inline fun SettingsListBuilder.spinnerSettingData(
	id: Int,
	action: SpinnerSettingData.() -> Unit,
): Unit = this.let { list.add(SpinnerSettingData(id).also(action)) }


@SettingsItemDSL
inline fun SpinnerSettingData.onSpinnerItemSelected(
	crossinline value: (
		AdapterView<*>?,
		View?,
		@ParameterName("position") Int,
		@ParameterName("id") Long,
	) -> Unit,
) {
	spinnerOnItemSelectedListener = object : AdapterView.OnItemSelectedListener {
		override fun onNothingSelected(parent: AdapterView<*>?) {}

		override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
			value(parent, view, position, id)
		}
	}
}

@SettingsItemDSL
inline fun SpinnerSettingData.spinnerValue(
	value: SpinnerSettingData.() -> Int,
): Unit = value().let { spinnerSelection = it }

@SettingsItemDSL
inline fun SpinnerSettingData.spinnerField(
	crossinline action: SpinnerSettingData.() -> KMutableProperty0<Int>,
) {
	val property = action()
	spinnerValue { property.get() }
	onSpinnerItemSelected { _, _, position, _ -> property.set(position) }
}

