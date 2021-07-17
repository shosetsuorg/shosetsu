package app.shosetsu.android.viewmodel.impl.settings

import androidx.work.WorkInfo
import app.shosetsu.android.backend.workers.onetime.NovelUpdateWorker
import app.shosetsu.android.backend.workers.onetime.RepositoryUpdateWorker
import app.shosetsu.android.backend.workers.perodic.NovelUpdateCycleWorker
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.viewmodel.abstracted.settings.AUpdateSettingsViewModel
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.consts.settings.SettingKey.*
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.domain.repositories.base.getIntOrDefault
import app.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.coroutines.flow.*

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
	private val novelUpdateCycleManager: NovelUpdateCycleWorker.Manager,
	private val novelUpdateManager: NovelUpdateWorker.Manager,
	private val repoUpdateManager: RepositoryUpdateWorker.Manager
) : AUpdateSettingsViewModel(iSettingsRepository) {

	override suspend fun settings(): List<SettingsItemData> = listOf(

		headerSettingItemData(0) {
			titleRes = R.string.settings_update_header_novel
		},

		// Update frequency
		seekBarSettingData(1) {
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
		switchSettingData(2) {
			title { "Download on update" }
			checkSettingValue(IsDownloadOnUpdate)
		},
		// Update only ongoing
		switchSettingData(3) {
			title { "Only update ongoing" }
			checkSettingValue(SettingKey.OnlyUpdateOngoing)
		},
		switchSettingData(4) {
			title { "Allow updating on metered connection" }
			checkSettingValue(SettingKey.UpdateOnMeteredConnection)
		},
		switchSettingData(5) {
			title { "Update on low battery" }
			checkSettingValue(SettingKey.UpdateOnLowBattery)
		},
		switchSettingData(6) {
			title { "Update on low storage" }
			checkSettingValue(SettingKey.UpdateOnLowStorage)
		},
		switchSettingData(7) {
			title { "Update only when idle" }
			requiredVersion { android.os.Build.VERSION_CODES.M }
			checkSettingValue(SettingKey.UpdateOnlyWhenIdle)
		},
		switchSettingData(8) {
			title { "Notification Style" }
			checkSettingValue(SettingKey.UpdateNotificationStyle)
		},
		switchSettingData(14) {
			title { "Show novel update progress" }
			checkSettingValue(SettingKey.NovelUpdateShowProgress)
		},
		switchSettingData(14) {
			title { "Classic novel update completion" }
			description { "Instead of showing you which how many chapters are in each novel, simply says \"Completed Update\"" }
			checkSettingValue(SettingKey.NovelUpdateClassicFinish)
		},
		headerSettingItemData(9) {
			titleRes = R.string.settings_update_header_repositories
		},
		switchSettingData(10) {
			title { "Allow updating on metered connection" }
			checkSettingValue(RepoUpdateOnMeteredConnection)
		},
		switchSettingData(11) {
			title { "Update on low battery" }
			checkSettingValue(RepoUpdateOnLowBattery)
		},
		switchSettingData(12) {
			title { "Update on low storage" }
			checkSettingValue(RepoUpdateOnLowStorage)
		},
		switchSettingData(13) {
			title { "Update only when idle" }
			requiredVersion { android.os.Build.VERSION_CODES.M }
			checkSettingValue(RepoUpdateOnlyWhenIdle)
		},
		// next 16
	)

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
	}

	override var novelUpdateSettingsChanged = false

	override var repoUpdateSettingsChanged = false

	override fun restartNovelUpdater() {
		// If the update manager was enqueued, kill it.
		if (novelUpdateManager.count != 0 && novelUpdateManager.getWorkerState() == WorkInfo.State.ENQUEUED)
			novelUpdateManager.stop()

		novelUpdateCycleManager.stop()
		novelUpdateCycleManager.start()
	}

	override fun restartRepoUpdater() {
		repoUpdateManager.stop()
		repoUpdateManager.start()
	}

	init {
		launchIO {
			var firstRun = true
			settingsRepo.getBooleanFlow(RepoUpdateOnMeteredConnection)
				.combine(settingsRepo.getBooleanFlow(RepoUpdateOnLowBattery)) { _, _ -> }
				.combine(settingsRepo.getBooleanFlow(RepoUpdateOnLowStorage)) { _, _ -> }
				.combine(settingsRepo.getBooleanFlow(UpdateOnlyWhenIdle)) { _, _ -> }
				.collect {
					if (firstRun) {
						firstRun = false
						return@collect
					}
					if (
						(novelUpdateCycleManager.count != 0 && novelUpdateCycleManager.getWorkerState() == WorkInfo.State.ENQUEUED) ||
						(novelUpdateManager.count != 0 && novelUpdateManager.getWorkerState() == WorkInfo.State.ENQUEUED)
					) {
						repoUpdateSettingsChanged = true
					}
				}
		}

		launchIO {
			var firstRun = true
			settingsRepo.getIntFlow(UpdateCycle)
				.combine(settingsRepo.getBooleanFlow(OnlyUpdateOngoing)) { _, _ -> }
				.combine(settingsRepo.getBooleanFlow(UpdateOnMeteredConnection)) { _, _ -> }
				.combine(settingsRepo.getBooleanFlow(UpdateOnLowStorage)) { _, _ -> }
				.combine(settingsRepo.getBooleanFlow(RepoUpdateOnlyWhenIdle)) { _, _ -> }
				.collect {
					if (firstRun) {
						firstRun = false
						return@collect
					}
					if (
						(repoUpdateManager.count != 0 && repoUpdateManager.getWorkerState() == WorkInfo.State.ENQUEUED)
					) {
						novelUpdateSettingsChanged = true
					}
				}
		}
	}
}