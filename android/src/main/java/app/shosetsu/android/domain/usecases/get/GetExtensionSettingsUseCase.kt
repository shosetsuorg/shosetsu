package app.shosetsu.android.domain.usecases.get

import android.app.Application
import android.widget.ArrayAdapter
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.launchUI
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.domain.usecases.update.UpdateExtensionSettingUseCase
import app.shosetsu.android.view.uimodels.settings.DividerSettingItemData
import app.shosetsu.android.view.uimodels.settings.ListSettingData
import app.shosetsu.android.view.uimodels.settings.TriStateButtonSettingData
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.view.widget.TriState
import app.shosetsu.common.domain.repositories.base.IExtensionSettingsRepository
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository
import app.shosetsu.common.dto.*
import app.shosetsu.lib.Filter
import com.github.doomsdayrs.apps.shosetsu.R
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import kotlinx.coroutines.flow.*

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * shosetsu
 * 14 / 09 / 2020
 *
 * Loads the extension settings
 */
class GetExtensionSettingsUseCase(
	private val iExtensionsRepository: IExtensionsRepository,
	private val extSettingsRepository: IExtensionSettingsRepository,
	private val application: Application,
	private val updateSetting: UpdateExtensionSettingUseCase,
	private val getExt: GetExtensionUseCase
) {
	private suspend fun asSettingItem(extensionID: Int, filter: Filter<Boolean>) =
		extSettingsRepository.getBooleanFlow(
			extensionID,
			filter.id,
			filter.state
		).map { state ->
			switchSettingData(filter.id) {
				titleText = filter.name
				isChecked = state
				onChecked { _, isChecked ->
					launchIO {
						updateSetting(
							extensionID,
							filter.id,
							isChecked
						)
					}
				}
			}
		}

	private suspend fun Array<Filter<*>>.convert(extensionID: Int): Array<Flow<SettingsItemData>> =
		mapNotNull { filter ->
			when (filter) {
				is Filter.Text -> {
					this@GetExtensionSettingsUseCase.logV("Converting Filter.Text")
					extSettingsRepository.getStringFlow(
						extensionID,
						filter.id,
						filter.state
					).map { state ->
						textInputSettingData(filter.id) {
							titleText = filter.name
							initialText = state
							doAfterTextChanged { editable ->
								launchIO {
									updateSetting(
										extensionID,
										filter.id,
										editable.toString()
									)
								}
							}
						}
					}
				}
				is Filter.Switch -> {
					this@GetExtensionSettingsUseCase.logV("Converting Filter.Switch")
					asSettingItem(extensionID, filter)
				}
				is Filter.Checkbox -> {
					this@GetExtensionSettingsUseCase.logV("Converting Filter.Checkbox")
					asSettingItem(extensionID, filter)
				}
				is Filter.TriState -> {
					this@GetExtensionSettingsUseCase.logV("Converting Filter.TriState")
					extSettingsRepository.getIntFlow(
						extensionID,
						filter.id,
						filter.state
					).map { newState ->
						TriStateButtonSettingData(filter.id).apply {
							titleText = filter.name
							checkedRes = R.drawable.checkbox_checked
							uncheckedRes = R.drawable.checkbox_inter
							ignoredRes = R.drawable.checkbox_ignored

							onStateChanged = {
								launchIO {
									updateSetting(
										extensionID,
										filter.id,
										state.ordinal
									)
								}
							}

							state = when (newState) {
								Filter.TriState.STATE_EXCLUDE -> TriState.State.UNCHECKED
								Filter.TriState.STATE_IGNORED -> TriState.State.IGNORED
								Filter.TriState.STATE_INCLUDE -> TriState.State.CHECKED
								else -> TriState.State.IGNORED
							}
						}
					}
				}
				is Filter.Dropdown -> {
					this@GetExtensionSettingsUseCase.logV("Converting Filter.Dropdown")
					extSettingsRepository.getIntFlow(
						extensionID,
						filter.id,
						filter.state
					).map { state ->
						spinnerSettingData(filter.id) {
							titleText = filter.name
							arrayAdapter = ArrayAdapter(
								application,
								android.R.layout.simple_spinner_dropdown_item,
								filter.choices
							)
							spinnerValue { state }
							onSpinnerItemSelected { _, _, position, _ ->
								launchIO {
									updateSetting(
										extensionID,
										filter.id,
										position
									)
								}
							}
						}
					}
				}
				is Filter.RadioGroup -> {
					this@GetExtensionSettingsUseCase.logV("Converting Filter.RadioGroup")
					extSettingsRepository.getIntFlow(
						extensionID,
						filter.id,
						filter.state
					).map { state ->
						//TODO RadioGroup
						spinnerSettingData(filter.id) {
							titleText = filter.name
							arrayAdapter = ArrayAdapter(
								application,
								android.R.layout.simple_spinner_dropdown_item,
								filter.choices
							)
							spinnerValue { state }
							onSpinnerItemSelected { _, _, position, _ ->
								launchIO {
									updateSetting(
										extensionID,
										filter.id,
										position
									)
								}
							}
						}
					}
				}
				is Filter.List -> {
					this@GetExtensionSettingsUseCase.logV("Converting Filter.List")

					val list = ListSettingData(filter.id).apply {
						titleText = filter.name
						launchIO {
							filter.filters.convert(extensionID).combine().collectLatest {
								launchUI {
									FastAdapterDiffUtil[itemAdapter] =
										FastAdapterDiffUtil.calculateDiff(itemAdapter, it)
								}
							}
						}
					}
					flow {
						emit(list)
					}
				}
				is Filter.Group<*> -> {
					this@GetExtensionSettingsUseCase.logV("Converting Filter.Group")
					val list = ListSettingData(filter.id).apply {
						titleText = filter.name
						launchIO {
							(filter.filters as Array<Filter<*>>).convert(extensionID).combine()
								.collectLatest {
									launchUI {
										FastAdapterDiffUtil[itemAdapter] =
											FastAdapterDiffUtil.calculateDiff(itemAdapter, it)
									}
								}
						}
					}
					flow {
						emit(list)
					}
				}
				is Filter.Header -> flow {
					this@GetExtensionSettingsUseCase.logV("Converting Filter.Header")
					emit(headerSettingItemData(filter.id) {
						titleText = filter.name
					})
				}
				is Filter.Separator -> {
					this@GetExtensionSettingsUseCase.logV("Converting Filter.Separator")
					flow { emit(DividerSettingItemData(filter.id)) }
				}
			}
		}.toTypedArray()

	private fun Array<Flow<SettingsItemData>>.combine(): Flow<List<SettingsItemData>> =
		combine(*this) { it.toList() }

	operator fun invoke(extensionID: Int): Flow<HResult<List<SettingsItemData>>> =
		flow {
			if (extensionID == -1) {
				emit(empty)
				return@flow
			}

			getExt(extensionID).handle(
				onLoading = { emit(loading) },
				onEmpty = { emit(empty) },
				onError = { emit(it) }
			) { extension ->
				emitAll(extension.settingsModel.convert(extensionID).combine().mapToSuccess())
			}
		}
}