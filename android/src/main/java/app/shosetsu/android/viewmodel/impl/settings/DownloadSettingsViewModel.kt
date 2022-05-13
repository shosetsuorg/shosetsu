package app.shosetsu.android.viewmodel.impl.settings

import androidx.work.WorkInfo
import app.shosetsu.android.backend.workers.onetime.DownloadWorker
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.viewmodel.abstracted.settings.ADownloadSettingsViewModel
import app.shosetsu.common.consts.settings.SettingKey.*
import app.shosetsu.android.domain.repository.base.ISettingsRepository
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
class DownloadSettingsViewModel(
	iSettingsRepository: ISettingsRepository,
	private val manager: DownloadWorker.Manager
) : ADownloadSettingsViewModel(iSettingsRepository) {


	override var downloadWorkerSettingsChanged: Boolean = false

	init {
		launchIO {
			var ran = false
			settingsRepo.getBooleanFlow(DownloadOnlyWhenIdle)
				.combine(settingsRepo.getBooleanFlow(DownloadOnLowStorage)) { _, _ -> }
				.combine(settingsRepo.getBooleanFlow(DownloadOnLowBattery)) { _, _ -> }
				.combine(settingsRepo.getBooleanFlow(DownloadOnMeteredConnection)) { _, _ -> }
				.collect {
					if (!ran) {
						ran = true
						return@collect
					}

					if (manager.getCount() != 0 && manager.getWorkerState() == WorkInfo.State.ENQUEUED)
						downloadWorkerSettingsChanged = true
				}
		}
	}

	override fun restartDownloadWorker() {
		manager.stop()
		manager.start()
	}
}