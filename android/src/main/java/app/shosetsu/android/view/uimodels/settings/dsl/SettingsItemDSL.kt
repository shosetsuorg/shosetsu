package app.shosetsu.android.view.uimodels.settings.dsl

import app.shosetsu.android.view.uimodels.settings.CheckBoxSettingData
import app.shosetsu.android.view.uimodels.settings.SwitchSettingData
import app.shosetsu.android.view.uimodels.settings.TextSettingData
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
 * 20 / 06 / 2020
 */
@DslMarker
annotation class SettingsItemDSL

// - Builders

@SettingsItemDSL
inline fun switchSettingData(
	id: Int,
	action: SwitchSettingData.() -> Unit,
): SettingsItemData = SwitchSettingData(id).also(action)

@SettingsItemDSL
inline fun textSettingData(
	id: Int,
	action: TextSettingData.() -> Unit,
): SettingsItemData = TextSettingData(id).also(action)

// - Functions

@SettingsItemDSL
@Deprecated("Use propery accessor")
inline fun SettingsItemData.title(value: SettingsItemData.() -> Any): Unit =
	value().let {
		when (it) {
			is String -> titleText = it
			is Int -> titleRes = it
			else -> titleText = it.toString()
		}
	}

@SettingsItemDSL
fun SettingsItemData.boldTitle() {
	isBoldTitle = true
}


@SettingsItemDSL
inline fun SettingsItemData.description(value: SettingsItemData.() -> Any): Unit =
	value().let {
		when (it) {
			is String -> descText = it
			is Int -> descRes = it
			else -> titleText = it.toString()
		}
	}

@SettingsItemDSL
inline fun SettingsItemData.requiredVersion(value: SettingsItemData.() -> Int) {
	minVersionCode = value()
}


// - Settings list dsl

inline fun settingsList(crossinline listBuilder: SettingsListBuilder.() -> Unit): Lazy<ArrayList<SettingsItemData>> =
	lazy {
		SettingsListBuilder().also(listBuilder).list
	}

@SettingsItemDSL
inline fun SettingsListBuilder.checkBoxSettingData(
	id: Int,
	action: CheckBoxSettingData.() -> Unit,
): Unit = this.let { list.add(CheckBoxSettingData(id).also(action)) }


@SettingsItemDSL
inline fun SettingsListBuilder.switchSettingData(
	id: Int,
	action: SwitchSettingData.() -> Unit,
): Unit = this.let { list.add(SwitchSettingData(id).also(action)) }

@SettingsItemDSL
inline fun SettingsListBuilder.textSettingData(
	id: Int,
	action: TextSettingData.() -> Unit,
): Unit = this.let { list.add(TextSettingData(id).also(action)) }