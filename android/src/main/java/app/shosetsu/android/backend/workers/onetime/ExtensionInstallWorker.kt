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
import app.shosetsu.common.domain.repositories.base.*
import app.shosetsu.common.dto.handle
import app.shosetsu.common.dto.ifSo
import app.shosetsu.common.dto.successResult
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
		if (extensionId == -1) {
			logE("Received negative extension id, aborting")
			return Result.failure()
		}
		val notify: Boolean = settingsRepository.getBooleanOrDefault(NotifyExtensionDownload)

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
		extensionRepository.getExtension(extensionId).handle(
			onEmpty = {
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
			},
			onLoading = {
				markExtensionDownloadAsError()

				logE("Received loading result when loading extension from db($extensionId)")

				notifyError(
					"Received loading on load from db",
					applicationContext.getString(
						R.string.notification_content_text_extension_load_error,
						extensionId
					)
				)

				return Result.failure()
			},
			onError = {
				markExtensionDownloadAsError()

				logE(
					"Received error result when loading extension from db($extensionId)\n" +
							"Code:${it.code}",
					it.exception
				)

				notifyError(
					it.message,
					applicationContext.getString(
						R.string.notification_content_text_extension_load_error,
						extensionId
					)
				)

				ACRA.errorReporter.handleException(it.exception)

				return Result.failure()
			}
		) { extension ->

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
				imageLoadJob.cancel("Extension already installed")

				imageBitmap?.recycle()
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
			).handle {
				installExtension(extension).handle(
					onEmpty = {
						markExtensionDownloadAsError()

						logE("Received empty result when loading extension from db:($extensionId)")

						notifyError(
							"Received empty when installing extension",
							applicationContext.getString(
								R.string.notification_content_text_extension_installed_failed,
								extension.name
							)
						)

						cleanupImageLoader()

						return Result.failure()
					},
					onLoading = {
						markExtensionDownloadAsError()

						logE("Received loading result when loading extension from db($extensionId)")

						notifyError(
							"Received loading when installing extension",
							applicationContext.getString(
								R.string.notification_content_text_extension_installed_failed,
								extension.name
							)
						)

						cleanupImageLoader()

						return Result.failure()
					},
					onError = {
						markExtensionDownloadAsError()

						notifyError(
							it.message,
							applicationContext.getString(
								R.string.notification_content_text_extension_installed_failed,
								extension.name
							)
						)

						logE("Failed to install ${extension.name}", it.exception)

						ACRA.errorReporter.handleException(it.exception)

						cleanupImageLoader()

						return Result.failure()
					}
				) { flags ->
					extensionDownloadRepository.updateStatus(
						extensionId,
						DownloadStatus.COMPLETE
					).ifSo {
						cancelDefault()

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
										setContentInfo(extensionDownloaderString)
										setLargeIcon(imageBitmap)
										removeProgress()
										setNotOngoing()
									}.build()
								)
							successResult()
						}
					}
					if (flags.deleteChapters) {
						chaptersRepository.getChaptersByExtension(extensionId).handle(
							onError = {
								logE("Failed to get chapters by extension", it.exception)

								ACRA.errorReporter.handleException(it.exception)
							}
						) { list ->
							list.forEach {
								chaptersRepository.deleteChapterPassage(it, flags.oldType!!)
							}
						}
					}
				}
			}

			cleanupImageLoader()
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
			.setContentTitle(extensionDownloaderString)
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