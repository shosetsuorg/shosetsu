package app.shosetsu.android.viewmodel.impl

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.delete.DeleteDownloadUseCase
import app.shosetsu.android.domain.usecases.load.LoadDownloadsUseCase
import app.shosetsu.android.domain.usecases.start.StartDownloadWorkerUseCase
import app.shosetsu.android.domain.usecases.update.UpdateDownloadUseCase
import app.shosetsu.android.view.uimodels.model.DownloadUI
import app.shosetsu.android.viewmodel.abstracted.ADownloadsViewModel
import app.shosetsu.common.consts.settings.SettingKey.IsDownloadPaused
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.dto.*
import app.shosetsu.common.enums.DownloadStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emitAll
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
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class DownloadsViewModel(
	private val getDownloadsUseCase: LoadDownloadsUseCase,
	private val startDownloadWorkerUseCase: StartDownloadWorkerUseCase,
	private val updateDownloadUseCase: UpdateDownloadUseCase,
	private val deleteDownloadUseCase: DeleteDownloadUseCase,
	private val settings: ISettingsRepository,
	private var isOnlineUseCase: IsOnlineUseCase,
	private val reportExceptionUseCase: ReportExceptionUseCase
) : ADownloadsViewModel() {

	private fun List<DownloadUI>.sort() = sortedWith(compareBy<DownloadUI> {
		it.status == DownloadStatus.ERROR
	}.thenBy {
		it.status == DownloadStatus.PAUSED
	}.thenBy {
		it.status == DownloadStatus.PENDING
	}.thenBy {
		it.status == DownloadStatus.WAITING
	}.thenBy {
		it.status == DownloadStatus.DOWNLOADING
	})

	@ExperimentalCoroutinesApi
	override val liveData: LiveData<HResult<List<DownloadUI>>> by lazy {
		flow {
			emit(loading())
			emitAll(getDownloadsUseCase().mapLatestResult { list ->
				successResult(list.sort())
			})
		}.asIOLiveData()
	}

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}

	override fun isOnline(): Boolean = isOnlineUseCase()

	override val isDownloadPaused: LiveData<Boolean> by lazy {
		settings.getBooleanFlow(IsDownloadPaused).asIOLiveData()
	}

	override fun togglePause() {
		launchIO {
			settings.getBoolean(IsDownloadPaused).handle { isPaused ->
				settings.setBoolean(IsDownloadPaused, !isPaused)
				if (isPaused) startDownloadWorkerUseCase()
			}
		}
	}

	override fun delete(downloadUI: DownloadUI) {
		launchIO { deleteDownloadUseCase(downloadUI) }
	}

	override fun pause(downloadUI: DownloadUI) {
		launchIO { updateDownloadUseCase(downloadUI.copy(status = DownloadStatus.PAUSED)) }
	}

	override fun start(downloadUI: DownloadUI) {
		launchIO { updateDownloadUseCase(downloadUI.copy(status = DownloadStatus.PENDING)) }
	}

	override fun pauseAll(list: List<DownloadUI>) {
		launchIO {
			list.forEach { updateDownloadUseCase(it.copy(status = DownloadStatus.PAUSED)) }
		}
	}

	override fun startAll(list: List<DownloadUI>) {
		launchIO {
			list.forEach { updateDownloadUseCase(it.copy(status = DownloadStatus.PENDING)) }
		}
	}

	override fun deleteAll(list: List<DownloadUI>) {
		launchIO {
			list.forEach { deleteDownloadUseCase(it) }
		}
	}
}