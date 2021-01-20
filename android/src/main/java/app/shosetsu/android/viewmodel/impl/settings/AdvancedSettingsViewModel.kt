package app.shosetsu.android.viewmodel.impl.settings

import android.content.Context
import android.content.res.Resources
import android.widget.ArrayAdapter
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.toast
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.viewmodel.abstracted.settings.AAdvancedSettingsViewModel
import app.shosetsu.common.consts.ErrorKeys
import app.shosetsu.common.consts.settings.SettingKey.AppTheme
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.errorResult
import app.shosetsu.common.dto.handle
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
class AdvancedSettingsViewModel(
	iSettingsRepository: ISettingsRepository,
	private val context: Context,
	private val reportExceptionUseCase: ReportExceptionUseCase
) : AAdvancedSettingsViewModel(iSettingsRepository) {
	override suspend fun settings(): List<SettingsItemData> = listOf(
		spinnerSettingData(1) {
			title { R.string.theme }
			try {
				arrayAdapter = ArrayAdapter(
					context,
					android.R.layout.simple_spinner_dropdown_item,
					context.resources.getStringArray(R.array.application_themes)
				)
			} catch (e: Resources.NotFoundException) {
				reportExceptionUseCase(errorResult(ErrorKeys.ERROR_IMPOSSIBLE, e))
			}

			iSettingsRepository.getInt(AppTheme).handle {
				spinnerValue { it }
			}

			onSpinnerItemSelected { _, _, selectedTheme, _ ->
				launchIO { iSettingsRepository.setInt(AppTheme, selectedTheme) }
			}
		},
		buttonSettingData(2) {
			title { R.string.remove_novel_cache }
			onButtonClicked {
				try {
					// TODO purge
				} catch (e: android.database.SQLException) {
					context.toast("SQLITE Error")
					android.util.Log.e("AdvancedSettings", "DatabaseError", e)
				}
			}
		}
	)

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportError(error, isSilent)
	}
}