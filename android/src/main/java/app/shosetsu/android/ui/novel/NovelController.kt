package app.shosetsu.android.ui.novel

import android.os.Bundle
import android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
import android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
import android.view.*
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.os.bundleOf
import app.shosetsu.android.activity.MainActivity
import app.shosetsu.android.common.consts.SELECTED_STROKE_WIDTH
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.migration.MigrationController
import app.shosetsu.android.ui.migration.MigrationController.Companion.TARGETS_BUNDLE_KEY
import app.shosetsu.android.view.compose.LazyColumnScrollbar
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.view.controller.base.FABController
import app.shosetsu.android.view.openQRCodeShareDialog
import app.shosetsu.android.view.openShareMenu
import app.shosetsu.android.view.uimodels.model.ChapterUI
import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.android.viewmodel.abstracted.ANovelViewModel
import app.shosetsu.common.enums.ReadingStatus
import app.shosetsu.lib.Novel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.R.id
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerNovelJumpDialogBinding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.acra.ACRA
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
	ShosetsuController(bundle),
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

	private var actionMode: ActionMode? = null

	private val bottomMenuView: View
		get() = NovelFilterMenuBuilder(
			this,
			activity!!.layoutInflater,
			viewModel
		).build()

	init {
		setHasOptionsMenu(true)
	}

	override fun onAttach(view: View) {
		if (viewModel.isFromChapterReader) viewModel.deletePrevious()
		super.onAttach(view)
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
		viewModel.refresh().observe(
			catch = {
				logE("Failed refreshing the novel data", it)
				makeSnackBar(it.message ?: "Unknown exception")?.show()
			}
		) {
			logI("Successfully reloaded novel")
		}
	}

	override fun showFAB(fab: FloatingActionButton) {
		if (actionMode == null) super.showFAB(fab)
	}

	override fun manipulateFAB(fab: FloatingActionButton) {
		resume = fab
		fab.setOnClickListener {
			viewModel.openLastRead().observe(catch = {
				logE("Loading last read hit an error")
			}) { chapterUI ->
				if (chapterUI != null) {
					activity?.openChapter(chapterUI)
				} else {
					makeSnackBar(R.string.controller_novel_snackbar_finished_reading)?.show()
				}
			}
		}
		fab.setImageResource(R.drawable.play_arrow)
	}

	@ExperimentalMaterialApi
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

	@OptIn(ExperimentalMaterialApi::class)
	private fun openShare() {
		openShareMenu(
			activity!!,
			this,
			activity as MainActivity,
			shareBasicURL = {
				viewModel.getShareInfo().observe(
					catch = {
						makeSnackBar(
							getString(
								R.string.controller_novel_error_share,
								it.message ?: "Unknown"
							)
						)
							?.setAction(R.string.report) { _ ->
								ACRA.errorReporter.handleSilentException(it)
							}?.show()
					}
				) { info ->
					if (info != null)
						activity?.openShare(info.novelURL, info.novelTitle)
				}
			},
			shareQRCode = {
				openQRCodeShareDialog(
					activity!!,
					this,
					activity as MainActivity,
					viewModel.getQRCode()
				)
			}
		)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
		id.source_migrate -> {
			// migrateOpen()
			makeSnackBar(R.string.regret)?.dismiss()
			true
		}
		id.webview -> {
			openWebView()
			true
		}
		id.share -> {
			openShare()
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
			ControllerNovelJumpDialogBinding.inflate(LayoutInflater.from(activity!!))

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

		AlertDialog.Builder(activity!!)
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

				viewModel.scrollTo(predicate).collectLA(this, catch = {}) {
					if (!it) {
						makeSnackBar(R.string.toast_error_chapter_jump_invalid_target)
							?.setAction(R.string.generic_question_retry) {
								openChapterJumpDialog()
							}
							?.show()
					}
				}
			}
			.show()
	}

	/**
	 * download a custom amount of chapters
	 */
	private fun downloadCustom() {
		if (context == null) return
		AlertDialog.Builder(activity!!).apply {
			setTitle(R.string.download_custom_chapters)
			val numberPicker = NumberPicker(activity!!).apply {
				minValue = 0
				maxValue = 0 // TODO Compose alert
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

		runBlocking {
			menu.findItem(id.source_migrate).isVisible = viewModel.isBookmarked().first()
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup,
		savedViewState: Bundle?
	): View = ComposeView(container.context).apply {
		setViewTitle()
		setContent {
			val novelInfo by viewModel.novelLive.collectAsState(null)
			val chapters by viewModel.chaptersLive.collectAsState(emptyList())
			val isRefreshing by viewModel.isRefreshing.collectAsState(false)
			val hasSelected by viewModel.hasSelected.collectAsState(false)
			val itemAt by viewModel.itemIndex.collectAsState(0)

			activity?.invalidateOptionsMenu()
			// If the data is not present, loads it
			if (novelInfo != null && !novelInfo!!.loaded) {
				if (viewModel.isOnline()) {
					refresh()
				} else {
					displayOfflineSnackBar(R.string.controller_novel_snackbar_cannot_inital_load_offline)
				}
			}

			MdcTheme {
				NovelInfoContent(
					novelInfo,
					chapters,
					itemAt,
					isRefreshing,
					onRefresh = {
						if (viewModel.isOnline())
							refresh()
						else displayOfflineSnackBar()
					},
					openWebView = ::openWebView,
					toggleBookmark = viewModel::toggleNovelBookmark,
					chapterContent = {
						NovelChapterContent(
							chapter = it,
							openChapter = {
								viewModel.isFromChapterReader = true
								activity?.openChapter(it)
							},
							onToggleSelection = {
								viewModel.toggleSelection(it)
							},
							selectionMode = hasSelected
						)
					},
					openFilter = ::openFilterMenu,
					openChapterJump = ::openChapterJumpDialog
				)
			}
		}
	}

	override fun onViewCreated(view: View) {
		viewModel.setNovelID(args.getNovelID())
		if (viewModel.isFromChapterReader) viewModel.deletePrevious()

		viewModel.hasSelected.collectLatestLA(this, catch = {}) { hasSelected ->
			if (hasSelected) {
				startSelectionAction()
			} else {
				finishSelectionAction()
			}
		}
	}

/* TODO Re-implement
private fun calculateBottomSelectionMenuChanges() {
	val chaptersSelected =
		fastAdapter.getSelectExtension().selectedItems.filterIsInstance<ChapterUI>()

	// If any chapters are bookmarked, show the remove bookmark logo
	binding.bottomMenu.findItem(id.remove_bookmark)?.isEnabled =
		chaptersSelected.any { it.bookmarked }

	// If any chapters are not bookmarked, show bookmark
	binding.bottomMenu.findItem(id.bookmark)?.isEnabled =
		chaptersSelected.any { !it.bookmarked }

	// If any are downloaded, show delete
	binding.bottomMenu.findItem(id.chapter_delete_selected)?.isEnabled =
		chaptersSelected.any { it.isSaved }

	// If any are not downloaded, show download option
	binding.bottomMenu.findItem(id.chapter_download_selected)?.isEnabled =
		chaptersSelected.any { !it.isSaved }

	// If any are unread, show read option
	binding.bottomMenu.findItem(id.mark_read)?.isEnabled =
		chaptersSelected.any { it.readingStatus != ReadingStatus.READ }

	// If any are read, show unread option
	binding.bottomMenu.findItem(id.mark_unread)?.isEnabled =
		chaptersSelected.any { it.readingStatus != ReadingStatus.UNREAD }
}
*/

/* TODO re-implement
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


}

 */

	private fun openWebView() {
		viewModel.getNovelURL().observe(
			catch = {
				makeSnackBar(
					getString(
						R.string.controller_novel_error_url,
						it.message ?: "Unknown"
					)
				)
					?.setAction(R.string.report) { _ ->
						ACRA.errorReporter.handleSilentException(it)
					}?.show()
			}
		) {
			if (it != null)
				activity?.openInWebView(it)
		}
	}

	private fun openFilterMenu() {
		BottomSheetDialog(activity!!).apply {
			setContentView(bottomMenuView)
		}.show()
	}

	override fun onDestroy() {
		try {
			viewModel.destroy()
		} catch (e: DestroyFailedException) {
			ACRA.errorReporter.handleException(e)
		}
		actionMode?.finish()
		super.onDestroy()
	}

	private fun setObserver() {
		/* TODO handle novel load exception
		makeSnackBar(
					getString(
						R.string.controller_novel_error_load,
						it.message ?: "Unknown"
					)
				)?.setAction(R.string.report) { _ ->
					ACRA.errorReporter.handleSilentException(it)
				}?.show()
		 */

		/* TODO handle chapter load exception
		 * makeSnackBar(
				getString(
					R.string.controller_novel_error_load_chapters,
					it.message ?: "Unknown"
				)
			)?.setAction(R.string.report) { _ ->
				ACRA.errorReporter.handleSilentException(it)
			}?.show()
		 */
	}

	private fun bookmarkSelected() {
		viewModel.bookmarkSelected()
	}

	private fun removeSelectedBookmark() {
		viewModel.removeBookmarkFromSelected()
	}

	private fun selectAll() {
		viewModel.selectAll()
	}

	private fun invertSelection() {
		viewModel.invertSelection()
	}

	/**
	 * Selects all chapters between the first and last selected chapter
	 */
	private fun selectBetween() {
		viewModel.selectBetween()
	}

	private fun trueDeleteSelection() {
		viewModel.trueDeleteSelected()
	}

	private inner class SelectionActionMode : ActionMode.Callback {
		override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
			// Hides the original action bar
			// (activity as MainActivity?)?.supportActionBar?.hide()

			mode.menuInflater.inflate(R.menu.toolbar_novel_chapters_selected, menu)

			viewModel.getIfAllowTrueDelete().observe(
				catch = {
					makeSnackBar(
						getString(
							R.string.controller_novel_error_true_delete,
							it.message ?: "Unknown"
						)
					)
						?.setAction(R.string.report) { _ ->
							ACRA.errorReporter.handleSilentException(it)
						}?.show()
				}
			) {
				menu.findItem(id.true_delete).isVisible = it
			}

			mode.setTitle(R.string.selection)
/*			 TODO
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

 */
//			calculateBottomSelectionMenuChanges() TODO
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
				id.true_delete -> {
					trueDeleteSelection()
					true
				}
				else -> false
			}

		override fun onDestroyActionMode(mode: ActionMode) {
			//binding.bottomMenu.hide() TODO
			//binding.bottomMenu.clear()
			actionMode = null
			showFAB(resume!!)
			//fastAdapter.getSelectExtension().deselect()
		}
	}
}

@Preview
@Composable
fun PreviewNovelInfoContent() {

	val info = NovelUI(
		id = 0,
		novelURL = "",
		extID = 1,
		extName = "Test",
		bookmarked = false,
		title = "Title",
		imageURL = "",
		description = "laaaaaaaaaaaaaaaaaaaaaaaaaa\nlaaaaaaaaaaaaaaaaaaa\nklaaaaaaaaaaaaa",
		loaded = true,
		language = "eng",
		genres = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"),
		authors = listOf("A", "B", "C"),
		artists = listOf("A", "B", "C"),
		tags = listOf("A", "B", "C"),
		status = Novel.Status.COMPLETED
	)

	val chapters = List(10) {
		ChapterUI(
			id = it,
			novelID = 0,
			link = "",
			extensionID = 0,
			title = "Test",
			releaseDate = "10/10/10",
			order = it.toDouble(),
			readingPosition = 0.95,
			readingStatus = when {
				it % 2 == 0 -> {
					ReadingStatus.READING
				}
				else -> {
					ReadingStatus.READ
				}
			},
			bookmarked = it % 2 == 0,
			isSaved = it % 2 != 0
		)

	}

	MdcTheme {
		NovelInfoContent(
			novelInfo = info,
			chapters = chapters,
			isRefreshing = false,
			onRefresh = {},
			openWebView = {},
			toggleBookmark = {},
			itemAt = 0,
			chapterContent = {
				NovelChapterContent(
					chapter = it,
					openChapter = { /*TODO*/ },
					selectionMode = false
				) {}
			},
			openFilter = {},
			openChapterJump = {}
		)
	}
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NovelInfoContent(
	novelInfo: NovelUI?,
	chapters: List<ChapterUI>?,
	itemAt: Int,
	isRefreshing: Boolean,
	onRefresh: () -> Unit,
	openWebView: () -> Unit,
	toggleBookmark: () -> Unit,
	openFilter: () -> Unit,
	openChapterJump: () -> Unit,
	chapterContent: @Composable (ChapterUI) -> Unit
) {

	Column(
		modifier = Modifier.fillMaxSize()
	) {
		if (isRefreshing)
			LinearProgressIndicator(
				modifier = Modifier.fillMaxWidth()
			)

		SwipeRefresh(state = SwipeRefreshState(false), onRefresh = onRefresh) {
			val state = rememberLazyListState(itemAt)

			LaunchedEffect(itemAt) {
				launch {
					state.scrollToItem(itemAt)
				}
			}

			LazyColumnScrollbar(
				state,
				thumbColor = MaterialTheme.colors.primary,
				thumbSelectedColor = MaterialTheme.colors.background
			) {
				LazyColumn(
					modifier = Modifier.fillMaxSize(),
					state = state
				) {
					if (novelInfo != null)
						item {
							NovelInfoHeaderContent(
								novelInfo = novelInfo,
								openWebview = openWebView,
								toggleBookmark = toggleBookmark,
								openChapterJump = openChapterJump,
								openFilter = openFilter
							)
						}
					else {
						item {
							LinearProgressIndicator(
								modifier = Modifier.fillMaxWidth()
							)
						}
					}

					if (chapters != null)
						NovelInfoChaptersContent(
							this,
							chapters,
							chapterContent
						)
				}
			}
		}
	}
}

@Preview
@Composable
fun PreviewChapterContent() {
	val chapter = ChapterUI(
		id = 0,
		novelID = 0,
		link = "",
		extensionID = 0,
		title = "Test",
		releaseDate = "10/10/10",
		order = 0.0,
		readingPosition = 0.95,
		readingStatus = ReadingStatus.READING,
		bookmarked = true,
		isSaved = true
	)

	MdcTheme {
		NovelChapterContent(
			chapter,
			openChapter = {},
			onToggleSelection = {},
			selectionMode = false
		)
	}
}

fun NovelInfoChaptersContent(
	scope: LazyListScope,
	chapters: List<ChapterUI>,
	chapterContent: @Composable (ChapterUI) -> Unit
) {
	chapters.forEach {
		scope.item {
			chapterContent(it)
		}
	}
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun NovelChapterContent(
	chapter: ChapterUI,
	selectionMode: Boolean,
	openChapter: () -> Unit,
	onToggleSelection: () -> Unit
) {
	Card(
		shape = RectangleShape,
		modifier = Modifier
			.let {
				if (chapter.readingStatus == ReadingStatus.READ)
					it.alpha(.5f)
				else it
			}
			.combinedClickable(
				onClick =
				if (!selectionMode)
					openChapter
				else onToggleSelection,
				onLongClick = onToggleSelection
			),
		border =
		if (chapter.isSelected) {
			BorderStroke(
				width = SELECTED_STROKE_WIDTH.dp,
				color = MaterialTheme.colors.primary
			)
		} else {
			null
		}
	) {
		Column(
			modifier = Modifier.padding(16.dp)
		) {
			Text(
				chapter.title,
				maxLines = 1,
				modifier = Modifier
					.fillMaxWidth()
					.padding(bottom = 8.dp),
				color = if (chapter.bookmarked) MaterialTheme.colors.primary else Color.Unspecified
			)

			Row(
				horizontalArrangement = Arrangement.SpaceBetween,
				modifier = Modifier.fillMaxWidth()
			) {
				Row {
					Text(
						chapter.releaseDate,
						fontSize = 12.sp,
						modifier = Modifier.padding(end = 8.dp)
					)

					if (chapter.readingStatus == ReadingStatus.READING)
						Row {
							Text(
								stringResource(R.string.controller_novel_chapter_position),
								fontSize = 12.sp,
								modifier = Modifier.padding(end = 4.dp)
							)
							Text(
								"%2.1f%%".format(chapter.readingPosition),
								fontSize = 12.sp
							)
						}
				}

				if (chapter.isSaved)
					Text(
						stringResource(R.string.downloaded),
						fontSize = 12.sp
					)
			}
		}
	}
}

@Preview
@Composable
fun PreviewHeaderContent() {
	val info = NovelUI(
		id = 0,
		novelURL = "",
		extID = 1,
		extName = "Test",
		bookmarked = false,
		title = "Title",
		imageURL = "",
		description = "laaaaaaaaaaaaaaaaaaaaaaaaaa\nlaaaaaaaaaaaaaaaaaaa\nklaaaaaaaaaaaaa",
		loaded = true,
		language = "eng",
		genres = listOf("A", "B", "C"),
		authors = listOf("A", "B", "C"),
		artists = listOf("A", "B", "C"),
		tags = listOf("A", "B", "C"),
		status = Novel.Status.COMPLETED
	)

	MdcTheme {
		NovelInfoHeaderContent(
			info,
			{},
			{},
			{},
			{}
		)
	}
}

@Composable
fun NovelInfoCoverContent(
	imageURL: String,
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
) {
	AsyncImage(
		ImageRequest.Builder(LocalContext.current)
			.data(imageURL)
			.placeholder(R.drawable.animated_refresh)
			.error(R.drawable.broken_image)
			.build(),
		stringResource(R.string.controller_novel_info_image),
		modifier = modifier
			.aspectRatio(.75f)
			.padding(top = 8.dp)
			.clickable(onClick = onClick)
	)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NovelInfoHeaderContent(
	novelInfo: NovelUI,
	openWebview: () -> Unit,
	toggleBookmark: () -> Unit,
	openFilter: () -> Unit,
	openChapterJump: () -> Unit
) {
	var isCoverClicked: Boolean by remember { mutableStateOf(false) }
	if (isCoverClicked)
		Dialog(onDismissRequest = { isCoverClicked = false }) {
			NovelInfoCoverContent(
				novelInfo.imageURL,
				modifier = Modifier.fillMaxWidth()
			) {
				isCoverClicked = false
			}
		}

	Column(
		modifier = Modifier.fillMaxWidth(),
	) {
		Box(
			modifier = Modifier.fillMaxWidth(),
		) {
			AsyncImage(
				ImageRequest.Builder(LocalContext.current)
					.data(novelInfo.imageURL)
					.placeholder(R.drawable.animated_refresh)
					.error(R.drawable.broken_image)
					.build(),
				stringResource(R.string.controller_novel_info_image),
				modifier = Modifier
					.matchParentSize()
					.alpha(.10f),
				contentScale = ContentScale.Crop,
			)

			Column(
				modifier = Modifier.fillMaxWidth(),
			) {
				Row(
					modifier = Modifier.fillMaxWidth(),
				) {
					NovelInfoCoverContent(
						novelInfo.imageURL,
						modifier = Modifier.fillMaxWidth(.35f)
					) {
						isCoverClicked = true
					}
					Column {
						Row(
							modifier = Modifier.padding(bottom = 8.dp)
						) {
							Text(novelInfo.title, style = MaterialTheme.typography.h5)
						}
						Row(
							modifier = Modifier.padding(bottom = 8.dp)
						) {
							Text(
								stringResource(R.string.site),
								style = MaterialTheme.typography.caption
							)
							Text(
								novelInfo.extName,
								style = MaterialTheme.typography.caption
							)
						}
						if (novelInfo.authors.isNotEmpty())
							Row(
								modifier = Modifier.padding(bottom = 8.dp)
							) {
								Text(
									stringResource(R.string.authors),
									style = MaterialTheme.typography.caption
								)
								Text(
									novelInfo.authors.toString(),
									style = MaterialTheme.typography.caption
								)
							}
						if (novelInfo.artists.isNotEmpty())
							Row(
								modifier = Modifier.padding(bottom = 8.dp)
							) {
								Text(
									stringResource(R.string.artists),
									style = MaterialTheme.typography.caption
								)
								Text(
									novelInfo.artists.toString(),
									style = MaterialTheme.typography.caption
								)
							}
						Row {
							Text(
								stringResource(R.string.publishing_state),
								style = MaterialTheme.typography.caption
							)
							Text(
								when (novelInfo.status) {
									Novel.Status.PUBLISHING -> stringResource(R.string.publishing)
									Novel.Status.COMPLETED -> stringResource(R.string.completed)
									Novel.Status.PAUSED -> stringResource(R.string.paused)
									Novel.Status.UNKNOWN -> stringResource(R.string.unknown)
								},
								style = MaterialTheme.typography.caption
							)
						}
					}
				}

				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceEvenly,
					verticalAlignment = Alignment.CenterVertically
				) {
					IconButton(
						onClick = toggleBookmark,
						modifier = Modifier.padding(8.dp)
					) {
						Column(
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							if (novelInfo.bookmarked) {
								Icon(
									painterResource(R.drawable.ic_heart_svg_filled),
									null,
									tint = MaterialTheme.colors.primary
								)
								Text(stringResource(R.string.controller_novel_in_library))
							} else {
								Icon(
									painterResource(R.drawable.ic_heart_svg),
									null,
									tint = MaterialTheme.colors.primary
								)
								Text(stringResource(R.string.controller_novel_add_to_library))
							}
						}
					}
					IconButton(onClick = openWebview) {
						Column(
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							Icon(
								painterResource(R.drawable.open_in_browser),
								stringResource(R.string.controller_novel_info_open_web)
							)
							Text(stringResource(R.string.open_in_webview))
						}
					}
				}

				LazyRow(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.Center,
					contentPadding = PaddingValues(start = 8.dp, end = 8.dp)
				) {
					items(novelInfo.genres) {
						Card(
							modifier = Modifier.padding(end = 8.dp),
						) {
							Text(it, modifier = Modifier.padding(8.dp))
						}
					}
				}

			}
		}

		ExpandedText(
			text = novelInfo.description,
			modifier = Modifier
				.fillMaxWidth()
				.padding(start = 8.dp, end = 8.dp, top = 8.dp),
		)

		Divider()

		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
			modifier = Modifier
				.fillMaxWidth()
				.padding(start = 8.dp, end = 8.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(stringResource(R.string.chapters))

			Row {
				Card(
					onClick = openChapterJump,
					modifier = Modifier.padding(end = 8.dp),
				) {
					Text(
						stringResource(R.string.jump_to_chapter_short),
						modifier = Modifier.padding(8.dp),
					)
				}

				Card(
					onClick = openFilter
				) {
					Row(
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier.padding(8.dp),
					) {
						Icon(painterResource(R.drawable.filter), null)
						Text(stringResource(R.string.filter))
					}
				}
			}
		}

		Divider()
	}
}

@Composable
fun ExpandedText(
	text: String,
	modifier: Modifier = Modifier,
) {
	var isExpanded by remember { mutableStateOf(false) }

	Column(
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = modifier
	) {
		Text(
			if (isExpanded) {
				text
			} else {
				text.let {
					if (it.length > 100)
						it.substring(0, 100) + "..."
					else it
				}
			}
		)

		TextButton(
			onClick = {
				isExpanded = !isExpanded
			}
		) {
			Text(
				if (!isExpanded)
					stringResource(R.string.more)
				else stringResource(R.string.less)
			)
		}
	}
}