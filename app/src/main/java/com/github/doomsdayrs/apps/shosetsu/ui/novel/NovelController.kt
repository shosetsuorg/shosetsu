package com.github.doomsdayrs.apps.shosetsu.ui.novel

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.bluelinelabs.conductor.Controller
import com.github.doomsdayrs.api.shosetsu.services.core.Formatter
import com.github.doomsdayrs.api.shosetsu.services.core.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.ViewedController
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.getChapter
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.getChapterStatus
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getChapterIDFromChapterURL
import com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters.NovelPagerAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.novel.async.NovelLoader
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentChapters
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentInfo
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status
import com.github.doomsdayrs.apps.shosetsu.variables.obj.DefaultScrapers
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener


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
class NovelController : ViewedController() {
    interface Custom {
        fun customCheck(status: Status): Boolean
    }

    override val idRes: Int = R.layout.fragment_novel


    // This is a never before loaded novel
    var new: Boolean = true

    var novelID = -2
    var novelURL: String = ""
    var novelPage = Novel.Info()
    lateinit var formatter: Formatter

    var status = Status.UNREAD
    var novelChapters: List<Novel.Chapter> = ArrayList()

    var novelFragmentInfo: NovelFragmentInfo? = null
    var novelFragmentChapters: NovelFragmentChapters? = null


    var fragmentNovelTablayout: TabLayout? = null
    var fragmentNovelViewpager: ViewPager? = null
    var fragmentNovelMainRefresh: SwipeRefreshLayout? = null

    init {
        setHasOptionsMenu(true)
    }

    override fun onDestroyView(view: View) {
        fragmentNovelTablayout = null
        fragmentNovelViewpager = null
        fragmentNovelMainRefresh = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("novelID", novelID)
        outState.putString("novelURL", novelURL)
        outState.putInt("formatter", formatter.formatterID)
        outState.putInt("status", status.a)
        outState.putBoolean("new", new)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        novelID = savedInstanceState.getInt("novelID")
        novelURL = savedInstanceState.getString("novelURL", "")
        formatter = DefaultScrapers.getByID(savedInstanceState.getInt("formatter"))
        status = Status.getStatus(savedInstanceState.getInt("status"))
        novelPage = Database.DatabaseNovels.getNovelPage(novelID)
        new = savedInstanceState.getBoolean("new")
        setViewPager()
    }

    override fun onViewCreated(view: View) {
        fragmentNovelTablayout = view.findViewById(R.id.fragment_novel_tabLayout)
        fragmentNovelViewpager = view.findViewById(R.id.fragment_novel_viewpager)

        // Attach UI to program
        // Create sub-fragments
        run {
            novelFragmentInfo = NovelFragmentInfo()
            novelFragmentInfo!!.novelFragment = this
            novelFragmentChapters = NovelFragmentChapters()
            novelFragmentChapters!!.novelFragment = (this)
        }
        //TODO FINISH TRACKING
        //boolean track = SettingsController.isTrackingEnabled();
        if (Utilities.isOnline && Database.DatabaseNovels.isNotInNovels(novelID)) {
            setViewPager()
            fragmentNovelTablayout!!.post { NovelLoader(novelURL, novelID, formatter, this, true).execute() }
        } else {
            novelPage = Database.DatabaseNovels.getNovelPage(novelID)
            new = false
            //   novelChapters = DatabaseChapter.getChapters(novelID)
            status = Database.DatabaseNovels.getNovelStatus(novelID)
            if (activity != null && activity!!.actionBar != null) activity!!.actionBar!!.title = novelPage.title
            setViewPager()
        }

    }

    private fun setViewPager() {
        val fragments: MutableList<Controller> = ArrayList()
        run {
            Log.d("FragmentLoading", "Main")
            fragments.add(novelFragmentInfo!!)
            Log.d("FragmentLoading", "Chapters")
            fragments.add(novelFragmentChapters!!)
        }
        val pagerAdapter = NovelPagerAdapter(this, fragments)
        fragmentNovelViewpager?.adapter = pagerAdapter
        fragmentNovelViewpager?.addOnPageChangeListener(TabLayoutOnPageChangeListener(fragmentNovelTablayout))
        fragmentNovelTablayout?.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                fragmentNovelViewpager?.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        fragmentNovelTablayout?.post { fragmentNovelTablayout?.setupWithViewPager(fragmentNovelViewpager) }
    }

    @Suppress("unused")
            /**
             * @param chapterURL Current chapter URL
             * @return chapter after the input, returns the current chapter if no more
             */
    fun getNextChapter(chapterURL: Int, novelChapters: IntArray?): Novel.Chapter? {
        if (novelChapters != null && novelChapters.isNotEmpty())
            for (x in novelChapters.indices) {
                if (novelChapters[x] == chapterURL) {
                    return if (novelFragmentChapters?.reversed!!) {
                        if (x - 1 != -1) getChapter(novelChapters[x - 1]) else getChapter(novelChapters[x])
                    } else {
                        if (x + 1 != novelChapters.size) getChapter(novelChapters[x + 1]) else getChapter(novelChapters[x])
                    }
                }
            }
        return null
    }

    fun getNextChapter(chapterURL: String, novelChapters: List<Novel.Chapter>): Novel.Chapter? {
        if (novelChapters.isNotEmpty())
            for (x in novelChapters.indices) {
                if (novelChapters[x].link == chapterURL) {
                    return if (novelFragmentChapters?.reversed!!) {
                        if (x - 1 != -1) getChapter(getChapterIDFromChapterURL(novelChapters[x - 1].link)) else getChapter(getChapterIDFromChapterURL(novelChapters[x].link))
                    } else {
                        if (x + 1 != novelChapters.size) getChapter(getChapterIDFromChapterURL(novelChapters[x + 1].link)) else getChapter(getChapterIDFromChapterURL(novelChapters[x].link))
                    }
                }
            }
        return null
    }

    fun getCustom(count: Int, check: Custom): List<Novel.Chapter> {
        Log.d("NovelFragment", "CustomGet of chapters: Count:$count")
        val customChapters: ArrayList<Novel.Chapter> = ArrayList()
        val lastReadChapter = getLastRead()
        var found = false
        if (!novelChapters.isNullOrEmpty()) if (!novelFragmentChapters!!.reversed) {
            for (x in novelChapters.size - 1 downTo 0) {
                if (lastReadChapter.link == novelChapters[x].link)
                    found = true
                if (found) {
                    var y = x
                    while (y < novelChapters.size) {
                        if (customChapters.size <= count) {
                            if (check.customCheck(getChapterStatus(getChapterIDFromChapterURL(novelChapters[y].link))))
                                customChapters.add(novelChapters[y])
                        }
                        Log.d("NovelFragment", "Size ${customChapters.size}")
                        y++
                    }
                }

            }
        } else {
            for (x in novelChapters.indices) {
                if (lastReadChapter.link == novelChapters[x].link)
                    found = true
                if (found) {
                    var y = x
                    while (y > 0) {
                        if (customChapters.size <= count) {
                            if (check.customCheck(getChapterStatus(getChapterIDFromChapterURL(novelChapters[y].link))))
                                customChapters.add(novelChapters[y])
                        }
                        y--
                    }
                }

            }
        }

        return customChapters
    }

    fun getLastRead(): Novel.Chapter {
        if (!novelChapters.isNullOrEmpty())
            if (!novelFragmentChapters!!.reversed)
                for (x in novelChapters.size - 1 downTo 0) {
                    val stat = getChapterStatus(getChapterIDFromChapterURL(novelChapters[x].link))
                    if (stat == Status.READ || stat == Status.READING)
                        return novelChapters[x]
                }
            else for (x in novelChapters) {
                val stat = getChapterStatus(getChapterIDFromChapterURL(x.link))
                if (stat == Status.READ || stat == Status.READING)
                    return x
            }
        return if (novelFragmentChapters!!.reversed) novelChapters[0] else novelChapters[novelChapters.size - 1]
    }

    /**
     * @return position of last read chapter, reads array from reverse. If -1 then the array is null, if -2 the array is empty, else if not found plausible chapter returns the first.
     */
    fun lastRead(): Int {
        return if (novelChapters.isNotEmpty()) {
            if (!novelFragmentChapters?.reversed!!) {
                for (x in novelChapters.indices.reversed()) {
                    when (getChapterStatus(getChapterIDFromChapterURL(novelChapters[x].link))) {
                        Status.READ -> return x + 1
                        Status.READING -> return x
                        else -> {
                        }
                    }
                }
            } else {
                for (x in novelChapters.indices) {
                    when (getChapterStatus(getChapterIDFromChapterURL(novelChapters[x].link))) {
                        Status.READ -> return x - 1
                        Status.READING -> return x
                        else -> {
                        }
                    }
                }
            }
            0
        } else -2
    }

}