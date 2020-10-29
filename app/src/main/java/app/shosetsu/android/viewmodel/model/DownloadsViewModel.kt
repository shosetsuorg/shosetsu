package app.shosetsu.android.viewmodel.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import app.shosetsu.android.common.consts.settings.SettingKey.IsDownloadPaused
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.handle
import app.shosetsu.android.common.dto.loading
import app.shosetsu.android.common.enums.DownloadStatus
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.liveDataIO
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.StartDownloadWorkerUseCase
import app.shosetsu.android.domain.usecases.delete.DeleteDownloadUseCase
import app.shosetsu.android.domain.usecases.load.LoadDownloadsUseCase
import app.shosetsu.android.domain.usecases.update.UpdateDownloadUseCase
import app.shosetsu.android.view.uimodels.model.DownloadUI
import app.shosetsu.android.viewmodel.abstracted.IDownloadsViewModel
import kotlinx.coroutines.Dispatchers

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
) : IDownloadsViewModel() {
	override val liveData: LiveData<HResult<List<DownloadUI>>> by lazy {
		liveDataIO {
			emit(loading())
			emitSource(getDownloadsUseCase().asLiveData(viewModelScope.coroutineContext + Dispatchers.IO))
		}
	}

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}

	override fun isOnline(): Boolean = isOnlineUseCase()

	override val isDownloadPaused: LiveData<Boolean> by lazy {
		settings.observeBoolean(IsDownloadPaused).asLiveData(viewModelScope.coroutineContext + Dispatchers.IO)
	}

	override fun togglePause() {
		if (!isOnline()) return
		launchIO {
			settings.getBoolean(IsDownloadPaused).handle { isPaused ->
				settings.setBoolean(IsDownloadPaused, !isPaused)
				if (!isPaused) startDownloadWorkerUseCase()
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
}