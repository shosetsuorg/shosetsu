package com.github.doomsdayrs.apps.shosetsu.view.uimodels.settings

import android.view.View
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.settings.base.SettingsItemData

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
class InfoSettingData(id: Int) : SettingsItemData(id) {
	var itemViewOnClick: (View) -> Unit = {}
	override fun bindView(settingsItem: ViewHolder, payloads: List<Any>) {
		super.bindView(settingsItem, payloads)
		with(settingsItem) {
			itemView.setOnClickListener(itemViewOnClick)
		}
	}

	override fun unbindView(settingsItem: ViewHolder) {
		super.unbindView(settingsItem)
		settingsItem.itemView.setOnClickListener(null)
	}

}