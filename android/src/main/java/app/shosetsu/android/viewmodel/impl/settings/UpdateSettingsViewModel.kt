package app.shosetsu.android.viewmodel.impl.settings

import androidx.work.WorkInfo
import app.shosetsu.android.backend.workers.onetime.NovelUpdateWorker
import app.shosetsu.android.backend.workers.onetime.RepositoryUpdateWorker
import app.shosetsu.android.backend.workers.perodic.NovelUpdateCycleWorker
import app.shosetsu.android.common.SettingKey.*
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.domain.usecases.get.GetCategoriesUseCase
import app.shosetsu.android.view.uimodels.model.CategoryUI
import app.shosetsu.android.viewmodel.abstracted.settings.AUpdateSettingsViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest

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
	private val getCategoriesUseCase: GetCategoriesUseCase,
) : AUpdateSettingsViewModel(iSettingsRepository) {
	private fun restartNovelUpdater() {
		launchIO {
			logI("Restarting novel updaters")
			// If the update manager was enqueued, kill it.
			if (novelUpdateManager.getCount() != 0 && novelUpdateManager.getWorkerState() == WorkInfo.State.ENQUEUED)
				novelUpdateManager.stop()

			novelUpdateCycleManager.stop()
			novelUpdateCycleManager.start()
		}
	}

	private fun restartRepoUpdater() {
		logI("Restarting repo updater")

		repoUpdateManager.stop()
		repoUpdateManager.start()
	}

	override val categories: Flow<List<CategoryUI>> by lazy {
		getCategoriesUseCase().mapLatest {
			listOf(CategoryUI.default()) + it
		}
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