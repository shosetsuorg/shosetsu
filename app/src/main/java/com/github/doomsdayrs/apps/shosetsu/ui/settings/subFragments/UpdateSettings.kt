package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments

import android.widget.CompoundButton.OnCheckedChangeListener
import com.github.doomsdayrs.apps.shosetsu.common.Settings
import com.github.doomsdayrs.apps.shosetsu.ui.settings.SettingsSubController
import com.github.doomsdayrs.apps.shosetsu.ui.settings.settingsItemData
import com.github.doomsdayrs.apps.shosetsu.ui.settings.title
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem.SettingsItemData.SettingsType.SWITCH

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
class UpdateSettings : SettingsSubController() {
	override val settings: List<SettingsItemData> by lazy {
		listOf(
				// Update frequency
				// Download on update
				settingsItemData(0, SWITCH) {
					title { "Download on update" }
					isChecked = Settings.downloadOnUpdate
					onCheckedListener = OnCheckedChangeListener { _, isChecked ->
						Settings.downloadOnUpdate = isChecked
					}
				},
				// Update only ongoing
				settingsItemData(1, SWITCH) {
					title { "Only update ongoing" }
					isChecked = Settings.onlyUpdateOngoing
					onCheckedListener = OnCheckedChangeListener { _, isChecked ->
						Settings.onlyUpdateOngoing = isChecked
					}
				}
		)
	}

}