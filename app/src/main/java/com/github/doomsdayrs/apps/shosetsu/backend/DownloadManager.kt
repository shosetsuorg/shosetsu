package com.github.doomsdayrs.apps.shosetsu.backend

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.scraper.WebViewScrapper
import com.github.doomsdayrs.apps.shosetsu.ui.downloads.adapters.DownloadAdapter
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem
import com.github.doomsdayrs.apps.shosetsu.variables.Settings
import needle.CancelableTask
import needle.Needle
import java.io.*
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

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

    internal class DownloadTask(val activity: Activity?) : CancelableTask() {
        init {
            if (activity == null) cancel()
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

        override fun doWork() {
            while (Database.DatabaseDownloads.getDownloadCount() >= 1 && !Settings.downloadPaused) {
                val downloadItem = Database.DatabaseDownloads.getFirstDownload()

                // Starts up
                if (downloadItem != null) {
                    toggleProcess(activity?.findViewById<RecyclerView>(R.id.fragment_downloads_recycler)?.adapter as DownloadAdapter?, downloadItem)
                }
                if (downloadItem != null) try {
                    run {
                        Log.d("DownloadManager", Utilities.shoDir + "download/")
                        val folder = File(Utilities.shoDir + "/download/" + downloadItem.formatter.formatterID + "/" + Utilities.cleanString(downloadItem.novelName))
                        Log.d("DownloadManager", folder.toString())
                        if (!folder.exists()) if (!folder.mkdirs()) {
                            throw IOException("Failed to mkdirs")
                        }
                        val formattedName = Utilities.cleanString(downloadItem.chapterName)
                        val passage = downloadItem.formatter.getNovelPassage(WebViewScrapper.docFromURL(downloadItem.chapterURL, downloadItem.formatter.hasCloudFlare)!!)
                        val fileOutputStream = FileOutputStream(
                                folder.path + "/" + formattedName + ".txt"
                        )
                        fileOutputStream.write(passage.toByteArray())
                        fileOutputStream.close()
                        Database.DatabaseChapter.addSavedPath(downloadItem.chapterURL, folder.path + "/" + formattedName + ".txt")

                        if (activity != null) {
                            val recyclerView: RecyclerView? = activity.findViewById(R.id.fragment_novel_chapters_recycler)
                            recyclerView?.post { if (recyclerView.adapter != null) recyclerView.adapter!!.notifyDataSetChanged() }
                        }
                        Log.d("DownloadManager", "Downloaded:" + downloadItem.novelName + " " + formattedName)
                    }
                    // Clean up
                    Database.DatabaseDownloads.removeDownload(downloadItem)
                    toggleProcess(activity?.findViewById<RecyclerView>(R.id.fragment_downloads_recycler)?.adapter as DownloadAdapter?, downloadItem)
                    removeDownloads(activity?.findViewById<RecyclerView>(R.id.fragment_downloads_recycler)?.adapter as DownloadAdapter?, downloadItem)
                    // Rate limiting
                    try {
                        TimeUnit.MILLISECONDS.sleep(10)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                } catch (e: SocketTimeoutException) { // Mark download as faulted
                    markError(activity?.findViewById<RecyclerView>(R.id.fragment_downloads_recycler)?.adapter as DownloadAdapter?, downloadItem)
                } catch (e: IOException) {
                    e.printStackTrace()
                    exitProcess(1)
                }
            }
            cancel()
        }

    }


    /**
     * Download loop controller
     * TODO Notification of download progress (( What is being downloaded
     * TODO Skip over paused chapters or move them to the bottom of the list
     */
}