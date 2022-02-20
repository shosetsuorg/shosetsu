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

import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.domain.usecases.UninstallExtensionUseCase
import app.shosetsu.android.domain.usecases.get.GetExtListingNamesUseCase
import app.shosetsu.android.domain.usecases.get.GetExtSelectedListingFlowUseCase
import app.shosetsu.android.domain.usecases.get.GetExtensionSettingsUseCase
import app.shosetsu.android.domain.usecases.get.GetExtensionUIUseCase
import app.shosetsu.android.domain.usecases.update.UpdateExtSelectedListing
import app.shosetsu.android.domain.usecases.update.UpdateExtensionSettingUseCase
import app.shosetsu.android.viewmodel.abstracted.AExtensionConfigureViewModel
import app.shosetsu.common.domain.model.local.FilterEntity
import app.shosetsu.common.domain.model.local.InstalledExtensionEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.transformLatest

/**
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExtensionConfigureViewModel(
	private val loadExtensionUI: GetExtensionUIUseCase,
	private val uninstallExtensionUI: UninstallExtensionUseCase,
	private val getExtensionSettings: GetExtensionSettingsUseCase,
	private val getExtListNames: GetExtListingNamesUseCase,
	private val updateExtSelectedListing: UpdateExtSelectedListing,
	private val getExtSelectedListingFlow: GetExtSelectedListingFlowUseCase,
	private val updateSetting: UpdateExtensionSettingUseCase,
) : AExtensionConfigureViewModel() {
	private val extensionIdFlow: MutableStateFlow<Int> by lazy { MutableStateFlow(-1) }

	override val liveData: Flow<InstalledExtensionEntity?> by lazy {
		extensionIdFlow.transformLatest { id ->
			emitAll(loadExtensionUI(id))
		}
	}

	private val extListNamesFlow: Flow<ListingSelectionData> by lazy {
		extensionIdFlow.transformLatest { extensionID ->
			val listingNames: List<String> = getExtListNames(extensionID)

			emitAll(
				getExtSelectedListingFlow(extensionID).transformLatest { hResult ->
					emit(ListingSelectionData(listingNames, hResult))
				}
			)
		}
	}

	private val extensionSettingsFlow: Flow<List<FilterEntity>> by lazy {
		extensionIdFlow.transformLatest { extensionID ->
			emitAll(getExtensionSettings(extensionID))
		}
	}

	override val extensionSettings: Flow<List<FilterEntity>> by lazy {
		extensionSettingsFlow
	}

	override val extensionListing: Flow<ListingSelectionData> by lazy {
		extListNamesFlow
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

	override fun uninstall(extension: InstalledExtensionEntity) {
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

