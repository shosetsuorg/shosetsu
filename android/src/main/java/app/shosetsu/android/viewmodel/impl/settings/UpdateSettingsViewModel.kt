package app.shosetsu.android.viewmodel.impl.settings

import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.viewmodel.abstracted.settings.AUpdateSettingsViewModel
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.domain.repositories.base.getIntOrDefault
import app.shosetsu.common.dto.HResult

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
class UpdateSettingsViewModel(
	iSettingsRepository: ISettingsRepository,
	private val reportExceptionUseCase: ReportExceptionUseCase
) : AUpdateSettingsViewModel(iSettingsRepository) {

	override suspend fun settings(): List<SettingsItemData> = listOf(
		// Update frequency
		seekBarSettingData(6) {
			title { "Update frequency" }
			range { 0F to 6F }
			progressValue = when (settingsRepo.getIntOrDefault(SettingKey.UpdateCycle)) {
				1 -> 0F
				2 -> 1F
				4 -> 2F
				6 -> 3F
				12 -> 4F
				24 -> 5F
				168 -> 6F
				else -> 0F
			}
			array.apply {
				put(0, "1 Hour")
				put(1, "2 Hours")
				put(2, "4 Hours")
				put(3, "6 Hours")
				put(4, "12 Hours")
				put(5, "Daily")
				put(6, "Weekly")
			}
			onProgressChanged { _, progress, _, fromUser ->
				if (fromUser) {
					launchIO {
						settingsRepo.setInt(
							SettingKey.UpdateCycle, when (progress) {
								0 -> 1
								1 -> 2
								2 -> 4
								3 -> 6
								4 -> 12
								5 -> 24
								6 -> 168
								else -> 1
							}
						)
					}
				}
			}
			showSectionMark = true
			showSectionText = true

			seekBySection = true
			seekByStepSection = true
			autoAdjustSectionMark = true
			touchToSeek = true
			hideBubble = true
			sectionC = 6
		},
		// Download on update
		switchSettingData(0) {
			title { "Download on update" }
			checkSettingValue(SettingKey.IsDownloadOnUpdate)
		},
		// Update only ongoing
		switchSettingData(1) {
			title { "Only update ongoing" }
			checkSettingValue(SettingKey.OnlyUpdateOngoing)
		},
		switchSettingData(2) {
			title { "Allow updating on metered connection" }
			checkSettingValue(SettingKey.UpdateOnMeteredConnection)
		},
		switchSettingData(3) {
			title { "Update on low battery" }
			checkSettingValue(SettingKey.UpdateOnLowBattery)
		},
		switchSettingData(4) {
			title { "Update on low storage" }
			checkSettingValue(SettingKey.UpdateOnLowStorage)
		},
		switchSettingData(5) {
			title { "Update only when idle" }
			requiredVersion { android.os.Build.VERSION_CODES.M }
			checkSettingValue(SettingKey.UpdateOnlyWhenIdle)
		},
		switchSettingData(6) {
			title { "Notification Style" }
			checkSettingValue(SettingKey.UpdateNotificationStyle)
		}
	)

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}
}