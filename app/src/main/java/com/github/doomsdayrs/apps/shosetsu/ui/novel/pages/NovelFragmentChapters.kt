package com.github.doomsdayrs.apps.shosetsu.ui.novel.pages

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.doomsdayrs.api.shosetsu.services.core.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.DownloadManager
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities.openChapter
import com.github.doomsdayrs.apps.shosetsu.backend.ViewedController
import com.github.doomsdayrs.apps.shosetsu.backend.async.ChapterLoader
import com.github.doomsdayrs.apps.shosetsu.backend.async.ChapterLoader.ChapterLoaderAction
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.addToChapters
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.getChapter
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.getChapterStatus
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.getChapters
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.isNotInChapters
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.isSaved
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.setChapterStatus
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.updateChapter
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getChapterIDFromChapterURL
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseUpdates.addToUpdates
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController
import com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters.ChaptersAdapter
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status
import com.github.doomsdayrs.apps.shosetsu.variables.ext.context
import com.github.doomsdayrs.apps.shosetsu.variables.ext.getString
import com.github.doomsdayrs.apps.shosetsu.variables.ext.toast
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Broadcasts
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
class NovelFragmentChapters : ViewedController() {
    override val layoutRes: Int = R.layout.fragment_novel_chapters


    @Attach(R.id.resume)
    var resume: FloatingActionButton? = null
    @Attach(R.id.page_count)
    var pageCount: Chip? = null
    @Attach(R.id.fragment_novel_chapters_refresh)
    var fragmentNovelChaptersRefresh: SwipeRefreshLayout? = null
    @Attach(R.id.fragment_novel_chapters_recycler)
    var fragmentNovelChaptersRecycler: RecyclerView? = null

    private val chaptersLoadedAction: ChapterLoaderAction
    private var currentMaxPage = 1
    var selectedChapters = ArrayList<Int>()
    var adapter: ChaptersAdapter? = ChaptersAdapter(this)
    var novelFragment: NovelController? = null
    var reversed = false

    var menu: Menu? = null

    init {
        setHasOptionsMenu(true)
        chaptersLoadedAction = object : ChapterLoaderAction {
            override fun onPreExecute() {
                fragmentNovelChaptersRefresh?.isRefreshing = true
            }

            override fun onPostExecute(result: Boolean, finalChapters: ArrayList<Novel.Chapter>) {
                fragmentNovelChaptersRefresh?.isRefreshing = false

                if (result) {
                    novelFragment?.novelChapters = finalChapters
                    adapter?.notifyDataSetChanged()
                }
            }

            override fun onJustBeforePost(finalChapters: ArrayList<Novel.Chapter>) {
                val s = getString(R.string.processing_data)
                pageCount?.post { pageCount?.text = s }
                for ((count: Int, novelChapter: Novel.Chapter) in finalChapters.withIndex()) {
                    val sc = s + ": $count/${finalChapters.size}"
                    pageCount?.post { pageCount?.text = sc }
                    if (novelFragment != null && novelFragment!!.novelID != -1) {
                        if (isNotInChapters(novelChapter.link)) {
                            Log.i("ChapterLoader", "Adding ${novelChapter.link}")
                            addToChapters(novelFragment!!.novelID, novelChapter)
                            if (!novelFragment!!.new)
                                addToUpdates(novelFragment!!.novelID, novelChapter.link, System.currentTimeMillis())
                        } else {
                            updateChapter(novelChapter)
                        }
                    } else Log.e("ChapterLoader", "Invalid novelID")
                }
            }

            override fun onIncrementingProgress(page: Int, max: Int) {
                val s = "Page: $page/$max"
                pageCount?.post { pageCount?.text = s }
            }

            override fun errorReceived(errorString: String) {
                Log.e("ChapterLoader", errorString)
                activity?.runOnUiThread {
                    context?.toast(errorString)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        reversed = false
        Log.d("NFChapters", "Destroy")
        activity?.unregisterReceiver(receiver)
    }

    override fun onViewCreated(view: View) {
        resume?.visibility = GONE
        if (novelFragment != null)
            fragmentNovelChaptersRefresh?.setOnRefreshListener {
                if (novelFragment != null && novelFragment!!.novelURL.isNotEmpty())
                    ChapterLoader(chaptersLoadedAction, novelFragment!!.formatter, novelFragment!!.novelURL).execute()
            }

        setChapters()
        resume?.setOnClickListener {
            val i = novelFragment!!.lastRead()
            if (i != -1 && i != -2) {
                if (activity != null) openChapter(activity!!, novelFragment!!.novelChapters[i], novelFragment!!.novelID, novelFragment!!.formatter.formatterID)
            } else context?.toast("No chapters! How did you even press this!")
        }
        val filter = IntentFilter()
        filter.addAction(Broadcasts.BROADCAST_NOTIFY_DATA_CHANGE)
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
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
     * @param savedInstanceState save
     * @return View
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        novelFragment = parentController as NovelController?
        novelFragment!!.novelFragmentChapters = this
        Log.d("NovelFragmentChapters", "Creating")
        return super.onCreateView(inflater, container)
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
            for (chapter in ten)
                DownloadManager.addToDownload(activity!!, DownloadItem(novelFragment!!.formatter, novelFragment!!.novelPage.title, chapter.title, getChapterIDFromChapterURL(chapter.link)))
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
                                    for (chapter in novelFragment?.novelChapters!!)
                                        DownloadManager.addToDownload(activity!!, DownloadItem(novelFragment!!.formatter, novelFragment!!.novelPage.title, chapter.title, getChapterIDFromChapterURL(chapter.link)))
                                }
                                1 -> {
                                    // Unread
                                    for (chapter in novelFragment?.novelChapters!!) {
                                        val id = getChapterIDFromChapterURL(chapter.link)
                                        if (getChapterStatus(id) == (Status.UNREAD))
                                            DownloadManager.addToDownload(activity!!, DownloadItem(novelFragment!!.formatter, novelFragment!!.novelPage.title, chapter.title, id))
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
                for (novelChapter in novelFragment!!.novelChapters) if (!contains(novelChapter)) selectedChapters.add(getChapterIDFromChapterURL(novelChapter.link))
                updateAdapter()
                true
            }
            R.id.chapter_download_selected -> {
                for (chapterID in selectedChapters) {
                    val chapter = getChapter(chapterID)
                    if (!isSaved(chapterID)) {
                        val downloadItem = DownloadItem(novelFragment!!.formatter, novelFragment!!.novelPage.title, chapter!!.title, chapterID)
                        DownloadManager.addToDownload(activity, downloadItem)
                    }
                }
                updateAdapter()
                true
            }
            R.id.chapter_delete_selected -> {
                for (chapterID in selectedChapters) {
                    val chapter = getChapter(chapterID)
                    if (isSaved(chapterID)) DownloadManager.delete(context, DownloadItem(novelFragment!!.formatter, novelFragment!!.novelPage.title, chapter!!.title, chapterID))
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
                    if (getChapterStatus(chapterID).a != 2) setChapterStatus(chapterID, Status.READ)
                }
                updateAdapter()
                true
            }
            R.id.chapter_mark_unread -> {
                for (chapterID in selectedChapters) {
                    if (getChapterStatus(chapterID).a != 0) setChapterStatus(chapterID, Status.UNREAD)
                }
                updateAdapter()
                true
            }
            R.id.chapter_mark_reading -> {
                for (chapterID in selectedChapters) {
                    if (getChapterStatus(chapterID).a != 0) setChapterStatus(chapterID, Status.READING)
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
                        val id = getChapterIDFromChapterURL(novelFragment!!.novelChapters[x].link)
                        selectedChapters.add(id)
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
        for (n in selectedChapters) if (getChapter(n)!!.link.equals(novelChapter.link, ignoreCase = true)) return true
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

    private lateinit var receiver: BroadcastReceiver

}