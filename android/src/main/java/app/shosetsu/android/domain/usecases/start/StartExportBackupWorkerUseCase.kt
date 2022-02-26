package app.shosetsu.android.domain.usecases.start

import android.net.Uri
import androidx.work.Data
import app.shosetsu.android.backend.workers.onetime.ExportBackupWorker
import app.shosetsu.android.domain.repository.base.IBackupUriRepository

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
 * Shosetsu
 *
 * @since 15 / 09 / 2021
 * @author Doomsdayrs
 */
class StartExportBackupWorkerUseCase(
	private val manager: ExportBackupWorker.Manager,
	private val backupUri: IBackupUriRepository
) {

	operator fun invoke(backupToExport: String, uri: Uri) {

		backupUri.give(uri)

		manager.start(data = Data.Builder().apply {
			putString(ExportBackupWorker.KEY_EXPORT_NAME, backupToExport)
		}.build())
	}
}