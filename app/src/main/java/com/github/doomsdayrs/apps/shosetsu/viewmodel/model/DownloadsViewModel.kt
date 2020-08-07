package com.github.doomsdayrs.apps.shosetsu.viewmodel.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.loading
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchIO
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.DeleteDownloadUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.GetDownloadsUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.StartDownloadWorkerUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.UpdateDownloadUseCase
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.DownloadUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IDownloadsViewModel
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
		private val getDownloadsUseCase: GetDownloadsUseCase,
		private val startDownloadWorkerUseCase: StartDownloadWorkerUseCase,
		private val updateDownloadUseCase: UpdateDownloadUseCase,
		private val deleteDownloadUseCase: DeleteDownloadUseCase,
		private val settings: ShosetsuSettings
) : IDownloadsViewModel() {
	override val liveData: LiveData<HResult<List<DownloadUI>>> by lazy {
		liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
			emit(loading())
			emitSource(getDownloadsUseCase())
		}
	}

	override fun togglePause(): Boolean {
		settings.isDownloadPaused = !settings.isDownloadPaused
		if (settings.isDownloadPaused)
			startDownloadWorkerUseCase()
		return settings.isDownloadPaused
	}

	override fun delete(downloadUI: DownloadUI) {
		launchIO { deleteDownloadUseCase(downloadUI) }
	}

	override fun pause(downloadUI: DownloadUI) {
		launchIO { updateDownloadUseCase(downloadUI.copy(status = 2)) }
	}

	override fun start(downloadUI: DownloadUI) {
		launchIO { updateDownloadUseCase(downloadUI.copy(status = 0)) }
	}
}