package app.shosetsu.android.view.uimodels.settings.dsl

import app.shosetsu.android.view.uimodels.settings.DoubleNumberSettingData

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

@SettingsItemDSL
inline fun floatButtonSettingData(
	id: Int,
	action: DoubleNumberSettingData.() -> Unit,
): DoubleNumberSettingData = DoubleNumberSettingData(id).also(action)

@SettingsItemDSL
inline fun DoubleNumberSettingData.onValueSelected(
	crossinline action: (
		@ParameterName("newValue") Double,
	) -> Unit,
) {
	onValueSelected = { action(it) }
}