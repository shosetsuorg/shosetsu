package app.shosetsu.android.domain.usecases.get

import android.app.Application
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.domain.usecases.update.UpdateExtensionSettingUseCase
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.viewmodel.base.spinnerValue
import app.shosetsu.common.domain.repositories.base.IExtensionSettingsRepository
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository
import app.shosetsu.common.dto.*
import app.shosetsu.lib.Filter
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

	@ExperimentalCoroutinesApi
	operator fun invoke(extensionID: Int): Flow<HResult<List<SettingsItemData>>> =
		flow {
			getExt(extensionID).handle(
				onLoading = {
					emit(loading)
				},
				onEmpty = {
					emit(empty)
				},
				onError = {
					emit(it)
				}
			) { extension ->
				combine(extension.settingsModel.mapNotNull { filter ->
					when (filter) {
						is Filter.Text -> {
							extSettingsRepository.getStringFlow(
								extensionID,
								filter.id,
								filter.state
							).map { state ->
								textInputSettingData(filter.id) {
									title { filter.name }
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
							extSettingsRepository.getBooleanFlow(
								extensionID,
								filter.id,
								filter.state
							).map { state ->
								switchSettingData(filter.id) {
									title { filter.name }
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
						}
						is Filter.Checkbox -> {
							extSettingsRepository.getBooleanFlow(
								extensionID,
								filter.id,
								filter.state
							).map { state ->
								switchSettingData(filter.id) {
									title { filter.name }
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
						}
						is Filter.TriState -> {
							extSettingsRepository.getIntFlow(
								extensionID,
								filter.id,
								filter.state
							)
							null
						}
						is Filter.Dropdown -> {
							extSettingsRepository.getIntFlow(
								extensionID,
								filter.id,
								filter.state
							).map { state ->
								spinnerSettingData(filter.id) {
									title { filter.name }
									arrayAdapter = android.widget.ArrayAdapter(
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
							extSettingsRepository.getIntFlow(
								extensionID,
								filter.id,
								filter.state
							).map { state ->
								//TODO RadioGroup
								spinnerSettingData(filter.id) {
									title { filter.name }
									arrayAdapter = android.widget.ArrayAdapter(
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
							null
						}
						is Filter.Group<*> -> {
							null
						}
						else -> null
					}
				}) {
					it.toList()
				}.mapLatestToSuccess().let {
					emitAll(it)
				}
			}
		}
}