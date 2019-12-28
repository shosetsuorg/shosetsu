package com.github.doomsdayrs.apps.shosetsu.backend.async

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelChapter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers.Companion.getByID
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelCard
import needle.CancelableTask
import needle.Needle

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
 */


/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class NewChapterUpdater(val novelCards: ArrayList<Int>, context: Context) : CancelableTask() {
    private val ID = 1917
    private val channel_ID = "shosetsu_updater"

    private val continueProcesss = true
    private var notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var builder: Notification.Builder
    private val updatedNovels = ArrayList<NovelCard>()


    init {
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channel_ID, "Shosetsu Update", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
            Notification.Builder(context, channel_ID)
        } else Notification.Builder(context)
    }




    private fun add(mangaCount: Int, novelID: Int, novelChapter: NovelChapter, novelCard: NovelCard) {
        if (continueProcesss && Database.DatabaseChapter.isNotInChapters(novelChapter.link)) {
            println("Adding #" + mangaCount + ": " + novelChapter.link)
            Database.DatabaseChapter.addToChapters(novelID, novelChapter)
            Database.DatabaseUpdates.addToUpdates(novelID, novelChapter.link, System.currentTimeMillis())
            if (!updatedNovels.contains(novelCard)) updatedNovels.add(novelCard)
        }
    }

    override fun doWork() {
        // Setup
        builder = builder
                .setSmallIcon(R.drawable.ic_system_update_alt_black_24dp)
                .setContentTitle("Update")
                .setContentText("Update in progress")
                .setProgress(novelCards.size, 0, false)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
        notificationManager.notify(ID, builder.build())

        // Main process
        for (x in novelCards.indices) {

            val novelCard = DatabaseNovels.getNovel(novelCards[x])
            val novelID = DatabaseIdentification.getNovelIDFromNovelURL(novelCard.novelURL)
            val formatter = getByID(novelCard.formatterID)

            // Updates notification
            builder.setContentText(novelCard.title)
            builder.setProgress(novelCards.size, x + 1, false)
            notificationManager.notify(ID, builder.build())
            // Runs process
            if (formatter != null)
                NewChapterLoader(object : NewChapterLoader.ChapterLoaderAction {
                    override fun onPreExecute() {
                    }

                    override fun onPostExecute(result: Boolean, finalChapters: ArrayList<NovelChapter>) {
                    }

                    override fun onJustBeforePost(finalChapters: ArrayList<NovelChapter>) {
                        for ((mangaCount, chapter) in finalChapters.withIndex()) add(mangaCount, novelID, chapter, novelCard)
                    }

                    override fun onIncrementingProgress(page: Int, max: Int) {
                    }

                    override fun errorReceived(errorString: String) {
                        Log.e("ChapterUpdater", errorString)
                    }
                }, formatter, novelCard.novelURL).doInBackground()

            Utilities.wait(1000)
        }

        // Completion
        val stringBuilder = StringBuilder()
        if (updatedNovels.size > 0) {
            builder.setContentTitle("Completed Update")
            for (novelCard in updatedNovels) stringBuilder.append(novelCard.title).append("\n")
            builder.style = Notification.BigTextStyle()
        } else stringBuilder.append("No updates found")
        builder.setContentText(stringBuilder.toString())
        builder.setProgress(0, 0, false)
        builder.setOngoing(false)
        notificationManager.notify(ID, builder.build())

        cancel()
    }
}