package com.github.doomsdayrs.apps.shosetsu.ui.settings

import android.view.View
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.SettingsCard
import com.github.doomsdayrs.apps.shosetsu.ui.settings.SettingsController.Types.*
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
	override val layoutRes: Int = R.layout.settings
	override val viewTitleRes: Int = R.string.settings

	init {
		recyclerArray.clear()
		recyclerArray.addAll(arrayListOf(
				SettingsCard(DOWNLOAD),
				SettingsCard(UPDATE),
				SettingsCard(VIEW),
				SettingsCard(READER),
				SettingsCard(ADVANCED),
				SettingsCard(INFO),
				SettingsCard(BACKUP)
		))
	}

	override fun onViewCreated(view: View) {
	}

	override fun difAreItemsTheSame(oldItem: SettingsCard, newItem: SettingsCard): Boolean =
			oldItem.id == newItem.id

	override fun setupRecyclerView() {
		super.setupRecyclerView()
		recyclerView?.setHasFixedSize(true)
	}

	override fun createRecyclerAdapter(): SettingsAdapter =
			SettingsAdapter(recyclerArray, router)

	enum class Types {
		DOWNLOAD, // Settings for download options
		UPDATE,
		VIEW, // Settings for application appearance
		ADVANCED, // Settings that control more advanced application features
		INFO,  // Information of the app
		BACKUP, // Settings for backup and restoring data
		READER // Settings for reading novels in application
	}
}