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
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.*
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ExtensionUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IExtensionsViewModel

/**
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionsViewModel(
		private val getExtensionsUIUseCase: GetExtensionsUIUseCase,
		private val refreshRepositoryUseCase: RefreshRepositoryUseCase,
		private val reloadFormattersUseCase: ReloadFormattersUseCase,
		private val installExtensionUIUseCase: InstallExtensionUIUseCase,
		private val uninstallExtensionUIUseCase: UninstallExtensionUIUseCase
) : IExtensionsViewModel() {

	override fun reloadFormatters() =
			reloadFormattersUseCase()

	override fun refreshRepository() =
			refreshRepositoryUseCase()

	override fun installExtension(extensionUI: ExtensionUI) =
			installExtensionUIUseCase(extensionUI)

	override fun uninstallExtension(extensionUI: ExtensionUI) =
			uninstallExtensionUIUseCase(extensionUI)

	override val liveData: LiveData<HResult<List<ExtensionUI>>> by lazy { getExtensionsUIUseCase() }
}