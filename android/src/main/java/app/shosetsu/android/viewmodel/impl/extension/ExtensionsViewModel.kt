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
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.InitializeExtensionsUseCase
import app.shosetsu.android.domain.usecases.InstallExtensionUIUseCase
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.UninstallExtensionUIUseCase
import app.shosetsu.android.domain.usecases.load.LoadExtensionsUIUseCase
import app.shosetsu.android.domain.usecases.toast.StringToastUseCase
import app.shosetsu.android.view.uimodels.model.ExtensionUI
import app.shosetsu.android.viewmodel.abstracted.IExtensionsViewModel
import app.shosetsu.common.dto.HResult
import kotlinx.coroutines.*

/**
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionsViewModel(
	private val getExtensionsUIUseCase: LoadExtensionsUIUseCase,
	private val initializeExtensionsUseCase: InitializeExtensionsUseCase,
	private val installExtensionUIUseCase: InstallExtensionUIUseCase,
	private val uninstallExtensionUIUseCase: UninstallExtensionUIUseCase,
	private val stringToastUseCase: StringToastUseCase,
	private var isOnlineUseCase: IsOnlineUseCase,
	private val reportExceptionUseCase: ReportExceptionUseCase
) : IExtensionsViewModel() {

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}

	override fun refreshRepository() {
		app.shosetsu.android.common.ext.launchIO {
			initializeExtensionsUseCase.invoke {
				stringToastUseCase { it }
			}
		}
	}

	override fun installExtension(extensionUI: ExtensionUI) {
		GlobalScope.launch(Dispatchers.IO, start = CoroutineStart.DEFAULT) {
			when (val result = installExtensionUIUseCase(extensionUI)) {
				is HResult.Success -> {
					stringToastUseCase {
						"Installed!"
					}
				}
				is HResult.Error -> {
					result.error?.printStackTrace()
					stringToastUseCase {
						"Cannot install due to error ${result.code} by ${result.message} due to " +
								"${result.error?.let { it::class.simpleName }}"
					}
				}
			}
		}
	}

	override fun uninstallExtension(extensionUI: ExtensionUI): Unit =
		uninstallExtensionUIUseCase(extensionUI)

	@ExperimentalCoroutinesApi
	override val liveData: LiveData<HResult<List<ExtensionUI>>> by lazy {
		getExtensionsUIUseCase().asIOLiveData()
	}

	override fun isOnline(): Boolean = isOnlineUseCase()
}