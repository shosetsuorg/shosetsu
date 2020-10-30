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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.handleReturn
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.UninstallExtensionUIUseCase
import app.shosetsu.android.domain.usecases.get.GetExtensionSettingsUseCase
import app.shosetsu.android.domain.usecases.load.LoadExtensionUIUseCase
import app.shosetsu.android.domain.usecases.update.UpdateExtensionEntityUseCase
import app.shosetsu.android.view.uimodels.model.ExtensionUI
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.viewmodel.abstracted.IExtensionConfigureViewModel
import kotlinx.coroutines.flow.mapLatest

/**
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionConfigureViewModel(
		private val loadExtensionUIUI: LoadExtensionUIUseCase,
		private val updateExtensionEntityUseCase: UpdateExtensionEntityUseCase,
		private val uninstallExtensionUIUseCase: UninstallExtensionUIUseCase,
		private val getExtensionSettings: GetExtensionSettingsUseCase,
		private val reportExceptionUseCase: ReportExceptionUseCase
) : IExtensionConfigureViewModel() {
	private val idLive by lazy {
		MutableLiveData(internalID)
	}
	private var internalID: Int = -1

	override val liveData: LiveData<HResult<ExtensionUI>> by lazy {
		idLive.switchMap {
			loadExtensionUIUI(it).asIOLiveData()
		}
	}

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}

	override val extensionSettings: LiveData<HResult<List<SettingsItemData>>> by lazy {
		idLive.switchMap {
			getExtensionSettings(it).mapLatest { r ->
				r.handleReturn {
					successResult(arrayListOf<SettingsItemData>())
				}
			}.asIOLiveData()
		}
	}

	override fun setExtensionID(id: Int) {
		launchIO {
			when {
				internalID == id -> {
					logI("ID the same, ignoring")
					return@launchIO
				}
				internalID != id -> {
					logI("ID not equal, resetting")
					destroy()
				}
				internalID == -1 -> {
					logI("ID is new, setting")
				}
			}
			internalID = id
			idLive.postValue(id)
		}
	}

	override suspend fun saveSetting(id: Int, value: Any) {
	}

	override fun uninstall(extensionUI: ExtensionUI) {
		uninstallExtensionUIUseCase(extensionUI)
	}

	override fun destroy() {
		idLive.postValue(-1)
		internalID = -1
	}
}

