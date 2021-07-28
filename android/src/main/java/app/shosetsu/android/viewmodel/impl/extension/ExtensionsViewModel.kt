package app.shosetsu.android.viewmodel.impl.extension

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

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.*
import app.shosetsu.android.domain.usecases.load.LoadExtensionsUIUseCase
import app.shosetsu.android.view.uimodels.model.ExtensionUI
import app.shosetsu.android.viewmodel.abstracted.ABrowseViewModel
import app.shosetsu.common.dto.HResult
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionsViewModel(
	private val getExtensionsUI: LoadExtensionsUIUseCase,
	private val startRepositoryUpdateManager: StartRepositoryUpdateManagerUseCase,
	private val installExtensionUI: InstallExtensionUIUseCase,
	private val uninstallExtensionUI: UninstallExtensionUIUseCase,
	private val cancelExtensionInstall: CancelExtensionInstallUseCase,
	private var isOnlineUseCase: IsOnlineUseCase,
	private val reportException: ReportExceptionUseCase
) : ABrowseViewModel() {

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportException(error)
	}

	override fun refreshRepository() {
		startRepositoryUpdateManager()
	}

	override fun installExtension(extensionUI: ExtensionUI) {
		launchIO {
			installExtensionUI(extensionUI)
		}
	}

	override fun uninstallExtension(extensionUI: ExtensionUI) {
		launchIO {
			uninstallExtensionUI(extensionUI)
		}
	}

	override fun cancelInstall(extensionUI: ExtensionUI) {
		launchIO {
			cancelExtensionInstall(extensionUI)
		}
	}

	@ExperimentalCoroutinesApi
	override val liveData: LiveData<HResult<List<ExtensionUI>>> by lazy {
		getExtensionsUI().asIOLiveData()
	}

	override fun isOnline(): Boolean = isOnlineUseCase()

}