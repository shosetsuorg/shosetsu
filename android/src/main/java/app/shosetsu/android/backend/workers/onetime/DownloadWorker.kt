package app.shosetsu.android.backend.workers.onetime

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import androidx.work.NetworkType.CONNECTED
import androidx.work.NetworkType.UNMETERED
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.backend.workers.NotificationCapable
import app.shosetsu.android.common.consts.Notifications.CHANNEL_DOWNLOAD
import app.shosetsu.android.common.consts.Notifications.ID_CHAPTER_DOWNLOAD
import app.shosetsu.android.common.consts.WorkerTags.DOWNLOAD_WORK_ID
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.usecases.get.GetExtensionUseCase
import app.shosetsu.common.consts.settings.SettingKey.*
import app.shosetsu.common.domain.model.local.DownloadEntity
import app.shosetsu.common.domain.repositories.base.*
import app.shosetsu.common.dto.*
import app.shosetsu.common.enums.DownloadStatus
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
 * shosetsu
 * 08 / 02 / 2020
 *
 * @author github.com/doomsdayrs
 */
class DownloadWorker(
	appContext: Context,
	params: WorkerParameters,
) : CoroutineWorker(appContext, params), DIAware, NotificationCapable {
	override val notifyContext: Context
		get() = applicationContext
	override val defaultNotificationID: Int = ID_CHAPTER_DOWNLOAD

	override val notificationManager: NotificationManagerCompat by notificationManager()

	override val baseNotificationBuilder: NotificationCompat.Builder
		get() = notificationBuilder(applicationContext, CHANNEL_DOWNLOAD)
			.setSmallIcon(R.drawable.download)
			.setContentTitle("Downloader")
			.setPriority(NotificationCompat.PRIORITY_HIGH)
			.setOngoing(true)

	override val di: DI by closestDI(applicationContext)
	private val downloadsRepo by instance<IDownloadsRepository>()
	private val chapRepo by instance<IChaptersRepository>()
	private val settingRepo by instance<ISettingsRepository>()
	private val getExt by instance<GetExtensionUseCase>()

	/** How many jobs are currently running */
	@get:Synchronized
	@set:Synchronized
	private var activeJobs = 0

	/** Which extensions are currently having network calls */
	@get:Synchronized
	private val activeExtensions = ArrayList<Int>()

	/** Retrieves the setting for if the download system is paused or not */
	private suspend fun isDownloadPaused(): Boolean =
		settingRepo.getBooleanOrDefault(IsDownloadPaused)

	/** Retrieves the setting for simultaneous download threads allowed */
	private suspend fun getDownloadThreads(): Int =
		settingRepo.getIntOrDefault(DownloadThreadPool)

	/** Retrieves the setting for simultaneous download threads allowed per extension */
	private suspend fun getDownloadThreadsPerExtension(): Int =
		settingRepo.getIntOrDefault(DownloadExtThreads)

	/** Loads the download count that is present currently */
	private suspend fun getDownloadCount(): Int =
		downloadsRepo.loadDownloadCount().unwrap() ?: -1


	private suspend fun download(downloadEntity: DownloadEntity): HResult<*> =
		chapRepo.getChapter(downloadEntity.chapterID).transform { chapterEntity ->
			getExt(chapterEntity.extensionID).transform { iExtension ->
				chapRepo.getChapterPassage(iExtension, chapterEntity).transform { passage ->
					chapRepo.saveChapterPassageToStorage(
						chapterEntity,
						iExtension.chapterType,
						passage
					)
					successResult("Chapter Loaded")
				}
			}
		}

	@Synchronized
	private fun activeExt(id: Int): Int {
		var count = 0
		for (i in activeExtensions.size - 1 downTo 0) {
			try {
				val aEI = activeExtensions[i]
				if (aEI == id)
					count++
			} catch (e: NullPointerException) {
				// Ignoring this due to async
			} catch (e: IndexOutOfBoundsException) {
				// Ignoring this due to async
			}
		}
		return count
	}


	private suspend fun notify(downloadEntity: DownloadEntity, isComplete: Boolean = false) {
		val messageId = if (!isComplete) when (downloadEntity.status) {
			DownloadStatus.PENDING -> R.string.pending
			DownloadStatus.WAITING -> R.string.waiting
			DownloadStatus.DOWNLOADING -> R.string.downloading
			DownloadStatus.PAUSED -> R.string.paused
			DownloadStatus.ERROR -> R.string.error
			else -> R.string.completed
		} else R.string.completed

		notify(messageId, downloadEntity.chapterID + 2000) {
			setNotOngoing()
			setSubText("Download")
			setContentTitle(downloadEntity.novelName + "\t" + downloadEntity.chapterName)
			priority = NotificationCompat.PRIORITY_LOW
			setNotificationSilent()
			if (isComplete)
				removeProgress()
			else if (downloadEntity.status == DownloadStatus.DOWNLOADING)
				setProgress(1, 0, true)
		}

		if (isComplete) {
			delay(5000)
			notificationManager.cancel(downloadEntity.chapterID + 2000)
		}
	}


	/**
	 * Creates a sub job that starts downloading a chapter async
	 * This allows the creation of multiple jobs
	 * This will respect the amount of threads running currently
	 */
	private fun launchDownload(): Job = launchIO {
		downloadsRepo.loadFirstDownload().handle { downloadEntity ->
			val extID = downloadEntity.extensionID

			//	notify("Pending", downloadEntity.chapterID + 100) { setNotOngoing()setSubText("Download")setContentTitle(downloadEntity.chapterName) }

			// This will loop until the downloadEntity status is DOWNLOADING
			while (downloadEntity.status != DownloadStatus.DOWNLOADING) {
				/*
				 * Will stop if download is paused.
				 * This is here in case the user presses
				 * the pause button while downloads are WAITING.
				 */
				if (isDownloadPaused()) {
					downloadsRepo.update(
						downloadEntity.copy(
							status = DownloadStatus.PENDING
						)
					)
					//		notify("Cancelled", downloadEntity.chapterID + 100) { setNotOngoing()setSubText("Download")setContentTitle(downloadEntity.chapterName) }
					return@launchIO
				}

				/*
				 * The code below prevents an extension from
				 * being overloaded with too many async connections.
				 *
				 * Checks if there is space for the extension to download from;
				 * If space is free, will start the extension download and break out of the loop
				 */
				if (activeExt(extID) <= getDownloadThreadsPerExtension()) {
					// There is a connection available, starting this task
					downloadEntity.status = DownloadStatus.DOWNLOADING
					downloadsRepo.update(downloadEntity)
					// Break out of the while
					break
				} else {
					/*
					 * If the status is pending, the downloadEntity will be set to "WAITING".
					 * This will tell the user that the download is waiting
					 * for others to not overload the site.
					 */
					if (downloadEntity.status == DownloadStatus.PENDING) {
						downloadEntity.status = DownloadStatus.WAITING
						downloadsRepo.update(downloadEntity)
						notify(downloadEntity)
					}
					// Continues the loop, letting the check repeat
				}
				// Prevent slowdowns to the application code by delaying each iteration by 100ms
				delay(100)
			}

			// Adds the job as working
			activeExtensions.add(extID)
			activeJobs++

			logV("Downloading $downloadEntity")
			notify(downloadEntity)

			download(downloadEntity).handle(
				onError = {
					downloadsRepo.update(
						downloadEntity.copy(
							status = DownloadStatus.ERROR
						).also {
							notify(it)
						}
					)
					launchUI {
						toast { it.message }
					}
				},
				onEmpty = {
					downloadsRepo.update(
						downloadEntity.copy(
							status = DownloadStatus.ERROR
						).also {
							notify(it)
						}
					)
					launchUI {
						toast { "Empty Error" }
					}
				},
				onLoading = {
					error("Impossible")
				}
			) {
				notify(downloadEntity, isComplete = true)
				downloadsRepo.deleteEntity(downloadEntity)
			}


			activeJobs-- // Drops active job count once completed task
			activeExtensions.remove(downloadEntity.extensionID)
		}
	}

	override suspend fun doWork(): Result {
		logI("Starting loop")
		if (isDownloadPaused())
			logI("Loop Paused")
		else {
			// Notifies that application is downloading chapters
			notify("Downloading chapters") {
				setOngoing()
			}

			// Will not run if there are no downloads to complete or if the download is paused
			while (getDownloadCount() >= 1 && !isDownloadPaused()) {
				/*
				* Launches a job as long as there are threads to download via.
				* Otherwise will continue, and the while loop will keep repeating until
				* there is space to launch another thread for downloading.
				* */
				if (activeJobs <= getDownloadThreads())
					launchDownload()
			}

			// Wait untill there are no more jobs
			while (activeJobs < 0)
				delay(100)

			// Downloads the chapters
			notify("Completed") {
				setNotOngoing()
			}
		}
		logI("Completed download loop")
		return Result.success()
	}

	/**
	 * Manager of [DownloadWorker]
	 */
	class Manager(context: Context) : CoroutineWorkerManager(context) {
		private val iSettingsRepository by instance<ISettingsRepository>()

		private suspend fun downloadOnMetered(): Boolean =
			iSettingsRepository.getBooleanOrDefault(DownloadOnMeteredConnection)

		private suspend fun downloadOnLowStorage(): Boolean =
			iSettingsRepository.getBooleanOrDefault(DownloadOnLowStorage)

		private suspend fun downloadOnLowBattery(): Boolean =
			iSettingsRepository.getBooleanOrDefault(DownloadOnLowBattery)

		private suspend fun downloadOnlyIdle(): Boolean =
			iSettingsRepository.getBooleanOrDefault(DownloadOnlyWhenIdle)

		override fun getWorkerState(index: Int) =
			workerManager.getWorkInfosForUniqueWork(DOWNLOAD_WORK_ID).get()[index].state

		override val count: Int
			get() = workerManager.getWorkInfosForUniqueWork(DOWNLOAD_WORK_ID).get().size

		/**
		 * Returns the status of the service.
		 *
		 * @return true if the service is running, false otherwise.
		 */
		override fun isRunning(): Boolean = try {
			getWorkerState() == WorkInfo.State.RUNNING
		} catch (e: Exception) {
			false
		}

		/**
		 * Starts the service. It will be started only if there isn't another instance already
		 * running.
		 */
		override fun start(data: Data) {
			launchIO {
				workerManager.enqueueUniqueWork(
					DOWNLOAD_WORK_ID,
					ExistingWorkPolicy.REPLACE,
					OneTimeWorkRequestBuilder<DownloadWorker>()
						.setConstraints(Constraints.Builder().apply {
							setRequiredNetworkType(
								if (downloadOnMetered()) {
									CONNECTED
								} else UNMETERED
							)
							setRequiresStorageNotLow(!downloadOnLowStorage())
							setRequiresBatteryNotLow(!downloadOnLowBattery())
							if (SDK_INT >= VERSION_CODES.M)
								setRequiresDeviceIdle(downloadOnlyIdle())
						}.build())
						.build()
				)
			}
		}

		/**
		 * Stops the service.
		 */
		override fun stop(): Operation = workerManager.cancelUniqueWork(DOWNLOAD_WORK_ID)
	}
}