package app.shosetsu.android.viewmodel.impl.settings

import androidx.work.WorkInfo
import app.shosetsu.android.backend.workers.onetime.NovelUpdateWorker
import app.shosetsu.android.backend.workers.onetime.RepositoryUpdateWorker
import app.shosetsu.android.backend.workers.perodic.NovelUpdateCycleWorker
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.viewmodel.abstracted.settings.AUpdateSettingsViewModel
import app.shosetsu.common.consts.settings.SettingKey.*
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
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
	private fun restartNovelUpdater() {
		logI("Restarting novel updaters")
		// If the update manager was enqueued, kill it.
		if (novelUpdateManager.count != 0 && novelUpdateManager.getWorkerState() == WorkInfo.State.ENQUEUED)
			novelUpdateManager.stop()

		novelUpdateCycleManager.stop()
		novelUpdateCycleManager.start()
	}

	private fun restartRepoUpdater() {
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