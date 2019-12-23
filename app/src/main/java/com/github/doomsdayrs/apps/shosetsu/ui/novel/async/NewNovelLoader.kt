package com.github.doomsdayrs.apps.shosetsu.ui.novel.async

import android.os.AsyncTask
import android.view.View
import com.github.doomsdayrs.api.shosetsu.services.core.dep.Formatter
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelChapter
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelPage
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.scraper.WebViewScrapper
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelFragment
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.UNREAD
import kotlinx.android.synthetic.main.fragment_novel.*
import kotlinx.android.synthetic.main.fragment_novel_main.*
import kotlinx.android.synthetic.main.network_error.*
import org.jsoup.nodes.Document


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
 * 22 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
class NewNovelLoader(val novelURL: String, var novelID: Int, val formatter: Formatter, val novelFragment: NovelFragment?, private val loadChapters: Boolean) : AsyncTask<Void, Void, Boolean>() {
    var novelPage: NovelPage = NovelPage()

    constructor(newNovelLoader: NewNovelLoader) : this(newNovelLoader.novelURL, newNovelLoader.novelID, newNovelLoader.formatter, newNovelLoader.novelFragment, newNovelLoader.loadChapters)

    override fun onPreExecute() {
        super.onPreExecute()
        novelFragment?.activity?.runOnUiThread { novelFragment.fragment_novel_main_refresh.isRefreshing = true }
    }

    override fun onPostExecute(result: Boolean) {
        super.onPostExecute(result)
        if (result)
            novelFragment?.fragment_novel_viewpager?.post {
                novelFragment.novelPage = novelPage
                novelFragment.novelFragmentInfo?.setData(); novelFragment.fragment_novel_main_refresh.isRefreshing = false
                if (formatter.isIncrementingChapterList && loadChapters) TODO("New chapter loader") else novelFragment.novelChapters = novelFragment.novelPage.novelChapters
            }
    }

    override fun doInBackground(vararg params: Void?): Boolean {
        val document: Document? = WebViewScrapper.docFromURL(novelURL, formatter.hasCloudFlare)
        return if (document != null) {
            try {
                // Parses data
                novelPage = formatter.parseNovel(document)

                // Checks if it is not in DB, if true then it adds else it updates
                if (Database.DatabaseNovels.isNotInDatabase(novelURL))
                    Database.DatabaseNovels.addToLibrary(formatter.formatterID, novelPage, novelURL, UNREAD.a)
                else Database.DatabaseNovels.updateData(novelURL, novelPage)

                // Updates novelID
                novelID = if (novelID == -2) Database.DatabaseIdentification.getNovelIDFromNovelURL(novelURL) else novelID

                // Goes through the chapterList
                for (chapter: NovelChapter in novelPage.novelChapters) if (Database.DatabaseChapter.isNotInChapters(chapter.link)) Database.DatabaseChapter.addToChapters(novelID, chapter) else TODO("Didn't create an update condition yet")
                true
            } catch (e: Exception) {
                // Errors out the program and returns a false
                novelFragment?.activity?.runOnUiThread {
                    novelFragment.novelFragmentInfo?.fragment_novel_main_refresh?.isRefreshing = false;novelFragment.network_error!!.visibility = View.VISIBLE;novelFragment.error_message!!.text = e.message
                    novelFragment.error_button!!.setOnClickListener { NewNovelLoader(this).execute() }
                }
                false
            }
        } else false
    }
}