package com.github.doomsdayrs.apps.shosetsu.ui.settings

import android.view.View
import androidx.annotation.StringRes
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.view.base.RecyclerController
import com.github.doomsdayrs.apps.shosetsu.ui.settings.adapter.SettingItemsAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.settings.viewHolder.SettingsItem
import java.util.*

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
 * ====================================================================
 */

/**
 * shosetsu
 * 29 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
abstract class SettingsSubController : RecyclerController<SettingItemsAdapter, SettingsItem.SettingsItemData>() {

	override val layoutRes: Int = R.layout.settings
	abstract val settings: ArrayList<SettingsItem.SettingsItemData>


	override fun onViewCreated(view: View) {
		adapter = SettingItemsAdapter(settings)
	}

	fun findDataByID(@StringRes id: Int): Int {
		for ((index, data) in settings.withIndex()) {
			if (data.titleID == id)
				return index
		}
		return -1
	}
}
