package app.shosetsu.android.ui.settings

import android.view.View
import app.shosetsu.android.common.SettingsCard
import app.shosetsu.android.ui.settings.SettingsController.Types.*
import app.shosetsu.android.ui.settings.adapter.SettingsAdapter
import app.shosetsu.android.view.base.RecyclerController.BasicRecyclerController
import com.github.doomsdayrs.apps.shosetsu.R

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
class SettingsController : BasicRecyclerController<SettingsAdapter, SettingsCard>() {
	override val viewTitleRes: Int = R.string.settings
	override var recyclerArray: ArrayList<SettingsCard>
		get() = arrayListOf(
				SettingsCard(DOWNLOAD),
				SettingsCard(UPDATE),
				SettingsCard(VIEW),
				SettingsCard(READER),
				SettingsCard(ADVANCED),
				SettingsCard(INFO),
				SettingsCard(BACKUP)
		)
		set(_) {}


	override fun onViewCreated(view: View) {}

	override fun difAreItemsTheSame(oldItem: SettingsCard, newItem: SettingsCard): Boolean =
			oldItem.id == newItem.id

	override fun setupRecyclerView() {
		super.setupRecyclerView()
		recyclerView.setHasFixedSize(true)
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