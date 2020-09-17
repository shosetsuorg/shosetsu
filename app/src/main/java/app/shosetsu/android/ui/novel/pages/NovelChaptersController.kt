package app.shosetsu.android.ui.novel.pages

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.enums.ReadingStatus
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.novel.NovelController
import app.shosetsu.android.ui.novel.adapters.ChaptersAdapter
import app.shosetsu.android.view.base.FABController
import app.shosetsu.android.view.base.FastAdapterRecyclerController
import app.shosetsu.android.view.uimodels.model.ChapterUI
import app.shosetsu.android.viewmodel.abstracted.INovelChaptersViewModel
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.fastadapter.select.selectExtension

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
	: FastAdapterRecyclerController.BasicFastAdapterRecyclerController<ChapterUI>(bundle), FABController {
	private val viewModel: INovelChaptersViewModel by viewModel()
	private var resume: FloatingActionButton? = null
	override val viewTitle: String = ""

	override val fastAdapter: FastAdapter<ChapterUI> by lazy {
		val adapter = ChaptersAdapter(viewModel)
		adapter.addAdapter(0, itemAdapter)
		adapter
	}

	init {
		setHasOptionsMenu(true)
	}


	private fun isVisible() =
			(parentController as? NovelController)?.pageListener?.currentPosition == 1

	override fun onViewCreated(view: View) {
		viewModel.setNovelID(args.getNovelID())
		resume = (parentController as NovelController).fab
	}

	override fun setupRecyclerView() {
		recyclerView.setHasFixedSize(false)
		super.setupRecyclerView()
		recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
				if (isVisible())
					when (newState) {
						RecyclerView.SCROLL_STATE_DRAGGING -> {
							resume?.let { hideFAB(it) }
						}
						RecyclerView.SCROLL_STATE_SETTLING -> {
						}
						RecyclerView.SCROLL_STATE_IDLE -> {
							resume?.let { showFAB(it) }
						}
					}
			}
		})
		setObserver()
	}

	override fun setupFastAdapter() {
		fastAdapter.selectExtension {
			isSelectable = true
			multiSelect = true
			selectOnLongClick = true
			setSelectionListener { item, _ ->
				// Recreates the item view
				fastAdapter.notifyItemChanged(fastAdapter.getPosition(item))

				// Swaps the options menu on top
				val size = selectedItems.size
				if (size == 0 || size == 1) activity?.invalidateOptionsMenu()
			}
		}
		fastAdapter.setOnPreClickListener FastAdapterClick@{ _, _, item, position ->
			// Handles one click select when in selection mode
			fastAdapter.selectExtension {
				if (selectedItems.isNotEmpty()) {
					if (!item.isSelected)
						select(
								item = item,
								considerSelectableFlag = true
						)
					else
						deselect(position)
					return@FastAdapterClick true
				}
			}
			false
		}

		fastAdapter.setOnClickListener { _, _, item, _ ->
			activity?.openChapter(item)
			false
		}
	}

	private fun setObserver() {
		viewModel.liveData.observe(this, { handleRecyclerUpdate(it) })
	}

	override fun updateUI(newList: List<ChapterUI>) {
		Log.d(logID(), "Received chapter count of ${newList.size}")
		super.updateUI(newList)
		resume?.let {
			if (newList.isNotEmpty() && isVisible()) showFAB(it) else hideFAB(it)
		}
	}

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
			viewModel.openLastRead(recyclerArray).observe(this, { result ->
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
							recyclerView.scrollToPosition(chapterIndex)
							activity?.openChapter(recyclerArray[chapterIndex])
						}
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
				if (fastAdapter.getSelectExtension().selectedItems.isEmpty()) {
					R.menu.toolbar_chapters
				} else {
					R.menu.toolbar_chapters_selected
				},
				menu
		)
	}

	@Suppress("KDocMissingDocumentation")
	override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
		R.id.download -> {
			//  optionDownload()
			true
		}
		R.id.chapter_select_all -> {
			fastAdapter.getSelectExtension().select()
			true
		}
		R.id.chapter_download_selected -> {
			viewModel.download(*fastAdapter.getSelectExtension().selectedItems.toTypedArray())
			true
		}
		R.id.chapter_delete_selected -> {
			viewModel.delete(*fastAdapter.getSelectExtension().selectedItems.toTypedArray())
			true
		}
		R.id.chapter_deselect_all -> {
			fastAdapter.getSelectExtension().deselect()
			true
		}
		R.id.chapter_mark_read -> {
			viewModel.markAllAs(
					*fastAdapter.getSelectExtension().selectedItems.toTypedArray(),
					readingStatus = ReadingStatus.READ
			)
			true
		}
		R.id.chapter_mark_unread -> {
			viewModel.markAllAs(
					*fastAdapter.getSelectExtension().selectedItems.toTypedArray(),
					readingStatus = ReadingStatus.UNREAD
			)
			true
		}
		R.id.chapter_select_between -> {
			launchIO {
				fastAdapter.selectExtension {
					val selectedItems = selectedItems.toList().sortedBy { it.order }
					val adapterList = itemAdapter.adapterItems
					if (adapterList.isEmpty()) {
						launchUI { toast(R.string.chapter_select_between_error_empty_adapter) }
						return@launchIO
					}
					adapterList.subList(
							adapterList.indexOfFirst { it.id == selectedItems.first().id },
							adapterList.indexOfFirst { it.id == selectedItems.last().id }
					).map { fastAdapter.getPosition(it) }.let { launchUI { select(it) } }
				}
			}
			true
		}
		R.id.chapter_filter -> {
			itemAdapter.itemList.items.reverse()
			fastAdapter.notifyAdapterDataSetChanged()
			true
		}
		else -> false
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