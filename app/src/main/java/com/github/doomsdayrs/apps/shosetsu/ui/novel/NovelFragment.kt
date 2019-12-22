package com.github.doomsdayrs.apps.shosetsu.ui.novel

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.github.doomsdayrs.api.shosetsu.services.core.dep.Formatter
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelChapter
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelPage
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.ErrorView
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter
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
import kotlinx.android.synthetic.main.network_error.*
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
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class NovelFragment : Fragment() {
    @JvmField
    var novelID = 0
    @JvmField
    var novelURL: String? = null
    @JvmField
    var novelPage: NovelPage? = null
    @JvmField
    var formatter: Formatter? = null
    var status = Status.UNREAD
    @JvmField
    var novelChapters: List<NovelChapter>? = ArrayList()

    /**
     * @return position of last read chapter, reads array from reverse. If -1 then the array is null, if -2 the array is empty, else if not found plausible chapter returns the first.
     */
    fun lastRead(): Int {
        return if (novelChapters != null) {
            if (novelChapters!!.isNotEmpty()) {
                if (!NovelFragmentChapters.reversed) {
                    for (x in novelChapters!!.indices.reversed()) {
                        when (DatabaseChapter.getStatus(Database.DatabaseIdentification.getChapterIDFromChapterURL(novelChapters!![x].link))) {
                            Status.READ -> return x + 1
                            Status.READING -> return x
                            else -> {
                            }
                        }
                    }
                } else {
                    for (x in novelChapters!!.indices) {
                        when (DatabaseChapter.getStatus(Database.DatabaseIdentification.getChapterIDFromChapterURL(novelChapters!![x].link))) {
                            Status.READ -> return x - 1
                            Status.READING -> return x
                            else -> {
                            }
                        }
                    }
                }
                0
            } else -2
        } else -1
    }

    @JvmField
    var novelFragmentInfo: NovelFragmentInfo? = null
    var novelFragmentChapters: NovelFragmentChapters? = null


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("novelID", novelID)
        outState.putString("novelURL", novelURL)
        outState.putInt("formatter", formatter!!.formatterID)
        outState.putInt("status", status.a)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("OnCreateView", "NovelFragment")
        return inflater.inflate(R.layout.fragment_novel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Attach UI to program
        // Create sub-fragments
        run {
            novelFragmentInfo = NovelFragmentInfo()
            novelFragmentInfo!!.setNovelFragment(this)
            novelFragmentChapters = NovelFragmentChapters()
            novelFragmentChapters!!.setNovelFragment(this)
        }
        //TODO FINISH TRACKING
//boolean track = SettingsController.isTrackingEnabled();
        if (savedInstanceState == null) {
            if (Utilities.isOnline() && !Database.DatabaseNovels.inDatabase(novelID)) {
                setViewPager()
                fragment_novel_tabLayout!!.post { NovelLoader(this, ErrorView(activity, network_error, error_message, error_button), false).execute() }
            } else {
                novelPage = Database.DatabaseNovels.getNovelPage(novelID)
                status = Database.DatabaseNovels.getStatus(novelID)
                if (novelPage != null && activity != null && activity!!.actionBar != null) activity!!.actionBar!!.title = novelPage!!.title
                setViewPager()
            }
        } else {
            novelID = savedInstanceState.getInt("novelID")
            novelURL = savedInstanceState.getString("novelURL")
            formatter = DefaultScrapers.getByID(savedInstanceState.getInt("formatter"))
            status = Status.getStatus(savedInstanceState.getInt("status"))
            novelPage = Database.DatabaseNovels.getNovelPage(novelID)
            setViewPager()
        }
    }

    fun getErrorView(): ConstraintLayout? {
        return network_error
    }

    fun getErrorMessage(): TextView? {
        return error_message
    }

    fun getErrorButton(): Button? {
        return error_button
    }

    private fun setViewPager() {
        val fragments: MutableList<Fragment?> = ArrayList()
        run {
            Log.d("FragmentLoading", "Main")
            fragments.add(novelFragmentInfo)
            Log.d("FragmentLoading", "Chapters")
            fragments.add(novelFragmentChapters)
        }
        val pagerAdapter = NovelPagerAdapter(childFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragments)
        fragment_novel_viewpager!!.adapter = pagerAdapter
        fragment_novel_viewpager!!.addOnPageChangeListener(TabLayoutOnPageChangeListener(fragment_novel_tabLayout))
        fragment_novel_tabLayout!!.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                fragment_novel_viewpager!!.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        fragment_novel_tabLayout!!.post { fragment_novel_tabLayout!!.setupWithViewPager(fragment_novel_viewpager) }
    }

    companion object {
        /**
         * @param chapterURL Current chapter URL
         * @return chapter after the input, returns the current chapter if no more
         */
        @JvmStatic
        fun getNextChapter(chapterURL: Int, novelChapters: IntArray?): NovelChapter? {
            if (novelChapters != null && novelChapters.isNotEmpty()) for (x in novelChapters.indices) {
                if (novelChapters[x] == chapterURL) {
                    return if (NovelFragmentChapters.reversed) {
                        if (x - 1 != -1) DatabaseChapter.getChapter(novelChapters[x - 1]) else DatabaseChapter.getChapter(novelChapters[x])
                    } else {
                        if (x + 1 != novelChapters.size) DatabaseChapter.getChapter(novelChapters[x + 1]) else DatabaseChapter.getChapter(novelChapters[x])
                    }
                }
            }
            return null
        }
    }

    init {
        setHasOptionsMenu(true)
    }
}