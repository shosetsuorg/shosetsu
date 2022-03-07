package app.shosetsu.android.domain.usecases.start

import app.shosetsu.android.backend.workers.onetime.BackupWorker
import app.shosetsu.android.backend.workers.onetime.NovelUpdateWorker
import app.shosetsu.android.common.ext.launchIO

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
class StartBackupWorkerUseCase(
	private val manager: BackupWorker.Manager,
	private val updateWorkerManager: NovelUpdateWorker.Manager
) {
	operator fun invoke() {
		launchIO {
			if (!manager.isRunning()) {
				// Stops the update worker to prevent it from interfering
				if (updateWorkerManager.isRunning())
					updateWorkerManager.stop()

				manager.start()

			}
		}
	}
}