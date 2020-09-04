package com.github.doomsdayrs.apps.shosetsu.viewmodel.model.extension

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
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.GetExtensionsUIUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.InitializeExtensionsUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.InstallExtensionUIUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.UninstallExtensionUIUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.toast.StringToastUseCase
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.ExtensionUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.abstracted.IExtensionsViewModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionsViewModel(
		private val getExtensionsUIUseCase: GetExtensionsUIUseCase,
		private val initializeExtensionsUseCase: InitializeExtensionsUseCase,
		private val installExtensionUIUseCase: InstallExtensionUIUseCase,
		private val uninstallExtensionUIUseCase: UninstallExtensionUIUseCase,
		private val stringToastUseCase: StringToastUseCase
) : IExtensionsViewModel() {


	override fun refreshRepository() {
		com.github.doomsdayrs.apps.shosetsu.common.ext.launchIO {
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

	override val liveData: LiveData<HResult<List<ExtensionUI>>> by lazy { getExtensionsUIUseCase() }
}