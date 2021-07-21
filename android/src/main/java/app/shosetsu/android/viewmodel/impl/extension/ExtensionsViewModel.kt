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
import app.shosetsu.android.domain.usecases.InstallExtensionUIUseCase
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.StartRepositoryUpdateManagerUseCase
import app.shosetsu.android.domain.usecases.UninstallExtensionUIUseCase
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
	private val getExtensionsUIUseCase: LoadExtensionsUIUseCase,
	private val startRepositoryUpdateManagerUseCase: StartRepositoryUpdateManagerUseCase,
	private val installExtensionUIUseCase: InstallExtensionUIUseCase,
	private val uninstallExtensionUIUseCase: UninstallExtensionUIUseCase,
	private var isOnlineUseCase: IsOnlineUseCase,
	private val reportExceptionUseCase: ReportExceptionUseCase
) : ABrowseViewModel() {

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}

	override fun refreshRepository() {
		startRepositoryUpdateManagerUseCase()
	}

	override fun installExtension(extensionUI: ExtensionUI) {
		launchIO {
			installExtensionUIUseCase(extensionUI)
		}
	}

	override fun uninstallExtension(extensionUI: ExtensionUI) {
		launchIO {
			uninstallExtensionUIUseCase(extensionUI)
		}
	}

	@ExperimentalCoroutinesApi
	override val liveData: LiveData<HResult<List<ExtensionUI>>> by lazy {
		getExtensionsUIUseCase().asIOLiveData()
	}

	override fun isOnline(): Boolean = isOnlineUseCase()
}