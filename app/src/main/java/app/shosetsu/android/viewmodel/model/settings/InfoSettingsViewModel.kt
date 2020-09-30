package app.shosetsu.android.viewmodel.model.settings

import android.util.Log
import app.shosetsu.android.backend.workers.onetime.AppUpdateWorker
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.viewmodel.abstracted.settings.AInfoSettingsViewModel
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import com.github.doomsdayrs.apps.shosetsu.R

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
 * 31 / 08 / 2020
 */
class InfoSettingsViewModel(
		private val manager: AppUpdateWorker.Manager,
		iSettingsRepository: ISettingsRepository
) : AInfoSettingsViewModel(iSettingsRepository) {
	fun checkForAppUpdate() {
		if (!manager.isRunning())
			manager.start()
	}

	override suspend fun settings(): List<SettingsItemData> = listOf(
			infoSettingData(0) {
				title { R.string.version }
				description { BuildConfig.VERSION_NAME }
			},
			infoSettingData(1) {
				title { (R.string.report_bug) }
				description { R.string.report_bug_link }
			},
			infoSettingData(2) {
				title { com.github.doomsdayrs.apps.shosetsu.R.string.author }
				description { com.github.doomsdayrs.apps.shosetsu.R.string.author_name }
			},
			infoSettingData(3) {
				title { com.github.doomsdayrs.apps.shosetsu.R.string.disclaimer }
			},
			infoSettingData(4) {
				title { com.github.doomsdayrs.apps.shosetsu.R.string.license }
			},
			buttonSettingData(5) {
				title { "Check for update" }
				onButtonClicked { checkForAppUpdate() }
			}
	)

}