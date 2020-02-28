package com.github.doomsdayrs.apps.shosetsu.ui.reader

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.github.doomsdayrs.api.shosetsu.services.core.Formatter
import com.github.doomsdayrs.apps.shosetsu.R.layout.chapter_reader
import com.github.doomsdayrs.apps.shosetsu.backend.ViewedController
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.reader.adapters.ChapterReaderAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.reader.listeners.ChapterViewChange
import com.github.doomsdayrs.apps.shosetsu.variables.obj.DefaultScrapers

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
 */ /**
 * shosetsu
 * 13 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
class ChapterReader(bundle: Bundle) : ViewedController(bundle) {
    // NovelData
    var chapterIDs: ArrayList<Int> = arrayListOf()
    var formatter: Formatter? = null
    var novelID = 0

    private lateinit var chapterReaderAdapter: ChapterReaderAdapter
    private var currentChapterID = -1
    override val idRes: Int = chapter_reader


    init {
        bundle.getIntegerArrayList("chapters")?.let {
            chapterIDs = it
        }
        run {
            val chapterID: Int = bundle.getInt("chapterID", -1)
            currentChapterID = chapterID
        }
        novelID = bundle.getInt("novelID", -1)
        formatter = DefaultScrapers.getByID(bundle.getInt("formatter", -1))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray("chapters", chapterIDs.toIntArray())
        outState.putInt("novelID", novelID)
        outState.putInt("formatter", formatter!!.formatterID)
        outState.putParcelable("adapter", chapterReaderAdapter.saveState())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
// Sets default values
        formatter = DefaultScrapers.getByID(savedInstanceState.getInt("formatter"))
        novelID = savedInstanceState.getInt("novelID")

        val temp = savedInstanceState.getIntArray("chapters")
        for (x in temp!!.indices) chapterIDs.add(temp[x])
    }


    override fun onViewCreated(view: View) {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
        } else {

        }

        if (chapterIDs.isEmpty()) {
            val integers = Database.DatabaseChapter.getChaptersOnlyIDs(novelID)
            for (x in integers.indices) chapterIDs.add(integers[x])
        }

        setupViewPager(savedInstanceState)
    }

    private fun setupViewPager(savedInstanceState: Bundle?) {
        chapterReaderAdapter = ChapterReaderAdapter(router, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, this)

        if (savedInstanceState != null) {
            chapterReaderAdapter.restoreState(savedInstanceState.getParcelable("adapter"), ClassLoader.getSystemClassLoader())
        }

        viewpager.adapter = chapterReaderAdapter
        viewpager.addOnPageChangeListener(ChapterViewChange(chapterReaderAdapter))
        if (currentChapterID != -1) viewpager.currentItem = findCurrentPosition(currentChapterID)
    }

    fun findCurrentPosition(id: Int): Int {
        for (x in chapterIDs.indices) if (chapterIDs[x] == id) return x
        return -1
    }

    fun getViewPager(): ViewPager? {
        return viewpager
    }

    fun getToolbar(): Toolbar? {
        return toolbar as Toolbar
    }
}