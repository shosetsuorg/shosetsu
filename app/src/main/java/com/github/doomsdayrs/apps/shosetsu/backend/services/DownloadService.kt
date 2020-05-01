package com.github.doomsdayrs.apps.shosetsu.backend.services

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.downloadsDao
import com.github.doomsdayrs.apps.shosetsu.common.ext.*
import com.github.doomsdayrs.apps.shosetsu.common.consts.Broadcasts.BC_DOWNLOADS_MARK_ERROR
import com.github.doomsdayrs.apps.shosetsu.common.consts.Broadcasts.BC_DOWNLOADS_RECEIVED_URL
import com.github.doomsdayrs.apps.shosetsu.common.consts.Broadcasts.BC_DOWNLOADS_REMOVE
import com.github.doomsdayrs.apps.shosetsu.common.consts.Broadcasts.BC_DOWNLOADS_TOGGLE
import com.github.doomsdayrs.apps.shosetsu.common.consts.Broadcasts.BC_NOTIFY_DATA_CHANGE
import com.github.doomsdayrs.apps.shosetsu.common.consts.Notifications.CHANNEL_DOWNLOAD
import com.github.doomsdayrs.apps.shosetsu.common.consts.Notifications.ID_CHAPTER_DOWNLOAD
import needle.CancelableTask
import needle.Needle
import java.io.File
import java.io.FileOutputStream
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
class DownloadService : Service() {
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
			} else Log.d(logID(), "Can't start, is running")
		}

		/**
		 * Stops the service.
		 *
		 * @param context the application context.
		 */
		fun stop(context: Context) {
			context.stopService(Intent(context, DownloadService::class.java))
		}
	}

	private val notificationManager by lazy {
		(getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
	}

	private val progressNotification by lazy {
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
		Log.d(logID(), "Canceling previous task")
		job?.cancel()
		Log.d(logID(), "Making new job")
		job = Job(this)
		Log.d(logID(), "Executing job")
		job?.let { Needle.onBackgroundThread().execute(it) }
				?: Log.e(logID(), "Job nullified before could be started")
		return super.onStartCommand(intent, flags, startId)
	}

	internal class Job(private val service: DownloadService) : CancelableTask() {

		private fun sendMessage(action: String, data: Map<String, String?> = mapOf()) {
			val i = Intent()
			i.action = action

			for ((key, value) in data)
				i.putExtra(key, value)

			service.sendBroadcast(i)
		}

		/**
		 * Download loop controller
		 * TODO Skip over paused chapters or move them to the bottom of the list
		 */
		override fun doWork() {
			Log.i(logID(), "Starting loop")
			while (downloadsDao.loadDownloadCount() >= 1 && !Settings.isDownloadPaused)
				downloadsDao.loadFirstDownload().let { downloadItem ->
					val pr = service.progressNotification
					pr.setOngoing(true)
					pr.setContentText(downloadItem.chapterName)
					pr.setProgress(MAX_CHAPTER_DOWNLOAD_PROGRESS, 0, false)
					service.notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())

					sendMessage(BC_DOWNLOADS_TOGGLE, mapOf(Pair(BC_DOWNLOADS_RECEIVED_URL, downloadItem.chapterURL)))

					try {
						pr.setProgress(MAX_CHAPTER_DOWNLOAD_PROGRESS, 1, false)
						service.notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())

						val folder = File("${Utilities.shoDir}/download/${downloadItem.formatter.formatterID}/${downloadItem.novelName.clean()}")
						//Log.d(logID(), folder.toString())
						if (!folder.exists()) if (!folder.mkdirs())
							throw IOException("Failed to mkdirs")

						pr.setProgress(MAX_CHAPTER_DOWNLOAD_PROGRESS, 2, false)
						service.notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())

						val formattedName = (downloadItem.chapterName).clean()
						val passage = downloadItem.formatter.getPassage(downloadItem.chapterURL)

						pr.setProgress(MAX_CHAPTER_DOWNLOAD_PROGRESS, 3, false)
						service.notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())

						val fileOutputStream = FileOutputStream("${folder.path}/$formattedName.txt")
						fileOutputStream.write(passage.toByteArray())
						fileOutputStream.close()
						Database.DatabaseChapter.addSavedPath(downloadItem.chapterURL, "${folder.path}/$formattedName.txt")

						pr.setProgress(MAX_CHAPTER_DOWNLOAD_PROGRESS, 4, false)
						service.notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())

						sendMessage(BC_NOTIFY_DATA_CHANGE)

						Log.d(logID(), "Downloaded: ${downloadItem.chapterID} of ${downloadItem.novelName}")

						pr.setProgress(MAX_CHAPTER_DOWNLOAD_PROGRESS, 5, false)
						service.notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())

						// Clean up
						downloadsDao.deleteDownloadEntity(downloadItem)
						sendMessage(BC_DOWNLOADS_TOGGLE, mapOf(Pair(BC_DOWNLOADS_RECEIVED_URL, downloadItem.chapterURL)))
						sendMessage(BC_DOWNLOADS_REMOVE, mapOf(Pair(BC_DOWNLOADS_RECEIVED_URL, downloadItem.chapterURL)))

						pr.setProgress(MAX_CHAPTER_DOWNLOAD_PROGRESS, 6, false)
						service.notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())
						// Rate limiting
						Utilities.wait(10)
					} catch (e: Exception) { // Mark download as faulted
						Log.e(logID(), "A critical error occurred", e)
						sendMessage(BC_DOWNLOADS_MARK_ERROR, mapOf(Pair(BC_DOWNLOADS_RECEIVED_URL, downloadItem.chapterURL)))
					} catch (e: IOException) {

					}
				}

			if (Settings.isDownloadPaused) Log.i(logID(), "Loop Paused")
			stop(service)
			service.notificationManager.notify(ID_CHAPTER_DOWNLOAD,
					service.progressNotification.setOngoing(false).setProgress(0, 0, false).setContentText(service.getString(R.string.completed)).build())
			Log.i(logID(), "Completed download loop")
		}
	}
}