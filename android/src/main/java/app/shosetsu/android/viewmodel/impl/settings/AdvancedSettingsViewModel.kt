package app.shosetsu.android.viewmodel.impl.settings

import androidx.work.await
import app.shosetsu.android.backend.workers.onetime.RepositoryUpdateWorker
import app.shosetsu.android.backend.workers.perodic.AppUpdateCheckCycleWorker
import app.shosetsu.android.backend.workers.perodic.BackupCycleWorker
import app.shosetsu.android.backend.workers.perodic.NovelUpdateCycleWorker
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.domain.usecases.PurgeNovelCacheUseCase
import app.shosetsu.android.viewmodel.abstracted.settings.AAdvancedSettingsViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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
class AdvancedSettingsViewModel(
	iSettingsRepository: ISettingsRepository,
	private val purgeNovelCacheUseCase: PurgeNovelCacheUseCase,
	private val backupCycleManager: BackupCycleWorker.Manager,
	private val appUpdateCycleManager: AppUpdateCheckCycleWorker.Manager,
	private val novelUpdateCycleManager: NovelUpdateCycleWorker.Manager,
	private val repoManager: RepositoryUpdateWorker.Manager,
) : AAdvancedSettingsViewModel(iSettingsRepository) {
	override fun purgeUselessData(): Flow<Unit> =
		flow {
			emit(purgeNovelCacheUseCase())
		}.onIO()

	override fun killCycleWorkers() {
		backupCycleManager.stop()
		appUpdateCycleManager.stop()
		novelUpdateCycleManager.stop()
	}

	override fun startCycleWorkers() {
		backupCycleManager.start()
		appUpdateCycleManager.start()
		novelUpdateCycleManager.start()
	}

	override fun forceRepoSync() {
		launchIO {
			try {
				repoManager.stop().await()
			} finally {
				repoManager.start(force = true)
			}
		}
	}
}