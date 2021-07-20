package app.shosetsu.android.ui.novel

import android.os.Bundle
import android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
import android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
import android.view.*
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import app.shosetsu.android.activity.MainActivity
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.migration.MigrationController
import app.shosetsu.android.ui.migration.MigrationController.Companion.TARGETS_BUNDLE_KEY
import app.shosetsu.android.view.controller.FastAdapterRecyclerController
import app.shosetsu.android.view.controller.base.FABController
import app.shosetsu.android.view.controller.base.syncFABWithRecyclerView
import app.shosetsu.android.view.uimodels.model.ChapterUI
import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.android.viewmodel.abstracted.ANovelViewModel
import app.shosetsu.common.consts.ErrorKeys
import app.shosetsu.common.dto.*
import app.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.R.id
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerNovelInfoBinding
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerNovelInfoBinding.inflate
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerNovelJumpDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import com.mikepenz.fastadapter.select.getSelectExtension
import com.mikepenz.fastadapter.select.selectExtension
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
	FABController {

	/*
	/** Fixes invalid adapter postion errors */
	override fun createLayoutManager(): RecyclerView.LayoutManager =
			object : LinearLayoutManager(context) {
				override fun supportsPredictiveItemAnimations(): Boolean = false
			}
	*/

	internal val viewModel: ANovelViewModel by viewModel()
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
		logI("Refreshing the novel data")
		viewModel.refresh().observe { refreshResult ->
			refreshResult.handle(
				onLoading = {
					binding.progressBar.isVisible = true
				},
				onEmpty = {
					makeSnackBar(R.string.controller_novel_snackbar_refresh_empty_result)?.show()
				},
				onError = {
					logE("Failed refreshing the novel data", it.exception)
					makeSnackBar(it.message)?.show()
				}
			) {
				logI("Successfully reloaded novel")
			}
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

	override fun showFAB(fab: FloatingActionButton) {
		if (actionMode == null) super.showFAB(fab)
	}

	override fun manipulateFAB(fab: FloatingActionButton) {
		resume = fab
		fab.setOnClickListener {
			viewModel.openLastRead(getChapters()).observe(this, { result ->
				when (result) {
					is HResult.Error -> {
						logE("Loading last read hit an error")
					}
					is HResult.Empty -> {
						makeSnackBar(R.string.controller_novel_snackbar_finished_reading)?.show()
					}
					is HResult.Success -> {
						val chapterIndex = result.data
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
			makeSnackBar(R.string.regret)?.dismiss()
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
		id.option_chapter_jump -> {
			openChapterJumpDialog()
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
			downloadCustom()
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

	private fun openChapterJumpDialog() {
		val binding =
			ControllerNovelJumpDialogBinding.inflate(LayoutInflater.from(recyclerView.context))

		// Change hint & input type depending on findByChapterName state
		binding.findByChapterName.setOnCheckedChangeListener { _, isChecked ->
			if (isChecked) {
				binding.editTextNumber.inputType = TYPE_TEXT_FLAG_NO_SUGGESTIONS
				binding.editTextNumber.setHint(R.string.controller_novel_jump_dialog_hint_chapter_title)
			} else {
				binding.editTextNumber.inputType = TYPE_NUMBER_FLAG_DECIMAL
				binding.editTextNumber.setHint(R.string.controller_novel_jump_dialog_hint_chapter_number)
			}
		}

		AlertDialog.Builder(recyclerView.context)
			.setView(binding.root)
			.setTitle(R.string.jump_to_chapter)
			.setNegativeButton(android.R.string.cancel) { _, _ ->
			}
			.setPositiveButton(R.string.alert_dialog_jump_positive) { _, _ ->
				/**
				 * The predicate to use to find the chapter to scroll to
				 */
				val predicate: (ChapterUI) -> Boolean

				@Suppress("LiftReturnOrAssignment")
				if (binding.findByChapterName.isChecked) {
					// Predicate will be searching if the title contains the text
					val text = binding.editTextNumber.text.toString()
					if (text.isBlank()) {
						makeSnackBar(R.string.toast_error_chapter_jump_empty_title)
							?.setAction(R.string.generic_question_retry) { openChapterJumpDialog() }
							?.show()
						return@setPositiveButton
					}

					predicate = { it.title.contains(text) }
				} else {
					// Predicate will be searching if the # is equal

					val selectedNumber =
						binding.editTextNumber.text.toString().toDoubleOrNull()?.plus(1.0) ?: run {
							makeSnackBar(R.string.toast_error_chapter_jump_invalid_number)
								?.setAction(R.string.generic_question_retry) { openChapterJumpDialog() }
								?.show()
							return@setPositiveButton
						}

					predicate = { it.order == selectedNumber }
				}

				// Search for chapter on IO, then jump to it on UI
				launchIO {
					chapterUIAdapter.adapterItems
						.indexOfFirst(predicate)
						.takeIf { it != -1 }?.let { index ->
							launchUI {
								recyclerView.smoothScrollToPosition(index)
							}
						} ?: launchUI {
						makeSnackBar(R.string.toast_error_chapter_jump_invalid_target)
							?.setAction(R.string.generic_question_retry) {
								openChapterJumpDialog()
							}
							?.show()
					}
				}
			}.show()
	}

	/**
	 * download a custom amount of chapters
	 */
	private fun downloadCustom() {
		if (context == null) return
		AlertDialog.Builder(binding.root.context!!).apply {
			setTitle(R.string.download_custom_chapters)
			val numberPicker = NumberPicker(binding.root.context).apply {
				minValue = 0
				maxValue = getChapters().size
			}
			setView(numberPicker)

			setPositiveButton(android.R.string.ok) { d, _ ->
				viewModel.downloadNextCustomChapters(numberPicker.value)
				d.dismiss()
			}
			setNegativeButton(android.R.string.cancel) { d, _ ->
				d.cancel()
			}
		}.show()
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
			else displayOfflineSnackBar()
			binding.swipeRefreshLayout.isRefreshing = false
		}

		(activity as? MainActivity)?.holdAtBottom(binding.bottomMenu)
	}

	private fun calculateBottomSelectionMenuChanges() {
		val chaptersSelected =
			fastAdapter.getSelectExtension().selectedItems.filterIsInstance<ChapterUI>()

		// If any chapters are bookmarked, show the remove bookmark logo
		binding.bottomMenu.findItem(id.remove_bookmark)?.isVisible =
			chaptersSelected.any { it.bookmarked }

		// If any chapters are not bookmarked, show bookmark
		binding.bottomMenu.findItem(id.bookmark)?.isVisible =
			chaptersSelected.any { !it.bookmarked }

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
			setSelectionListener { item, isSelected ->
				// Recreates the item view
				this@setupFastAdapter.notifyItemChanged(this@setupFastAdapter.getPosition(item))

				// Updates action mode
				calculateBottomSelectionMenuChanges()

				// Swaps the options menu on top
				val size = selectedItems.size

				// Incase the size is empty and the item is selected, add the item and try again
				if (size == 0 && isSelected) {
					logE("Migrating selection bug")
					(fastAdapter.getItemById(item.identifier)?.first as? ChapterUI)?.isSelected =
						true
					this.select(item, true)
					return@setSelectionListener
				}

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

		hookClickEvent(
			bind = { it: NovelUI.ViewHolder -> it.binding.chipJumpTo }
		) { _, _, _, _ ->
			openChapterJumpDialog()
		}

		setObserver()
	}

	private fun openFilterMenu() {
		BottomSheetDialog(binding.root.context).apply {
			setContentView(bottomMenuView)
		}.show()
	}

	override fun onDestroyView(view: View) {
		(activity as? MainActivity)?.removeHoldAtBottom(binding.bottomMenu)
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
		viewModel.novelLive.observe { result ->
			result.handle(onError = { handleErrorResult(it) }) {
				activity?.invalidateOptionsMenu()
				// If the data is not present, loads it
				if (!it.loaded) {
					if (viewModel.isOnline()) {
						refresh()
					} else {
						displayOfflineSnackBar(R.string.controller_novel_snackbar_cannot_inital_load_offline)
					}
				}
			}

			handleRecyclerUpdate(
				novelUIAdapter,
				{ showEmpty() },
				{ hideEmpty() },
				result.transform { successResult(listOf(it)) }
			)
		}

		viewModel.chaptersLive.observe(this) {
			handleRecyclerUpdate(chapterUIAdapter, { showEmpty() }, { hideEmpty() }, it)
			it.handle {
				binding.progressBar.isVisible = false
			}
		}
	}

	override fun showLoading() {
		binding.progressBar.isVisible = true
	}

	override fun handleErrorResult(e: HResult.Error) {
		viewModel.reportError(e)
	}

	private val selectedChapters: List<ChapterUI>
		get() = fastAdapter.getSelectExtension().selectedItems.filterIsInstance<ChapterUI>()

	private val selectedChapterArray: Array<ChapterUI>
		get() = selectedChapters.toTypedArray()

	private fun bookmarkSelected() {
		viewModel.bookmarkChapters(*selectedChapterArray)
	}

	private fun removeSelectedBookmark() {
		viewModel.removeChapterBookmarks(*selectedChapterArray)
	}

	private fun selectAll() {
		fastAdapter.getSelectExtension().select(true)
	}

	private fun invertSelection() {
		fastAdapter.invertSelection()
	}

	private fun downloadSelected() {
		viewModel.downloadChapter(
			chapterUI = selectedChapterArray,
			startManager = true
		)
	}

	private fun deleteSelected() {
		viewModel.delete(*selectedChapterArray)
	}

	private fun markSelectedAs(readingStatus: ReadingStatus) {
		viewModel.markAllChaptersAs(
			*selectedChapterArray,
			readingStatus = readingStatus
		)
	}

	/**
	 * Selects all chapters between the first and last selected chapter
	 */
	private fun selectBetween() {
		fastAdapter.selectBetween(
			chapterUIAdapter as ItemAdapter<AbstractItem<*>>,
			selectedChapters
		)
	}

	private inner class SelectionActionMode : ActionMode.Callback {
		override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
			// Hides the original action bar
			// (activity as MainActivity?)?.supportActionBar?.hide()

			mode.menuInflater.inflate(R.menu.toolbar_novel_chapters_selected, menu)
			mode.setTitle(R.string.selection)
			binding.bottomMenu.show(mode, R.menu.toolbar_novel_chapters_selected_bottom) {
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

	private val bottomMenuView: View
		get() = NovelFilterMenuBuilder(
			this,
			activity!!.layoutInflater,
			viewModel
		).build()
}