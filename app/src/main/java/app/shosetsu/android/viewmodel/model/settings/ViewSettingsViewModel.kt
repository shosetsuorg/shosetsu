package app.shosetsu.android.viewmodel.model.settings

import android.content.Context
import android.widget.ArrayAdapter
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.viewmodel.abstracted.settings.AViewSettingsViewModel
import app.shosetsu.common.com.consts.settings.SettingKey.*
import app.shosetsu.common.com.dto.HResult
import app.shosetsu.common.com.dto.handle
import com.github.doomsdayrs.apps.shosetsu.R

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

/**
 * shosetsu
 * 31 / 08 / 2020
 */
class ViewSettingsViewModel(
		iSettingsRepository: ISettingsRepository,
		private val context: Context,
		private val reportExceptionUseCase: ReportExceptionUseCase
) : AViewSettingsViewModel(iSettingsRepository) {
	override suspend fun settings(): List<SettingsItemData> = listOf(

			numberPickerSettingData(1) {
				title { R.string.columns_of_novel_listing_p }
				description { (R.string.columns_zero_automatic) }
				iSettingsRepository.getInt(ChapterColumnsInPortait).handle {
					initalValue { it }
				}
				onValuePicked { _, _, newVal ->
					launchIO {
						iSettingsRepository.setInt(ChapterColumnsInPortait, newVal)
					}
				}
				range { 0 to 10 }
			},
			numberPickerSettingData(2) {
				title { R.string.columns_of_novel_listing_h }
				description { (R.string.columns_zero_automatic) }
				iSettingsRepository.getInt(ChapterColumnsInLandscape).handle {
					initalValue { it }
				}
				onValuePicked { _, _, newVal ->
					launchIO {
						iSettingsRepository.setInt(ChapterColumnsInLandscape, newVal)
					}
				}
				range { 0 to 10 }
			},
			spinnerSettingData(3) {
				title { R.string.novel_card_type_selector_title }
				description { R.string.novel_card_type_selector_desc }
				iSettingsRepository.getInt(NovelCardType).handle {
					spinnerValue { it }
				}
				onSpinnerItemSelected { _, _, position, _ ->
					launchIO {
						iSettingsRepository.setInt(NovelCardType, position)
					}
				}
				arrayAdapter = ArrayAdapter(
						context,
						android.R.layout.simple_spinner_dropdown_item,
						context.resources!!.getStringArray(R.array.novel_card_types)
				)
			},
			switchSettingData(4) {
				title { "Legacy navigation" }
				description { "Disableds bottom navigation, enables drawer" }
				iSettingsRepository.getInt(NavStyle).handle {
					isChecked = it == 1
				}
			}
	)

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}
}