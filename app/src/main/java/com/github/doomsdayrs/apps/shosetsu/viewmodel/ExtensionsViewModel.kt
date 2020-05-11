package com.github.doomsdayrs.apps.shosetsu.viewmodel

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
import com.github.doomsdayrs.apps.shosetsu.common.utils.FormatterUtils
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ExtensionUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IExtensionsViewModel

/**
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionsViewModel(
		val formatterUtils: FormatterUtils
) : IExtensionsViewModel() {

	override fun reloadFormatters() {
		TODO("Not yet implemented")
	}

	override fun refreshRepository() {
		TODO("Not yet implemented")
	}

	override fun installExtension(extensionEntity: ExtensionUI) {
		extensionEntity.installed = true
		extensionEntity.enabled = true
		TODO("installExtension")
	}

	override fun uninstallExtension(extensionEntity: ExtensionUI) {
		extensionEntity.installed = false
		extensionEntity.enabled = false
		TODO("uninstallExtension")
	}

	override val liveData: LiveData<HResult<List<ExtensionUI>>>
		get() = TODO("Not yet implemented")
}