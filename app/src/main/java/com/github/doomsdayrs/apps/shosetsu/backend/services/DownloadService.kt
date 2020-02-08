package com.github.doomsdayrs.apps.shosetsu.backend.services

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.downloads.adapters.DownloadAdapter
import com.github.doomsdayrs.apps.shosetsu.variables.*
import com.github.doomsdayrs.apps.shosetsu.variables.Notifications.CHANNEL_DOWNLOAD
import com.github.doomsdayrs.apps.shosetsu.variables.Notifications.ID_CHAPTER_DOWNLOAD
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

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
        private const val LOG_NAME = "DownloadService";


        /**
         * Returns the status of the service.
         *
         * @param context the application context.
         * @return true if the service is running, false otherwise.
         */
        fun isRunning(context: Context): Boolean {
            return context.isServiceRunning(DownloadService::class.java)
        }

        /**
         * Starts the service. It will be started only if there isn't another instance already
         * running.
         *
         * @param context the application context.
         * @param category a specific category to update, or null for global update.
         * @param target defines what should be updated.
         */
        fun start(context: Context) {
            if (!isRunning(context)) {
                context.toast(R.string.update)
                val intent = Intent(context, UpdateService::class.java)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    context.startService(intent)
                } else {
                    context.startForegroundService(intent)
                }
            }
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

    private var job: CoroutineContext? = null


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
        job = GlobalScope.launch {
            downloadLoop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun refreshList(adapter: DownloadAdapter?) {
        adapter?.downloadsFragment?.activity?.runOnUiThread { adapter.notifyDataSetChanged() }
    }

    private fun removeDownloads(adapter: DownloadAdapter?, downloadItem: DownloadItem) {
        if (adapter != null)
            for (x in adapter.downloadsFragment.downloadItems.indices) if (adapter.downloadsFragment.downloadItems[x].chapterURL == downloadItem.chapterURL) {
                adapter.downloadsFragment.downloadItems.removeAt(x)
                return
            }
        refreshList(adapter)
    }

    private fun markError(adapter: DownloadAdapter?, d: DownloadItem) {
        if (adapter != null)
            for (downloadItem in adapter.downloadsFragment.downloadItems) if (downloadItem.chapterURL == d.chapterURL) d.status = "Error"
        refreshList(adapter)
    }

    private fun toggleProcess(adapter: DownloadAdapter?, d: DownloadItem) {
        if (adapter != null)
            for (downloadItem in adapter.downloadsFragment.downloadItems) if (downloadItem.chapterURL == d.chapterURL) if (downloadItem.status == "Pending" || downloadItem.status == "Error") downloadItem.status = "Downloading" else downloadItem.status = "Pending"
        refreshList(adapter)
    }

    /**
     * Download loop controller
     * TODO Skip over paused chapters or move them to the bottom of the list
     */
    private fun downloadLoop() {
        Log.i(LOG_NAME,"Starting loop")
        while (Database.DatabaseDownloads.getDownloadCount() >= 1 && !Settings.downloadPaused) {
            val activity: Activity? = application.applicationContext as Activity
            Database.DatabaseDownloads.getFirstDownload()?.let { downloadItem ->
                val pr = progressNotification

                pr.setContentText(downloadItem.chapterName)

                pr.setProgress(6, 0, false)
                notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())

                toggleProcess(activity?.findViewById<RecyclerView>(R.id.fragment_downloads_recycler)?.adapter as DownloadAdapter?, downloadItem)

                try {
                    run {
                        pr.setProgress(6, 1, false)
                        notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())

                        Log.d(LOG_NAME, Utilities.shoDir + "download/")
                        val folder = File(Utilities.shoDir + "/download/" + downloadItem.formatter.formatterID + "/" + downloadItem.novelName.clean())
                        Log.d(LOG_NAME, folder.toString())
                        if (!folder.exists()) if (!folder.mkdirs()) throw IOException("Failed to mkdirs")

                        pr.setProgress(6, 2, false)
                        notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())

                        val formattedName = (downloadItem.chapterName).clean()
                        val passage = downloadItem.formatter.getPassage(downloadItem.chapterURL!!)

                        pr.setProgress(6, 3, false)
                        notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())

                        val fileOutputStream = FileOutputStream(folder.path + "/" + formattedName + ".txt")
                        fileOutputStream.write(passage.toByteArray())
                        fileOutputStream.close()
                        Database.DatabaseChapter.addSavedPath(downloadItem.chapterURL, folder.path + "/" + formattedName + ".txt")

                        pr.setProgress(6, 4, false)
                        notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())

                        if (activity != null) {
                            val recyclerView: RecyclerView? = activity.findViewById(R.id.fragment_novel_chapters_recycler)
                            recyclerView?.post { if (recyclerView.adapter != null) recyclerView.adapter!!.notifyDataSetChanged() }
                        }
                        Log.d(LOG_NAME, "Downloaded:" + downloadItem.novelName + " " + formattedName)
                    }
                    pr.setProgress(6, 5, false)
                    notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())

                    // Clean up
                    Database.DatabaseDownloads.removeDownload(downloadItem)
                    toggleProcess(activity?.findViewById<RecyclerView>(R.id.fragment_downloads_recycler)?.adapter as DownloadAdapter?, downloadItem)
                    removeDownloads(activity?.findViewById<RecyclerView>(R.id.fragment_downloads_recycler)?.adapter as DownloadAdapter?, downloadItem)

                    pr.setProgress(6, 6, false)
                    notificationManager.notify(ID_CHAPTER_DOWNLOAD, pr.build())
                    // Rate limiting
                    try {
                        TimeUnit.MILLISECONDS.sleep(10)
                    } catch (e: InterruptedException) {
                        Log.e(LOG_NAME, "Failed to wait", e)
                    }
                } catch (e: Exception) { // Mark download as faulted
                    Log.e(LOG_NAME, "A critical error occurred", e)
                    markError(activity?.findViewById<RecyclerView>(R.id.fragment_downloads_recycler)?.adapter as DownloadAdapter?, downloadItem)
                }
            }
            progressNotification.setOngoing(false)
            progressNotification.setContentText(getString(R.string.completed))
            notificationManager.notify(ID_CHAPTER_DOWNLOAD, progressNotification.build())
        }
    }

}