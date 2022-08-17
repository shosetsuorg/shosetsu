package app.shosetsu.android.backend.workers.onetime

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.EXTRA_NOTIFICATION_ID
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import androidx.work.NetworkType.CONNECTED
import androidx.work.NetworkType.UNMETERED
import app.shosetsu.android.R
import app.shosetsu.android.backend.receivers.NotificationBroadcastReceiver
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.backend.workers.NotificationCapable
import app.shosetsu.android.common.FileNotFoundException
import app.shosetsu.android.common.FilePermissionException
import app.shosetsu.android.common.SettingKey.*
import app.shosetsu.android.common.consts.Notifications.CHANNEL_DOWNLOAD
import app.shosetsu.android.common.consts.Notifications.ID_CHAPTER_DOWNLOAD
import app.shosetsu.android.common.consts.WorkerTags.DOWNLOAD_WORK_ID
import app.shosetsu.android.common.enums.DownloadStatus
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.model.local.DownloadEntity
import app.shosetsu.android.domain.repository.base.IChaptersRepository
import app.shosetsu.android.domain.repository.base.IDownloadsRepository
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.domain.usecases.get.GetExtensionUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import org.luaj.vm2.LuaError
import java.io.IOException

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

	private fun NotificationCompat.Builder.addCancelAction() {
		addAction(
			R.drawable.ic_baseline_cancel_24, getString(android.R.string.cancel),
			PendingIntent.getBroadcast(
				applicationContext,
				0,
				Intent(applicationContext, NotificationBroadcastReceiver::class.java).apply {
					action = ACTION_CANCEL_CHAPTER_DOWNLOAD
					putExtra(EXTRA_NOTIFICATION_ID, defaultNotificationID)
				},
				if (SDK_INT >= VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
			)
		)
	}

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
		settingRepo.getBoolean(IsDownloadPaused)

	/** Retrieves the setting for simultaneous download threads allowed */
	private suspend fun getDownloadThreads(): Int =
		settingRepo.getInt(DownloadThreadPool)

	/** Retrieves the setting for simultaneous download threads allowed per extension */
	private suspend fun getDownloadThreadsPerExtension(): Int =
		settingRepo.getInt(DownloadExtThreads)

	/** Retrieves the setting for simultaneous download threads allowed per extension */
	private suspend fun getNotifyIndividualChapters(): Boolean =
		settingRepo.getBoolean(DownloadNotifyChapters)

	/** Loads the download count that is present currently */
	@Throws(SQLiteException::class)
	private suspend fun getDownloadCount(): Int =
		downloadsRepo.loadDownloadCount()

	@Throws(
		IOException::class,
		SQLiteException::class,
		FilePermissionException::class,
		FileNotFoundException::class,
		LuaError::class
	)
	private suspend fun download(downloadEntity: DownloadEntity) =
		chapRepo.getChapter(downloadEntity.chapterID)!!.let { chapterEntity ->
			getExt(chapterEntity.extensionID)!!.let { iExtension ->
				chapRepo.getChapterPassage(iExtension, chapterEntity).let { passage ->
					chapRepo.saveChapterPassageToStorage(
						chapterEntity,
						iExtension.chapterType,
						passage
					)
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
		if (!getNotifyIndividualChapters()) return

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
			setSilent(true)
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
		downloadsRepo.loadFirstDownload()?.let { downloadEntity ->
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
			try {
				activeExtensions.add(extID)
			} catch (ignored: ArrayIndexOutOfBoundsException) {
				logE("Adding extID to active extensions failed, attempting again in 100ms")
				delay(100)
				try {
					activeExtensions.add(extID)
				} catch (ignored: ArrayIndexOutOfBoundsException) {
					logE("Failed to add job again, aborting")
					downloadsRepo.update(
						downloadEntity.copy(
							status = DownloadStatus.ERROR
						)
					)
					return@launchIO
				}
				logD("Added extID to active jobs successfully")
			}
			activeJobs++

			logV("Downloading $downloadEntity")
			notify(downloadEntity)

			try {
				download(downloadEntity)

				notify(downloadEntity, isComplete = true)
				downloadsRepo.deleteEntity(downloadEntity)
			} catch (e: Exception) {//TODO specify
				downloadsRepo.update(
					downloadEntity.copy(
						status = DownloadStatus.ERROR
					).also {
						notify(it)
					}
				)
				launchUI {
					toast { e.message ?: "Download error" }
				}
			} finally {
				delay(500) // Runtime delay
				activeJobs-- // Drops active job count once completed task
				activeExtensions.remove(downloadEntity.extensionID)
			}
		}
	}

	/**
	 * Delay maintainer. Handles a progressively increasing delay.
	 */
	class ProgressiveDelayer {
		private var count: Int = 0

		suspend fun delay() {
			count + 1
			delay(count * 100L)
		}

		fun reset() {
			count = 0
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
				addCancelAction()
			}

			/**
			 * Maintains delay between each app launch, ensuring there is breathing room before
			 * each call.
			 */
			val launcherDelayer = ProgressiveDelayer()

			// Will not run if there are no downloads to complete or if the download is paused
			while (getDownloadCount() >= 1 && !isDownloadPaused()) {
				/*
				* Launches a job as long as there are threads to download via.
				* Otherwise will continue, and the while loop will keep repeating until
				* there is space to launch another thread for downloading.
				* */
				if (activeJobs <= getDownloadThreads()) {
					launchDownload()
					launcherDelayer.reset() // Reset delay, starting the cycle over again
				}

				// Delay the process, progressively longer to lower system usage
				launcherDelayer.delay()
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
			iSettingsRepository.getBoolean(DownloadOnMeteredConnection)

		private suspend fun downloadOnLowStorage(): Boolean =
			iSettingsRepository.getBoolean(DownloadOnLowStorage)

		private suspend fun downloadOnLowBattery(): Boolean =
			iSettingsRepository.getBoolean(DownloadOnLowBattery)

		private suspend fun downloadOnlyIdle(): Boolean =
			iSettingsRepository.getBoolean(DownloadOnlyWhenIdle)

		override suspend fun getWorkerState(index: Int) =
			getWorkerInfoList()[index].state

		override suspend fun getWorkerInfoList(): List<WorkInfo> =
			workerManager.getWorkInfosForUniqueWork(DOWNLOAD_WORK_ID).await()

		override suspend fun getCount(): Int =
			getWorkerInfoList().size

		/**
		 * Returns the status of the service.
		 *
		 * @return true if the service is running, false otherwise.
		 */
		override suspend fun isRunning(): Boolean = try {
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

	companion object {
		const val ACTION_CANCEL_CHAPTER_DOWNLOAD = "shosetsu_action_cancel_chapter_download"
	}
}