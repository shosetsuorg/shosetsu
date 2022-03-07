package app.shosetsu.android.domain.usecases.start

import android.net.Uri
import androidx.work.Data
import app.shosetsu.android.backend.workers.onetime.RestoreBackupWorker
import app.shosetsu.android.backend.workers.onetime.RestoreBackupWorker.Companion.BACKUP_DATA_KEY
import app.shosetsu.android.backend.workers.onetime.RestoreBackupWorker.Companion.BACKUP_DIR_KEY
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.domain.repository.base.IBackupUriRepository

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
 * 02 / 02 / 2021
 */
class StartRestoreWorkerUseCase(
	private val manager: RestoreBackupWorker.Manager,
	private val backupRepository: IBackupUriRepository
) {
	operator fun invoke(path: String) {
		launchIO {
			if (!manager.isRunning())
				manager.start(
					Data.Builder().apply {
						putString(BACKUP_DATA_KEY, path)
						putBoolean(BACKUP_DIR_KEY, false)
					}.build()
				)
		}
	}

	operator fun invoke(path: Uri) {
		launchIO {
			if (!manager.isRunning()) {
				backupRepository.give(path)
				manager.start(
					Data.Builder().apply {
						putBoolean(BACKUP_DIR_KEY, true)
					}.build()
				)
			}
		}
	}
}