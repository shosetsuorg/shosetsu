package app.shosetsu.android.viewmodel.impl.settings

import android.net.Uri
import androidx.lifecycle.LiveData
import app.shosetsu.android.backend.workers.onetime.NovelUpdateWorker
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.load.LoadInternalBackupNamesUseCase
import app.shosetsu.android.domain.usecases.start.StartBackupWorkerUseCase
import app.shosetsu.android.domain.usecases.start.StartExportBackupWorkerUseCase
import app.shosetsu.android.domain.usecases.start.StartRestoreWorkerUseCase
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.viewmodel.abstracted.settings.ABackupSettingsViewModel
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.emptyResult
import app.shosetsu.common.dto.successResult
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
class BackupSettingsViewModel(
	iSettingsRepository: ISettingsRepository,
	private val reportExceptionUseCase: ReportExceptionUseCase,
	private val manager: NovelUpdateWorker.Manager,
	private val startBackupWorkerUseCase: StartBackupWorkerUseCase,
	private val loadInternalBackupNamesUseCase: LoadInternalBackupNamesUseCase,
	private val startRestoreWorker: StartRestoreWorkerUseCase,
	private val startExportWorker: StartExportBackupWorkerUseCase
) : ABackupSettingsViewModel(iSettingsRepository) {

	override fun startBackup() {
		if (manager.isRunning()) manager.stop()
		startBackupWorkerUseCase()
	}

	override fun loadInternalOptions(): LiveData<HResult<List<String>>> = flow {
		emit(loadInternalBackupNamesUseCase())
	}.asIOLiveData()

	override fun restore(path: String) {
		logV("Restoring: $path ")
		startRestoreWorker(path)
	}

	override fun restore(uri: Uri) {
		logV("Restoring: $uri")
		startRestoreWorker(uri)
	}

	private var backupToExport: String? = null

	override fun holdBackupToExport(backupToExport: String) {
		this.backupToExport = backupToExport
	}

	override fun getBackupToExport(): HResult<String> =
		if (backupToExport != null) successResult(backupToExport!!) else emptyResult()

	override fun clearExport() {
		backupToExport = null
	}

	override fun exportBackup(uri: Uri) {
		if (backupToExport == null) return

		startExportWorker(backupToExport!!, uri)
	}

	override suspend fun settings(): List<SettingsItemData> = listOf(
		switchSettingData(0) {
			title { R.string.backup_chapters_option }
			description { R.string.backup_chapters_option_description }
			checkSettingValue(SettingKey.ShouldBackupChapters)
		},
		switchSettingData(1) {
			title { R.string.backup_settings_option }
			description { R.string.backup_settings_option_desc }
			checkSettingValue(SettingKey.ShouldBackupSettings)
		},
		buttonSettingData(3) {
			title { R.string.backup_now }
			text { R.string.backup_now }
		},
		buttonSettingData(4) {
			title { R.string.restore_now }
			text { R.string.restore_now }
		},
		buttonSettingData(5) {
			titleRes = R.string.settings_backup_export
			textRes = R.string.settings_backup_export
		}
	)

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}
}