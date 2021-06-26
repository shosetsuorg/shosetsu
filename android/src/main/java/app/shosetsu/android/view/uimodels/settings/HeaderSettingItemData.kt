package app.shosetsu.android.view.uimodels.settings

import androidx.core.view.isVisible
import app.shosetsu.android.view.uimodels.settings.base.BottomSettingsItemData
import com.github.doomsdayrs.apps.shosetsu.databinding.SettingsItemBinding

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

/**
 * 19 / 05 / 2021
 */
class HeaderSettingItemData(id: Int) : BottomSettingsItemData(id) {

	override fun bindBinding(holder: SettingsItemBinding, payloads: List<Any>) {
		super.bindBinding(holder, payloads)
		holder.divider.isVisible = true
		holder.settingsItemDesc.isVisible = false
	}

	override fun unbindBinding(holder: SettingsItemBinding) {
		super.unbindBinding(holder)
		holder.divider.isVisible = false
		holder.settingsItemDesc.isVisible = true
	}
}

