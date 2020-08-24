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
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.GetExtensionsUIUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.UpdateExtensionEntityUseCase
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.ExtensionConfigUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IExtensionsConfigureViewModel

/**
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionsConfigureViewModel(
		private val getExtensionsUIUseCase: GetExtensionsUIUseCase,
		private val updateExtensionEntityUseCase: UpdateExtensionEntityUseCase
) : IExtensionsConfigureViewModel() {
	override val liveData: LiveData<HResult<List<ExtensionConfigUI>>> by lazy {
		liveData<HResult<List<ExtensionConfigUI>>> {
			emitSource(
					getExtensionsUIUseCase().map { hR ->
						when (hR) {
							is HResult.Success -> successResult(hR.data.map {
								ExtensionConfigUI(
										it.id,
										it.repoID,
										it.name,
										it.fileName,
										it.imageURL ?: "",
										it.lang,
										it.enabled,
										it.installed,
										it.installedVersion,
										it.repositoryVersion,
										it.md5
								)
							})
							is HResult.Empty -> hR
							is HResult.Loading -> hR
							is HResult.Error -> hR
						}
					}
			)
		}
	}

	override suspend fun updateExtensionConfig(
			extensionConfigUI: ExtensionConfigUI,
			enabled: Boolean
	) =
			updateExtensionEntityUseCase(extensionConfigUI.convertTo())
}

