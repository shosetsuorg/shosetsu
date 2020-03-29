package com.github.doomsdayrs.apps.shosetsu.ui.settings

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.controllers.ViewedController
import com.github.doomsdayrs.apps.shosetsu.ui.settings.adapter.SettingsAdapter
import com.github.doomsdayrs.apps.shosetsu.variables.SettingsCard
import com.github.doomsdayrs.apps.shosetsu.variables.ext.getString

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
 * ====================================================================
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class SettingsController : ViewedController() {
	enum class Types { DOWNLOAD, VIEW, ADVANCED, INFO, BACKUP, READER }

	override val layoutRes: Int = R.layout.settings
	private val cards by lazy {
		arrayListOf(
				SettingsCard(Types.DOWNLOAD),
				SettingsCard(Types.VIEW),
				SettingsCard(Types.READER),
				SettingsCard(Types.ADVANCED),
				SettingsCard(Types.INFO),
				SettingsCard(Types.BACKUP)
		)
	}

	override fun onViewCreated(view: View) {
		Utilities.setActivityTitle(activity, getString(R.string.settings))
		val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
		recyclerView.setHasFixedSize(true)
		recyclerView.layoutManager = LinearLayoutManager(view.context)
		recyclerView.adapter = SettingsAdapter(cards, router)
	}
}