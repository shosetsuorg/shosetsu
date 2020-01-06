package com.github.doomsdayrs.apps.shosetsu.backend.async

import android.os.AsyncTask
import android.util.Log
import com.github.doomsdayrs.api.shosetsu.services.core.dep.Formatter
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelChapter
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelPage
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.scraper.WebViewScrapper
import org.jsoup.nodes.Document

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
        fun onPostExecute(result: Boolean, finalChapters: ArrayList<NovelChapter>)

        fun onJustBeforePost(finalChapters: ArrayList<NovelChapter>)

        // If formatter is an incrementing chapterList, This is called when an update occurs
        fun onIncrementingProgress(page: Int, max: Int)

        fun errorReceived(errorString: String)
    }

    private val finalChapters: ArrayList<NovelChapter> = ArrayList()

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        if (result != null) action.onPostExecute(result, finalChapters)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        action.onPreExecute()
    }

    private val nullPage: String = "Page returned null, Skipping"

    public override fun doInBackground(vararg p0: Void?): Boolean {
        var novelPage = NovelPage()
        return if (formatter.isIncrementingChapterList) {
            // sets the max page to one higher then normal
            novelPage.maxChapterPage = 2

            // Keeps track of what manga the loader is at
            var mangaCount = 0

            var page = 1
            // Iterates through the pages
            while (page <= novelPage.maxChapterPage) {

                // run's an incrementing progress update
                action.onIncrementingProgress(page, novelPage.maxChapterPage)

                //if (novelFragmentChapters!!.getPageCount() != null) {
                //    val s = "Page: " + page + "/" + novelPage.maxChapterPage
                //    novelFragmentChapters!!.getPageCount()!!.post { novelFragmentChapters!!.getPageCount()!!.text = s }
                //} else toastError("PageCount returned null")

                // loads page
                val doc: Document? = WebViewScrapper.docFromURL(formatter.novelPageCombiner(novelURL, page), formatter.hasCloudFlare)
                if (doc != null) {
                    novelPage = formatter.parseNovel(doc, page)

                    // Iterates through chapters
                    for (novelChapter in novelPage.novelChapters) {
                        log(novelChapter, mangaCount)
                        finalChapters.add(novelChapter)
                        mangaCount++
                    }
                } else action.errorReceived(nullPage)
                Utilities.wait(300)
                page++
            }
            action.onJustBeforePost(finalChapters)
            true
        } else {
            // loads page
            val doc: Document? = WebViewScrapper.docFromURL(novelURL, formatter.hasCloudFlare)

            if (doc != null) {
                novelPage = formatter.parseNovel(doc, 1)

                // Iterates through chapters
                for ((mangaCount, novelChapter) in novelPage.novelChapters.withIndex()) {
                    log(novelChapter, mangaCount)
                    finalChapters.add(novelChapter)
                }
            } else action.errorReceived(nullPage)
            action.onJustBeforePost(finalChapters)
            true
        }
    }

    fun log(novelChapter: NovelChapter, mangaCount: Int) {
        Log.i("ChapterLoader", "Loading #$mangaCount: ${novelChapter.link}")
    }
}