package app.shosetsu.android.view.uimodels.settings

import android.view.View
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import com.github.doomsdayrs.apps.shosetsu.databinding.SettingsItemBinding

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
class CustomSettingData(id: Int) : SettingsItemData(id) {

	/**
	 * Custom view
	 */
	var customView: () -> View? = { null }

	override fun bindBinding(holder: SettingsItemBinding, payloads: List<Any>) {
		super.bindBinding(holder, payloads)
		holder.itemView.apply {
			removeAllViews()
			customView()?.let { addView(it) }
		}
	}
}