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
import com.github.doomsdayrs.apps.shosetsu.variables.ext.clean
import com.github.doomsdayrs.apps.shosetsu.variables.ext.isServiceRunning
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Broadcasts
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Notifications.CHANNEL_DOWNLOAD
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Notifications.ID_CHAPTER_DOWNLOAD
import needle.CancelableTask
import needle.Needle
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

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
        private const val LOG_NAME = "DownloadService"


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
            } else Log.d(LOG_NAME, "Can't start, is running")
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
                .setOngoing(true)
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
        Log.d(LOG_NAME, "Canceling previous task")
        job?.cancel()
        Log.d(LOG_NAME, "Making new job")
        job = Job(this)
        Log.d(LOG_NAME, "Executing job")
        Needle.onBackgroundThread().execute(job)
        return super.onStartCommand(intent, flags, startId)
    }


    internal class Job(private val service: DownloadService) : CancelableTask() {

        private fun sendMessage(action: String, data: Map<String, String?> = mapOf()) {
            val i = Intent()
            i.action = action

            for (m in data)
                i.putExtra(m.key, m.value)

            service.sendBroadcast(i)
        }

        /**
         * Download loop controller
         * TODO Skip over paused chapters or move them to the bottom of the list
         */
        override fun doWork() {
            Log.i(LOG_NAME, "Starting loop")
            while (Database.DatabaseDownloads.downloadCount >= 1 && !Settings.downloadPaused) {
                Database.DatabaseDownloads.firstDownload?.let { downloadItem ->

                    val pr = service.progressNotification
                    pr.setContentText(downloadItem.chapterName)
                    pr.setProgress(6, 0, false)
                    service.notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())

                    sendMessage(Broadcasts.DOWNLOADS_TOGGLE, mapOf(Pair(Broadcasts.DOWNLOADS_RECEIVED_URL, downloadItem.chapterURL)))

                    try {
                        run {
                            pr.setProgress(6, 1, false)
                            service.notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())

                            Log.d(LOG_NAME, Utilities.shoDir + "download/")
                            val folder = File(Utilities.shoDir + "/download/" + downloadItem.formatter.formatterID + "/" + downloadItem.novelName.clean())
                            Log.d(LOG_NAME, folder.toString())
                            if (!folder.exists()) if (!folder.mkdirs()) throw IOException("Failed to mkdirs")

                            pr.setProgress(6, 2, false)
                            service.notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())

                            val formattedName = (downloadItem.chapterName).clean()
                            val passage = downloadItem.formatter.getPassage(downloadItem.chapterURL)

                            pr.setProgress(6, 3, false)
                            service.notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())

                            val fileOutputStream = FileOutputStream(folder.path + "/" + formattedName + ".txt")
                            fileOutputStream.write(passage.toByteArray())
                            fileOutputStream.close()
                            Database.DatabaseChapter.addSavedPath(downloadItem.chapterURL, folder.path + "/" + formattedName + ".txt")

                            pr.setProgress(6, 4, false)
                            service.notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())


                            sendMessage(Broadcasts.BROADCAST_NOTIFY_DATA_CHANGE)

                            Log.d(LOG_NAME, "Downloaded:" + downloadItem.novelName + " " + formattedName)
                        }
                        pr.setProgress(6, 5, false)
                        service.notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())

                        // Clean up
                        Database.DatabaseDownloads.removeDownload(downloadItem)

                        sendMessage(Broadcasts.DOWNLOADS_TOGGLE, mapOf(Pair(Broadcasts.DOWNLOADS_RECEIVED_URL, downloadItem.chapterURL)))
                        sendMessage(Broadcasts.DOWNLOADS_REMOVE, mapOf(Pair(Broadcasts.DOWNLOADS_RECEIVED_URL, downloadItem.chapterURL)))

                        pr.setProgress(6, 6, false)
                        service.notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())
                        // Rate limiting
                        try {
                            TimeUnit.MILLISECONDS.sleep(10)
                        } catch (e: InterruptedException) {
                            Log.e(LOG_NAME, "Failed to wait", e)
                        }
                    } catch (e: Exception) { // Mark download as faulted
                        Log.e(LOG_NAME, "A critical error occurred", e)
                        sendMessage(Broadcasts.DOWNLOADS_MARK_ERROR, mapOf(Pair(Broadcasts.DOWNLOADS_RECEIVED_URL, downloadItem.chapterURL)))

                    }
                }
                stop(service)
            }

            service.notificationManager.notify(ID_CHAPTER_DOWNLOAD,
                    service.progressNotification.setOngoing(false)
                            .setContentText(service.getString(R.string.completed))
                            .build())
            Log.i(LOG_NAME, "Completed download loop")
        }
    }
}