package com.github.doomsdayrs.apps.shosetsu.viewmodel.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.domain.model.remote.DebugAppUpdate
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.IsOnlineUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.load.LoadAppUpdateLiveUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.ShareUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.StartDownloadWorkerUseCase
import com.github.doomsdayrs.apps.shosetsu.viewmodel.abstracted.IMainViewModel
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
 * 20 / 06 / 2020
 */
class MainViewModel(
		private val startDownloadWorkerUseCase: StartDownloadWorkerUseCase,
		private val loadAppUpdateUseCase: LoadAppUpdateLiveUseCase,
		private val isOnlineUseCase: IsOnlineUseCase,
		private val shareUseCase: ShareUseCase,
) : IMainViewModel() {
	override fun share(string: String, int: String) {
		shareUseCase(string, string)
	}

	override fun startDownloadWorker() {
		startDownloadWorkerUseCase()
	}

	override fun startUpdateWorker() {}

	override fun startUpdateCheck(): LiveData<HResult<DebugAppUpdate>> {
		return liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
			emitSource(loadAppUpdateUseCase())
		}
	}

	override fun isOnline(): Boolean = isOnlineUseCase()
}