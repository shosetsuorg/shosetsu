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
import app.shosetsu.android.view.uimodels.settings.dsl.onSpinnerItemSelected
import app.shosetsu.android.view.uimodels.settings.dsl.spinnerSettingData
import app.shosetsu.android.view.uimodels.settings.dsl.title
import app.shosetsu.android.viewmodel.abstracted.IExtensionConfigureViewModel
import app.shosetsu.android.viewmodel.base.spinnerValue
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
	private val idLive: MutableStateFlow<Int> by lazy {
		MutableStateFlow(-1)
	}

	@ExperimentalCoroutinesApi
	override val liveData: LiveData<HResult<ExtensionUI>> by lazy {
		idLive.transformLatest { id ->
			emitAll(loadExtensionUIUI(id))
		}.asIOLiveData()
	}

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}

	@ExperimentalCoroutinesApi
	override val extensionSettings: LiveData<HResult<List<SettingsItemData>>> by lazy {
		idLive.transformLatest { id ->
			getExtListNames(id).handle(
				onError = { emit(it) },
				onEmpty = { emit(empty) },
				onLoading = { emit(loading) }
			) { nameList ->
				emitAll(
					getExtensionSettings(id).mapLatestResult { filterList ->
						successResult(
							listOf(
								spinnerSettingData(0) {
									title { "Listing" }
									getExtSelectedListing(id).handle { selectedListing ->
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
											updateExtSelectedListing(id, position)
										}
									}
								}
							)
						)
					}
				)
			}
		}.asIOLiveData()
	}

	override fun setExtensionID(id: Int) {
		launchIO {
			when {
				idLive.value == id -> {
					logI("ID the same, ignoring")
					return@launchIO
				}
				idLive.value != id -> {
					logI("ID not equal, resetting")
					destroy()
				}
				idLive.value == -1 -> {
					logI("ID is new, setting")
				}
			}
			idLive.value = id
		}
	}

	override fun uninstall(extensionUI: ExtensionUI) {
		uninstallExtensionUIUseCase(extensionUI)
	}

	override fun destroy() {
		idLive.value = -1
	}
}

