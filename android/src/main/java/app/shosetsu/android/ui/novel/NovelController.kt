package app.shosetsu.android.ui.novel

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.migration.MigrationController
import app.shosetsu.android.ui.migration.MigrationController.Companion.TARGETS_BUNDLE_KEY
import app.shosetsu.android.view.controller.FastAdapterRecyclerController
import app.shosetsu.android.view.controller.base.BottomMenuController
import app.shosetsu.android.view.controller.base.FABController
import app.shosetsu.android.view.controller.base.syncFABWithRecyclerView
import app.shosetsu.android.view.uimodels.model.ChapterUI
import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.android.view.widget.SlidingUpBottomMenu
import app.shosetsu.android.viewmodel.abstracted.INovelViewModel
import app.shosetsu.common.consts.ErrorKeys
import app.shosetsu.common.dto.*
import app.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.R.id
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerNovelInfoBinding
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerNovelInfoBinding.inflate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.fastadapter.select.selectExtension
import com.mikepenz.fastadapter.utils.AdapterPredicate
import javax.security.auth.DestroyFailedException

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
class NovelController(bundle: Bundle) :
	FastAdapterRecyclerController<ControllerNovelInfoBinding,
			AbstractItem<*>>(bundle),
	FABController,
	BottomMenuController {

	/*
	/** Fixes invalid adapter postion errors */
	override fun createLayoutManager(): RecyclerView.LayoutManager =
			object : LinearLayoutManager(context) {
				override fun supportsPredictiveItemAnimations(): Boolean = false
			}
	*/

	internal val viewModel: INovelViewModel by viewModel()
	override val viewTitle: String
		get() = ""
	private var resume: FloatingActionButton? = null
	private val novelUIAdapter by lazy { ItemAdapter<NovelUI>() }
	private val chapterUIAdapter by lazy { ItemAdapter<ChapterUI>() }

	@Suppress("UNCHECKED_CAST")
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

	/** Refreshes the novel */
	private fun refresh() {
		logD("Refreshing")

		viewModel.refresh().observe {
			binding.swipeRefreshLayout.isRefreshing = (it is HResult.Loading)
		}
	}

	override fun setupRecyclerView() {
		recyclerView.setHasFixedSize(false)
		resume?.let {
			syncFABWithRecyclerView(recyclerView, it)
		}
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
							recyclerView.scrollToPosition(itemAdapter.getAdapterPosition(getChapters()[chapterIndex].identifier))
							activity?.openChapter(getChapters()[chapterIndex])
						}
					}
					is HResult.Loading -> {
						// Ignore the loading
					}
				}
			})
		}
		fab.setImageResource(R.drawable.play_arrow)
	}

	override fun bindView(inflater: LayoutInflater): ControllerNovelInfoBinding =
		inflate(inflater).also {
			this.recyclerView = it.recyclerView
		}

	@Suppress("unused")
	fun migrateOpen() {
		parentController?.router?.pushController(
			MigrationController(
				bundleOf(
					Pair(
						TARGETS_BUNDLE_KEY,
						arrayOf(args.getNovelID()).toIntArray()
					)
				)
			).withFadeTransaction()
		)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
		id.source_migrate -> {
			// migrateOpen()
			toast { "Sorry, This hasn't been implemented yet" }
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
			binding.bottomMenu.findItem(id.bookmark)?.isVisible = false
			binding.bottomMenu.findItem(id.remove_bookmark)?.isVisible = true
		} else {
			binding.bottomMenu.findItem(id.bookmark)?.isVisible = true
			binding.bottomMenu.findItem(id.remove_bookmark)?.isVisible = false
		}

		// If any are downloaded, show delete
		binding.bottomMenu.findItem(id.chapter_delete_selected)?.isVisible =
			chaptersSelected.any { it.isSaved }

		// If any are not downloaded, show download option
		binding.bottomMenu.findItem(id.chapter_download_selected)?.isVisible =
			chaptersSelected.any { !it.isSaved }

		// If any are unread, show read option
		if (chaptersSelected.any { it.readingStatus == ReadingStatus.UNREAD }) {
			binding.bottomMenu.findItem(id.mark_unread)?.isVisible = false
			binding.bottomMenu.findItem(id.mark_read)?.isVisible = true
		} else {
			binding.bottomMenu.findItem(id.mark_unread)?.isVisible = true
			binding.bottomMenu.findItem(id.mark_read)?.isVisible = false
		}
	}

	override fun FastAdapter<AbstractItem<*>>.setupFastAdapter() {
		selectExtension {
			isSelectable = true
			multiSelect = true
			selectOnLongClick = true
			setSelectionListener { item, _ ->
				// Recreates the item view
				this@setupFastAdapter.notifyItemChanged(this@setupFastAdapter.getPosition(item))

				// Updates action mode
				calculateBottomSelectionMenuChanges()

				// Swaps the options menu on top
				val size = selectedItems.size
				if (size == 1) startSelectionAction() else if (size == 0) finishSelectionAction()
			}
		}
		setOnPreClickListener FastAdapterClick@{ _, _, item, position ->
			// Handles one click select when in selection mode
			selectExtension {
				if (selectedItems.isNotEmpty()) {
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
		setOnClickListener { _, _, item, _ ->
			if (item !is ChapterUI) return@setOnClickListener false
			activity?.openChapter(item)
			true
		}

		hookClickEvent(
			bind = { it: NovelUI.ViewHolder -> it.binding.inLibrary }
		) { _, _, _, _ ->
			viewModel.toggleNovelBookmark()
		}

		hookClickEvent(
			bind = { it: NovelUI.ViewHolder -> it.binding.webView }
		) { _, _, _, _ ->
			viewModel.openWebView()
		}

		hookClickEvent(
			bind = { it: NovelUI.ViewHolder -> it.binding.filterChip }
		) { _, _, _, _ ->
			openFilterMenu()
		}

		setObserver()
	}

	internal fun openFilterMenu() {
		bottomMenuRetriever.invoke()?.show()
	}

	override fun onDestroy() {
		try {
			viewModel.destroy()
		} catch (e: DestroyFailedException) {
			viewModel.reportError(errorResult(ErrorKeys.ERROR_IMPOSSIBLE, e))
		}
		actionMode?.finish()
		super.onDestroy()
	}

	private fun setObserver() {
		viewModel.novelLive.observe(this) { result ->
			result.handle(onError = { handleErrorResult(it) }) {
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
				hResult.transform { successResult(listOf(it)) }
			)
		}

		viewModel.chaptersLive.observe(this) {
			handleRecyclerUpdate(chapterUIAdapter, { showEmpty() }, { hideEmpty() }, it)
		}
	}

	override fun handleErrorResult(e: HResult.Error) {
		super.handleErrorResult(e)
		viewModel.reportError(e)
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
			adapterList.subList(smallest, largest).map { fastAdapter.getPosition(it) }
				.let { launchUI { select(it) } }
		}
	}

	private inner class SelectionActionMode : ActionMode.Callback {
		override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
			// Hides the original action bar
			// (activity as MainActivity?)?.supportActionBar?.hide()

			mode.menuInflater.inflate(R.menu.toolbar_chapters_selected, menu)
			mode.setTitle(R.string.selection)
			binding.bottomMenu.show(mode, R.menu.toolbar_chapters_selected_bottom) {
				when (it.itemId) {
					id.chapter_download_selected -> {
						downloadSelected()
						finishSelectionAction()
						true
					}
					id.chapter_delete_selected -> {
						deleteSelected()
						finishSelectionAction()
						true
					}
					id.mark_read -> {
						markSelectedAs(ReadingStatus.READ)
						finishSelectionAction()
						true
					}
					id.mark_unread -> {
						markSelectedAs(ReadingStatus.UNREAD)
						finishSelectionAction()
						true
					}
					id.bookmark -> {
						bookmarkSelected()
						finishSelectionAction()
						true
					}
					id.remove_bookmark -> {
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
				id.chapter_select_all -> {
					selectAll()
					true
				}
				id.chapter_select_between -> {
					selectBetween()
					true
				}
				id.chapter_inverse -> {
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

	override var bottomMenuRetriever: (() -> SlidingUpBottomMenu?) = { null }

	override fun getBottomMenuView(): View = NovelFilterMenuBuilder(
		this,
		activity!!.layoutInflater,
		viewModel
	).build()
}