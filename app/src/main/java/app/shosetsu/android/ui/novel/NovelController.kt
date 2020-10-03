package app.shosetsu.android.ui.novel

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.common.dto.*
import app.shosetsu.android.common.enums.ReadingStatus
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.migration.MigrationController
import app.shosetsu.android.ui.migration.MigrationController.Companion.TARGETS_BUNDLE_KEY
import app.shosetsu.android.view.base.FABController
import app.shosetsu.android.view.base.FastAdapterRecyclerController
import app.shosetsu.android.view.uimodels.model.ChapterUI
import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.android.viewmodel.abstracted.INovelViewModel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.R.id
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerNovelInfoBinding
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerNovelInfoBinding.inflate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.fastadapter.select.selectExtension
import com.mikepenz.fastadapter.utils.AdapterPredicate

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
 * The page you see when you select a novel
 */
class NovelController(bundle: Bundle)
	: FastAdapterRecyclerController<ControllerNovelInfoBinding, AbstractItem<*>>(bundle), FABController {
	val viewModel: INovelViewModel by viewModel()
	override val viewTitle: String
		get() = ""
	private var resume: FloatingActionButton? = null

	private val novelUIAdapter by lazy { ItemAdapter<NovelUI>() }
	private val chapterUIAdapter by lazy { ItemAdapter<ChapterUI>() }
	override val fastAdapter: FastAdapter<AbstractItem<*>> by lazy {
		FastAdapter<AbstractItem<*>>().apply {
			addAdapter(0, novelUIAdapter as ItemAdapter<AbstractItem<*>>)
			addAdapter(1, chapterUIAdapter as ItemAdapter<AbstractItem<*>>)
		}
	}

	private var actionMode: ActionMode? = null

	init {
		setHasOptionsMenu(true)
	}

	private fun startSelectionAction(): Boolean {
		if (actionMode != null) return false
		hideFAB(resume!!)
		actionMode = activity?.startActionMode(SelectionActionMode())
		return true
	}

	private fun finishSelectionAction() {
		actionMode?.finish()
		//	recyclerView.postDelayed(400) { (activity as MainActivity?)?.supportActionBar?.show() }
	}

	private inner class SelectionActionMode : ActionMode.Callback {
		override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
			// Hides the original action bar
			// (activity as MainActivity?)?.supportActionBar?.hide()

			mode.menuInflater.inflate(R.menu.toolbar_chapters_selected, menu)
			mode.setTitle(R.string.selection)
			binding.bottomMenu.show(mode, R.menu.toolbar_chapters_selected_bottom) {
				when (it.itemId) {
					R.id.chapter_download_selected -> {
						downloadSelected()
						finishSelectionAction()
						true
					}
					R.id.chapter_delete_selected -> {
						deleteSelected()
						finishSelectionAction()
						true
					}
					R.id.mark_read -> {
						markSelectedAs(ReadingStatus.READ)
						finishSelectionAction()
						true
					}
					R.id.mark_unread -> {
						markSelectedAs(ReadingStatus.UNREAD)
						finishSelectionAction()
						true
					}
					R.id.bookmark -> {
						bookmarkSelected()
						finishSelectionAction()
						true
					}
					R.id.remove_bookmark -> {
						removeSelectedBookmark()
						finishSelectionAction()
						true
					}
					else -> false
				}
			}
			calculateBottomSelectionMenuChanges()
			return true
		}

		override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = false

		override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean =
				when (item.itemId) {
					R.id.chapter_select_all -> {
						selectAll()
						true
					}
					R.id.chapter_select_between -> {
						selectBetween()
						true
					}
					R.id.chapter_inverse -> {
						invertSelection()
						true
					}
					else -> false
				}

		override fun onDestroyActionMode(mode: ActionMode) {
			binding.bottomMenu.hide()
			binding.bottomMenu.clear()
			actionMode = null
			showFAB(resume!!)
			fastAdapter.getSelectExtension().deselect()
		}
	}

	/*
	/** Fixes invalid adapter postion errors */
	override fun createLayoutManager(): RecyclerView.LayoutManager =
			object : LinearLayoutManager(context) {
				override fun supportsPredictiveItemAnimations(): Boolean = false
			}
	*/

	/** Refreshes the novel */
	private fun refresh() {
		logD("Refreshing")
		viewModel.refresh().observe(this) {
			when (it) {
				is HResult.Loading -> binding.swipeRefreshLayout.isRefreshing = true
				is HResult.Success -> binding.swipeRefreshLayout.isRefreshing = false
				is HResult.Error -> binding.swipeRefreshLayout.isRefreshing = false
				is HResult.Empty -> binding.swipeRefreshLayout.isRefreshing = false
			}
		}
	}

	override fun setupRecyclerView() {
		recyclerView.setHasFixedSize(false)
		recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
			override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
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
		super.setupRecyclerView()
	}

	private fun getChapters(): List<ChapterUI> = chapterUIAdapter.itemList.items

	override fun hideFAB(fab: FloatingActionButton) {
		if (getChapters().isNotEmpty()) super.hideFAB(fab)
	}

	override fun showFAB(fab: FloatingActionButton) {
		if (getChapters().isNotEmpty() && actionMode == null) super.showFAB(fab)
	}

	override fun manipulateFAB(fab: FloatingActionButton) {
		resume = fab
		fab.setOnClickListener {
			viewModel.openLastRead(getChapters()).observe(this, { result ->
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
							activity?.openChapter(getChapters()[chapterIndex])
						}
					}
				}
			})
		}
		fab.setImageResource(R.drawable.play_arrow)
	}

	override fun bindView(inflater: LayoutInflater): ControllerNovelInfoBinding = inflate(inflater).also {
		this.recyclerView = it.recyclerView
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
		id.source_migrate -> {
			parentController?.router?.pushController(MigrationController(bundleOf(Pair(
					TARGETS_BUNDLE_KEY,
					arrayOf(args.getNovelID()).toIntArray()
			))).withFadeTransaction())
			true
		}
		id.webview -> {
			viewModel.openWebView()
			true
		}
		id.browser -> {
			viewModel.openBrowser()
			true
		}
		id.share -> {
			viewModel.share()
			true
		}
		id.download_next -> {
			viewModel.downloadNextChapter()
			true
		}
		id.download_next_5 -> {
			viewModel.downloadNext5Chapters()
			true
		}
		id.download_next_10 -> {
			viewModel.downloadNext10Chapters()
			true
		}
		id.download_custom -> {
			true
		}
		id.download_unread -> {
			viewModel.downloadAllUnreadChapters()
			true
		}
		id.download_all -> {
			viewModel.downloadAllChapters()
			true
		}
		else -> super.onOptionsItemSelected(item)
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_novel, menu)
		menu.findItem(id.source_migrate).isVisible = viewModel.isBookmarked()
	}

	override fun onViewCreated(view: View) {
		viewModel.setNovelID(args.getNovelID())
		binding.swipeRefreshLayout.setOnRefreshListener {
			if (viewModel.isOnline())
				refresh()
			else toast(R.string.you_not_online)
		}
	}

	private fun calculateBottomSelectionMenuChanges() {
		val chaptersSelected =
				fastAdapter.getSelectExtension().selectedItems.filterIsInstance<ChapterUI>()

		// If any are not bookmarked, show bookmark option
		if (chaptersSelected.any { !it.bookmarked }) {
			binding.bottomMenu.findItem(R.id.bookmark)?.isVisible = false
			binding.bottomMenu.findItem(R.id.remove_bookmark)?.isVisible = true
		} else {
			binding.bottomMenu.findItem(R.id.bookmark)?.isVisible = true
			binding.bottomMenu.findItem(R.id.remove_bookmark)?.isVisible = false
		}

		// If any are downloaded, show delete
		binding.bottomMenu.findItem(R.id.chapter_delete_selected)?.isVisible =
				chaptersSelected.any { it.isSaved }

		// If any are not downloaded, show download option
		binding.bottomMenu.findItem(R.id.chapter_download_selected)?.isVisible =
				chaptersSelected.any { !it.isSaved }

		// If any are unread, show read option
		if (chaptersSelected.any { it.readingStatus == ReadingStatus.UNREAD }) {
			binding.bottomMenu.findItem(R.id.mark_unread)?.isVisible = false
			binding.bottomMenu.findItem(R.id.mark_read)?.isVisible = true
		} else {
			binding.bottomMenu.findItem(R.id.mark_unread)?.isVisible = true
			binding.bottomMenu.findItem(R.id.mark_read)?.isVisible = false
		}
	}

	override fun setupFastAdapter() {
		fastAdapter.selectExtension {
			isSelectable = true
			multiSelect = true
			selectOnLongClick = true
			setSelectionListener { item, _ ->
				// Recreates the item view
				fastAdapter.notifyItemChanged(fastAdapter.getPosition(item))

				// Updates action mode
				calculateBottomSelectionMenuChanges()

				// Swaps the options menu on top
				val size = selectedItems.size
				if (size == 1) startSelectionAction() else if (size == 0) finishSelectionAction()
			}
		}
		fastAdapter.setOnPreClickListener FastAdapterClick@{ _, _, item, position ->
			// Handles one click select when in selection mode
			fastAdapter.selectExtension {
				if (selectedItems.isNotEmpty()) {
					logV("Is item selected? ${item.isSelected}")
					if (!item.isSelected) {
						select(
								item = item,
								considerSelectableFlag = true
						)
					} else {
						deselect(position)
					}
					return@FastAdapterClick true
				}
			}
			false
		}
		fastAdapter.setOnClickListener { _, _, item, _ ->
			if (item !is ChapterUI) return@setOnClickListener false
			activity?.openChapter(item)
			true
		}

		fastAdapter.addEventHook(object : ClickEventHook<NovelUI>() {
			override fun onBind(viewHolder: RecyclerView.ViewHolder): View? = if (viewHolder is NovelUI.ViewHolder) viewHolder.binding.inLibrary else null

			override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<NovelUI>, item: NovelUI) {
				viewModel.toggleBookmark()
			}
		})

		fastAdapter.addEventHook(object : ClickEventHook<NovelUI>() {
			override fun onBind(viewHolder: RecyclerView.ViewHolder): View? = if (viewHolder is NovelUI.ViewHolder) viewHolder.binding.webView else null

			override fun onClick(v: View, position: Int, fastAdapter: FastAdapter<NovelUI>, item: NovelUI) {
				viewModel.openWebView()
			}
		})
		setObserver()
	}

	override fun onDestroy() {
		viewModel.destroy()
		actionMode?.finish()
		super.onDestroy()
	}

	private fun setObserver() {
		viewModel.novelLive.observe(this) { result ->
			result.handle(onError = { showError(it) }) {
				activity?.invalidateOptionsMenu()
				// If the data is not present, loads it
				if (!it.loaded) refresh()
			}
		}
		/**
		viewModel.chaptersLive.observe(this, {
		handleRecyclerUpdate(it)
		})
		 */

		//viewModel.novelUILive().observe(this) { handleRecyclerUpdate(it) }
		viewModel.novelLive.observe(this) { hResult ->
			handleRecyclerUpdate(
					novelUIAdapter,
					{ showEmpty() },
					{ hideEmpty() },
					hResult.handleReturn { successResult(listOf(it)) }
			)
		}

		viewModel.chaptersLive.observe(this) {
			handleRecyclerUpdate(chapterUIAdapter, { showEmpty() }, { hideEmpty() }, it)
		}
		viewModel.formatterName.observe(this) { result: HResult<String> ->
			result.handledReturnAny(
					{ "Loading" }, { ("UNKNOWN") },
					{ showError(it); "Error on loading" },
			) { it }?.let { setFormatterName(it) }
		}
	}

	private fun setFormatterName(text: String) {
	}

	private fun selectedChapters(): List<ChapterUI> =
			fastAdapter.getSelectExtension().selectedItems.filterIsInstance<ChapterUI>()

	private fun selectedChapterArray(): Array<ChapterUI> = selectedChapters().toTypedArray()

	private fun bookmarkSelected() {
		viewModel.bookmarkChapters(*selectedChapterArray())
	}

	private fun removeSelectedBookmark() {
		viewModel.removeChapterBookmarks(*selectedChapterArray())
	}

	private fun selectAll() {
		fastAdapter.getSelectExtension().select(true)
	}

	private fun invertSelection() {
		fastAdapter.recursive(object : AdapterPredicate<AbstractItem<*>> {
			override fun apply(
					lastParentAdapter: IAdapter<AbstractItem<*>>,
					lastParentPosition: Int,
					item: AbstractItem<*>,
					position: Int
			): Boolean {
				if (item.isSelected) {
					fastAdapter.getSelectExtension().deselect(item)
				} else {
					fastAdapter.getSelectExtension().select(
							adapter = lastParentAdapter,
							item = item,
							position = RecyclerView.NO_POSITION,
							fireEvent = false,
							considerSelectableFlag = true
					)
				}
				return false
			}
		}, false)
		fastAdapter.notifyDataSetChanged()
	}

	private fun downloadSelected() {
		viewModel.downloadChapter(*selectedChapterArray())
	}

	private fun deleteSelected() {
		viewModel.delete(*selectedChapterArray())
	}

	private fun deselectAll() {
		fastAdapter.getSelectExtension().deselect()
	}

	private fun markSelectedAs(readingStatus: ReadingStatus) {
		viewModel.markAllChaptersAs(
				*selectedChapterArray(),
				readingStatus = readingStatus
		)
	}

	private fun selectBetween() {
		fastAdapter.selectExtension {
			val selectedItems = selectedChapters().sortedBy { it.order }
			val adapterList = chapterUIAdapter.adapterItems
			if (adapterList.isEmpty()) {
				launchUI { toast(R.string.chapter_select_between_error_empty_adapter) }
				return
			}

			val first = adapterList.indexOfFirst { it.id == selectedItems.first().id }
			val last = adapterList.indexOfFirst { it.id == selectedItems.last().id }

			if (first == -1) return
			if (last == -1) return

			val smallest: Int
			val largest: Int
			when {
				first > last -> {
					largest = first
					smallest = last
				}
				else -> {
					smallest = first
					largest = last
				}
			}
			adapterList.subList(smallest, largest).map { fastAdapter.getPosition(it) }.let { launchUI { select(it) } }
		}
	}

	private fun reverseChapters() {
		viewModel.reverseChapters()
	}
}