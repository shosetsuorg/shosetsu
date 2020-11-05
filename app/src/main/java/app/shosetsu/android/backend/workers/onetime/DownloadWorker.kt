package app.shosetsu.android.backend.workers.onetime

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.util.Log
import androidx.core.content.getSystemService
import androidx.work.*
import androidx.work.NetworkType.CONNECTED
import androidx.work.NetworkType.UNMETERED
import app.shosetsu.android.backend.workers.CoroutineWorkerManager
import app.shosetsu.android.common.consts.ErrorKeys
import app.shosetsu.android.common.consts.Notifications.CHANNEL_DOWNLOAD
import app.shosetsu.android.common.consts.Notifications.ID_CHAPTER_DOWNLOAD
import app.shosetsu.android.common.consts.WorkerTags.DOWNLOAD_WORK_ID
import app.shosetsu.android.common.consts.settings.SettingKey.*
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.handle
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.common.enums.DownloadStatus
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.model.local.ChapterEntity
import app.shosetsu.android.domain.model.local.DownloadEntity
import app.shosetsu.android.domain.repository.base.IChaptersRepository
import app.shosetsu.android.domain.repository.base.IDownloadsRepository
import app.shosetsu.android.domain.repository.base.IExtensionsRepository
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.lib.IExtension
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.coroutines.delay
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import app.shosetsu.android.common.dto.HResult.Error as HError

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
) : CoroutineWorker(appContext, params), KodeinAware {

	private val notificationManager by lazy {
		applicationContext.getSystemService<NotificationManager>()!!
	}
	private val progressNotification by lazy {
		if (SDK_INT >= VERSION_CODES.O) {
			Notification.Builder(applicationContext, CHANNEL_DOWNLOAD)
		} else {
			// Suppressed due to lower API
			@Suppress("DEPRECATION")
			Notification.Builder(applicationContext)
		}
				.setSmallIcon(R.drawable.download)
				.setContentTitle(applicationContext.getString(R.string.app_name))
				.setContentText("Downloading Chapters")
				.setOngoing(true)
	}
	override val kodein: Kodein by closestKodein(applicationContext)
	private val downloadsRepo by instance<IDownloadsRepository>()
	private val chapRepo by instance<IChaptersRepository>()
	private val extRepo by instance<IExtensionsRepository>()
	private val settingRepo by instance<ISettingsRepository>()

	/** How many jobs are currently running */
	@get:Synchronized
	@set:Synchronized
	private var activeJobs = 0

	/** Which extensions are currently working */
	@get:Synchronized
	private val activeExtensions = ArrayList<Int>()

	/** Retrieves the setting for if the download system is paused or not */
	private suspend fun isDownloadPaused(): Boolean =
			settingRepo.getBoolean(IsDownloadPaused).let {
				if (it is HResult.Success)
					it.data
				else IsDownloadPaused.default
			}

	/** Retrieves the setting for simultaneous download threads allowed */
	private suspend fun getDownloadThreads(): Int =
			settingRepo.getInt(DownloadThreadPool).let {
				if (it is HResult.Success)
					it.data
				else DownloadThreadPool.default
			}

	/** Retrieves the setting for simultaneous download threads allowed per extension */
	private suspend fun getDownloadThreadsPerExtension(): Int =
			settingRepo.getInt(DownloadExtThreads).let {
				if (it is HResult.Success)
					it.data
				else DownloadExtThreads.default
			}

	/** Loads the download count that is present currently */
	private suspend fun getDownloadCount(): Int =
			downloadsRepo.loadDownloadCount().let { if (it is HResult.Success) it.data else -1 }


	private suspend fun download(downloadEntity: DownloadEntity): HResult<*> =
			chapRepo.loadChapter(downloadEntity.chapterID).let { cR: HResult<ChapterEntity> ->
				when (cR) {
					is HResult.Success -> {
						val chapterEntity = cR.data
						extRepo.loadIExtension(chapterEntity.formatterID).let { fR: HResult<IExtension> ->
							when (fR) {
								is HResult.Success -> {
									val formatterEntity = fR.data
									chapRepo.loadChapterPassage(formatterEntity, chapterEntity).let {
										when (it) {
											is HResult.Success -> {
												chapRepo.saveChapterPassageToStorage(chapterEntity, it.data)
												successResult("Chapter Loaded")
											}
											else -> it
										}
									}
								}
								else -> HError(ErrorKeys.ERROR_NOT_FOUND, "Formatter not found")
							}
						}
					}
					else -> HError(ErrorKeys.ERROR_NOT_FOUND, "Chapter Entity not found")
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

	private suspend fun launchDownload() {
		downloadsRepo.loadFirstDownload().handle { downloadEntity ->
			val extID = downloadEntity.extensionID

			// This will loop until the download status is DOWNLOADING
			while (downloadEntity.status != DownloadStatus.DOWNLOADING) {
				// Will stop if download is paused
				if (isDownloadPaused()) {
					downloadsRepo.update(downloadEntity.copy(
							status = DownloadStatus.PENDING
					))
					return
				}

				// Checks if there is space for the extension download
				// If space is free, will start the extension download and break out of the loop
				if (activeExt(extID) <= getDownloadThreadsPerExtension()) {
					downloadEntity.status = DownloadStatus.DOWNLOADING
					downloadsRepo.update(downloadEntity)
					break
				} else {
					// If the status is pending, it will now be waiting till the pool is open
					if (downloadEntity.status == DownloadStatus.PENDING) {
						downloadEntity.status = DownloadStatus.WAITING
						downloadsRepo.update(downloadEntity)
					}
				}
				delay(100)
			}

			// Adds the job as working
			activeExtensions.add(extID)
			activeJobs++

			logI("Downloading $downloadEntity")
			when (val downloadResult = download(downloadEntity)) {
				is HResult.Success -> downloadsRepo.deleteEntity(downloadEntity)
				is HError -> {
					downloadsRepo.update(downloadEntity.copy(
							status = DownloadStatus.ERROR
					))
					launchUI {
						toast { downloadResult.message }
					}
				}
				is HResult.Empty -> {
					downloadsRepo.update(downloadEntity.copy(
							status = DownloadStatus.ERROR
					))
					launchUI {
						toast { "Empty Error" }
					}
				}
				is HResult.Loading -> {
					throw Exception("Should not be loading")
				}
			}
			activeJobs-- // Drops active job count once completed task
			activeExtensions.remove(downloadEntity.extensionID)
		}
	}

	override suspend fun doWork(): Result {
		Log.i(logID(), "Starting loop")
		if (isDownloadPaused())
			Log.i(logID(), "Loop Paused")
		else {
			// Notifies the application is downloading chapters
			notificationManager.notify(ID_CHAPTER_DOWNLOAD, progressNotification.build())

			// Will not run if there are no downloads or if the download is paused
			while (getDownloadCount() >= 1 && !isDownloadPaused()) {
				// Launches a job as long as there are threads to download via
				val threadsAllowed = getDownloadThreads()
				if (activeJobs <= threadsAllowed) launchIO {
					launchDownload()
				}
			}

			// Downloads the chapters
			notificationManager.cancel(ID_CHAPTER_DOWNLOAD)
		}
		Log.i(logID(), "Completed download loop")
		return Result.success()
	}

	/**
	 * Manager of [DownloadWorker]
	 */
	class Manager(context: Context) : CoroutineWorkerManager(context) {
		private val iSettingsRepository by instance<ISettingsRepository>()

		private suspend fun downloadOnMetered(): Boolean =
				iSettingsRepository.getBoolean(DownloadOnMeteredConnection).let {
					if (it is HResult.Success)
						it.data
					else DownloadOnMeteredConnection.default
				}

		private suspend fun downloadOnLowStorage(): Boolean =
				iSettingsRepository.getBoolean(DownloadOnLowStorage).let {
					if (it is HResult.Success)
						it.data
					else DownloadOnLowStorage.default
				}

		private suspend fun downloadOnLowBattery(): Boolean =
				iSettingsRepository.getBoolean(DownloadOnLowBattery).let {
					if (it is HResult.Success)
						it.data
					else DownloadOnLowBattery.default
				}

		private suspend fun downloadOnlyIdle(): Boolean =
				iSettingsRepository.getBoolean(DownloadOnlyWhenIdle).let {
					if (it is HResult.Success)
						it.data
					else DownloadOnlyWhenIdle.default
				}

		/**
		 * Returns the status of the service.
		 *
		 * @param context the application context.
		 * @return true if the service is running, false otherwise.
		 */
		override fun isRunning(): Boolean = try {
			workerManager.getWorkInfosForUniqueWork(DOWNLOAD_WORK_ID)
					.get()[0].state == WorkInfo.State.RUNNING
		} catch (e: Exception) {
			false
		}

		/**
		 * Starts the service. It will be started only if there isn't another instance already
		 * running.
		 */
		override fun start() {
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
		private const val MAX_CHAPTER_DOWNLOAD_PROGRESS = 3
	}
}