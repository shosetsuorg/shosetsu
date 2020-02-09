package com.github.doomsdayrs.apps.shosetsu.backend.async

import android.os.AsyncTask
import android.util.Log
import com.github.doomsdayrs.api.shosetsu.services.core.Formatter
import com.github.doomsdayrs.api.shosetsu.services.core.Novel

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
class ChapterLoader(val action: ChapterLoaderAction, var formatter: Formatter, var novelURL: String) : AsyncTask<Void, Void, Boolean>() {
    interface ChapterLoaderAction {
        // What to do before task
        fun onPreExecute()

        // After task with results
        fun onPostExecute(result: Boolean, finalChapters: ArrayList<Novel.Chapter>)

        fun onJustBeforePost(finalChapters: ArrayList<Novel.Chapter>)

        // If formatter is an incrementing chapterList, This is called when an update occurs
        fun onIncrementingProgress(page: Int, max: Int)

        fun errorReceived(errorString: String)
    }

    private val finalChapters: ArrayList<Novel.Chapter> = ArrayList()

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        if (result != null) action.onPostExecute(result, finalChapters)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        action.onPreExecute()
    }


    public override fun doInBackground(vararg p0: Void?): Boolean {
        // loads page
        val novelPage: Novel.Info = formatter.parseNovel(novelURL, true) {}

        // Iterates through chapters
        for ((mangaCount, novelChapter) in novelPage.chapters.withIndex()) {
            log(novelChapter, mangaCount)
            finalChapters.add(novelChapter)
        }
        action.onJustBeforePost(finalChapters)
        return true
    }

    fun log(novelChapter: Novel.Chapter, mangaCount: Int) {
        Log.i("ChapterLoader", "Loading #$mangaCount: ${novelChapter.link}")
    }
}