package app.shosetsu.android.viewmodel.model.settings

import android.content.Context
import app.shosetsu.android.common.consts.settings.SettingKey.*
import app.shosetsu.android.common.dto.handle
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.viewmodel.abstracted.settings.ADownloadSettingsViewModel
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
class DownloadSettingsViewModel(
		private val context: Context,
		iSettingsRepository: ISettingsRepository
) : ADownloadSettingsViewModel(iSettingsRepository) {
	override suspend fun settings(): List<SettingsItemData> = listOf(
			textSettingData(1) {
				title { R.string.download_directory }
				iSettingsRepository.getString(CustomExportDirectory).handle {
					text {
						it
					}
				}
				//onClicked { performFileSearch() }
			},
			switchSettingData(3) {
				title { com.github.doomsdayrs.apps.shosetsu.R.string.download_chapter_updates }
				iSettingsRepository.getBoolean(IsDownloadOnUpdate).handle {
					isChecked = it
				}
				onChecked { _, isChecked ->
					launchIO {
						iSettingsRepository.setBoolean(IsDownloadOnUpdate, isChecked)
					}
				}
			},
			switchSettingData(2) {
				title { "Allow downloading on metered connection" }
				iSettingsRepository.getBoolean(DownloadOnMeteredConnection).handle {
					isChecked = it
				}
				onChecked { _, isChecked ->
					launchIO {
						iSettingsRepository.setBoolean(DownloadOnMeteredConnection, isChecked)
					}
				}
			},
			switchSettingData(3) {
				title { "Download on low battery" }
				iSettingsRepository.getBoolean(DownloadOnLowBattery).handle {
					isChecked = it
				}
				onChecked { _, isChecked ->
					launchIO {
						iSettingsRepository.setBoolean(DownloadOnLowBattery, isChecked)
					}
				}
			},
			switchSettingData(4) {
				title { "Download on low storage" }
				iSettingsRepository.getBoolean(DownloadOnLowStorage).handle {
					isChecked = it
				}
				onChecked { _, isChecked ->
					launchIO {
						iSettingsRepository.setBoolean(DownloadOnLowStorage, isChecked)
					}
				}
			},
			switchSettingData(5) {
				title { "Download only when idle" }
				requiredVersion { android.os.Build.VERSION_CODES.M }
				iSettingsRepository.getBoolean(DownloadOnlyWhenIdle).handle {
					isChecked = it
				}
				onChecked { _, isChecked ->
					launchIO {
						iSettingsRepository.setBoolean(DownloadOnlyWhenIdle, isChecked)
					}
				}
			}
	)
}