package app.shosetsu.android.viewmodel.model

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.model.remote.DebugAppUpdate
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.ShareUseCase
import app.shosetsu.android.domain.usecases.StartDownloadWorkerUseCase
import app.shosetsu.android.domain.usecases.load.LoadAppUpdateLiveUseCase
import app.shosetsu.android.domain.usecases.settings.LoadNavigationStyleUseCase
import app.shosetsu.android.viewmodel.abstracted.IMainViewModel
import kotlinx.coroutines.flow.collect

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
		private val loadNavigationStyleUseCase: LoadNavigationStyleUseCase,
		private val reportExceptionUseCase: ReportExceptionUseCase
) : IMainViewModel() {
	private var navigationStyle = 0

	init {
		launchIO {
			loadNavigationStyleUseCase().collect {
				navigationStyle = it
			}
		}
	}

	override fun share(string: String, int: String) {
		shareUseCase(string, string)
	}

	override fun startDownloadWorker() {
		startDownloadWorkerUseCase()
	}

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}

	override fun startUpdateWorker() {}

	override fun startUpdateCheck(): LiveData<HResult<DebugAppUpdate>> =
			loadAppUpdateUseCase().asIOLiveData()

	override fun navigationStyle(): Int = navigationStyle

	override fun isOnline(): Boolean = isOnlineUseCase()
}