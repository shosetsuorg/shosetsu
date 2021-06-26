package app.shosetsu.android.view.uimodels.settings.dsl

import app.shosetsu.android.view.uimodels.settings.HeaderSettingItemData

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
 * Shosetsu
 *
 * @since 19 / 05 / 2021
 * @author Doomsdayrs
 */
@SettingsItemDSL
inline fun headerSettingItemData(
	id: Int,
	action: HeaderSettingItemData.() -> Unit,
): HeaderSettingItemData = HeaderSettingItemData(id).also(action).apply {
	boldTitle()
}