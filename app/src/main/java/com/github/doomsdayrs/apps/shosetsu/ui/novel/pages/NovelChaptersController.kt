package com.github.doomsdayrs.apps.shosetsu.ui.novel.pages

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.context
import com.github.doomsdayrs.apps.shosetsu.common.ext.openChapter
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController
import com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters.ChaptersAdapter
import com.github.doomsdayrs.apps.shosetsu.view.base.RecyclerController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ChapterUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.INovelViewViewModel
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
class NovelChaptersController(bundle: Bundle)
	: RecyclerController<ChaptersAdapter, ChapterUI>(bundle) {
	override val layoutRes: Int = R.layout.novel_chapters
	override val resourceID: Int = R.id.fragment_novel_chapters_recycler

	@Attach(R.id.resume)
	var resume: FloatingActionButton? = null

	private val novelController: NovelController = parentController as NovelController
	private val viewModel: INovelViewViewModel = novelController.viewModel

	val inflater: MenuInflater = MenuInflater(context)

	val selectedChapters: ArrayList<Int> = arrayListOf()

	init {
		setHasOptionsMenu(true)
		viewModel.liveData.observe(this, Observer {
			when (it) {
				is HResult.Success -> {
					resume?.visibility = View.GONE
					activity?.invalidateOptionsMenu()
				}
				is HResult.Error -> TODO("Implement Error Handler")
				is HResult.Empty -> TODO("Implement Empty Handler")
				is HResult.Loading -> TODO("Implement Loading Handler")
			}
		})
	}

	override fun onViewCreated(view: View) {
		resume?.visibility = View.GONE
		resume?.setOnClickListener {
			viewModel.loadLastRead().observe(this, Observer { result ->
				when (result) {
					is HResult.Error -> {
					}
					is HResult.Empty -> {
					}
					is HResult.Success -> {
						activity?.openChapter(result.data)
					}
				}
			})
		}
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