package app.shosetsu.android.viewmodel.impl

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.CanAppSelfUpdateUseCase
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.ShareUseCase
import app.shosetsu.android.domain.usecases.load.LoadAppUpdateFlowLiveUseCase
import app.shosetsu.android.domain.usecases.load.LoadAppUpdateUseCase
import app.shosetsu.android.domain.usecases.load.LoadLiveAppThemeUseCase
import app.shosetsu.android.domain.usecases.settings.LoadNavigationStyleUseCase
import app.shosetsu.android.domain.usecases.settings.LoadRequireDoubleBackUseCase
import app.shosetsu.android.domain.usecases.start.StartAppUpdateInstallWorkerUseCase
import app.shosetsu.android.domain.usecases.start.StartDownloadWorkerUseCase
import app.shosetsu.android.viewmodel.abstracted.AMainViewModel
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.domain.model.local.AppUpdateEntity
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.handle
import app.shosetsu.common.enums.AppThemes
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
	private val loadAppUpdateFlowLiveUseCase: LoadAppUpdateFlowLiveUseCase,
	private val isOnlineUseCase: IsOnlineUseCase,
	private val shareUseCase: ShareUseCase,
	private val loadNavigationStyleUseCase: LoadNavigationStyleUseCase,
	private val loadRequireDoubleBackUseCase: LoadRequireDoubleBackUseCase,
	private val reportExceptionUseCase: ReportExceptionUseCase,
	private var loadLiveAppThemeUseCase: LoadLiveAppThemeUseCase,
	private val startInstallWorker: StartAppUpdateInstallWorkerUseCase,
	private val canAppSelfUpdateUseCase: CanAppSelfUpdateUseCase,
	private val loadAppUpdateUseCase: LoadAppUpdateUseCase
) : AMainViewModel() {
	private var _navigationStyle = 0
	private var _requireDoubleBackToExit = SettingKey.RequireDoubleBackToExit.default

	override val requireDoubleBackToExit: Boolean
		get() = _requireDoubleBackToExit

	init {
		launchIO {
			loadNavigationStyleUseCase().collect {
				_navigationStyle = it
			}
		}
		launchIO {
			loadRequireDoubleBackUseCase().collect {
				_requireDoubleBackToExit = it
			}
		}
	}

	override fun share(string: String, int: String) {
		shareUseCase(string, string)
	}

	override fun startDownloadWorker() {
		launchIO {
			startDownloadWorkerUseCase()
		}
	}

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}

	override fun startUpdateCheck(): LiveData<HResult<AppUpdateEntity>> =
		loadAppUpdateFlowLiveUseCase().asIOLiveData()

	override val navigationStyle: Int
		get() = _navigationStyle

	override fun isOnline(): Boolean = isOnlineUseCase()

	@ExperimentalCoroutinesApi
	override val appThemeLiveData: LiveData<AppThemes>
		get() = loadLiveAppThemeUseCase().asIOLiveData()

	override fun handleAppUpdate() {
		canAppSelfUpdateUseCase().handle(
			onError = {
				reportExceptionUseCase(it)
			}
		) { canSelfUpdate ->
			if (canSelfUpdate) {
				startInstallWorker()
			} else {
				launchIO {
					loadAppUpdateUseCase().handle {
						shareUseCase(it.url, it.version)
					}
				}
			}
		}
	}
}