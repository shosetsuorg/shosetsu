package com.github.doomsdayrs.apps.shosetsu.ui.novel

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.github.doomsdayrs.api.shosetsu.services.core.dep.Formatter
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelChapter
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelPage
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.getChapter
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.getStatus
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getChapterIDFromChapterURL
import com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters.NovelPagerAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.novel.async.NovelLoader
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentChapters
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentInfo
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import kotlinx.android.synthetic.main.fragment_novel.*


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
class NovelFragment : Fragment(R.layout.fragment_novel) {
    interface Custom {
        fun customCheck(status: Status): Boolean
    }

    // This is a never before loaded novel
    var new: Boolean = true

    var novelID = -2
    var novelURL: String = ""
    var novelPage: NovelPage = NovelPage()
    lateinit var formatter: Formatter

    var status = Status.UNREAD
    var novelChapters: List<NovelChapter> = ArrayList()

    var novelFragmentInfo: NovelFragmentInfo? = null
    var novelFragmentChapters: NovelFragmentChapters? = null

    init {
        setHasOptionsMenu(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("novelID", novelID)
        outState.putString("novelURL", novelURL)
        outState.putInt("formatter", formatter.formatterID)
        outState.putInt("status", status.a)
        outState.putBoolean("new", new)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Attach UI to program
        // Create sub-fragments
        run {
            novelFragmentInfo = NovelFragmentInfo()
            novelFragmentInfo!!.novelFragment = (this)
            novelFragmentChapters = NovelFragmentChapters()
            novelFragmentChapters!!.novelFragment = (this)
        }
        //TODO FINISH TRACKING
//boolean track = SettingsController.isTrackingEnabled();
        if (savedInstanceState == null) {
            if (Utilities.isOnline && Database.DatabaseNovels.isNotInNovels(novelID)) {
                setViewPager()
                fragment_novel_tabLayout!!.post { NovelLoader(novelURL, novelID, formatter, this, true).execute() }
            } else {
                novelPage = Database.DatabaseNovels.getNovelPage(novelID)
                new = false
                //   novelChapters = DatabaseChapter.getChapters(novelID)
                status = Database.DatabaseNovels.getStatus(novelID)
                if (activity != null && activity!!.actionBar != null) activity!!.actionBar!!.title = novelPage.title
                setViewPager()
            }
        } else {
            novelID = savedInstanceState.getInt("novelID")
            novelURL = savedInstanceState.getString("novelURL", "")
            formatter = DefaultScrapers.getByID(savedInstanceState.getInt("formatter"))!!
            status = Status.getStatus(savedInstanceState.getInt("status"))
            novelPage = Database.DatabaseNovels.getNovelPage(novelID)
            new = savedInstanceState.getBoolean("new")
            setViewPager()
        }
    }

    private fun setViewPager() {
        val fragments: MutableList<Fragment> = ArrayList()
        run {
            Log.d("FragmentLoading", "Main")
            fragments.add(novelFragmentInfo!!)
            Log.d("FragmentLoading", "Chapters")
            fragments.add(novelFragmentChapters!!)
        }
        val pagerAdapter = NovelPagerAdapter(childFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragments)
        fragment_novel_viewpager?.adapter = pagerAdapter
        fragment_novel_viewpager?.addOnPageChangeListener(TabLayoutOnPageChangeListener(fragment_novel_tabLayout))
        fragment_novel_tabLayout?.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                fragment_novel_viewpager!!.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        fragment_novel_tabLayout?.post { fragment_novel_tabLayout?.setupWithViewPager(fragment_novel_viewpager) }
    }

    @Suppress("unused")
            /**
             * @param chapterURL Current chapter URL
             * @return chapter after the input, returns the current chapter if no more
             */
    fun getNextChapter(chapterURL: Int, novelChapters: IntArray?): NovelChapter? {
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

    fun getNextChapter(chapterURL: String, novelChapters: List<NovelChapter>): NovelChapter? {
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

    fun getCustom(count: Int, check: Custom): List<NovelChapter> {
        Log.d("NovelFragment", "CustomGet of chapters: Count:$count")
        val customChapters: ArrayList<NovelChapter> = ArrayList()
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
                            if (check.customCheck(getStatus(getChapterIDFromChapterURL(novelChapters[y].link))))
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
                            if (check.customCheck(getStatus(getChapterIDFromChapterURL(novelChapters[y].link))))
                                customChapters.add(novelChapters[y])
                        }
                        y--
                    }
                }

            }
        }

        return customChapters
    }

    fun getLastRead(): NovelChapter {
        if (!novelChapters.isNullOrEmpty())
            if (!novelFragmentChapters!!.reversed)
                for (x in novelChapters.size - 1 downTo 0) {
                    val stat = getStatus(getChapterIDFromChapterURL(novelChapters[x].link))
                    if (stat == Status.READ || stat == Status.READING)
                        return novelChapters[x]
                }
            else for (x in novelChapters) {
                val stat = getStatus(getChapterIDFromChapterURL(x.link))
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
                    when (getStatus(getChapterIDFromChapterURL(novelChapters[x].link))) {
                        Status.READ -> return x + 1
                        Status.READING -> return x
                        else -> {
                        }
                    }
                }
            } else {
                for (x in novelChapters.indices) {
                    when (getStatus(getChapterIDFromChapterURL(novelChapters[x].link))) {
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