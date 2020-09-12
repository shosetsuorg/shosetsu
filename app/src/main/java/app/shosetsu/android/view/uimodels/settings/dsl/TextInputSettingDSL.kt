package app.shosetsu.android.view.uimodels.settings.dsl

import android.text.Editable
import app.shosetsu.android.view.uimodels.settings.TextInputSettingData
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
 * 26 / 06 / 2020
 */

@SettingsItemDSL
inline fun textInputSettingData(
		id: Int,
		action: TextInputSettingData.() -> Unit,
): SettingsItemData = TextInputSettingData(id).also(action)

@SettingsItemDSL
inline fun SettingsListBuilder.textInputSettingData(
		id: Int,
		action: TextInputSettingData.() -> Unit,
): Unit = this.let { list.add(TextInputSettingData(id).also(action)) }

@SettingsItemDSL
fun TextInputSettingData.doAfterTextChanged(action: (Editable) -> Unit) {
	this.onTextChanged = action
}