package app.shosetsu.android.viewmodel.impl

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.enums.NavigationStyle
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.domain.usecases.CanAppSelfUpdateUseCase
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.load.LoadAppUpdateFlowLiveUseCase
import app.shosetsu.android.domain.usecases.load.LoadAppUpdateUseCase
import app.shosetsu.android.domain.usecases.load.LoadBackupProgressFlowUseCase
import app.shosetsu.android.domain.usecases.load.LoadLiveAppThemeUseCase
import app.shosetsu.android.domain.usecases.settings.LoadNavigationStyleUseCase
import app.shosetsu.android.domain.usecases.settings.LoadRequireDoubleBackUseCase
import app.shosetsu.android.domain.usecases.start.StartAppUpdateInstallWorkerUseCase
import app.shosetsu.android.viewmodel.abstracted.AMainViewModel
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.domain.model.local.AppUpdateEntity
import app.shosetsu.common.domain.repositories.base.IBackupRepository
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.enums.AppThemes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
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
 * 20 / 06 / 2020
 */
class MainViewModel(
	private val loadAppUpdateFlowLiveUseCase: LoadAppUpdateFlowLiveUseCase,
	private val isOnlineUseCase: IsOnlineUseCase,
	private val loadNavigationStyleUseCase: LoadNavigationStyleUseCase,
	private val loadRequireDoubleBackUseCase: LoadRequireDoubleBackUseCase,
	private var loadLiveAppThemeUseCase: LoadLiveAppThemeUseCase,
	private val startInstallWorker: StartAppUpdateInstallWorkerUseCase,
	private val canAppSelfUpdateUseCase: CanAppSelfUpdateUseCase,
	private val loadAppUpdateUseCase: LoadAppUpdateUseCase,
	private val loadBackupProgress: LoadBackupProgressFlowUseCase,
	private val settingsRepository: ISettingsRepository
) : AMainViewModel() {
	private var _navigationStyle = NavigationStyle.BOTTOM_NAV
	private var _requireDoubleBackToExit = SettingKey.RequireDoubleBackToExit.default

	override val requireDoubleBackToExit: Boolean
		get() = _requireDoubleBackToExit

	init {
		launchIO {
			loadNavigationStyleUseCase().collect {
				_navigationStyle = NavigationStyle.values()[it]
			}
		}
		launchIO {
			loadRequireDoubleBackUseCase().collect {
				_requireDoubleBackToExit = it
			}
		}
	}

	override fun startAppUpdateCheck(): LiveData<AppUpdateEntity> =
		loadAppUpdateFlowLiveUseCase().asIOLiveData()

	override val navigationStyle: NavigationStyle
		get() = _navigationStyle

	override fun isOnline(): Boolean = isOnlineUseCase()

	@ExperimentalCoroutinesApi
	override val appThemeLiveData: LiveData<AppThemes>
		get() = loadLiveAppThemeUseCase().asIOLiveData()

	override fun handleAppUpdate(): LiveData<AppUpdateAction> =
		flow {
			emit(
				canAppSelfUpdateUseCase().let { canSelfUpdate ->
					if (canSelfUpdate) {
						startInstallWorker()
						AppUpdateAction.SelfUpdate
					} else {
						loadAppUpdateUseCase().let {
							AppUpdateAction.UserUpdate(
								it.version,
								it.url
							)
						}
					}
				}
			)
		}.asIOLiveData()

	override val backupProgressState: LiveData<IBackupRepository.BackupProgress> by lazy {
		loadBackupProgress().asIOLiveData()
	}

	private var showIntro = SettingKey.FirstTime.default

	init {
		launchIO {
			settingsRepository.getBooleanFlow(SettingKey.FirstTime).collectLatest {
				logV("Collected $it")
				showIntro = it
			}
		}
	}

	override suspend fun showIntro(): Boolean =
		settingsRepository.getBoolean(SettingKey.FirstTime)

	override fun toggleShowIntro() {
		launchIO {
			settingsRepository.setBoolean(SettingKey.FirstTime, !showIntro)
		}
	}
}