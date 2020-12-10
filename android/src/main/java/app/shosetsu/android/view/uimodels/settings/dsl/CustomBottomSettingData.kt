package app.shosetsu.android.view.uimodels.settings.dsl

import android.view.View
import android.view.ViewGroup
import app.shosetsu.android.view.uimodels.settings.CustomBottomSettingData
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.base.SettingsListBuilder

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
 * 19 / 08 / 2020
 */

@SettingsItemDSL
inline fun customBottomSettingData(
		id: Int,
		action: CustomBottomSettingData.() -> Unit,
): SettingsItemData = CustomBottomSettingData(id).also(action)

@SettingsItemDSL
inline fun SettingsListBuilder.customBottomSettingData(
		id: Int,
		action: CustomBottomSettingData.() -> Unit,
): Unit = this.let { list.add(CustomBottomSettingData(id).also(action)) }


@SettingsItemDSL
inline fun CustomBottomSettingData.customView(
		crossinline view: (@ParameterName("root") ViewGroup) -> View,
): CustomBottomSettingData = this.apply {
	customView = { view(it) }
}