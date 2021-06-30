package app.shosetsu.android.backend.workers.onetime

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.shosetsu.android.backend.workers.NotificationCapable
import app.shosetsu.android.common.ext.logD
import app.shosetsu.common.domain.repositories.base.IExtensionDownloadRepository
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.ifSo
import app.shosetsu.common.enums.DownloadStatus
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance

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
 * @since 30 / 06 / 2021
 * @author Doomsdayrs
 */
class ExtensionInstallWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(
	appContext,
	params,
), DIAware, NotificationCapable {
	override val di: DI by closestDI(appContext)
	private val extensionDownloadRepository: IExtensionDownloadRepository by instance()
	private val extensionRepository: IExtensionsRepository by instance()

	override suspend fun doWork(): Result {
		logD("Starting ExtensionInstallWorker")

		while (extensionDownloadRepository.size != 0) {
			// TODO Notify progress

			extensionDownloadRepository.first.handle { extension ->
				extensionDownloadRepository.updateStatus(extension, DownloadStatus.DOWNLOADING)

				extensionRepository.installExtension(extension).handle(
					onError = {
						extensionDownloadRepository.updateStatus(extension, DownloadStatus.ERROR)
						//TODO notify issue
					}
				) {
					extensionDownloadRepository.updateStatus(extension, DownloadStatus.COMPLETE)
						.ifSo {
							extensionDownloadRepository.remove(extension)
							//TODO notify completion
						}
				}
			}
		}
		return Result.success()
	}

	override val baseNotificationBuilder: NotificationCompat.Builder
		get() = TODO("Not yet implemented")
	override val notificationManager: NotificationManagerCompat
		get() = TODO("Not yet implemented")
	override val notifyContext: Context
		get() = TODO("Not yet implemented")
	override val defaultNotificationID: Int
		get() = TODO("Not yet implemented")
}