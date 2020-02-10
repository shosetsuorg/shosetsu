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
import com.github.doomsdayrs.api.shosetsu.services.core.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.DownloadManager
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.async.ChapterLoader
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem
import com.github.doomsdayrs.apps.shosetsu.variables.ext.isServiceRunning
import com.github.doomsdayrs.apps.shosetsu.variables.ext.toast
import com.github.doomsdayrs.apps.shosetsu.variables.obj.DefaultScrapers
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Notifications.CHANNEL_UPDATE
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Notifications.ID_CHAPTER_UPDATE
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard
import needle.CancelableTask
import needle.Needle
import java.security.InvalidKeyException

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
 * 07 / 02 / 2020
 *
 * @author github.com/doomsdayrs
 * <p>
 *     Handles update requests for the entire application
 * </p>
 */
class UpdateService : Service() {
    companion object {
        const val KEY_TARGET = "Target"
        const val KEY_CHAPTERS = "Novels"

        const val KEY_NOVELS = 0x00
        const val KEY_CATEGORY = 0x01


        /**
         * Returns the status of the service.
         *
         * @param context the application context.
         * @return true if the service is running, false otherwise.
         */
        private fun isRunning(context: Context): Boolean {
            return context.isServiceRunning(UpdateService::class.java)
        }

        /**
         * Starts the service. It will be started only if there isn't another instance already
         * running.
         *
         * @param context the application context.
         * @param category a specific category to update, or null for global update.
         */
        fun start(context: Context, category: Int, novelIDs: ArrayList<Int>) {
            if (!isRunning(context)) {
                context.toast(R.string.library_update)
                val intent = Intent(context, UpdateService::class.java)
                intent.putExtra(KEY_TARGET, category)
                intent.putIntegerArrayListExtra(KEY_CHAPTERS, novelIDs)
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
            context.stopService(Intent(context, UpdateService::class.java))
        }
    }

    /**
     * Wake lock that will be held until the service is destroyed.
     */
    //  private lateinit var wakeLock: PowerManager.WakeLock

    private val notificationManager by lazy {
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
    }

    private val progressNotification by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_UPDATE)
        } else {
            // Suppressed due to lower API
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }
                .setSmallIcon(R.drawable.ic_system_update_alt_black_24dp)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Update in progress")
                .setOngoing(true)
                .setOnlyAlertOnce(true)
    }

    private var job: CancelableTask? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(ID_CHAPTER_UPDATE, progressNotification.build())
        //   wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LibraryUpdateService:WakeLock")
        //       wakeLock.acquire(60 * 60 * 1000L /*10 minutes*/)
    }

    override fun onDestroy() {
        job?.cancel()
        //     if (wakeLock.isHeld) wakeLock.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        job?.cancel()
        job = when (intent?.getIntExtra(KEY_TARGET, KEY_NOVELS) ?: KEY_NOVELS) {
            KEY_NOVELS ->
                UpdateManga(this, intent
                        ?: Intent().putIntegerArrayListExtra(KEY_CHAPTERS, Database.DatabaseNovels.intLibrary))

            KEY_CATEGORY ->
                UpdateCategory()

            else -> throw InvalidKeyException("How did you reach this point")
        }
        Needle.onBackgroundThread().execute(job)
        return super.onStartCommand(intent, flags, startId)
    }

    internal class UpdateCategory : CancelableTask() {
        override fun doWork() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    internal class UpdateManga(private val updateService: UpdateService, val intent: Intent) : CancelableTask() {
        override fun doWork() {
            val updatedNovels = ArrayList<NovelCard>()
            intent.getIntegerArrayListExtra(KEY_CHAPTERS)?.let { novelCards ->
                // Main process
                for (x in novelCards.indices) {

                    val novelCard = Database.DatabaseNovels.getNovel(novelCards[x])
                    val novelID = Database.DatabaseIdentification.getNovelIDFromNovelURL(novelCard.novelURL)
                    val formatter = DefaultScrapers.getByID(novelCard.formatterID)

                    if (formatter != DefaultScrapers.unknown) {
                        // Updates notification
                        updateService.progressNotification.setContentText(novelCard.title)
                        updateService.progressNotification.setProgress(novelCards.size, x + 1, false)
                        updateService.notificationManager.notify(ID_CHAPTER_UPDATE, updateService.progressNotification.build())

                        // Runs process
                        ChapterLoader(object : ChapterLoader.ChapterLoaderAction {
                            override fun onPreExecute() {
                            }

                            override fun onPostExecute(result: Boolean, finalChapters: ArrayList<Novel.Chapter>) {
                            }

                            override fun onJustBeforePost(finalChapters: ArrayList<Novel.Chapter>) {
                                for ((mangaCount, chapter) in finalChapters.withIndex()) {
                                    add(updatedNovels, mangaCount, novelID, chapter, novelCard)
                                }
                            }

                            override fun onIncrementingProgress(page: Int, max: Int) {
                            }

                            override fun errorReceived(errorString: String) {
                                Log.e("ChapterUpdater", errorString)
                            }
                        }, formatter, novelCard.novelURL).doInBackground()
                        Utilities.wait(1000)
                    } else {
                        updateService.progressNotification.setProgress(novelCards.size, x + 1, false)
                        updateService.notificationManager.notify(ID_CHAPTER_UPDATE, updateService.progressNotification.build())
                    }
                }
            }
            // Completion
            val stringBuilder = StringBuilder()
            when {
                updatedNovels.size > 0 -> {
                    updateService.progressNotification.setContentTitle(updateService.getString(R.string.update_complete))
                    for (novelCard in updatedNovels) stringBuilder.append(novelCard.title).append("\n")
                    updateService.progressNotification.style = Notification.BigTextStyle()
                }
                else -> stringBuilder.append(updateService.getString(R.string.update_not_found))
            }
            updateService.progressNotification.setContentText(stringBuilder.toString())
            updateService.progressNotification.setProgress(0, 0, false)
            updateService.progressNotification.setOngoing(false)
            updateService.notificationManager.notify(ID_CHAPTER_UPDATE, updateService.progressNotification.build())
        }

        private fun add(updatedNovels: ArrayList<NovelCard>, mangaCount: Int, novelID: Int, novelChapter: Novel.Chapter, novelCard: NovelCard) {
            if (Database.DatabaseChapter.isNotInChapters(novelChapter.link)) {
                Log.i("ChaperUpdater", "add #$mangaCount\t: ${novelChapter.link} ")
                Database.DatabaseChapter.addToChapters(novelID, novelChapter)
                Database.DatabaseUpdates.addToUpdates(novelID, novelChapter.link, System.currentTimeMillis())
                if (!updatedNovels.contains(novelCard)) updatedNovels.add(novelCard)
            } else {
                Database.DatabaseChapter.updateChapter(novelChapter)
            }
            if (Settings.isDownloadOnUpdateEnabled)
                DownloadManager.addToDownload(updateService.applicationContext as Activity, DownloadItem(DefaultScrapers.getByID(novelCard.formatterID), novelCard.title, novelChapter.title, Database.DatabaseIdentification.getChapterIDFromChapterURL(novelChapter.link)))
        }


    }


}