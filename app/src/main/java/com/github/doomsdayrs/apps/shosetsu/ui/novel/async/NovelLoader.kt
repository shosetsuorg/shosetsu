package com.github.doomsdayrs.apps.shosetsu.ui.novel.async

import android.content.DialogInterface
import android.os.AsyncTask
import android.util.Log
import com.github.doomsdayrs.api.shosetsu.services.core.Formatter
import com.github.doomsdayrs.api.shosetsu.services.core.Novel
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.addToChapters
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.isNotInChapters
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.updateChapter
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getNovelIDFromNovelURL
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels.addToLibrary
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels.isNotInNovels
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels.updateNovel
import com.github.doomsdayrs.apps.shosetsu.ui.errorView.ErrorAlert
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status.UNREAD


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
class NovelLoader(val novelURL: String, var novelID: Int, val formatter: Formatter, private val novelFragment: NovelController?, private val loadChapters: Boolean) : AsyncTask<Void, Void, Boolean>() {
    private var novelPage: Novel.Info = Novel.Info()

    constructor(novelLoader: NovelLoader) : this(novelLoader.novelURL, novelLoader.novelID, novelLoader.formatter, novelLoader.novelFragment, novelLoader.loadChapters)

    override fun onPreExecute() {
        super.onPreExecute()
        // Sets the refresh layout to give the user a visible cue
        novelFragment?.activity?.runOnUiThread { novelFragment.fragmentNovelMainRefresh?.isRefreshing = true }
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)
        // If successful, it will complete the task
        if (result == true)
            novelFragment?.fragmentNovelViewpager?.post {
                // Set's the novel page to the fragment
                novelFragment.novelPage = novelPage

                // After setting the page, it will tell the view to set data
                novelFragment.novelFragmentInfo?.setData()

                // Turns off refresh view
                novelFragment.fragmentNovelMainRefresh?.isRefreshing = false
                novelFragment.novelChapters = novelFragment.novelPage.chapters
                novelFragment.novelFragmentChapters?.setChapters()
            }
    }

    override fun doInBackground(vararg params: Void?): Boolean {
        return run {
            try {
                // Parses data
                novelPage = formatter.parseNovel(novelURL, loadChapters) {}

                // Checks if it is not in DB, if true then it adds else it updates
                if (isNotInNovels(novelURL))
                    addToLibrary(formatter.formatterID, novelPage, novelURL, UNREAD.a)
                else updateNovel(novelURL, novelPage)

                // Updates novelID
                novelID = if (novelID <= 0) getNovelIDFromNovelURL(novelURL) else novelID
                novelFragment?.novelID = novelID

                // Goes through the chapterList
                for (chapter: Novel.Chapter in novelPage.chapters) if (isNotInChapters(chapter.link)) addToChapters(novelID, chapter) else updateChapter(chapter)
                true
            } catch (e: Exception) {
                // Errors out the program and returns a false
                Log.e("NovelLoader", "Error", e)
                novelFragment?.activity?.runOnUiThread {
                    ErrorAlert(novelFragment.activity!!) { dialog: DialogInterface?, _: Int -> NovelLoader(this).execute();dialog?.dismiss() }
                            .setMessage(e.message)
                            .show()
                }
                false
            }
        }
    }
}