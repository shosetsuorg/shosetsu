package app.shosetsu.android.ui.settings

import android.view.View
import app.shosetsu.android.common.ext.setOnClickListener
import app.shosetsu.android.common.ext.shosetsuPush
import app.shosetsu.android.ui.settings.sub.*
import app.shosetsu.android.ui.settings.sub.backup.BackupSettings
import app.shosetsu.android.view.controller.GenericFastAdapterRecyclerController
import app.shosetsu.android.view.uimodels.model.SettingsCategoryUI
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.enums.SettingCategory.*
import com.github.doomsdayrs.apps.shosetsu.R
import com.mikepenz.fastadapter.FastAdapter

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
class SettingsController : GenericFastAdapterRecyclerController<SettingsCategoryUI>() {
	override val viewTitleRes: Int = R.string.settings

	override var recyclerArray: ArrayList<SettingsCategoryUI>
		get() = arrayListOf(
			SettingsCategoryUI(VIEW, R.string.view, R.drawable.view_module),
			SettingsCategoryUI(READER, R.string.reader, R.drawable.book),
			SettingsCategoryUI(DOWNLOAD, R.string.download, R.drawable.download),
			SettingsCategoryUI(UPDATE, R.string.update, R.drawable.update),
			SettingsCategoryUI(BACKUP, R.string.backup, R.drawable.app_update),
			SettingsCategoryUI(ADVANCED, R.string.advanced, R.drawable.settings),
		)
		set(_) {}

	override fun onViewCreated(view: View) {
	}

	override fun setupRecyclerView() {
		recyclerView.setHasFixedSize(true)
		super.setupRecyclerView()
	}

	override fun FastAdapter<SettingsCategoryUI>.setupFastAdapter() {
		setOnClickListener { _, _, item, _ ->
			router.shosetsuPush(
				when (item.category) {
					VIEW -> ViewSettings()
					ADVANCED -> AdvancedSettings()
					DOWNLOAD -> DownloadSettings()
					BACKUP -> BackupSettings()
					READER -> ReaderSettings()
					UPDATE -> UpdateSettings()
				}
			)
			true
		}
		updateUI(recyclerArray)
	}

	override fun handleErrorResult(e: HResult.Error) {
		TODO("Not yet implemented")
	}
}