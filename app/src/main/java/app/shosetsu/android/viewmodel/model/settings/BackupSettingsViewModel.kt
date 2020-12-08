package app.shosetsu.android.viewmodel.model.settings

import android.widget.CompoundButton
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.viewmodel.abstracted.settings.ABackupSettingsViewModel
import app.shosetsu.common.com.consts.settings.SettingKey
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
class BackupSettingsViewModel(
		iSettingsRepository: ISettingsRepository,
		private val reportExceptionUseCase: ReportExceptionUseCase
) : ABackupSettingsViewModel(iSettingsRepository) {
	override suspend fun settings(): List<SettingsItemData> = listOf(
			checkBoxSettingData(0) {
				title { R.string.backup_chapters_option }
				description { R.string.backup_chapters_option_description }
				iSettingsRepository.getBoolean(SettingKey.BackupChapters).handle {
					isChecked = it
				}
				onChecked { _: CompoundButton?, iC: Boolean ->
					launchIO {
						iSettingsRepository.setBoolean(SettingKey.BackupChapters, iC)
					}
				}
			},
			checkBoxSettingData(1) {
				title { R.string.backup_settings_option }
				description { R.string.backup_settings_option_desc }
				iSettingsRepository.getBoolean(SettingKey.BackupSettings).handle {
					isChecked = it
				}
				onChecked { _: CompoundButton?, iC: Boolean ->
					launchIO {
						iSettingsRepository.setBoolean(SettingKey.BackupSettings, iC)
					}
				}
			},
			checkBoxSettingData(2) {
				title { R.string.backup_quick_option }
				description { R.string.backup_quick_option_desc }
				iSettingsRepository.getBoolean(SettingKey.BackupQuick).handle {
					isChecked = it
				}
				onChecked { _: CompoundButton?, iC: Boolean ->
					launchIO {
						iSettingsRepository.setBoolean(SettingKey.BackupQuick, iC)
					}
				}
			},
			buttonSettingData(3) {
				title { R.string.backup_now }
				text { R.string.restore_now }
				onButtonClicked { it.post { } }
			},
			buttonSettingData(4) {
				title { R.string.restore_now }
				text { R.string.restore_now }
			}
	)

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}
}