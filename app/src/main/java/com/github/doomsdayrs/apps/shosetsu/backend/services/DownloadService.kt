package com.github.doomsdayrs.apps.shosetsu.backend.services

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.content.getSystemService
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.backend.shoDir
import com.github.doomsdayrs.apps.shosetsu.common.consts.LogConstants.SERVICE_CANCEL_PREVIOUS
import com.github.doomsdayrs.apps.shosetsu.common.consts.LogConstants.SERVICE_EXECUTE
import com.github.doomsdayrs.apps.shosetsu.common.consts.LogConstants.SERVICE_NEW
import com.github.doomsdayrs.apps.shosetsu.common.consts.LogConstants.SERVICE_NULLIFIED
import com.github.doomsdayrs.apps.shosetsu.common.consts.LogConstants.SERVICE_REJECT_RUNNING
import com.github.doomsdayrs.apps.shosetsu.common.consts.Notifications.CHANNEL_DOWNLOAD
import com.github.doomsdayrs.apps.shosetsu.common.consts.Notifications.ID_CHAPTER_DOWNLOAD
import com.github.doomsdayrs.apps.shosetsu.common.ext.isServiceRunning
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchAsync
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.common.ext.wait
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.DownloadEntity
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IChaptersRepository
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IDownloadsRepository
import needle.CancelableTask
import needle.Needle
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.io.File
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
 * ====================================================================
 */

/**
 * shosetsu
 * 08 / 02 / 2020
 *
 * @author github.com/doomsdayrs
 */
class DownloadService : Service(), KodeinAware {
	companion object {
		private const val MAX_CHAPTER_DOWNLOAD_PROGRESS = 6

		/**
		 * Returns the status of the service.
		 *
		 * @param context the application context.
		 * @return true if the service is running, false otherwise.
		 */
		private fun isRunning(context: Context): Boolean {
			return context.isServiceRunning(DownloadService::class.java)
		}

		/**
		 * Starts the service. It will be started only if there isn't another instance already
		 * running.
		 *
		 * @param context the application context.
		 */
		fun start(context: Context) {
			if (!isRunning(context)) {
				val intent = Intent(context, DownloadService::class.java)
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
					context.startService(intent)
				} else {
					context.startForegroundService(intent)
				}
			} else Log.d(logID(), SERVICE_REJECT_RUNNING)
		}

		/**
		 * Stops the service.
		 *
		 * @param context the application context.
		 */
		fun stop(context: Context) {
			context.stopService(Intent(context, DownloadService::class.java))
		}

		/**
		 * Makes a download path for a downloadEntity
		 */
		fun makeDownloadPath(downloadEntity: DownloadEntity): String = with(downloadEntity) {
			"${shoDir}/download/${formatterID}/${novelID}"
		}
	}

	internal val notificationManager: NotificationManager by lazy { getSystemService()!! }


	internal val progressNotification by lazy {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			Notification.Builder(this, CHANNEL_DOWNLOAD)
		} else {
			// Suppressed due to lower API
			@Suppress("DEPRECATION")
			Notification.Builder(this)
		}
				.setSmallIcon(R.drawable.ic_file_download)
				.setContentTitle(getString(R.string.app_name))
				.setContentText("Downloading Chapters")
				.setOnlyAlertOnce(true)
	}

	override val kodein: Kodein by closestKodein()
	internal val downloadsRepo by kodein.instance<IDownloadsRepository>()
	internal val chaptersRepo by kodein.instance<IChaptersRepository>()

	private var job: Job? = null

	override fun onDestroy() {
		job?.cancel()
		super.onDestroy()
	}

	override fun onCreate() {
		startForeground(ID_CHAPTER_DOWNLOAD, progressNotification.build())
		super.onCreate()
	}

	override fun onBind(intent: Intent?): IBinder? {
		return null
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		Log.d(logID(), SERVICE_CANCEL_PREVIOUS)
		job?.cancel()
		Log.d(logID(), SERVICE_NEW)
		job = Job((applicationContext as KodeinAware).kodein)
		Log.d(logID(), SERVICE_EXECUTE)
		job?.let { Needle.onBackgroundThread().execute(it) } ?: Log.e(logID(), SERVICE_NULLIFIED)
		return super.onStartCommand(intent, flags, startId)
	}

	/**
	 * Job of this class
	 */
	inner class Job(override val kodein: Kodein) : CancelableTask(), KodeinAware {
		/**
		 * Download loop controller
		 * TODO Skip over paused chapters or move them to the bottom of the list
		 */
		override fun doWork() {
			downloadsRepo.resetList()
			Log.i(logID(), "Starting loop")
			while (downloadsRepo.loadDownloadCount() >= 1 && !Settings.isDownloadPaused)
				downloadsRepo.loadFirstDownload().let { downloadEntity: DownloadEntity ->
					val pr = progressNotification
					pr.setOngoing(true)
					pr.setContentText(downloadEntity.chapterName)
					pr.setProgress(MAX_CHAPTER_DOWNLOAD_PROGRESS, 0, false)
					notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())

					try {
						pr.setProgress(MAX_CHAPTER_DOWNLOAD_PROGRESS, 1, false)
						notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())

						val folder = File(makeDownloadPath(downloadEntity))

						//Log.d(logID(), folder.toString())
						if (!folder.exists()) if (!folder.mkdirs())
							throw IOException("Failed to mkdirs")

						pr.setProgress(MAX_CHAPTER_DOWNLOAD_PROGRESS, 2, false)
						notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())

						val formattedName = (downloadEntity.chapterID)
						val passage = downloadEntity.formatterID.getPassage(downloadEntity.chapterURL)

						pr.setProgress(MAX_CHAPTER_DOWNLOAD_PROGRESS, 3, false)
						notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())

						File("${folder.path}/$formattedName.txt").writeText(passage)

						chaptersRepo.addSavePath(
								downloadEntity.chapterID,
								"${folder.path}/$formattedName.txt"
						)

						pr.setProgress(MAX_CHAPTER_DOWNLOAD_PROGRESS, 4, false)
						notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())


						Log.d(logID(), "Downloaded: ${downloadEntity.chapterID} of ${downloadEntity.novelName}")

						pr.setProgress(MAX_CHAPTER_DOWNLOAD_PROGRESS, 5, false)
						notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())

						// Clean up
						launchAsync {
							downloadsRepo.suspendedDelete(downloadEntity)
						}

						pr.setProgress(MAX_CHAPTER_DOWNLOAD_PROGRESS, 6, false)
						notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())
						// Rate limiting
						wait(10)
					} catch (e: Exception) { // Mark download as faulted
						Log.e(logID(), "A critical error occurred", e)
						downloadEntity.status = -1
						downloadsRepo.blockingUpdate(downloadEntity)
					}
				}

			if (Settings.isDownloadPaused) Log.i(logID(), "Loop Paused")
			notificationManager.notify(ID_CHAPTER_DOWNLOAD,
					progressNotification.setOngoing(false).setProgress(
							0,
							0,
							false
					).setContentText(getString(R.string.completed)).build())
			Log.i(logID(), "Completed download loop")
			downloadsRepo.resetList()
			stop(this@DownloadService)
		}
	}
}