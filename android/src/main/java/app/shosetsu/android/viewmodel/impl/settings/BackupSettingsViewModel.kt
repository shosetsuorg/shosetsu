package app.shosetsu.android.viewmodel.impl.settings

import android.net.Uri
import app.shosetsu.android.backend.workers.onetime.NovelUpdateWorker
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.domain.usecases.load.LoadInternalBackupNamesUseCase
import app.shosetsu.android.domain.usecases.start.StartBackupWorkerUseCase
import app.shosetsu.android.domain.usecases.start.StartExportBackupWorkerUseCase
import app.shosetsu.android.domain.usecases.start.StartRestoreWorkerUseCase
import app.shosetsu.android.viewmodel.abstracted.settings.ABackupSettingsViewModel
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import kotlinx.coroutines.flow.Flow
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
	private val manager: NovelUpdateWorker.Manager,
	private val startBackupWorkerUseCase: StartBackupWorkerUseCase,
	private val loadInternalBackupNamesUseCase: LoadInternalBackupNamesUseCase,
	private val startRestoreWorker: StartRestoreWorkerUseCase,
	private val startExportWorker: StartExportBackupWorkerUseCase
) : ABackupSettingsViewModel(iSettingsRepository) {

	override fun startBackup() {
		launchIO {
			if (manager.isRunning()) manager.stop()
			startBackupWorkerUseCase()
		}
	}

	override fun loadInternalOptions(): Flow<List<String>> = flow {
		emit(loadInternalBackupNamesUseCase().sorted())
	}.onIO()

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

	override fun getBackupToExport(): String? =
		if (backupToExport != null) backupToExport!! else null

	override fun clearExport() {
		backupToExport = null
	}

	override fun exportBackup(uri: Uri) {
		if (backupToExport == null) return

		startExportWorker(backupToExport!!, uri)
	}
}