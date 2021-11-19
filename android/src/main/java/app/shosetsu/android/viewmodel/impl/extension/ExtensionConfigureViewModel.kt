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
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.UninstallExtensionUIUseCase
import app.shosetsu.android.domain.usecases.get.*
import app.shosetsu.android.domain.usecases.update.UpdateExtSelectedListing
import app.shosetsu.android.domain.usecases.update.UpdateExtensionSettingUseCase
import app.shosetsu.android.view.uimodels.model.ExtensionUI
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.viewmodel.abstracted.AExtensionConfigureViewModel
import app.shosetsu.common.domain.model.local.FilterEntity
import app.shosetsu.common.dto.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

/**
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionConfigureViewModel(
	private val loadExtensionUI: GetExtensionUIUseCase,
	private val uninstallExtensionUI: UninstallExtensionUIUseCase,
	private val getExtensionSettings: GetExtensionSettingsUseCase,
	private val reportException: ReportExceptionUseCase,
	private val getExtListNames: GetExtListingNamesUseCase,
	private val updateExtSelectedListing: UpdateExtSelectedListing,
	private val getExtSelectedListingFlow: GetExtSelectedListingFlowUseCase,
	private val updateSetting: UpdateExtensionSettingUseCase,
) : AExtensionConfigureViewModel() {
	private val extensionIdFlow: MutableStateFlow<Int> by lazy { MutableStateFlow(-1) }

	@ExperimentalCoroutinesApi
	override val liveData: LiveData<HResult<ExtensionUI>> by lazy {
		extensionIdFlow.transformLatest { id ->
			emitAll(loadExtensionUI(id))
		}.asIOLiveData()
	}

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportException(error)
	}

	@ExperimentalCoroutinesApi
	private val extListNamesFlow: Flow<HResult<ListingSelectionData>> by lazy {
		extensionIdFlow.transformLatest { extensionID ->
			emit(loading)
			val listingNames: List<String> = getExtListNames(extensionID).unwrap() ?: listOf()

			emitAll(
				getExtSelectedListingFlow(extensionID).transformLatest { hResult ->
					emit(loading)
					emit(hResult.transformToSuccess {
						ListingSelectionData(listingNames, it)
					})
				}
			)
		}
	}

	@ExperimentalCoroutinesApi
	private val extensionSettingsFlow: Flow<HResult<List<FilterEntity>>> by lazy {
		extensionIdFlow.transformLatest { extensionID ->
			emitAll(getExtensionSettings(extensionID))
		}
	}

	@ExperimentalCoroutinesApi
	override val extensionSettings: LiveData<HResult<List<FilterEntity>>> by lazy {
		extensionSettingsFlow.asIOLiveData()
	}

	@ExperimentalCoroutinesApi
	override val extensionListing: LiveData<HResult<ListingSelectionData>> by lazy {
		extListNamesFlow.asIOLiveData()
	}

	override fun setExtensionID(id: Int) {
		logV("Setting extension id = $id")
		launchIO {
			when {
				extensionIdFlow.value == id -> {
					this@ExtensionConfigureViewModel.logI("id is the same, ignoring")
					return@launchIO
				}
				extensionIdFlow.value != id -> {
					this@ExtensionConfigureViewModel.logI("id is different, resetting")
					destroy()
				}
				extensionIdFlow.value == -1 -> {
					this@ExtensionConfigureViewModel.logI("id is new, setting")
				}
			}
			extensionIdFlow.value = id
		}
	}

	override fun uninstall(extension: ExtensionUI) {
		launchIO {
			uninstallExtensionUI(extension)
		}
	}

	override fun destroy() {
		extensionIdFlow.value = -1
	}

	override fun saveSetting(id: Int, value: String) {
		launchIO {
			updateSetting(extensionIdFlow.value, id, value)
		}
	}

	override fun saveSetting(id: Int, value: Boolean) {
		launchIO {
			updateSetting(extensionIdFlow.value, id, value)
		}
	}

	override fun saveSetting(id: Int, value: Int) {
		launchIO {
			updateSetting(extensionIdFlow.value, id, value)
		}
	}

	override fun setSelectedListing(value: Int) {
		launchIO {
			updateExtSelectedListing(extensionIdFlow.value, value)
		}
	}
}

