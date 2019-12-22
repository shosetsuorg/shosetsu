package com.github.doomsdayrs.apps.shosetsu.ui.novel.async

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.Toast
import com.github.doomsdayrs.api.shosetsu.services.core.dep.Formatter
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelChapter
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelPage
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification
import com.github.doomsdayrs.apps.shosetsu.backend.scraper.WebViewScrapper
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentChapters
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
 * 17 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 *
 *
 * This task loads a novel for the novel fragment
 */
class ChapterLoader : AsyncTask<Activity?, Void?, Boolean> {
    private var novelPage: NovelPage
    private val novelURL: String
    private val formatter: Formatter
    private var novelFragmentChapters: NovelFragmentChapters? = null

    /**
     * Constructor
     */
    constructor(novelPage: NovelPage, novelURL: String, formatter: Formatter) {
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

    private fun toastError(@Suppress("SameParameterValue") errorString: String, error: Throwable) {
        Log.e("ChapterLoader", errorString, error)
        if (novelFragmentChapters != null && novelFragmentChapters!!.activity != null && novelFragmentChapters!!.context != null)
            novelFragmentChapters!!.activity!!.runOnUiThread { Toast.makeText(novelFragmentChapters!!.context, errorString, Toast.LENGTH_SHORT).show() }

    }

    private fun toastError(error: String) {
        Log.e("ChapterLoader", error)
        if (novelFragmentChapters != null && novelFragmentChapters!!.activity != null && novelFragmentChapters!!.context != null)
            novelFragmentChapters!!.activity!!.runOnUiThread { Toast.makeText(novelFragmentChapters!!.context, error, Toast.LENGTH_SHORT).show() }
    }

    /**
     * Background process
     *
     * @param voids activity to work with
     * @return if completed
     */
    override fun doInBackground(vararg voids: Activity?): Boolean {
        val activity = voids[0]
        if (activity != null) {
            novelPage = NovelPage()
            Log.d("ChapterLoader", novelURL)
            if (novelFragmentChapters != null) {
                if (novelFragmentChapters!!.activity != null) novelFragmentChapters!!.activity!!.runOnUiThread {
                    if (novelFragmentChapters!!.novelFragment != null) {
                        novelFragmentChapters!!.novelFragment!!.getErrorView()!!.visibility = View.GONE
                    }
                }
                try {
                    var page = 1
                    if (formatter.isIncrementingChapterList) {
                        var doc: Document? = WebViewScrapper.docFromURL(novelURL, formatter.hasCloudFlare)
                        if (doc != null) {
                            novelPage = formatter.parseNovel(doc, page)
                            var mangaCount = 0
                            while (page <= novelPage.maxChapterPage && !activity.isDestroyed) {

                                if (novelFragmentChapters!!.getPageCount() != null) {
                                    val s = "Page: " + page + "/" + novelPage.maxChapterPage
                                    novelFragmentChapters!!.getPageCount()!!.post { novelFragmentChapters!!.getPageCount()!!.text = s }
                                } else toastError("PageCount returned null")

                                doc = WebViewScrapper.docFromURL(formatter.novelPageCombiner(novelURL, page), formatter.hasCloudFlare)
                                if (doc != null) {
                                    novelPage = formatter.parseNovel(doc, page)
                                    for (novelChapter in novelPage.novelChapters) {
                                        add(activity, mangaCount, novelChapter)
                                        mangaCount++
                                    }
                                } else toastError("Page returned null,Skipping")
                                page++
                                Utilities.wait(300)
                            }
                        }
                    } else {
                        val doc: Document? = WebViewScrapper.docFromURL(novelURL, formatter.hasCloudFlare)
                        if (doc != null) {
                            novelPage = formatter.parseNovel(doc, page)
                            for ((mangaCount, novelChapter) in novelPage.novelChapters.withIndex())
                                add(activity, mangaCount, novelChapter)
                        }
                    }
                    return true
                } catch (e: Exception) {
                    if (novelFragmentChapters != null) if (novelFragmentChapters!!.activity != null) novelFragmentChapters!!.activity!!.runOnUiThread {
                        if (novelFragmentChapters!!.novelFragment != null && novelFragmentChapters!!.novelFragment!!.getErrorView() != null && novelFragmentChapters!!.novelFragment!!.getErrorMessage() != null && novelFragmentChapters!!.novelFragment!!.getErrorButton() != null) {
                            novelFragmentChapters!!.novelFragment!!.getErrorView()!!.visibility = View.VISIBLE
                            novelFragmentChapters!!.novelFragment!!.getErrorMessage()!!.text = e.message
                            novelFragmentChapters!!.novelFragment!!.getErrorButton()!!.setOnClickListener { refresh(activity) }
                        } else toastError("Cannot dump error via ErrorView", e)
                    } else toastError("Cannot dump error via ErrorView", e)
                }
            }
        }

        return false
    }

    private fun add(activity: Activity, mangaCount: Int, novelChapter: NovelChapter) { //TODO The getNovelID in this method likely will cause slowdowns due to IO
        if (!activity.isDestroyed && !Database.DatabaseChapter.isNotInChapters(novelChapter.link)) {
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
        if (novelFragmentChapters != null && novelFragmentChapters!!.getSwipeRefreshLayout() != null) {
            novelFragmentChapters!!.getSwipeRefreshLayout()!!.isRefreshing = true
            if (formatter.isIncrementingChapterList && novelFragmentChapters!!.getPageCount() != null)
                novelFragmentChapters!!.getPageCount()!!.visibility = View.VISIBLE
        }
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
        if (novelFragmentChapters != null) {
            val activity: Activity? = novelFragmentChapters!!.activity
            if (activity != null) {
                activity.runOnUiThread {
                    if (novelFragmentChapters!!.getSwipeRefreshLayout() != null) {
                        novelFragmentChapters!!.getSwipeRefreshLayout()!!.isRefreshing = false
                        if (formatter.isIncrementingChapterList)
                            novelFragmentChapters!!.getPageCount()!!.visibility = View.GONE
                        if (result && novelFragmentChapters!!.adapter != null) {
                            novelFragmentChapters!!.setChapters()
                            novelFragmentChapters!!.getResumeRead()!!.visibility = View.VISIBLE
                            novelFragmentChapters!!.adapter!!.notifyDataSetChanged()
                        } else toastError("Result is a negative")
                    } else toastError("SwipeRefreshLayout null")
                }
            } else toastError("Activity is null")
        } else toastError("NovelFragmentChapters is null")
    }
}