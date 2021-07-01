package app.shosetsu.android.backend.workers.onetime

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.backend.workers.NotificationCapable
import app.shosetsu.android.common.consts.LogConstants
import app.shosetsu.android.common.consts.WorkerTags.EXTENSION_INSTALL_WORK_ID
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logD
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.logI
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
		val extensionId = this.inputData.getInt(KEY_EXTENSION_ID, -1)
		if (extensionId == -1) {
			logE("Received negative extension id, aborting")
			return Result.failure()
		}

		logD("Starting ExtensionInstallWorker for $extensionId")

		// Notify progress
		extensionRepository.getExtensionEntity(extensionId).handle { extension ->
			extensionDownloadRepository.updateStatus(
				extensionId,
				DownloadStatus.DOWNLOADING
			).handle {
				extensionRepository.installExtension(extension).handle(
					onError = {
						extensionDownloadRepository.updateStatus(
							extensionId,
							DownloadStatus.ERROR
						)
						//TODO notify issue
					}
				) {
					extensionDownloadRepository.updateStatus(
						extensionId,
						DownloadStatus.COMPLETE
					).ifSo {
						extensionDownloadRepository.remove(extensionId)
						//TODO notify completion
					}
				}
			}

		}

		logD("Completed install")
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

	companion object {
		const val KEY_EXTENSION_ID = "extensionId"
	}

	/**
	 * Manager of [ExtensionInstallWorker]
	 */
	class Manager(context: Context) : CoroutineWorkerManager(context) {

		/**
		 * Returns the status of the service.
		 *
		 * @return true if the service is running, false otherwise.
		 */
		override fun isRunning(): Boolean = try {
			// Is this running
			(workerManager.getWorkInfosForUniqueWork(EXTENSION_INSTALL_WORK_ID)
				.get()[0].state == WorkInfo.State.RUNNING)
		} catch (e: Exception) {
			false
		}

		/**
		 * Starts the service.
		 * If there is one currently running, will append
		 */
		override fun start(data: Data) {
			launchIO {
				logI(LogConstants.SERVICE_NEW)
				workerManager.enqueueUniqueWork(
					EXTENSION_INSTALL_WORK_ID,
					ExistingWorkPolicy.APPEND,
					OneTimeWorkRequestBuilder<ExtensionInstallWorker>().setInputData(data).build()
				)
				logI(
					"Worker State ${
						workerManager.getWorkInfosForUniqueWork(EXTENSION_INSTALL_WORK_ID)
							.await()[0].state
					}"
				)
			}
		}

		/**
		 * Stops the service.
		 */
		override fun stop(): Operation =
			workerManager.cancelUniqueWork(EXTENSION_INSTALL_WORK_ID)
	}
}