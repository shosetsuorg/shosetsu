package app.shosetsu.android.ui.novel

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.common.dto.*
import app.shosetsu.android.common.enums.ReadingStatus
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.migration.MigrationController
import app.shosetsu.android.ui.migration.MigrationController.Companion.TARGETS_BUNDLE_KEY
import app.shosetsu.android.ui.novel.adapters.NovelMultiAdapter
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
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
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
		val a = NovelMultiAdapter(viewModel)
		a.addAdapter(0, novelUIAdapter as ItemAdapter<AbstractItem<*>>)
		a.addAdapter(1, chapterUIAdapter as ItemAdapter<AbstractItem<*>>)
		a
	}

	init {
		setHasOptionsMenu(true)
	}

	override fun createLayoutManager(): RecyclerView.LayoutManager =
			object : LinearLayoutManager(context) {
				override fun supportsPredictiveItemAnimations(): Boolean = false
			}

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
		if (recyclerArray.isNotEmpty()) super.hideFAB(fab)
	}

	override fun showFAB(fab: FloatingActionButton) {
		if (recyclerArray.isNotEmpty()) super.showFAB(fab)
	}

	override fun manipulateFAB(fab: FloatingActionButton) {
		resume = fab
		fab.setOnClickListener {
			viewModel.openLastRead(recyclerArray.filterIsInstance<ChapterUI>()).observe(this, { result ->
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
		fab.setImageResource(R.drawable.ic_play_arrow_24dp)
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
	private fun selectAll() {
		fastAdapter.getSelectExtension().select()
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
		launchIO {
			fastAdapter.selectExtension {
				val selectedItems = selectedChapters().sortedBy { it.order }
				val adapterList = itemAdapter.adapterItems.filterIsInstance<ChapterUI>()
				if (adapterList.isEmpty()) {
					launchUI { toast(R.string.chapter_select_between_error_empty_adapter) }
					return@launchIO
				}

				val first = adapterList.indexOfFirst { it.id == selectedItems.first().id }
				val last = adapterList.indexOfFirst { it.id == selectedItems.last().id }

				if (first == -1) return@launchIO
				if (last == -1) return@launchIO

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
	}

	private fun reverseChapters() {
		viewModel.reverseChapters()
	}
}