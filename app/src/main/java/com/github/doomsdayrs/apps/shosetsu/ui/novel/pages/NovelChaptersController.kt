package com.github.doomsdayrs.apps.shosetsu.ui.novel.pages

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.DownloadManager
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.controllers.RecyclerController
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.getChapter
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.getChapterStatus
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.getChapters
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.isSaved
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.setChapterStatus
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getChapterIDFromChapterURL
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels
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
class NovelChaptersController(bundle: Bundle)
	: RecyclerController<ChaptersAdapter, Novel.Chapter>(bundle) {
	init {
		setHasOptionsMenu(true)
	}

	override val layoutRes: Int = R.layout.novel_chapters
	override val resourceID: Int = R.id.fragment_novel_chapters_recycler

	var novelController: NovelController? = null
	var novelID: Int = -1

	private lateinit var receiver: BroadcastReceiver

	@Attach(R.id.resume)
	var resume: FloatingActionButton? = null

	@Attach(R.id.fragment_novel_chapters_recycler)
	var novelChaptersRecycler: RecyclerView? = null

	var selectedChapters = ArrayList<Int>()
	private var isArrayReversed = false


	override fun onDestroy() {
		super.onDestroy()
		isArrayReversed = false
		Log.d("NFChapters", "Destroy")
	}

	override fun onDestroyView(view: View) {
		super.onDestroyView(view)
		activity?.unregisterReceiver(receiver)
	}

	override fun onViewCreated(view: View) {
		novelController = parentController as NovelController?
		novelController?.novelChaptersController = this

		resume?.visibility = GONE
		setChapters()
		resume?.setOnClickListener {
			val i = lastRead()
			if (i != -1 && i != -2) {
				if (activity != null) openChapter(
						activity!!,
						recyclerArray[i],
						novelID,
						novelController!!.formatter.formatterID
				)
			} else context?.toast("No chapters! How did you even press this!")
		}

		// Attach receiver
		run {
			val filter = IntentFilter()
			filter.addAction(BC_NOTIFY_DATA_CHANGE)
			filter.addAction(BC_RELOAD_CHAPTERS_FROM_DB)
			receiver = object : BroadcastReceiver() {
				override fun onReceive(context: Context?, intent: Intent?) {
					if (intent?.action == BC_RELOAD_CHAPTERS_FROM_DB)
						novelController?.let {
							recyclerArray = getChapters(it.novelID) as ArrayList<Novel.Chapter>
						}
					novelChaptersRecycler?.adapter?.notifyDataSetChanged()
				}
			}
			activity?.registerReceiver(receiver, filter)
		}
	}

	/**
	 * Save data of view before destroyed
	 *
	 * @param outState output save
	 */
	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		outState.putIntegerArrayList("selChapter", selectedChapters)
		outState.putInt("novelID", novelID)
	}

	override fun onRestoreInstanceState(saved: Bundle) {
		super.onRestoreInstanceState(saved)
		selectedChapters.addAll(
				saved.getIntegerArrayList("selChapter") ?: arrayListOf()
		)
		novelID = saved.getInt("novelID")
	}

	/**
	 * Creates the option menu (on the top toolbar)
	 *
	 * @param menu     Menu reference to fill
	 * @param inflater Object to inflate the menu
	 */
	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		menu.clear()
		inflater.inflate(
				if (selectedChapters.size <= 0) {
					R.menu.toolbar_chapters
				} else {
					R.menu.toolbar_chapters_selected
				},
				menu
		)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.download -> {
				optionDownload()
				true
			}
			R.id.chapter_select_all -> {
				optionChapterSelectAll()
				true
			}
			R.id.chapter_download_selected -> {
				optionChapterDownloadSelected()
				true
			}
			R.id.chapter_delete_selected -> {
				optionChapterDeleteSelected()
				true
			}
			R.id.chapter_deselect_all -> {
				optionChapterDeselectAll()
				true
			}
			R.id.chapter_mark_read -> {
				optionChapterMarkRead()
				true
			}
			R.id.chapter_mark_unread -> {
				optionChapterMarkUnread()
				true
			}
			R.id.chapter_mark_reading -> {
				optionChapterMarkReading()
				true
			}
			R.id.chapter_select_between -> {
				optionChapterSelectBetween()
				true
			}
			R.id.chapter_filter -> {
				optionChapterFilter()
				true
			}
			else -> false
		}
	}

	/**
	 * Sets the novel chapters down
	 */
	fun setChapters() {
		novelChaptersRecycler!!.post {
			novelChaptersRecycler!!.setHasFixedSize(false)
			val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
			if (novelController != null && !DatabaseNovels.isNotInNovels(novelID)) {
				recyclerArray = getChapters(novelID) as ArrayList<Novel.Chapter>
				if (recyclerArray.isNotEmpty()) resume!!.visibility = VISIBLE
			}
			adapter = ChaptersAdapter(this)
			adapter!!.setHasStableIds(true)
			novelChaptersRecycler!!.layoutManager = layoutManager
			novelChaptersRecycler!!.adapter = adapter
		}
	}

	val inflater: MenuInflater?
		get() = MenuInflater(context)

	fun updateAdapter(): Boolean {
		return novelChaptersRecycler!!.post { if (adapter != null) adapter!!.notifyDataSetChanged() }
	}

	private fun customAdd(count: Int) {

		val ten = getCustom(count) { true }
		if (!ten.isNullOrEmpty())
			for ((_, title, link) in ten)
				DownloadManager.addToDownload(activity!!, DownloadItem(
						novelController!!.formatter,
						novelController!!.novelInfoController!!.novelPage!!.title,
						title,
						getChapterIDFromChapterURL(link)
				))
	}


	operator fun contains(novelChapter: Novel.Chapter): Boolean {
		try {
			for (n in selectedChapters)
				if (getChapter(n)!!.link.equals(novelChapter.link, ignoreCase = true))
					return true
		} catch (e: MissingResourceException) {
			e.handle(logID(), true)
		}
		return false
	}

	private fun findMinPosition(): Int {
		var min: Int = recyclerArray.size
		for (x in recyclerArray.indices) if (contains(recyclerArray[x]))
			if (x < min) min = x
		return min
	}

	private fun findMaxPosition(): Int {
		var max = -1
		for (x in recyclerArray.indices.reversed()) if (contains(recyclerArray[x]))
			if (x > max) max = x
		return max
	}

	private fun handleExceptionLogging(e: Exception) = e.handle(logID())

	@Suppress("unused")
			/**
			 * @param chapterURL Current chapter URL
			 * @return chapter after the input, returns the current chapter if no more
			 */
	fun getNextChapter(chapterURL: Int, novelChapters: IntArray?): Novel.Chapter? {
		if (novelChapters != null && novelChapters.isNotEmpty())
			for (x in novelChapters.indices) {
				if (novelChapters[x] == chapterURL) {
					return if (isArrayReversed!!) {
						if (x - 1 != -1) getChapter(novelChapters[x - 1])
						else
							getChapter(novelChapters[x])
					} else {
						if (x + 1 != novelChapters.size)
							getChapter(novelChapters[x + 1]) else
							getChapter(novelChapters[x])
					}
				}
			}
		return null
	}

	fun getNextChapter(chapterURL: String, novelChapters: List<Novel.Chapter>): Novel.Chapter? {
		if (novelChapters.isNotEmpty())
			for (x in novelChapters.indices) {
				if (novelChapters[x].link == chapterURL) {
					return if (isArrayReversed) {
						if (x - 1 != -1)
							getChapter(getChapterIDFromChapterURL(novelChapters[x - 1].link))
						else
							getChapter(getChapterIDFromChapterURL(novelChapters[x].link))
					} else {
						if (x + 1 != novelChapters.size)
							getChapter(getChapterIDFromChapterURL(novelChapters[x + 1].link))
						else
							getChapter(getChapterIDFromChapterURL(novelChapters[x].link))
					}
				}
			}
		return null
	}

	fun getCustom(count: Int, check: (Status) -> Boolean): List<Novel.Chapter> {
		Log.d("NovelFragment", "CustomGet of chapters: Count:$count")
		val customChapters: ArrayList<Novel.Chapter> = ArrayList()
		val lastReadChapter = getLastRead()
		var found = false
		if (!recyclerArray.isNullOrEmpty()) if (!isArrayReversed) {
			for (x in recyclerArray.size - 1 downTo 0) {
				if (lastReadChapter.link == recyclerArray[x].link)
					found = true
				if (found) {
					var y = x
					while (y < recyclerArray.size) {
						if (customChapters.size <= count) {
							if (check(getChapterStatus(getChapterIDFromChapterURL(recyclerArray[y].link))))
								customChapters.add(recyclerArray[y])
						}
						Log.d("NovelFragment", "Size ${customChapters.size}")
						y++
					}
				}

			}
		} else {
			for (x in recyclerArray.indices) {
				if (lastReadChapter.link == recyclerArray[x].link)
					found = true
				if (found) {
					var y = x
					while (y > 0) {
						if (customChapters.size <= count) {
							if (check(getChapterStatus(getChapterIDFromChapterURL(recyclerArray[y].link))))
								customChapters.add(recyclerArray[y])
						}
						y--
					}
				}

			}
		}

		return customChapters
	}

	fun getLastRead(): Novel.Chapter {
		if (!recyclerArray.isNullOrEmpty())
			if (!isArrayReversed)
				for (x in recyclerArray.size - 1 downTo 0) {
					val stat = getChapterStatus(getChapterIDFromChapterURL(recyclerArray[x].link))
					if (stat == Status.READ || stat == Status.READING)
						return recyclerArray[x]
				}
			else for (x in recyclerArray) {
				val stat = getChapterStatus(getChapterIDFromChapterURL(x.link))
				if (stat == Status.READ || stat == Status.READING)
					return x
			}
		return if (isArrayReversed) recyclerArray[0] else recyclerArray[recyclerArray.size - 1]
	}

	/**
	 * @return position of last read chapter, reads array from reverse. If -1 then the array is null, if -2 the array is empty, else if not found plausible chapter returns the first.
	 */
	fun lastRead(): Int {
		return if (recyclerArray.isNotEmpty()) {
			if (!isArrayReversed!!) {
				for (x in recyclerArray.indices.reversed()) {
					when (getChapterStatus(getChapterIDFromChapterURL(recyclerArray[x].link))) {
						Status.READ -> return x + 1
						Status.READING -> return x
						else -> {
						}
					}
				}
			} else {
				for (x in recyclerArray.indices) {
					when (getChapterStatus(getChapterIDFromChapterURL(recyclerArray[x].link))) {
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

	// Option menu functions

	private fun optionDownload() {
		val builder = AlertDialog.Builder(activity!!)
		builder.setTitle(R.string.download)
				.setItems(R.array.chapters_download_options) { _, which ->
					when (which) {
						0 -> {
							// All
							for ((_, title, link) in recyclerArray)
								DownloadManager.addToDownload(
										activity!!,
										DownloadItem(
												novelController!!.formatter,
												novelController!!.novelInfoController!!.novelPage!!.title,
												title,
												getChapterIDFromChapterURL(link)
										)
								)
						}
						1 -> {
							// Unread
							for ((_, title, link) in recyclerArray) {
								try {
									val id = getChapterIDFromChapterURL(link)

									if (getChapterStatus(id) == (Status.UNREAD))
										DownloadManager.addToDownload(
												activity!!,
												DownloadItem(
														novelController!!.formatter,
														novelController!!.novelInfoController!!.novelPage!!.title,
														title,
														id
												)
										)
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
							val last = getLastRead()
							val next = getNextChapter(
									last.link,
									recyclerArray
							)
							if (next != null)
								DownloadManager.addToDownload(
										activity!!,
										DownloadItem(
												novelController!!.formatter,
												novelController!!.novelInfoController!!.novelPage!!.title,
												next.title,
												getChapterIDFromChapterURL(next.link)
										)
								)
						}
					}
				}
		builder.create().show()
	}

	private fun optionChapterSelectAll() {
		try {
			for (novelChapter in recyclerArray)
				if (!contains(novelChapter))
					selectedChapters.add(getChapterIDFromChapterURL(novelChapter.link))
			updateAdapter()
		} catch (e: MissingResourceException) {
			handleExceptionLogging(e)
		}
	}

	private fun optionChapterDownloadSelected() {
		for (chapterID in selectedChapters) {
			try {
				val chapter = getChapter(chapterID)
				if (!isSaved(chapterID)) {
					val downloadItem = DownloadItem(
							novelController!!.formatter,
							novelController!!.novelInfoController!!.novelPage!!.title,
							chapter!!.title,
							chapterID
					)
					DownloadManager.addToDownload(activity, downloadItem)
				}
			} catch (e: MissingResourceException) {
				handleExceptionLogging(e)
				return
			}
		}
		updateAdapter()
	}

	private fun optionChapterDeleteSelected() {
		for (chapterID in selectedChapters) {
			try {
				val chapter = getChapter(chapterID)
				if (isSaved(chapterID)) DownloadManager.delete(context, DownloadItem(
						novelController!!.formatter,
						novelController!!.novelInfoController!!.novelPage!!.title,
						chapter!!.title,
						chapterID
				))
			} catch (e: MissingResourceException) {
				handleExceptionLogging(e)
				return
			}
		}
		updateAdapter()
	}

	private fun optionChapterDeselectAll() {
		selectedChapters = ArrayList()
		updateAdapter()
		if (inflater != null) activity?.invalidateOptionsMenu()
	}

	private fun optionChapterMarkRead() {
		for (chapterID in selectedChapters) {
			try {
				if (getChapterStatus(chapterID).a != 2) setChapterStatus(chapterID, Status.READ)
			} catch (e: Exception) {
				handleExceptionLogging(e)
				return
			}
		}
		updateAdapter()
	}

	private fun optionChapterMarkUnread() {
		for (chapterID in selectedChapters) {
			try {
				if (getChapterStatus(chapterID).a != 0) setChapterStatus(chapterID, Status.UNREAD)
			} catch (e: Exception) {
				handleExceptionLogging(e)
				return
			}
		}
		updateAdapter()
	}

	private fun optionChapterMarkReading() {
		for (chapterID in selectedChapters) {
			try {
				if (getChapterStatus(chapterID).a != 0) setChapterStatus(chapterID, Status.READING)
			} catch (e: Exception) {
				handleExceptionLogging(e)
				return
			}
		}
		updateAdapter()
	}

	private fun optionChapterSelectBetween() {
		val min = findMinPosition()
		val max = findMaxPosition()
		var x = min
		while (x < max) {
			if (!contains(recyclerArray[x])) {
				try {
					val id = getChapterIDFromChapterURL(recyclerArray[x].link)
					selectedChapters.add(id)
				} catch (e: MissingResourceException) {
					handleExceptionLogging(e)
					return
				}
			}
			x++
		}
		updateAdapter()
	}

	private fun optionChapterFilter() {
		recyclerArray.reverse()

		isArrayReversed = !isArrayReversed
		updateAdapter()
	}

}