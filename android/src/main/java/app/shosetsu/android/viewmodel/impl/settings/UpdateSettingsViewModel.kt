package app.shosetsu.android.viewmodel.impl.settings

import androidx.work.WorkInfo
import app.shosetsu.android.backend.workers.onetime.NovelUpdateWorker
import app.shosetsu.android.backend.workers.onetime.RepositoryUpdateWorker
import app.shosetsu.android.backend.workers.perodic.NovelUpdateCycleWorker
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.viewmodel.abstracted.settings.AUpdateSettingsViewModel
import app.shosetsu.common.consts.settings.SettingKey.*
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine

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
	private val repoUpdateManager: RepositoryUpdateWorker.Manager,
) : AUpdateSettingsViewModel(iSettingsRepository) {

	override suspend fun settings(): List<SettingsItemData> = listOf(

		headerSettingItemData(0) {
			titleRes = R.string.settings_update_header_novel
		},

		// Update frequency
		seekBarSettingData(1) {
			titleRes = R.string.settings_update_novel_frequency_title
			descRes = R.string.settings_update_novel_frequency_desc
			range { 0F to 6F }
			progressValue = when (settingsRepo.getInt(NovelUpdateCycle)) {
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
							NovelUpdateCycle, when (progress) {
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
			titleRes = R.string.settings_update_novel_on_update_title
			descRes = R.string.settings_update_novel_on_update_desc

			checkSettingValue(DownloadNewNovelChapters)
		},
		// Update only ongoing
		switchSettingData(3) {
			titleRes = R.string.settings_update_novel_only_ongoing_title
			descRes = R.string.settings_update_novel_only_ongoing_desc

			checkSettingValue(OnlyUpdateOngoingNovels)
		},
		switchSettingData(4) {
			titleRes = R.string.settings_update_novel_on_metered_title
			descRes = R.string.settings_update_novel_on_metered_desc

			checkSettingValue(NovelUpdateOnMeteredConnection)
		},
		switchSettingData(5) {
			titleRes = R.string.settings_update_novel_on_low_bat_title
			descRes = R.string.settings_update_novel_on_low_bat_desc

			checkSettingValue(NovelUpdateOnLowBattery)
		},
		switchSettingData(6) {
			titleRes = R.string.settings_update_novel_on_low_sto_title
			descRes = R.string.settings_update_novel_on_low_sto_desc

			checkSettingValue(NovelUpdateOnLowStorage)
		},
		switchSettingData(7) {
			titleRes = R.string.settings_update_novel_only_idle_title
			descRes = R.string.settings_update_novel_only_idle_desc

			requiredVersion { android.os.Build.VERSION_CODES.M }
			checkSettingValue(NovelUpdateOnlyWhenIdle)
		},
		switchSettingData(8) {
			titleRes = R.string.settings_update_novel_notification_style_title
			descRes = R.string.settings_update_novel_notification_style_desc

			checkSettingValue(UpdateNotificationStyle)
		},
		switchSettingData(14) {
			titleRes = R.string.settings_update_novel_show_progress_title
			descRes = R.string.settings_update_novel_show_progress_desc

			checkSettingValue(NovelUpdateShowProgress)
		},
		switchSettingData(15) {
			titleRes = R.string.settings_update_novel_classic_notification_title
			descRes = R.string.settings_update_novel_classic_notification_desc

			checkSettingValue(NovelUpdateClassicFinish)
		},
		headerSettingItemData(9) {
			titleRes = R.string.settings_update_header_repositories
		},
		switchSettingData(10) {
			titleRes = R.string.settings_update_repo_on_metered_title
			descRes = R.string.settings_update_repo_on_metered_desc

			checkSettingValue(RepoUpdateOnMeteredConnection)
		},
		switchSettingData(11) {
			titleRes = R.string.settings_update_repo_on_low_bat_title
			descRes = R.string.settings_update_repo_on_low_bat_desc

			checkSettingValue(RepoUpdateOnLowBattery)
		},
		switchSettingData(12) {
			titleRes = R.string.settings_update_repo_on_low_sto_title
			descRes = R.string.settings_update_repo_on_low_sto_desc

			checkSettingValue(RepoUpdateOnLowStorage)
		},
		switchSettingData(16) {
			titleRes = R.string.settings_update_repo_disable_on_fail_title
			descRes = R.string.settings_update_repo_disable_on_fail_desc
			checkSettingValue(RepoUpdateDisableOnFail)
		}
		// next 16
	)

	fun restartNovelUpdater() {
		logI("Restarting novel updaters")
		// If the update manager was enqueued, kill it.
		if (novelUpdateManager.count != 0 && novelUpdateManager.getWorkerState() == WorkInfo.State.ENQUEUED)
			novelUpdateManager.stop()

		novelUpdateCycleManager.stop()
		novelUpdateCycleManager.start()
	}

	fun restartRepoUpdater() {
		logI("Restarting repo updater")

		repoUpdateManager.stop()
		repoUpdateManager.start()
	}

	init {
		launchIO {
			var firstRun = true
			settingsRepo.getIntFlow(NovelUpdateCycle)
				.combine(settingsRepo.getBooleanFlow(NovelUpdateOnLowStorage)) { _, _ -> }
				.combine(settingsRepo.getBooleanFlow(NovelUpdateOnLowBattery)) { _, _ -> }
				.combine(settingsRepo.getBooleanFlow(NovelUpdateOnMeteredConnection)) { _, _ -> }
				.combine(settingsRepo.getBooleanFlow(NovelUpdateOnlyWhenIdle)) { _, _ -> }
				.collect {
					if (firstRun) {
						firstRun = false
						return@collect
					}
					restartNovelUpdater()
				}
		}

		launchIO {
			var firstRun = true
			settingsRepo.getBooleanFlow(RepoUpdateOnLowStorage)
				.combine(settingsRepo.getBooleanFlow(RepoUpdateOnLowBattery)) { _, _ -> }
				.combine(settingsRepo.getBooleanFlow(RepoUpdateOnMeteredConnection)) { _, _ -> }
				.collect {
					if (firstRun) {
						firstRun = false
						return@collect
					}
					restartRepoUpdater()
				}
		}
	}
}