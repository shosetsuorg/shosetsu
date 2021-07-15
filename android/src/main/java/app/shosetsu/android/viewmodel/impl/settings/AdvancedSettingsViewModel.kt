package app.shosetsu.android.viewmodel.impl.settings

import android.app.Application
import android.content.res.Resources
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.PurgeNovelCacheUseCase
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.viewmodel.abstracted.settings.AAdvancedSettingsViewModel
import app.shosetsu.common.consts.settings.SettingKey.*
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.domain.repositories.base.getIntOrDefault
import app.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.coroutines.flow.flow

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
	private val context: Application,
	private val reportExceptionUseCase: ReportExceptionUseCase,
	private val purgeNovelCacheUseCase: PurgeNovelCacheUseCase
) : AAdvancedSettingsViewModel(iSettingsRepository) {
	override fun purgeUselessData(): LiveData<HResult<*>> =
		flow {
			emit(purgeNovelCacheUseCase())
		}.asIOLiveData()

	override suspend fun settings(): List<SettingsItemData> = listOf(
		spinnerSettingData(1) {
			titleRes = R.string.theme
			try {
				arrayAdapter = ArrayAdapter(
					context,
					android.R.layout.simple_spinner_dropdown_item,
					context.resources.getStringArray(R.array.application_themes)
				)
			} catch (e: Resources.NotFoundException) {
				reportError(e.toHError())
			}

			spinnerValue { settingsRepo.getIntOrDefault(AppTheme) }
		},
		buttonSettingData(2) {
			titleRes = R.string.remove_novel_cache
			text { R.string.settings_advanced_purge_button }
		},
		switchSettingData(3) {
			titleRes = R.string.settings_advanced_verify_checksum_title
			description { R.string.settings_advanced_verify_checksum_desc }
			checkSettingValue(VerifyCheckSum)
		},
		switchSettingData(4) {
			titleRes = R.string.settings_advanced_require_double_back_title
			description { R.string.settings_advanced_require_double_back_desc }
			checkSettingValue(RequireDoubleBackToExit)
		}
	)

	override fun reportError(error: HResult.Error, isSilent: Boolean) =
		reportExceptionUseCase(error, isSilent)
}