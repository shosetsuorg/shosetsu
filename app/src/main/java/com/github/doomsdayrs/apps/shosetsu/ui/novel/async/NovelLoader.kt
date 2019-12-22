package com.github.doomsdayrs.apps.shosetsu.ui.novel.async

import android.os.AsyncTask
import android.util.Log
import android.view.View
import com.github.doomsdayrs.apps.shosetsu.backend.ErrorView
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification
import com.github.doomsdayrs.apps.shosetsu.backend.scraper.WebViewScrapper
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment
import kotlinx.android.synthetic.main.fragment_novel_chapters.*

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
 *
 */
class NovelLoader : AsyncTask<Void?, Void?, Boolean> {
    private var novelFragment: NovelFragment?
    private var loadAll: Boolean
    private var errorView: ErrorView

    /**
     * Constructor
     *
     * @param novelFragment reference to the fragment
     * @param errorView     Holder that contains error view and needed code;
     */
    constructor(novelFragment: NovelFragment?, errorView: ErrorView, loadAll: Boolean) {
        this.novelFragment = novelFragment
        this.loadAll = loadAll
        this.errorView = errorView
    }

    private constructor(novelLoader: NovelLoader) {
        novelFragment = novelLoader.novelFragment
        loadAll = novelLoader.loadAll
        errorView = novelLoader.errorView
    }

    override fun onPreExecute() {
        novelFragment!!.novelFragmentInfo!!.getSwipeRefresh()!!.isRefreshing = true
    }

    /**
     * Background process
     *
     * @param voids voided
     * @return if completed
     */
    override fun doInBackground(vararg voids: Void?): Boolean {
        Log.d("Loading", novelFragment!!.novelURL.toString())
        errorView.activity.runOnUiThread { errorView.errorView.visibility = View.GONE }
        try {
            val document = WebViewScrapper.docFromURL(novelFragment!!.novelURL, novelFragment!!.formatter!!.hasCloudFlare)!!
            novelFragment!!.novelPage = novelFragment!!.formatter!!.parseNovel(document)
            if (!errorView.activity.isDestroyed && Database.DatabaseNovels.isNotInDatabase(novelFragment!!.novelID)) {
                novelFragment!!.novelPage?.let { Database.DatabaseNovels.addToLibrary(novelFragment!!.formatter!!.formatterID, it, novelFragment!!.novelURL, com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.UNREAD.a) }
            }
            val novelID = DatabaseIdentification.getNovelIDFromNovelURL(novelFragment!!.novelURL)
            for (novelChapter in novelFragment!!.novelPage?.novelChapters!!) if (!errorView.activity.isDestroyed && !Database.DatabaseChapter.isNotInChapters(novelChapter.link)) Database.DatabaseChapter.addToChapters(novelID, novelChapter)
            Log.d("Loaded Novel:", novelFragment!!.novelPage!!.title)
            return true
        } catch (e: Exception) {
            errorView.activity.runOnUiThread { errorView.errorView.visibility = View.VISIBLE }
            errorView.activity.runOnUiThread { errorView.errorMessage.text = e.message }
            errorView.activity.runOnUiThread { errorView.errorButton.setOnClickListener { refresh() } }
            e.printStackTrace()
        }
        return false
    }

    private fun refresh() {
        NovelLoader(this).execute()
    }

    override fun onCancelled() {
        onPostExecute(false)
    }


    override fun onPostExecute(result: Boolean) {
        novelFragment!!.novelFragmentInfo!!.getSwipeRefresh()!!.isRefreshing = false
        if (Database.DatabaseNovels.isNotInDatabase(novelFragment!!.novelID)) {
            try {
                if (novelFragment!!.novelURL != null) {
                    if (novelFragment!!.novelPage != null) {
                        Database.DatabaseNovels.updateData(novelFragment!!.novelURL!!, novelFragment!!.novelPage!!)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (result) {
            if (loadAll && novelFragment != null && novelFragment!!.novelPage != null)
                errorView.activity.runOnUiThread { novelFragment!!.formatter?.let { novelFragment!!.novelURL?.let { it1 -> ChapterLoader(novelFragment!!.novelPage!!, it1, it).execute() } } }
            errorView.activity.runOnUiThread {
                novelFragment!!.novelFragmentInfo!!.setData()
            }
            if (!novelFragment!!.formatter!!.isIncrementingChapterList) {
                novelFragment!!.novelFragmentChapters!!.fragment_novel_chapters_recycler!!.post {
                    novelFragment!!.novelChapters = novelFragment!!.novelPage!!.novelChapters
                    novelFragment!!.novelFragmentChapters!!.setChapters()
                    novelFragment!!.novelFragmentChapters!!.adapter!!.notifyDataSetChanged()
                }
            }
        }
    }
}