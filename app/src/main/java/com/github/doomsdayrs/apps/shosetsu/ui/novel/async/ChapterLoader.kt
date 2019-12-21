package com.github.doomsdayrs.apps.shosetsu.ui.novel.async

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import android.view.View
import com.github.Doomsdayrs.api.shosetsu.services.core.dep.Formatter
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelChapter
import com.github.Doomsdayrs.api.shosetsu.services.core.objects.NovelPage
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification
import com.github.doomsdayrs.apps.shosetsu.backend.scraper.WebViewScrapper
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentChapters
import java.util.*

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
 * 17 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 *
 *
 * This task loads a novel for the novel fragment
 */
class ChapterLoader : AsyncTask<Activity?, Void?, Boolean> {
    private var novelPage: NovelPage?
    private val novelURL: String
    private val formatter: Formatter
    private var novelFragmentChapters: NovelFragmentChapters? = null

    /**
     * Constructor
     */
    constructor(novelPage: NovelPage?, novelURL: String, formatter: Formatter) {
        this.novelPage = novelPage
        this.novelURL = novelURL
        this.formatter = formatter
    }

    private constructor(chapterLoader: ChapterLoader) {
        novelPage = chapterLoader.novelPage
        novelURL = chapterLoader.novelURL
        formatter = chapterLoader.formatter
        novelFragmentChapters = chapterLoader.novelFragmentChapters
    }

    fun setNovelFragmentChapters(novelFragmentChapters: NovelFragmentChapters?): ChapterLoader {
        this.novelFragmentChapters = novelFragmentChapters
        return this
    }

    /**
     * Background process
     *
     * @param voids activity to work with
     * @return if completed
     */
    override fun doInBackground(vararg voids: Activity?): Boolean {
        val activity = voids[0]
        novelPage = null
        Log.d("ChapLoad", novelURL)
        if (novelFragmentChapters != null) {
            if (novelFragmentChapters!!.activity != null) novelFragmentChapters!!.activity!!.runOnUiThread {
                if (novelFragmentChapters!!.novelFragment != null) {
                    novelFragmentChapters!!.novelFragment!!.getErrorView()!!.visibility = View.GONE
                }
            }
            try {
                var page = 1
                if (formatter.isIncrementingChapterList) {
                    novelPage = formatter.parseNovel(WebViewScrapper.docFromURL(novelURL, formatter.hasCloudFlare()), page)
                    val mangaCount = 0
                    while (page <= novelPage.maxChapterPage && !activity.isDestroyed) {
                        val s = "Page: " + page + "/" + novelPage.maxChapterPage
                        Objects.requireNonNull(novelFragmentChapters!!.getPageCount()).post { novelFragmentChapters!!.getPageCount()!!.text = s }
                        novelPage = formatter.parseNovel(WebViewScrapper.docFromURL(novelURL, formatter.hasCloudFlare()), page)
                        for (novelChapter in novelPage.novelChapters) add(activity, mangaCount, novelChapter)
                        page++
                        Utilities.wait(300)
                    }
                } else {
                    novelPage = formatter.parseNovel(WebViewScrapper.docFromURL(novelURL, formatter.hasCloudFlare()), page)
                    val mangaCount = 0
                    for (novelChapter in novelPage.novelChapters) add(activity, mangaCount, novelChapter)
                }
                return true
            } catch (e: Exception) {
                if (novelFragmentChapters != null) if (novelFragmentChapters!!.activity != null) novelFragmentChapters!!.activity!!.runOnUiThread {
                    if (novelFragmentChapters!!.novelFragment != null) {
                        Objects.requireNonNull(novelFragmentChapters!!.novelFragment!!.getErrorView()).visibility = View.VISIBLE
                    }
                    if (novelFragmentChapters!!.novelFragment != null) {
                        Objects.requireNonNull(novelFragmentChapters!!.novelFragment!!.getErrorMessage()).text = e.message
                    }
                    if (novelFragmentChapters!!.novelFragment != null) {
                        Objects.requireNonNull(novelFragmentChapters!!.novelFragment!!.getErrorButton()).setOnClickListener { view: View? -> refresh(activity) }
                    }
                }
            }
        }
        return false
    }

    private fun add(activity: Activity, mangaCount: Int, novelChapter: NovelChapter) { //TODO The getNovelID in this method likely will cause slowdowns due to IO
        var mangaCount = mangaCount
        if (!activity.isDestroyed && !Database.DatabaseChapter.inChapters(novelChapter.link)) {
            mangaCount++
            Log.i("ChapterLoader", "Adding #" + mangaCount + ": " + novelChapter.link)
            Database.DatabaseChapter.addToChapters(DatabaseIdentification.getNovelIDFromNovelURL(novelURL), novelChapter)
        }
    }

    private fun refresh(activity: Activity) {
        ChapterLoader(this).execute(activity)
    }

    /**
     * Show progress bar
     */
    override fun onPreExecute() {
        Objects.requireNonNull(novelFragmentChapters!!.getSwipeRefreshLayout()).isRefreshing = true
        if (formatter.isIncrementingChapterList) Objects.requireNonNull(novelFragmentChapters!!.getPageCount()).visibility = View.VISIBLE
    }

    override fun onCancelled(aBoolean: Boolean) {
        Log.d("ChapterLoader", "Cancel")
        onPostExecute(false)
    }

    override fun onCancelled() {
        Log.d("ChapterLoader", "Cancel")
        onPostExecute(false)
    }

    /**
     * Hides progress and sets data
     *
     * @param result if completed
     */
    override fun onPostExecute(result: Boolean) {
        val activity: Activity? = novelFragmentChapters!!.activity
        activity?.runOnUiThread {
            novelFragmentChapters!!.getSwipeRefreshLayout()!!.isRefreshing = false
            if (formatter.isIncrementingChapterList) novelFragmentChapters!!.getPageCount()!!.visibility = View.GONE
            if (result) {
                if (novelFragmentChapters!!.activity != null) {
                    novelFragmentChapters!!.activity!!.runOnUiThread {
                        novelFragmentChapters!!.setChapters()
                        novelFragmentChapters!!.adapter!!.notifyDataSetChanged()
                        novelFragmentChapters!!.getResumeRead()!!.visibility = View.VISIBLE
                    }
                    novelFragmentChapters!!.activity!!.runOnUiThread { Objects.requireNonNull(novelFragmentChapters!!.adapter).notifyDataSetChanged() }
                } else Log.e("ChapterLoader", "Cannot set chapters")
            } else Log.e("ChapterLoader", "Result is a negative")
        }
                ?: Log.e("ChapterLoader", "Failed to retrieve an activity")
    }
}