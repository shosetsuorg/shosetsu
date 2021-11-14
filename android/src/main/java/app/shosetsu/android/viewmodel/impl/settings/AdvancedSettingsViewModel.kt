package app.shosetsu.android.viewmodel.impl.settings

import android.app.Application
import androidx.lifecycle.LiveData
import app.shosetsu.android.backend.workers.perodic.AppUpdateCheckCycleWorker
import app.shosetsu.android.backend.workers.perodic.BackupCycleWorker
import app.shosetsu.android.backend.workers.perodic.NovelUpdateCycleWorker
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.PurgeNovelCacheUseCase
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.viewmodel.abstracted.settings.AAdvancedSettingsViewModel
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.successResult
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
	private val context: Application,
	private val reportExceptionUseCase: ReportExceptionUseCase,
	private val purgeNovelCacheUseCase: PurgeNovelCacheUseCase,
	private val backupCycleManager: BackupCycleWorker.Manager,
	private val appUpdateCycleManager: AppUpdateCheckCycleWorker.Manager,
	private val novelUpdateCycleManager: NovelUpdateCycleWorker.Manager
) : AAdvancedSettingsViewModel(iSettingsRepository) {
	override fun purgeUselessData(): LiveData<HResult<*>> =
		flow {
			emit(purgeNovelCacheUseCase())
		}.asIOLiveData()

	override suspend fun settings(): List<SettingsItemData> = listOf(

	)

	override fun killCycleWorkers(): HResult<*> {
		backupCycleManager.stop()
		appUpdateCycleManager.stop()
		novelUpdateCycleManager.stop()
		return successResult()
	}

	override fun startCycleWorkers(): HResult<*> {
		backupCycleManager.start()
		appUpdateCycleManager.start()
		novelUpdateCycleManager.start()
		return successResult()
	}

	override fun reportError(error: HResult.Error, isSilent: Boolean) =
		reportExceptionUseCase(error, isSilent)
}