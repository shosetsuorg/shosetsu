package com.github.doomsdayrs.apps.shosetsu.ui.novel.pages

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.*
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController
import com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters.ChaptersAdapter
import com.github.doomsdayrs.apps.shosetsu.view.base.FABView
import com.github.doomsdayrs.apps.shosetsu.view.base.RecyclerController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ChapterUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.INovelChaptersViewModel
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
 */

/**
 * Shosetsu
 * 9 / June / 2019
 *
 * Displays the chapters the novel contains
 * TODO Check filesystem if the chapter is saved, even if not in DB.
 */
class NovelChaptersController(val bundle: Bundle)
	: RecyclerController<ChaptersAdapter, ChapterUI>(bundle), FABView {
	override val layoutRes: Int = R.layout.novel_chapters
	override val resourceID: Int = R.id.fragment_novel_chapters_recycler

	private val viewModel: INovelChaptersViewModel by viewModel()
	private var resume: FloatingActionButton? = null

	init {
		setHasOptionsMenu(true)
	}

	override fun onViewCreated(view: View) {
		viewModel.setNovelID(bundle.getNovelID())
		resume = (parentController as NovelController).fab
		adapter = ChaptersAdapter(this, viewModel)
		setObserver()
	}

	private fun setObserver() {
		viewModel.liveData.observe(this, Observer { handleRecyclerUpdate(it) })
	}

	override fun showLoading() {
		super.showLoading()
		Log.d(logID(), "[2]")
	}

	override fun updateUI(list: List<ChapterUI>) {
		super.updateUI(list)
		Log.d(logID(), "Updating ui with list size of ${list.size}")
		if (list.isNotEmpty()) resume?.show() else resume?.hide()
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
				if (recyclerArray.none { viewModel.isChapterSelected(it) }) {
					R.menu.toolbar_chapters
				} else {
					R.menu.toolbar_chapters_selected
				},
				menu
		)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
		R.id.download -> {
			//optionDownload()
			true
		}
		R.id.chapter_select_all -> {
			//	optionChapterSelectAll()
			true
		}
		R.id.chapter_download_selected -> {
			//	optionChapterDownloadSelected()
			true
		}
		R.id.chapter_delete_selected -> {
			//	optionChapterDeleteSelected()
			true
		}
		R.id.chapter_deselect_all -> {
			//	optionChapterDeselectAll()
			true
		}
		R.id.chapter_mark_read -> {
			//	optionChapterMarkRead()
			true
		}
		R.id.chapter_mark_unread -> {
			//	optionChapterMarkUnread()
			true
		}
		R.id.chapter_mark_reading -> {
			//	optionChapterMarkReading()
			true
		}
		R.id.chapter_select_between -> {
			//	optionChapterSelectBetween()
			true
		}
		R.id.chapter_filter -> {
			//	optionChapterFilter()
			true
		}
		else -> false
	}

	override fun difAreItemsTheSame(oldItem: ChapterUI, newItem: ChapterUI): Boolean =
			oldItem.id == newItem.id

	override fun hideFAB(fab: FloatingActionButton) {
		if (recyclerArray.isNotEmpty()) super.hideFAB(fab)
	}

	override fun showFAB(fab: FloatingActionButton) {
		if (recyclerArray.isNotEmpty()) super.showFAB(fab)
	}

	override fun setFABIcon(fab: FloatingActionButton) {
		fab.setImageResource(R.drawable.ic_play_arrow_24dp)
	}

	override fun manipulateFAB(fab: FloatingActionButton) {
		fab.setOnClickListener {
			viewModel.openLastRead(recyclerArray).observe(this, Observer { result ->
				when (result) {
					is HResult.Error -> {
						Log.e(logID(), "Loading last read hit an error")
					}
					is HResult.Empty -> {
						context?.toast("You already read all the chapters")
					}
					is HResult.Success -> {
						val chapterIndex = result.data
						Log.d(logID(), "Got a value of $chapterIndex")
						if (chapterIndex != -1) {
							recyclerView?.scrollToPosition(chapterIndex)
							activity?.openChapter(recyclerArray[chapterIndex])
						}
					}
				}
			})
		}

	}

	// Option menu functions
	/*
	private fun optionDownload() {
		val builder = AlertDialog.Builder(activity!!)
		builder.setTitle(R.string.download)
				.setItems(R.array.chapters_download_options) { _, which ->
					when (which) {
						0 -> {
							// All
							for (chapterUI in recyclerArray)
								DownloadManager.addToDownload(
										activity!!,
										with(chapterUI) {
											DownloadEntity(id, novelID, link, title,
													novelController
											)
										}
								)
						}
						1 -> {
							// Unread
							for ((_, title, link) in recyclerArray) {
								try {
									val id = getChapterIDFromChapterURL(link)

									if (getChapterStatus(id) == (ReadingStatus.UNREAD))
										DownloadManager.addToDownload(
												activity!!,
												DownloadEntity(
														id,
														novelController!!.novelInfoController!!.novelPage!!.title,
														title,
														status = "Pending"
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
										DownloadEntity(
												getChapterIDFromChapterURL(next.link),
												novelController!!.novelInfoController!!.novelPage!!.title,
												next.title,
												status = "Pending"
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
					selectedChapters.add(getChapterIDFromChapterURL(novelChapter.url))
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
					val downloadItem = DownloadEntity(
							chapterID,
							novelController!!.novelInfoController!!.novelPage!!.title,
							chapter!!.title,
							status = "Pending"
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
				if (isSaved(chapterID)) DownloadManager.delete(context, DownloadEntity(
						chapterID,
						novelController!!.novelInfoController!!.novelPage!!.title,
						chapter!!.title,
						status = "Pending"
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
				if (getChapterStatus(chapterID).a != 2) setChapterStatus(chapterID, ReadingStatus.READ)
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
				if (getChapterStatus(chapterID).a != 0) setChapterStatus(chapterID, ReadingStatus.UNREAD)
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
				if (getChapterStatus(chapterID).a != 0) setChapterStatus(chapterID, ReadingStatus.READING)
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
					val id = getChapterIDFromChapterURL(recyclerArray[x].url)
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
	*/
}