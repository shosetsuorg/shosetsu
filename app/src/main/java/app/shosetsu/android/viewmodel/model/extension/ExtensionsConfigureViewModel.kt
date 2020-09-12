package app.shosetsu.android.viewmodel.model.extension

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
import androidx.lifecycle.viewModelScope
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.domain.usecases.load.LoadExtensionsUIUseCase
import app.shosetsu.android.domain.usecases.update.UpdateExtensionEntityUseCase
import app.shosetsu.android.view.uimodels.model.ExtensionConfigUI
import app.shosetsu.android.viewmodel.abstracted.IExtensionsConfigureViewModel
import kotlinx.coroutines.Dispatchers

/**
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionsConfigureViewModel(
		private val getExtensionsUIUseCase: LoadExtensionsUIUseCase,
		private val updateExtensionEntityUseCase: UpdateExtensionEntityUseCase,
) : IExtensionsConfigureViewModel() {
	override val liveData: LiveData<HResult<List<ExtensionConfigUI>>> by lazy {
		liveData {
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
										it.isExtEnabled,
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

	override fun updateExtensionConfig(
			extensionConfigUI: ExtensionConfigUI,
			enabled: Boolean,
	): LiveData<HResult<*>> = liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
		emit(updateExtensionEntityUseCase(extensionConfigUI.convertTo()))
	}
}

