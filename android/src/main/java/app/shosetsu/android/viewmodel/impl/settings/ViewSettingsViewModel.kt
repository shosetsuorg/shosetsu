package app.shosetsu.android.viewmodel.impl.settings

import android.app.Application
import android.content.res.Resources
import android.widget.ArrayAdapter
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.viewmodel.abstracted.settings.AViewSettingsViewModel
import app.shosetsu.common.consts.settings.SettingKey.*
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.domain.repositories.base.getIntOrDefault
import app.shosetsu.common.dto.HResult
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
	private val application: Application,
	private val reportExceptionUseCase: ReportExceptionUseCase
) : AViewSettingsViewModel(iSettingsRepository) {
	override suspend fun settings(): List<SettingsItemData> = listOf(

		numberPickerSettingData(1) {
			title { R.string.columns_of_novel_listing_p }
			description { (R.string.columns_zero_automatic) }
			settingValue(ChapterColumnsInPortait)
			range { 0 to 10 }
		},
		numberPickerSettingData(2) {
			title { R.string.columns_of_novel_listing_h }
			description { (R.string.columns_zero_automatic) }
			settingValue(ChapterColumnsInLandscape)
			range { 0 to 10 }
		},
		spinnerSettingData(3) {
			title { R.string.novel_card_type_selector_title }
			description { R.string.novel_card_type_selector_desc }

			spinnerSettingValue(SelectedNovelCardType)

			try {
				arrayAdapter = ArrayAdapter(
					application,
					android.R.layout.simple_spinner_dropdown_item,
					application.resources!!.getStringArray(R.array.novel_card_types)
				)
			} catch (e: Resources.NotFoundException) {
				reportExceptionUseCase.invoke(e.toHError())
			}
		},
		switchSettingData(4) {
			title { "Legacy navigation" }
			description { "Disableds bottom navigation, enables drawer" }
			isChecked = settingsRepo.getIntOrDefault(NavStyle) == 1
		}
	)

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}
}