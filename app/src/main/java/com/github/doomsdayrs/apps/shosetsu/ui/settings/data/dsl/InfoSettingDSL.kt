package com.github.doomsdayrs.apps.shosetsu.ui.settings.data.dsl

import com.github.doomsdayrs.apps.shosetsu.ui.settings.data.InfoSettingData
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
 * 25 / 06 / 2020
 */

@SettingsItemDSL
inline fun infoSettingData(
		id: Int,
		action: InfoSettingData.() -> Unit
): SettingsItemData = InfoSettingData(id).also(action)

@SettingsItemDSL
inline fun InfoSettingData.onClick(crossinline action: InfoSettingData.() -> Unit) {
	itemViewOnClick = { action() }
}