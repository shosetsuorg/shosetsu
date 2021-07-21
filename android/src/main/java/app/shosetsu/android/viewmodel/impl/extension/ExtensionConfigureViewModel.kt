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

import android.app.Application
import androidx.lifecycle.LiveData
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.UninstallExtensionUIUseCase
import app.shosetsu.android.domain.usecases.get.GetExtListingNamesUseCase
import app.shosetsu.android.domain.usecases.get.GetExtSelectedListingUseCase
import app.shosetsu.android.domain.usecases.get.GetExtensionSettingsUseCase
import app.shosetsu.android.domain.usecases.get.GetExtensionUIUseCase
import app.shosetsu.android.domain.usecases.update.UpdateExtSelectedListing
import app.shosetsu.android.view.uimodels.model.ExtensionUI
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.viewmodel.abstracted.AExtensionConfigureViewModel
import app.shosetsu.common.dto.*
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

/**
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionConfigureViewModel(
	private val application: Application,
	private val loadExtensionUI: GetExtensionUIUseCase,
	private val uninstallExtensionUI: UninstallExtensionUIUseCase,
	private val getExtensionSettings: GetExtensionSettingsUseCase,
	private val reportException: ReportExceptionUseCase,
	private val getExtListNames: GetExtListingNamesUseCase,
	private val updateExtSelectedListing: UpdateExtSelectedListing,
	private val getExtSelectedListing: GetExtSelectedListingUseCase
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

	private suspend fun makeListingSetting(extensionID: Int, nameList: List<String>) =
		spinnerSettingData(0) {
			titleRes = R.string.listings
			getExtSelectedListing(extensionID).handle { selectedListing ->
				spinnerValue { selectedListing }
			}
			arrayAdapter = android.widget.ArrayAdapter(
				application.applicationContext,
				android.R.layout.simple_spinner_dropdown_item,
				nameList.toTypedArray()
			)
			var first = true
			onSpinnerItemSelected { _, _, position, _ ->
				if (first) {
					first = false
					return@onSpinnerItemSelected
				}
				launchIO {
					updateExtSelectedListing(extensionID, position)
				}
			}
		}

	@ExperimentalCoroutinesApi
	private val extListNamesFlow: Flow<HResult<List<SettingsItemData>>> by lazy {
		extensionIdFlow.transformLatest { extensionID ->
			emit(
				getExtListNames(extensionID).transformToSuccess { nameList ->
					listOf(makeListingSetting(extensionID, nameList))
				}
			)
		}
	}

	@ExperimentalCoroutinesApi
	private val extensionSettingsFlow: Flow<HResult<List<SettingsItemData>>> by lazy {
		extensionIdFlow.transformLatest { extensionID ->
			emitAll(getExtensionSettings(extensionID))
		}
	}

	@ExperimentalCoroutinesApi
	override val extensionSettings: LiveData<HResult<List<SettingsItemData>>> by lazy {
		extListNamesFlow.combine(extensionSettingsFlow) { a, b ->
			logD("Listing result: $a")
			logD("Settings result: $b")
			val listings = a.unwrap() ?: listOf()
			b.transform(
				onEmpty = {
					successResult(listings)
				},
			) {
				successResult(listings + it)
			}
		}.asIOLiveData()
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

	override fun uninstall(extensionUI: ExtensionUI) {
		launchIO {
			uninstallExtensionUI(extensionUI)
		}
	}

	override fun destroy() {
		extensionIdFlow.value = -1
	}
}

