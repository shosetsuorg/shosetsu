package com.github.doomsdayrs.apps.shosetsu.ui.novel.pages

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.DownloadManager
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.controllers.ViewedController
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.getChapter
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.getChapterStatus
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.getChapters
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.isSaved
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.setChapterStatus
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getChapterIDFromChapterURL
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController
import com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters.ChaptersAdapter
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status
import com.github.doomsdayrs.apps.shosetsu.variables.ext.*
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Broadcasts.BC_NOTIFY_DATA_CHANGE
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Broadcasts.BC_RELOAD_CHAPTERS_FROM_DB
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import kotlin.collections.ArrayList

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
 *
 *
 * Displays the chapters the novel contains
 * TODO Check filesystem if the chapter is saved, even if not in DB.
 *
 */
class NovelChaptersController : ViewedController() {

    override val layoutRes: Int = R.layout.novel_chapters
    private lateinit var receiver: BroadcastReceiver


    @Attach(R.id.resume)
    var resume: FloatingActionButton? = null

    @Attach(R.id.fragment_novel_chapters_recycler)
    var fragmentNovelChaptersRecycler: RecyclerView? = null

    private var currentMaxPage = 1
    var selectedChapters = ArrayList<Int>()
    var adapter: ChaptersAdapter? = ChaptersAdapter(this)
    var novelFragment: NovelController? = null
    var reversed = false

    var menu: Menu? = null

    init {
        setHasOptionsMenu(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        reversed = false
        Log.d("NFChapters", "Destroy")
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        activity?.unregisterReceiver(receiver)
    }

    override fun onViewCreated(view: View) {
        resume?.visibility = GONE

        setChapters()
        resume?.setOnClickListener {
            val i = novelFragment!!.lastRead()
            if (i != -1 && i != -2) {
                if (activity != null) openChapter(activity!!, novelFragment!!.novelChapters[i], novelFragment!!.novelID, novelFragment!!.formatter.formatterID)
            } else context?.toast("No chapters! How did you even press this!")
        }

        val filter = IntentFilter()
        filter.addAction(BC_NOTIFY_DATA_CHANGE)
        filter.addAction(BC_RELOAD_CHAPTERS_FROM_DB)
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == BC_RELOAD_CHAPTERS_FROM_DB)
                    novelFragment?.let { it.novelChapters = getChapters(it.novelID) }
                fragmentNovelChaptersRecycler?.adapter?.notifyDataSetChanged()
            }
        }
        activity?.registerReceiver(receiver, filter)
    }

    /**
     * Save data of view before destroyed
     *
     * @param outState output save
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("maxPage", currentMaxPage)
        outState.putIntegerArrayList("selChapter", selectedChapters)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        selectedChapters = savedInstanceState.getIntegerArrayList("selChapter")!!
        currentMaxPage = savedInstanceState.getInt("maxPage")
    }

    /**
     * Creates view
     *
     * @param inflater           inflater to retrieve objects
     * @param container          container of this fragment
     * @return View
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        novelFragment = parentController as NovelController?
        novelFragment!!.novelChaptersController = this
        Log.d("NovelFragmentChapters", "Creating")
        return super.onCreateView(inflater, container, savedViewState)
    }

    /**
     * Creates the option menu (on the top toolbar)
     *
     * @param menu     Menu reference to fill
     * @param inflater Object to inflate the menu
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menu = menu
        menu.clear()
        if (selectedChapters.size <= 0) inflater.inflate(R.menu.toolbar_chapters, menu) else inflater.inflate(R.menu.toolbar_chapters_selected, menu)
    }

    /**
     * Sets the novel chapters down
     */
    fun setChapters() {
        fragmentNovelChaptersRecycler!!.post {
            fragmentNovelChaptersRecycler!!.setHasFixedSize(false)
            val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
            if (novelFragment != null && !Database.DatabaseNovels.isNotInNovels(novelFragment!!.novelID)) {
                novelFragment!!.novelChapters = getChapters(novelFragment!!.novelID)
                if (novelFragment!!.novelChapters.isNotEmpty()) resume!!.visibility = VISIBLE
            }
            adapter = ChaptersAdapter(this)
            adapter!!.setHasStableIds(true)
            fragmentNovelChaptersRecycler!!.layoutManager = layoutManager
            fragmentNovelChaptersRecycler!!.adapter = adapter
        }
    }

    val inflater: MenuInflater?
        get() = MenuInflater(context)

    fun updateAdapter(): Boolean {
        return fragmentNovelChaptersRecycler!!.post { if (adapter != null) adapter!!.notifyDataSetChanged() }
    }

    private fun customAdd(count: Int) {

        val ten = novelFragment?.getCustom(count, object : NovelController.Custom {
            override fun customCheck(status: Status): Boolean {
                return true
            }
        })
        if (!ten.isNullOrEmpty())
            for ((_, title, link) in ten)
                DownloadManager.addToDownload(activity!!, DownloadItem(novelFragment!!.formatter, novelFragment!!.novelPage.title, title, getChapterIDFromChapterURL(link)))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.download -> {
                val builder = AlertDialog.Builder(activity!!)
                builder.setTitle(R.string.download)
                        .setItems(R.array.chapters_download_options) { _, which ->
                            when (which) {
                                0 -> {
                                    // All
                                    for ((_, title, link) in novelFragment?.novelChapters!!)
                                        DownloadManager.addToDownload(activity!!, DownloadItem(novelFragment!!.formatter, novelFragment!!.novelPage.title, title, getChapterIDFromChapterURL(link)))
                                }
                                1 -> {
                                    // Unread
                                    for ((_, title, link) in novelFragment?.novelChapters!!) {
                                        try {
                                            val id = getChapterIDFromChapterURL(link)

                                            if (getChapterStatus(id) == (Status.UNREAD))
                                                DownloadManager.addToDownload(activity!!, DownloadItem(novelFragment!!.formatter, novelFragment!!.novelPage.title, title, id))
                                        } catch (e: MissingResourceException) {
                                            TODO("Add error handling here")
                                        }
                                    }
                                }
                                2 -> {
                                    // TODO Custom
                                    Utilities.regret(context!!)
                                }
                                3 -> {
                                    Log.d("NovelFragmentChapters", "Downloading next 10")
                                    customAdd(10)
                                }
                                4 -> {
                                    Log.d("NovelFragmentChapters", "Downloading next 5")
                                    customAdd(5)
                                }
                                5 -> {
                                    // Download next
                                    val last = novelFragment!!.getLastRead()
                                    val next = novelFragment!!.getNextChapter(last.link, novelFragment!!.novelChapters)
                                    if (next != null)
                                        DownloadManager.addToDownload(activity!!, DownloadItem(novelFragment!!.formatter, novelFragment!!.novelPage.title, next.title, getChapterIDFromChapterURL(next.link)))
                                }
                            }
                        }
                builder.create().show()
                true
            }
            R.id.chapter_select_all -> {
                try {
                    for (novelChapter in novelFragment!!.novelChapters) if (!contains(novelChapter)) selectedChapters.add(getChapterIDFromChapterURL(novelChapter.link))
                    updateAdapter()
                } catch (e: MissingResourceException) {
                    handleExceptionLogging(e)
                    return false
                }
                true
            }
            R.id.chapter_download_selected -> {
                for (chapterID in selectedChapters) {
                    try {
                        val chapter = getChapter(chapterID)
                        if (!isSaved(chapterID)) {
                            val downloadItem = DownloadItem(novelFragment!!.formatter, novelFragment!!.novelPage.title, chapter!!.title, chapterID)
                            DownloadManager.addToDownload(activity, downloadItem)
                        }
                    } catch (e: MissingResourceException) {
                        handleExceptionLogging(e)
                        return false
                    }
                }
                updateAdapter()
                true
            }
            R.id.chapter_delete_selected -> {
                for (chapterID in selectedChapters) {
                    try {
                        val chapter = getChapter(chapterID)
                        if (isSaved(chapterID)) DownloadManager.delete(context, DownloadItem(novelFragment!!.formatter, novelFragment!!.novelPage.title, chapter!!.title, chapterID))
                    } catch (e: MissingResourceException) {
                        handleExceptionLogging(e)
                        return false
                    }
                }
                updateAdapter()
                true
            }
            R.id.chapter_deselect_all -> {
                selectedChapters = ArrayList()
                updateAdapter()
                if (inflater != null) activity?.invalidateOptionsMenu()
                true
            }
            R.id.chapter_mark_read -> {
                for (chapterID in selectedChapters) {
                    try {
                        if (getChapterStatus(chapterID).a != 2) setChapterStatus(chapterID, Status.READ)
                    } catch (e: Exception) {
                        handleExceptionLogging(e)
                        return false
                    }
                }
                updateAdapter()
                true
            }
            R.id.chapter_mark_unread -> {
                for (chapterID in selectedChapters) {
                    try {
                        if (getChapterStatus(chapterID).a != 0) setChapterStatus(chapterID, Status.UNREAD)
                    } catch (e: Exception) {
                        handleExceptionLogging(e)
                        return false
                    }
                }
                updateAdapter()
                true
            }
            R.id.chapter_mark_reading -> {
                for (chapterID in selectedChapters) {
                    try {
                        if (getChapterStatus(chapterID).a != 0) setChapterStatus(chapterID, Status.READING)
                    } catch (e: Exception) {
                        handleExceptionLogging(e)
                        return false
                    }
                }
                updateAdapter()
                true
            }
            R.id.chapter_select_between -> {
                val min = findMinPosition()
                val max = findMaxPosition()
                var x = min
                while (x < max) {
                    if (!contains(novelFragment!!.novelChapters[x])) {
                        try {
                            val id = getChapterIDFromChapterURL(novelFragment!!.novelChapters[x].link)
                            selectedChapters.add(id)
                        } catch (e: MissingResourceException) {
                            handleExceptionLogging(e)
                            return false
                        }
                    }
                    x++
                }
                updateAdapter()
                true
            }
            R.id.chapter_filter -> {
                novelFragment!!.novelChapters = novelFragment!!.novelChapters.reversed()
                reversed = !reversed
                return updateAdapter()
            }
            else -> false
        }
    }

    operator fun contains(novelChapter: Novel.Chapter): Boolean {
        try {
            for (n in selectedChapters) if (getChapter(n)!!.link.equals(novelChapter.link, ignoreCase = true)) return true
        } catch (e: MissingResourceException) {
            e.handle(logID(), true)
        }
        return false
    }

    private fun findMinPosition(): Int {
        var min: Int = novelFragment!!.novelChapters.size
        for (x in novelFragment!!.novelChapters.indices) if (contains(novelFragment!!.novelChapters[x])) if (x < min) min = x
        return min
    }

    private fun findMaxPosition(): Int {
        var max = -1
        for (x in novelFragment!!.novelChapters.indices.reversed()) if (contains(novelFragment!!.novelChapters[x])) if (x > max) max = x
        return max
    }

    private fun handleExceptionLogging(e: Exception) = e.handle(logID())
}