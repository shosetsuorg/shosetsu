package com.github.doomsdayrs.apps.shosetsu.ui.settings

import android.view.View
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.SettingsCard
import com.github.doomsdayrs.apps.shosetsu.common.ext.setActivityTitle
import com.github.doomsdayrs.apps.shosetsu.ui.settings.adapter.SettingsAdapter
import com.github.doomsdayrs.apps.shosetsu.view.base.RecyclerController

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
 * Shosetsu
 * 9 / June / 2019
 */
class SettingsController : RecyclerController<SettingsAdapter, SettingsCard>() {
	enum class Types { DOWNLOAD, VIEW, ADVANCED, INFO, BACKUP, READER }

	init {
		recyclerArray.clear()
		recyclerArray.addAll(arrayListOf(
				SettingsCard(Types.DOWNLOAD),
				SettingsCard(Types.VIEW),
				SettingsCard(Types.READER),
				SettingsCard(Types.ADVANCED),
				SettingsCard(Types.INFO),
				SettingsCard(Types.BACKUP)
		))
	}

	override val layoutRes: Int = R.layout.settings

	override fun onViewCreated(view: View) {
		activity?.setActivityTitle(R.string.settings)
		recyclerView?.setHasFixedSize(true)
		adapter = SettingsAdapter(recyclerArray, router)
	}

	override fun difAreItemsTheSame(oldItem: SettingsCard, newItem: SettingsCard): Boolean =
			oldItem.id == newItem.id
}