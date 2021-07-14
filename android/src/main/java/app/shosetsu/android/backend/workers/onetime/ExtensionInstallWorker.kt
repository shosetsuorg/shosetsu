package app.shosetsu.android.backend.workers.onetime

import android.content.Context
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.backend.workers.NotificationCapable
import app.shosetsu.android.common.consts.LogConstants
import app.shosetsu.android.common.consts.Notifications
import app.shosetsu.android.common.consts.WorkerTags.EXTENSION_INSTALL_WORK_ID
import app.shosetsu.android.common.ext.*
import app.shosetsu.common.consts.settings.SettingKey.NotifyExtensionDownload
import app.shosetsu.common.domain.repositories.base.IExtensionDownloadRepository
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.domain.repositories.base.getBooleanOrDefault
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.ifSo
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.enums.DownloadStatus
import com.github.doomsdayrs.apps.shosetsu.R
import com.squareup.picasso.Picasso
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
	private val settingsRepository: ISettingsRepository by instance()

	override suspend fun doWork(): Result {
		val extensionId = this.inputData.getInt(KEY_EXTENSION_ID, -1)
		if (extensionId == -1) {
			logE("Received negative extension id, aborting")
			return Result.failure()
		}
		val notify: Boolean = settingsRepository.getBooleanOrDefault(NotifyExtensionDownload)

		logD("Starting ExtensionInstallWorker for $extensionId")

		// Notify progress
		extensionRepository.getExtensionEntity(extensionId).handle { extension ->
			// Load image, this tbh may take longer then the actual extension

			val bitmap: Bitmap? =
				if (notify)
					Picasso.get().load(extension.imageURL).get()
				else null

			if (notify)
				notify(
					applicationContext.getString(
						R.string.notification_content_text_extension_download,
						extension.name
					),
				) {
					setProgress(0, 0, true)
					setLargeIcon(bitmap)
				}
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
						if (notify)
							notificationManager.cancel(defaultNotificationID)

						notify(
							it.message,
							extensionId * -1
						) {
							setContentTitle(
								applicationContext.getString(
									R.string.notification_content_text_extension_installed_failed,
									extension.name
								)
							)
							setContentInfo(
								getString(
									R.string.notification_content_title_extension_download
								)
							)
							setNotOngoing()
						}

						logE("Failed to install ${extension.name}", it.exception)
					}
				) {
					extensionDownloadRepository.updateStatus(
						extensionId,
						DownloadStatus.COMPLETE
					).ifSo {
						if (notify)
							notificationManager.cancel(defaultNotificationID)

						extensionDownloadRepository.remove(extensionId).ifSo {
							if (notify)
								notificationManager.notify(
									extensionId * -1,
									baseNotificationBuilder.apply {
										setContentTitle(
											applicationContext.getString(
												R.string.notification_content_text_extension_installed,
												extension.name
											)
										)
										setContentInfo(
											getString(
												R.string.notification_content_title_extension_download
											)
										)
										setLargeIcon(bitmap)
										removeProgress()
										setNotOngoing()
									}.build()
								)
							successResult()
						}
					}
				}
			}
		}
		logD("Completed install")
		return Result.success()
	}

	override val notifyContext: Context
		get() = applicationContext
	override val defaultNotificationID: Int = Notifications.ID_EXTENSION_DOWNLOAD

	override val notificationManager: NotificationManagerCompat by notificationManager()

	override val baseNotificationBuilder: NotificationCompat.Builder
		get() = notificationBuilder(applicationContext, Notifications.CHANNEL_DOWNLOAD)
			.setSmallIcon(R.drawable.download)
			.setContentTitle(getString(R.string.notification_content_title_extension_download))
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.setOngoing(true)

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
			(getWorkerState() == WorkInfo.State.RUNNING)
		} catch (e: Exception) {
			false
		}

		override fun getWorkerState(index: Int): WorkInfo.State =
			workerManager.getWorkInfosForUniqueWork(EXTENSION_INSTALL_WORK_ID).get()[index].state

		override val count: Int
			get() = workerManager.getWorkInfosForUniqueWork(EXTENSION_INSTALL_WORK_ID).get().size

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