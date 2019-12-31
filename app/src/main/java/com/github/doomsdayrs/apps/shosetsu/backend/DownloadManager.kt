package com.github.doomsdayrs.apps.shosetsu.backend

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.async.DownloadTask
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem
import needle.Needle
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 * Shosetsu
 * 16 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
object DownloadManager {

    //   private var download: Downloading = Downloading(null)
    private var downloadTask: DownloadTask = DownloadTask(null)


    /**
     * Initializes download manager
     */
    @JvmStatic
    fun initDownloadManager(activity: Activity) {
        if (downloadTask.isCanceled) downloadTask = DownloadTask(activity)
        if (Database.DatabaseDownloads.getDownloadCount() >= 1)
            Needle.onBackgroundThread().execute(downloadTask)
        //if (downloadTask.status == AsyncTask.Status.FINISHED || download.status == AsyncTask.Status.PENDING) download.execute()
    }

    /**
     * Adds to download list
     *
     * @param downloadItem download item to add
     */
    @JvmStatic
    fun addToDownload(activity: Activity?, downloadItem: DownloadItem) {
        if (!Database.DatabaseDownloads.inDownloads(downloadItem)) {
            Database.DatabaseDownloads.addToDownloads(downloadItem)
            if (downloadTask.isCanceled && Database.DatabaseDownloads.getDownloadCount() >= 1) {
                downloadTask = DownloadTask(activity)
                Needle.onBackgroundThread().execute(downloadTask)
            }
        }
    }

    /**
     * delete downloaded chapter
     *
     * @param context      context to work with
     * @param downloadItem download item to remove
     * @return if downloaded
     */
    @JvmStatic
    fun delete(context: Context?, downloadItem: DownloadItem): Boolean {
        Log.d("DeletingChapter", downloadItem.toString())
        val file = File(Utilities.shoDir + "/download/" + downloadItem.formatter.formatterID + "/" + downloadItem.novelName + "/" + downloadItem.chapterName + ".txt")
        Database.DatabaseChapter.removePath(downloadItem.chapterID)
        if (file.exists()) if (!file.delete()) if (context != null) {
            Toast.makeText(context, context.getString(R.string.download_fail_delete), Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    /**
     * Get saved text
     *
     * @param path path of saved chapter
     * @return Passage of saved chapter
     */
    @JvmStatic
    fun getText(path: String): String? {
        try {
            BufferedReader(FileReader(path)).use { br ->
                val sb = StringBuilder()
                var line = br.readLine()
                while (line != null) {
                    sb.append(line)
                    sb.append(System.lineSeparator())
                    line = br.readLine()
                }
                return sb.toString()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }



    /**
     * Download loop controller
     * TODO Notification of download progress (( What is being downloaded
     * TODO Skip over paused chapters or move them to the bottom of the list
     */
}