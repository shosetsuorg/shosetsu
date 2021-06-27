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
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.UninstallExtensionUIUseCase
import app.shosetsu.android.domain.usecases.get.GetExtListingNamessUseCase
import app.shosetsu.android.domain.usecases.get.GetExtSelectedListingUseCase
import app.shosetsu.android.domain.usecases.get.GetExtensionSettingsUseCase
import app.shosetsu.android.domain.usecases.get.GetExtensionUIUseCase
import app.shosetsu.android.domain.usecases.update.UpdateExtSelectedListing
import app.shosetsu.android.domain.usecases.update.UpdateExtensionEntityUseCase
import app.shosetsu.android.view.uimodels.model.ExtensionUI
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.viewmodel.abstracted.IExtensionConfigureViewModel
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
	private val loadExtensionUIUI: GetExtensionUIUseCase,
	private val updateExtensionEntityUseCase: UpdateExtensionEntityUseCase,
	private val uninstallExtensionUIUseCase: UninstallExtensionUIUseCase,
	private val getExtensionSettings: GetExtensionSettingsUseCase,
	private val reportExceptionUseCase: ReportExceptionUseCase,
	private val getExtListNames: GetExtListingNamessUseCase,
	private val updateExtSelectedListing: UpdateExtSelectedListing,
	private val getExtSelectedListing: GetExtSelectedListingUseCase
) : IExtensionConfigureViewModel() {
	private val extensionIDFlow: MutableStateFlow<Int> by lazy {
		MutableStateFlow(-1)
	}

	@ExperimentalCoroutinesApi
	override val liveData: LiveData<HResult<ExtensionUI>> by lazy {
		extensionIDFlow.transformLatest { id ->
			emitAll(loadExtensionUIUI(id))
		}.asIOLiveData()
	}

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}

	@ExperimentalCoroutinesApi
	override val extensionSettings: LiveData<HResult<List<SettingsItemData>>> by lazy {
		extensionIDFlow.transformLatest { extensionID ->
			getExtListNames(extensionID).handle(
				onError = { emit(it) },
				onEmpty = { emit(empty) },
				onLoading = { emit(loading) }
			) { nameList ->
				emitAll(
					getExtensionSettings(extensionID).mapLatestResult { extensionSettings ->
						successResult(
							ArrayList(extensionSettings).apply {
								if (nameList.size > 1) {
									add(0, spinnerSettingData(0) {
										titleID = R.string.listings
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
									})
								}
							}
						)
					}
				)
			}
		}.asIOLiveData()
	}

	override fun setExtensionID(id: Int) {
		logV("Setting extension id = $id")
		launchIO {
			when {
				extensionIDFlow.value == id -> {
					this@ExtensionConfigureViewModel.logI("id is the same, ignoring")
					return@launchIO
				}
				extensionIDFlow.value != id -> {
					this@ExtensionConfigureViewModel.logI("id is different, resetting")
					destroy()
				}
				extensionIDFlow.value == -1 -> {
					this@ExtensionConfigureViewModel.logI("id is new, setting")
				}
			}
			extensionIDFlow.value = id
		}
	}

	override fun uninstall(extensionUI: ExtensionUI) {
		uninstallExtensionUIUseCase(extensionUI)
	}

	override fun destroy() {
		extensionIDFlow.value = -1
	}
}

