package com.github.doomsdayrs.apps.shosetsu.ui.settings.data.base

import android.os.Build
import androidx.annotation.CallSuper
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem

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
 * Data for [SettingsItem]
 */
abstract class SettingsItemData(val id: Int) {
	/** Min version required for this setting to be visible */
	var minVersionCode: Int = Build.VERSION_CODES.Q

	var titleID: Int = -1
	var titleText: String = ""

	var descID: Int = -1
	var descText: String = ""

	@CallSuper
	open fun setupView(settingsItem: SettingsItem) {
		with(settingsItem) {
			if (titleID != -1)
				itemTitle.setText(titleID)
			else
				itemTitle.text = titleText

			if (descID != -1)
				itemDescription.setText(descID)
			else
				itemDescription.text = descText
		}
	}

}
