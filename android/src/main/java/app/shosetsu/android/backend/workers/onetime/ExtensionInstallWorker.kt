package app.shosetsu.android.backend.workers.onetime

import android.content.Context
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.work.*
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.backend.workers.NotificationCapable
import app.shosetsu.android.common.consts.LogConstants
import app.shosetsu.android.common.consts.Notifications
import app.shosetsu.android.common.consts.WorkerTags.EXTENSION_INSTALL_WORK_ID
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.usecases.InstallExtensionUseCase
import app.shosetsu.common.consts.settings.SettingKey.NotifyExtensionDownload
import app.shosetsu.common.domain.repositories.base.IChaptersRepository
import app.shosetsu.common.domain.repositories.base.IExtensionDownloadRepository
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.enums.DownloadStatus
import coil.imageLoader
import coil.request.ImageRequest
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.coroutines.cancel
import org.acra.ACRA
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
	private val installExtension: InstallExtensionUseCase by instance()
	private val settingsRepository: ISettingsRepository by instance()
	private val chaptersRepository: IChaptersRepository by instance()

	private val extensionDownloaderString by lazy {
		getString(R.string.notification_content_title_extension_download)
	}

	override suspend fun doWork(): Result {
		val extensionId = this.inputData.getInt(KEY_EXTENSION_ID, -1)
		val repositoryId = this.inputData.getInt(KEY_REPOSITORY_ID, -1)

		if (extensionId == -1) {
			logE("Received negative extension id, aborting")
			return Result.failure()
		}
		if (repositoryId == -1) {
			logE("Received negative repository id, aborting")
			return Result.failure()
		}
		val notify: Boolean = settingsRepository.getBoolean(NotifyExtensionDownload)

		/** Cancel default notification if present */
		fun cancelDefault() {
			if (notify)
				notificationManager.cancel(defaultNotificationID)
		}

		/**
		 * Cancels default and notifies the user
		 */
		fun notifyError(contentText: String, contentTitle: String) {
			cancelDefault()

			notify(
				contentText,
				extensionId * -1
			) {
				setContentTitle(contentTitle)
				setContentInfo(extensionDownloaderString)
				setNotOngoing()
			}
		}

		/** Mark extension download status as having an error*/
		suspend fun markExtensionDownloadAsError() {
			extensionDownloadRepository.updateStatus(
				extensionId,
				DownloadStatus.ERROR
			)
		}

		logD("Starting ExtensionInstallWorker for $extensionId")

		// Notify progress
		val extension = try {
			extensionRepository.getExtension(repositoryId, extensionId)
		} catch (e: Exception) {//TODO specify
			markExtensionDownloadAsError()

			logE(
				"Received error result when loading extension from db($extensionId)",
				e
			)

			notifyError(
				e.message ?: "???",
				applicationContext.getString(
					R.string.notification_content_text_extension_load_error,
					extensionId
				)
			)

			ACRA.errorReporter.handleException(e)

			return Result.failure()
		}
		if (extension == null) {
			markExtensionDownloadAsError()

			logE("Received empty result when loading extension from db:($extensionId)")

			notifyError(
				"Received empty on load from db",
				applicationContext.getString(
					R.string.notification_content_text_extension_load_error,
					extensionId
				)
			)

			return Result.failure()
		}

		// Load image, this tbh may take longer then the actual extension
		var imageBitmap: Bitmap? = null

		val imageLoadJob = launchIO {
			imageBitmap = if (notify) {
				applicationContext.imageLoader.execute(
					ImageRequest.Builder(applicationContext).data(extension.imageURL).build()
				).drawable?.toBitmap()
			} else null
		}

		/**
		 * Cancel image loading job and clear out bitmap
		 */
		fun cleanupImageLoader() {
			imageLoadJob.cancel("Extension install task over")
			imageBitmap = null
		}

		if (notify)
			notify(
				applicationContext.getString(
					R.string.notification_content_text_extension_download,
					extension.name
				),
			) {
				setProgress(0, 0, true)
				setLargeIcon(imageBitmap)
			}

		extensionDownloadRepository.updateStatus(
			extensionId,
			DownloadStatus.DOWNLOADING
		)

		val flags = try {
			installExtension(extension)
		} catch (e: Exception) {// TODO specify
			markExtensionDownloadAsError()

			notifyError(
				e.message ?: "???",
				applicationContext.getString(
					R.string.notification_content_text_extension_installed_failed,
					extension.name
				)
			)

			logE("Failed to install ${extension.name}", e)

			ACRA.errorReporter.handleIfValid(e)

			cleanupImageLoader()

			return Result.failure()
		}

		extensionDownloadRepository.updateStatus(
			extensionId,
			DownloadStatus.COMPLETE
		)

		cancelDefault()

		extensionDownloadRepository.remove(extensionId)

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
					setContentInfo(extensionDownloaderString)
					setLargeIcon(imageBitmap)
					removeProgress()
					setNotOngoing()
				}.build()
			)

		if (flags.deleteChapters) {
			val list = try {
				chaptersRepository.getChaptersByExtension(extensionId)
			} catch (e: Exception) {// TODO specify
				logE("Failed to get chapters by extension", e)

				ACRA.errorReporter.handleSilentException(e)

				emptyList()
			}
			list.forEach {
				chaptersRepository.deleteChapterPassage(it, flags.oldType!!)
			}
		}

		cleanupImageLoader()
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
			.setContentTitle(extensionDownloaderString)
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.setOngoing(true)

	companion object {
		const val KEY_EXTENSION_ID = "extensionId"
		const val KEY_REPOSITORY_ID = "repoID"
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
		override suspend fun isRunning(): Boolean = try {
			// Is this running
			(getWorkerState() == WorkInfo.State.RUNNING)
		} catch (e: Exception) {
			false
		}

		override suspend fun getWorkerState(index: Int): WorkInfo.State =
			getWorkerInfoList()[index].state

		override suspend fun getWorkerInfoList(): List<WorkInfo> =
			workerManager.getWorkInfosForUniqueWork(EXTENSION_INSTALL_WORK_ID).await()

		override suspend fun getCount(): Int =
			getWorkerInfoList().size

		/**
		 * Starts the service.
		 * If there is one currently running, will append
		 */
		override fun start(data: Data) {
			launchIO {
				logI(LogConstants.SERVICE_NEW)
				workerManager.enqueueUniqueWork(
					EXTENSION_INSTALL_WORK_ID,
					ExistingWorkPolicy.APPEND_OR_REPLACE,
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
