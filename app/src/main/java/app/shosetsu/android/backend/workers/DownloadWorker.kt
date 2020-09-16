package app.shosetsu.android.backend.workers

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
import app.shosetsu.android.common.ShosetsuSettings
import app.shosetsu.android.common.consts.ErrorKeys
import app.shosetsu.android.common.consts.Notifications.CHANNEL_DOWNLOAD
import app.shosetsu.android.common.consts.Notifications.ID_CHAPTER_DOWNLOAD
import app.shosetsu.android.common.consts.WorkerTags.DOWNLOAD_WORK_ID
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.common.ext.toast
import app.shosetsu.android.domain.model.local.ChapterEntity
import app.shosetsu.android.domain.model.local.DownloadEntity
import app.shosetsu.android.domain.repository.base.IChaptersRepository
import app.shosetsu.android.domain.repository.base.IDownloadsRepository
import app.shosetsu.android.domain.repository.base.IExtensionsRepository
import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.R
import okio.IOException
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.io.File
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
				.setSmallIcon(R.drawable.ic_file_download)
				.setContentTitle(applicationContext.getString(R.string.app_name))
				.setContentText("Downloading Chapters")
				.setOnlyAlertOnce(true)
	}
	override val kodein: Kodein by closestKodein(applicationContext)
	private val downloadsRepo by instance<IDownloadsRepository>()
	private val chapRepo by instance<IChaptersRepository>()
	private val extRepo by instance<IExtensionsRepository>()
	private val settings by instance<ShosetsuSettings>()
	private suspend fun getDownloadCount(): Int =
			downloadsRepo.loadDownloadCount().let { if (it is HResult.Success) it.data else -1 }

	private suspend fun download(downloadEntity: DownloadEntity): HResult<*> =
			chapRepo.loadChapter(downloadEntity.chapterID).let { cR: HResult<ChapterEntity> ->
				when (cR) {
					is HResult.Success -> {
						val chapterEntity = cR.data
						extRepo.loadFormatter(chapterEntity.formatterID).let { fR: HResult<Formatter> ->
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

	override suspend fun doWork(): Result {
		Log.i(logID(), "Starting loop")
		if (settings.isDownloadPaused)
			Log.i(logID(), "Loop Paused")
		else {
			val pr = progressNotification

			while (getDownloadCount() >= 1 && !settings.isDownloadPaused) {
				Log.d(logID(), "Loop")
				downloadsRepo.loadFirstDownload().let {
					if (it is HResult.Success) {
						val downloadEntity: DownloadEntity = it.data
						downloadEntity.status = 1
						downloadsRepo.update(downloadEntity)


						notificationManager.notify(ID_CHAPTER_DOWNLOAD,
								pr.setOngoing(true)
										.setContentText(downloadEntity.chapterName)
										.setProgress(MAX_CHAPTER_DOWNLOAD_PROGRESS, 0, false)
										.build()
						)

						val folder = File(makeDownloadPath(applicationContext, downloadEntity))
						if (!folder.exists()) if (!folder.mkdirs())
							throw IOException("Failed to mkdirs")


						notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr
								.setProgress(MAX_CHAPTER_DOWNLOAD_PROGRESS, 1, false)
								.build()
						)

						val downloadResult = download(downloadEntity)

						notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr
								.setProgress(MAX_CHAPTER_DOWNLOAD_PROGRESS, 2, false)
								.build()
						)

						when (downloadResult) {
							is HResult.Success -> {
								downloadEntity.status = 2
								downloadsRepo.update(downloadEntity)
								downloadsRepo.delete(downloadEntity)
								notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr
										.setProgress(MAX_CHAPTER_DOWNLOAD_PROGRESS, 3, false)
										.build()
								)
							}
							is HError -> {
								downloadEntity.status = -1
								downloadsRepo.update(downloadEntity)
								notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr
										.setProgress(MAX_CHAPTER_DOWNLOAD_PROGRESS, 3, false)
										.build()
								)

								app.shosetsu.android.common.ext.launchUI {
									toast { downloadResult.message }
								}
							}
							is HResult.Empty -> {
								app.shosetsu.android.common.ext.launchUI {
									toast { "Empty Error" }
								}
							}
							is HResult.Loading -> {
								Exception("Should not be loading")
							}
						}
					}
				}
			}
			notificationManager.notify(ID_CHAPTER_DOWNLOAD,
					pr.setOngoing(false).setProgress(
							0,
							0,
							false
					).setContentText(applicationContext.getString(R.string.completed)).build())
		}
		Log.i(logID(), "Completed download loop")
		return Result.success()
	}

	/**
	 * Manager of [DownloadWorker]
	 */
	class Manager(context: Context) : CoroutineWorkerManager(context) {
		private val settings by instance<ShosetsuSettings>()

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
			workerManager.enqueueUniqueWork(
					DOWNLOAD_WORK_ID,
					ExistingWorkPolicy.REPLACE,
					OneTimeWorkRequestBuilder<DownloadWorker>()
							.setConstraints(Constraints.Builder().apply {
								setRequiredNetworkType(
										if (settings.downloadOnMetered) {
											CONNECTED
										} else UNMETERED
								)
								setRequiresStorageNotLow(!settings.downloadOnLowStorage)
								setRequiresBatteryNotLow(!settings.downloadOnLowBattery)
								if (SDK_INT >= VERSION_CODES.M)
									setRequiresDeviceIdle(settings.downloadOnlyIdle)
							}.build())
							.build()
			)
		}

		/**
		 * Stops the service.
		 */
		override fun stop(): Operation = workerManager.cancelUniqueWork(DOWNLOAD_WORK_ID)
	}

	/**
	 * Makes a download path for a downloadEntity
	 */
	fun makeDownloadPath(context: Context, downloadEntity: DownloadEntity): String = with(downloadEntity) {
		"${context.filesDir}/download/${formatterID}/${novelID}"
	}

	companion object {
		private const val MAX_CHAPTER_DOWNLOAD_PROGRESS = 3
	}
}