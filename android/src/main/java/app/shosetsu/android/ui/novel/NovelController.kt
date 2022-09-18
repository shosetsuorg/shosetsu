package app.shosetsu.android.ui.novel

import android.content.Context
import android.content.res.Resources
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
import android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
import android.view.*
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import app.shosetsu.android.R
import app.shosetsu.android.R.drawable
import app.shosetsu.android.R.plurals
import app.shosetsu.android.R.string
import app.shosetsu.android.activity.MainActivity
import app.shosetsu.android.common.FilePermissionException
import app.shosetsu.android.common.NoSuchExtensionException
import app.shosetsu.android.common.enums.ReadingStatus
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.databinding.ControllerNovelJumpDialogBinding
import app.shosetsu.android.ui.migration.MigrationController.Companion.TARGETS_BUNDLE_KEY
import app.shosetsu.android.view.compose.ImageLoadingError
import app.shosetsu.android.view.compose.LazyColumnScrollbar
import app.shosetsu.android.view.compose.ShosetsuCompose
import app.shosetsu.android.view.compose.TextButton
import app.shosetsu.android.view.compose.coverRatio
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.view.controller.base.ExtendedFABController
import app.shosetsu.android.view.controller.base.ExtendedFABController.EFabMaintainer
import app.shosetsu.android.view.controller.base.syncFABWithCompose
import app.shosetsu.android.view.openQRCodeShareDialog
import app.shosetsu.android.view.openShareMenu
import app.shosetsu.android.view.uimodels.model.CategoryUI
import app.shosetsu.android.view.uimodels.model.ChapterUI
import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.android.viewmodel.abstracted.ANovelViewModel
import app.shosetsu.android.viewmodel.abstracted.ANovelViewModel.SelectedChaptersState
import app.shosetsu.lib.Novel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
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
class NovelController : ShosetsuController(),
	ExtendedFABController, MenuProvider {

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

	private var resume: EFabMaintainer? = null

	private var actionMode: ActionMode? = null

	private val bottomMenuView: View
		get() = NovelFilterMenuBuilder(
			this,
			activity!!.layoutInflater,
			viewModel
		).build()

	override fun onAttach(context: Context) {
		if (viewModel.isFromChapterReader) viewModel.deletePrevious().collectDeletePrevious()
		super.onAttach(context)
	}

	private fun startSelectionAction() {
		if (actionMode != null) return
		hideFAB(resume!!)
		actionMode = activity?.startActionMode(SelectionActionMode())
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

	override fun showFAB(fab: EFabMaintainer) {
		if (actionMode == null) super.showFAB(fab)
	}

	override fun manipulateFAB(fab: EFabMaintainer) {
		resume = fab
		fab.setOnClickListener { openLastRead() }
		fab.setIconResource(drawable.play_arrow)
		fab.setText(string.resume)
	}

	private fun openLastRead() {
		var job: Job? = null
		job = viewModel.openLastRead().firstLa(this, catch = {
			logE("Loading last read hit an error")
		}) { chapterUI ->
			if (chapterUI != null) {
				activity?.openChapter(chapterUI)
			} else {
				makeSnackBar(string.controller_novel_snackbar_finished_reading)?.show()
			}
			job?.cancel()
		}
	}

	@Suppress("unused")
	fun migrateOpen() {
		findNavController().navigateSafely(
			R.id.action_novelController_to_migrationController,
			bundleOf(
				TARGETS_BUNDLE_KEY to arrayOf(arguments!!.getNovelID()).toIntArray()
			),
			navOptions {
				setShosetsuTransition()
			}
		)
	}

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
								string.controller_novel_error_share,
								it.message ?: "Unknown"
							)
						)
							?.setAction(string.report) { _ ->
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

	override fun onMenuItemSelected(item: MenuItem): Boolean = when (item.itemId) {
		R.id.source_migrate -> {
			migrateOpen()
			true
		}
		R.id.webview -> {
			openWebView()
			true
		}
		R.id.share -> {
			openShare()
			true
		}
		R.id.option_chapter_jump -> {
			openChapterJumpDialog()
			true
		}
		R.id.download_next -> {
			viewModel.downloadNextChapter()
			true
		}
		R.id.download_next_5 -> {
			viewModel.downloadNext5Chapters()
			true
		}
		R.id.download_next_10 -> {
			viewModel.downloadNext10Chapters()
			true
		}
		R.id.download_custom -> {
			downloadCustom()
			true
		}
		R.id.download_unread -> {
			viewModel.downloadAllUnreadChapters()
			true
		}
		R.id.download_all -> {
			viewModel.downloadAllChapters()
			true
		}
		R.id.set_categories -> {
			categoriesDialogOpen = true
			true
		}
		else -> false
	}

	private fun openChapterJumpDialog() {
		val binding =
			ControllerNovelJumpDialogBinding.inflate(LayoutInflater.from(activity!!))

		// Change hint & input type depending on findByChapterName state
		binding.findByChapterName.setOnCheckedChangeListener { _, isChecked ->
			if (isChecked) {
				binding.editTextNumber.inputType = TYPE_TEXT_FLAG_NO_SUGGESTIONS
				binding.editTextNumber.setHint(string.controller_novel_jump_dialog_hint_chapter_title)
			} else {
				binding.editTextNumber.inputType = TYPE_NUMBER_FLAG_DECIMAL
				binding.editTextNumber.setHint(string.controller_novel_jump_dialog_hint_chapter_number)
			}
		}

		AlertDialog.Builder(activity!!)
			.setView(binding.root)
			.setTitle(string.jump_to_chapter)
			.setNegativeButton(android.R.string.cancel) { _, _ ->
			}
			.setPositiveButton(string.alert_dialog_jump_positive) { _, _ ->
				/**
				 * The predicate to use to find the chapter to scroll to
				 */
				val predicate: (ChapterUI) -> Boolean

				@Suppress("LiftReturnOrAssignment")
				if (binding.findByChapterName.isChecked) {
					// Predicate will be searching if the title contains the text
					val text = binding.editTextNumber.text.toString()
					if (text.isBlank()) {
						makeSnackBar(string.toast_error_chapter_jump_empty_title)
							?.setAction(string.generic_question_retry) { openChapterJumpDialog() }
							?.show()
						return@setPositiveButton
					}

					predicate = { it.title.contains(text) }
				} else {
					// Predicate will be searching if the # is equal

					val selectedNumber =
						binding.editTextNumber.text.toString().toDoubleOrNull()?.plus(1.0) ?: run {
							makeSnackBar(string.toast_error_chapter_jump_invalid_number)
								?.setAction(string.generic_question_retry) { openChapterJumpDialog() }
								?.show()
							return@setPositiveButton
						}

					predicate = { it.order == selectedNumber }
				}

				viewModel.scrollTo(predicate).collectLA(this, catch = {}) {
					if (!it) {
						makeSnackBar(string.toast_error_chapter_jump_invalid_target)
							?.setAction(string.generic_question_retry) {
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
		viewModel.getChapterCount().collectLA(this, catch = {}) { max ->
			AlertDialog.Builder(activity!!).apply {
				setTitle(string.download_custom_chapters)
				val numberPicker = NumberPicker(activity!!).apply {
					minValue = 0
					maxValue = max
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

	}

	override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_novel, menu)

		runBlocking {
			menu.findItem(R.id.source_migrate).isVisible = viewModel.isBookmarked().first()
			menu.findItem(R.id.set_categories).isVisible = viewModel.categories.first().isNotEmpty()
		}
	}

	private var state = LazyListState(0)
	private var categoriesDialogOpen by mutableStateOf(false)

	private fun setCategories(categories: IntArray) {
		viewModel.setNovelCategories(categories).firstLa(this, catch = {}) {}
	}

	private fun toggleBookmark() {
		viewModel.toggleNovelBookmark().firstLa(this@NovelController, catch = {}) {
			when (it) {
				is ANovelViewModel.ToggleBookmarkResponse.DeleteChapters -> {
					makeSnackBar(
						try {
							resources.getQuantityString(
								plurals.controller_novel_toggle_delete_chapters,
								it.chapters,
								it.chapters
							)
						} catch (e: Resources.NotFoundException) {
							"Delete ${it.chapters} chapters?"
						}
					)?.setAction(string.delete) {
						viewModel.deleteChapters()
					}?.show()
				}
				ANovelViewModel.ToggleBookmarkResponse.Nothing -> {
				}
			}
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedViewState: Bundle?
	): View {
		activity?.addMenuProvider(this, viewLifecycleOwner)
		setViewTitle()
		return ComposeView(requireContext()).apply {
			setContent {
				if (resume != null)
					syncFABWithCompose(state, resume!!)
				val novelInfo by viewModel.novelLive.collectAsState(null)
				val chapters by viewModel.chaptersLive.collectAsState(emptyList())
				val isRefreshing by viewModel.isRefreshing.collectAsState(false)
				val hasSelected by viewModel.hasSelected.collectAsState(false)
				val itemAt by viewModel.itemIndex.collectAsState(0)
				val categories by viewModel.categories.collectAsState(emptyList())
				val novelCategories by viewModel.novelCategories.collectAsState(emptyList())

				activity?.invalidateOptionsMenu()
				// If the data is not present, loads it
				if (novelInfo != null && !novelInfo!!.loaded) {
					if (viewModel.isOnline()) {
						refresh()
					} else {
						displayOfflineSnackBar(string.controller_novel_snackbar_cannot_inital_load_offline)
					}
				}

				ShosetsuCompose {
					NovelInfoContent(
						novelInfo,
						chapters,
						selectedChaptersStateFlow = viewModel.selectedChaptersState,
						itemAt,
						updateItemAt = viewModel::setItemAt,
						isRefreshing,
						onRefresh = {
							if (viewModel.isOnline())
								refresh()
							else displayOfflineSnackBar()
						},
						openWebView = ::openWebView,
						categories = categories,
						setCategoriesDialogOpen = { categoriesDialogOpen = true },
						toggleBookmark = ::toggleBookmark,
						openFilter = ::openFilterMenu,
						openChapterJump = ::openChapterJumpDialog,
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
						downloadSelected = viewModel::downloadSelected,
						deleteSelected = viewModel::deleteSelected,
						markSelectedAsRead = {
							viewModel.markSelectedAs(ReadingStatus.READ)
						},
						markSelectedAsUnread = {
							viewModel.markSelectedAs(ReadingStatus.UNREAD)
						},
						bookmarkSelected = viewModel::bookmarkSelected,
						unbookmarkSelected = viewModel::removeBookmarkFromSelected,
						hasSelected = hasSelected,
						state = state
					)

					if (categoriesDialogOpen)
						CategoriesDialog(
							onDismissRequest = { categoriesDialogOpen = false },
							categories = categories,
							novelCategories = novelCategories,
							setCategories = ::setCategories
						)
				}
			}
		}
	}

	private fun Flow<Boolean>.collectDeletePrevious() {
		collectLA(this@NovelController, catch = {
			when (it) {
				is SQLiteException ->
					makeSnackBar(
						getString(
							string.controller_novel_delete_previous_fail,
							it.message ?: ""
						)
					)?.show()
				is FilePermissionException ->
					makeSnackBar(
						getString(
							string.controller_novel_delete_previous_fail,
							it.message ?: ""
						)
					)?.show()
				is NoSuchExtensionException ->
					makeSnackBar(
						getString(
							string.missing_extension,
							it.extensionId
						)
					)?.show()
			}
			emit(false)
		}) {
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		viewModel.setNovelID(arguments!!.getNovelID())
		if (viewModel.isFromChapterReader) viewModel.deletePrevious().collectDeletePrevious()

		viewModel.hasSelected.collectLatestLA(this, catch = {}) { hasSelected ->
			if (hasSelected) {
				startSelectionAction()
			} else {
				finishSelectionAction()
			}
		}

		viewModel.novelException.collectLatestLA(this, catch = {}) {
			if (it != null)
				makeSnackBar(
					getString(
						string.controller_novel_error_load,
						it.message ?: "Unknown"
					)
				)?.setAction(string.report) { _ ->
					ACRA.errorReporter.handleSilentException(it)
				}?.show()
		}

		viewModel.chaptersException.collectLatestLA(this, catch = {}) {
			if (it != null)
				makeSnackBar(
					getString(
						string.controller_novel_error_load_chapters,
						it.message ?: "Unknown"
					)
				)?.setAction(string.report) { _ ->
					ACRA.errorReporter.handleSilentException(it)
				}?.show()
		}

		viewModel.otherException.collectLatestLA(this, catch = {}) {
			// TODO Figure out use of other exception
		}

	}

	private fun openWebView() {
		var job: Job? = null
		job = viewModel.getNovelURL().firstLa(
			this,
			catch = {
				makeSnackBar(
					getString(
						string.controller_novel_error_url,
						it.message ?: "Unknown"
					)
				)
					?.setAction(string.report) { _ ->
						ACRA.errorReporter.handleSilentException(it)
					}?.show()
			}
		) {
			if (it != null) {
				activity?.openInWebView(it)
				job?.cancel("")
			}
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
		state = LazyListState(0)
		super.onDestroy()
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
							string.controller_novel_error_true_delete,
							it.message ?: "Unknown"
						)
					)
						?.setAction(string.report) { _ ->
							ACRA.errorReporter.handleSilentException(it)
						}?.show()
				}
			) {
				menu.findItem(R.id.true_delete).isVisible = it
			}

			mode.setTitle(string.selection)
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
				R.id.true_delete -> {
					trueDeleteSelection()
					true
				}
				else -> false
			}

		override fun onDestroyActionMode(mode: ActionMode) {
			actionMode = null
			showFAB(resume!!)
			viewModel.clearSelection()
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

	ShosetsuCompose {
		NovelInfoContent(
			novelInfo = info,
			chapters = chapters,
			selectedChaptersStateFlow = flow { },
			itemAt = 0,
			{},
			isRefreshing = false,
			onRefresh = {},
			openWebView = {},
			emptyList(),
			{},
			toggleBookmark = {},
			openFilter = {},
			openChapterJump = {},
			chapterContent = {
				NovelChapterContent(
					chapter = it,
					openChapter = { },
					selectionMode = false
				) {}
			},
			{},
			{},
			{},
			{},
			{},
			{},
			hasSelected = false,
			state = rememberLazyListState(0)
		)
	}
}

@Composable
fun NovelInfoContent(
	novelInfo: NovelUI?,
	chapters: List<ChapterUI>?,
	selectedChaptersStateFlow: Flow<SelectedChaptersState>,
	itemAt: Int,
	updateItemAt: (Int) -> Unit,
	isRefreshing: Boolean,
	onRefresh: () -> Unit,
	openWebView: () -> Unit,
	categories: List<CategoryUI>,
	setCategoriesDialogOpen: (Boolean) -> Unit,
	toggleBookmark: () -> Unit,
	openFilter: () -> Unit,
	openChapterJump: () -> Unit,
	chapterContent: @Composable (ChapterUI) -> Unit,
	downloadSelected: () -> Unit,
	deleteSelected: () -> Unit,
	markSelectedAsRead: () -> Unit,
	markSelectedAsUnread: () -> Unit,
	bookmarkSelected: () -> Unit,
	unbookmarkSelected: () -> Unit,
	hasSelected: Boolean,
	state: LazyListState
) {
	Box(
		modifier = Modifier.fillMaxSize()
	) {
		Column(
			modifier = Modifier.fillMaxSize()
		) {
			SwipeRefresh(state = SwipeRefreshState(false), onRefresh = onRefresh) {
				LazyColumnScrollbar(
					state,
					thumbColor = MaterialTheme.colors.primary,
					thumbSelectedColor = Color.Gray
				) {
					LazyColumn(
						modifier = Modifier.fillMaxSize(),
						state = state,
						contentPadding = PaddingValues(bottom = 256.dp)
					) {
						if (novelInfo != null)
							item {
								NovelInfoHeaderContent(
									novelInfo = novelInfo,
									openWebview = openWebView,
									categories = categories,
									setCategoriesDialogOpen = setCategoriesDialogOpen,
									toggleBookmark = toggleBookmark,
									openChapterJump = openChapterJump,
									openFilter = openFilter,
									chapterCount = chapters?.size ?: 0
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
								chapters,
								chapterContent
							)
					}
				}

				// Do not save progress when there is nothing being displayed
				if (novelInfo != null && chapters != null) {
					LaunchedEffect(itemAt) {
						launch {
							if (!state.isScrollInProgress)
								state.scrollToItem(itemAt)
						}
					}

					LaunchedEffect(state) {
						snapshotFlow { state.layoutInfo.visibleItemsInfo.firstOrNull()?.index }
							.distinctUntilChanged()
							.collect { item ->
								if (item == null) return@collect
								println("Updating item at: $item")
								updateItemAt(item)
							}
					}
				}
			}
		}

		if (chapters != null && hasSelected) {
			val selectedChaptersState by selectedChaptersStateFlow.collectAsState(
				SelectedChaptersState()
			)
			Card(
				modifier = Modifier
					.align(BiasAlignment(0f, 0.7f))
			) {
				Row {
					IconButton(
						onClick = downloadSelected,
						enabled = selectedChaptersState.showDownload
					) {
						Icon(
							painterResource(drawable.download),
							stringResource(string.controller_novel_selected_download)
						)
					}
					IconButton(
						onClick = deleteSelected,
						enabled = selectedChaptersState.showDelete
					) {
						Icon(
							painterResource(drawable.trash),
							stringResource(string.controller_novel_selected_delete)
						)
					}
					IconButton(
						onClick = markSelectedAsRead,
						enabled = selectedChaptersState.showMarkAsRead
					) {
						Icon(
							painterResource(drawable.read_mark),
							stringResource(string.controller_novel_selected_read)
						)
					}
					IconButton(
						onClick = markSelectedAsUnread,
						enabled = selectedChaptersState.showMarkAsUnread
					) {
						Icon(
							painterResource(drawable.unread_mark),
							stringResource(string.controller_novel_selected_unread)
						)
					}
					IconButton(
						onClick = bookmarkSelected,
						enabled = selectedChaptersState.showBookmark
					) {
						Icon(
							painterResource(drawable.ic_outline_bookmark_add_24),
							stringResource(string.controller_novel_selected_bookmark)
						)
					}
					IconButton(
						onClick = unbookmarkSelected,
						enabled = selectedChaptersState.showRemoveBookmark
					) {
						Icon(
							painterResource(drawable.ic_baseline_bookmark_remove_24),
							stringResource(string.controller_novel_selected_unbookmark)
						)
					}
				}
			}
		}
		if (isRefreshing)
			LinearProgressIndicator(
				modifier = Modifier.fillMaxWidth()
			)
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

	ShosetsuCompose {
		NovelChapterContent(
			chapter,
			openChapter = {},
			onToggleSelection = {},
			selectionMode = false
		)
	}
}

fun LazyListScope.NovelInfoChaptersContent(
	chapters: List<ChapterUI>,
	chapterContent: @Composable (ChapterUI) -> Unit
) {
	items(chapters) { chapterContent(it) }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NovelChapterContent(
	chapter: ChapterUI,
	selectionMode: Boolean,
	openChapter: () -> Unit,
	onToggleSelection: () -> Unit
) {
	Box(
		modifier = Modifier
			.let {
				if (chapter.readingStatus == ReadingStatus.READ)
					it.alpha(.5f)
				else it
			}
			.background(
				if (chapter.isSelected) {
					MaterialTheme.colors.secondary.copy(alpha = if (isSystemInDarkTheme()) 0.08f else 0.22f)
				} else {
					MaterialTheme.colors.surface
				}
			)
			.combinedClickable(
				onClick =
				if (!selectionMode)
					openChapter
				else onToggleSelection,
				onLongClick = onToggleSelection
			),
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
								stringResource(string.controller_novel_chapter_position),
								fontSize = 12.sp,
								modifier = Modifier.padding(end = 4.dp)
							)
							Text(
								chapter.displayPosition,
								fontSize = 12.sp
							)
						}
				}

				if (chapter.isSaved)
					Text(
						stringResource(string.downloaded),
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

	ShosetsuCompose {
		NovelInfoHeaderContent(
			info,
			chapterCount = 0,
			{},
			emptyList(),
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
	contentScale: ContentScale = ContentScale.Fit,
	onClick: () -> Unit,
) {
	SubcomposeAsyncImage(
		ImageRequest.Builder(LocalContext.current)
			.data(imageURL)
			.crossfade(true)
			.build(),
		stringResource(string.controller_novel_info_image),
		modifier = modifier
			.clickable(onClick = onClick),
		contentScale = contentScale,
		error = {
			ImageLoadingError()
		},
		loading = {
			Box(Modifier.placeholder(true))
		}
	)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NovelInfoHeaderContent(
	novelInfo: NovelUI,
	chapterCount: Int,
	openWebview: () -> Unit,
	categories: List<CategoryUI>,
	toggleBookmark: () -> Unit,
	openFilter: () -> Unit,
	openChapterJump: () -> Unit,
	setCategoriesDialogOpen: (Boolean) -> Unit,
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
		// Novel information
		Box(
			modifier = Modifier.fillMaxWidth(),
		) {
			SubcomposeAsyncImage(
				ImageRequest.Builder(LocalContext.current)
					.data(novelInfo.imageURL)
					.crossfade(true)
					.build(),
				stringResource(string.controller_novel_info_image),
				modifier = Modifier
					.matchParentSize()
					.alpha(.10f),
				contentScale = ContentScale.Crop,
				error = {
					ImageLoadingError()
				},
				loading = {
					Box(Modifier.placeholder(true))
				}
			)

			Column(
				modifier = Modifier.fillMaxWidth(),
			) {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.padding(end = 8.dp),
					verticalAlignment = Alignment.CenterVertically
				) {
					NovelInfoCoverContent(
						novelInfo.imageURL,
						modifier = Modifier
							.fillMaxWidth(.35f)
							.aspectRatio(coverRatio)
							.padding(top = 8.dp, start = 4.dp)
							.clip(MaterialTheme.shapes.medium),
						contentScale = ContentScale.Crop
					) {
						isCoverClicked = true
					}
					Column(
						modifier = Modifier.padding(
							top = 16.dp,
							start = 8.dp,
							end = 8.dp
						),
						verticalArrangement = Arrangement.Center
					) {
						Text(
							novelInfo.title,
							style = MaterialTheme.typography.h6,
							modifier = Modifier
								.padding(bottom = 8.dp)
								.fillMaxWidth(),
						)
						if (novelInfo.authors.isNotEmpty() && novelInfo.authors.all { it.isNotEmpty() })
							Row(
								modifier = Modifier.padding(bottom = 8.dp)
							) {
								if (novelInfo.artists.isEmpty() && novelInfo.artists.none { it.isNotEmpty() })
									Text(
										stringResource(string.novel_author),
										style = MaterialTheme.typography.subtitle2
									)
								Text(
									novelInfo.displayAuthors,
									style = MaterialTheme.typography.subtitle2
								)
							}

						if (novelInfo.artists.isNotEmpty() && novelInfo.artists.all { it.isNotEmpty() })
							Row(
								modifier = Modifier.padding(bottom = 8.dp)
							) {
								if (novelInfo.authors.isEmpty() && novelInfo.authors.none { it.isNotEmpty() })
									Text(
										stringResource(string.artist_s),
										style = MaterialTheme.typography.subtitle2
									)
								Text(
									novelInfo.displayArtists,
									style = MaterialTheme.typography.subtitle2
								)
							}

						Row {
							Text(
								when (novelInfo.status) {
									Novel.Status.PUBLISHING -> stringResource(string.publishing)
									Novel.Status.COMPLETED -> stringResource(string.completed)
									Novel.Status.PAUSED -> stringResource(string.paused)
									Novel.Status.UNKNOWN -> stringResource(string.unknown)
								},
								style = MaterialTheme.typography.subtitle2
							)
							Text(
								" • ",
								style = MaterialTheme.typography.subtitle2
							)
							Text(
								novelInfo.extName,
								style = MaterialTheme.typography.subtitle2
							)
						}
					}
				}

				// Bookmark & Web view
				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.SpaceEvenly,
					verticalAlignment = Alignment.CenterVertically
				) {
					TextButton(
						onClick = {
							if (novelInfo.bookmarked || categories.isEmpty()) {
								toggleBookmark()
							} else {
								setCategoriesDialogOpen(true)
							}
						},
						onLongClick = {
							setCategoriesDialogOpen(true)
						},
						modifier = Modifier
							.padding(vertical = 8.dp, horizontal = 4.dp)
							.weight(1F)
					) {
						Column(
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							Icon(
								if (novelInfo.bookmarked) {
									painterResource(drawable.ic_heart_svg_filled)
								} else {
									painterResource(drawable.ic_heart_svg)
								},
								null,
								tint = if (novelInfo.bookmarked)
									MaterialTheme.colors.primary
								else
									MaterialTheme.colors.onSurface,
								modifier = Modifier.size(20.dp)
							)
							Spacer(Modifier.height(4.dp))
							Text(
								stringResource(
									if (novelInfo.bookmarked) {
										string.controller_novel_in_library
									} else {
										string.controller_novel_add_to_library
									}
								),
								style = MaterialTheme.typography.body1,
								color = if (novelInfo.bookmarked)
									MaterialTheme.colors.primary
								else
									MaterialTheme.colors.onSurface,
								fontSize = 12.sp,
								textAlign = TextAlign.Center,
							)
						}
					}
					TextButton(
						onClick = openWebview,
						modifier = Modifier
							.padding(vertical = 8.dp, horizontal = 4.dp)
							.weight(1F)
					) {
						Column(
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							Icon(
								painterResource(drawable.open_in_browser),
								stringResource(string.controller_novel_info_open_web),
								modifier = Modifier.size(20.dp),
								tint = MaterialTheme.colors.onSurface
							)
							Spacer(Modifier.height(4.dp))
							Text(
								stringResource(string.controller_novel_info_open_web_text),
								color = MaterialTheme.colors.onSurface,
								fontSize = 12.sp,
								textAlign = TextAlign.Center,
							)
						}
					}
				}

				LazyRow(
					modifier = Modifier
						.fillMaxWidth()
						.padding(bottom = 8.dp),
					horizontalArrangement = Arrangement.Center,
					contentPadding = PaddingValues(start = 8.dp, end = 8.dp)
				) {
					items(novelInfo.genres) {
						Card(
							modifier = Modifier.padding(end = 8.dp),
						) {
							Text(
								it,
								modifier = Modifier.padding(8.dp),
								style = MaterialTheme.typography.body2
							)
						}
					}
				}
			}
		}

		// Description
		ExpandedText(
			text = novelInfo.description,
			modifier = Modifier
				.fillMaxWidth()
				.padding(start = 8.dp, end = 8.dp, top = 8.dp),
		)

		Divider()

		// Chapters header bar
		Row(
			horizontalArrangement = Arrangement.SpaceBetween,
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 16.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Row {
				Text(stringResource(string.chapters))
				Text("$chapterCount", modifier = Modifier.padding(start = 8.dp))
			}

			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.padding(8.dp)
					.height(34.dp)
			) {
				Card(
					onClick = openChapterJump,
					modifier = Modifier.height(32.dp),
				) {
					Box(
						modifier = Modifier
							.fillMaxHeight()
							.padding(horizontal = 4.dp),
						contentAlignment = Alignment.Center
					) {
						Text(
							stringResource(string.jump_to_chapter_short),
						)
					}
				}

				Card(
					onClick = openFilter,
					modifier = Modifier
						.padding(start = 8.dp)
						.height(32.dp)
				) {
					Row(
						Modifier
							.fillMaxHeight()
							.padding(horizontal = 4.dp),
						verticalAlignment = Alignment.CenterVertically,
					) {
						Icon(
							painterResource(drawable.filter),
							null,
						)
						Text(stringResource(string.filter))
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
		modifier = modifier then Modifier.clickable(
			indication = null,
			onClick = { isExpanded = !isExpanded },
			interactionSource = remember { MutableInteractionSource() }
		)
	) {
		Text(
			if (isExpanded) {
				text
			} else {
				text.let {
					if (it.length > 200)
						it.substring(0, 200) + "..."
					else it
				}
			},
			style = MaterialTheme.typography.body2
		)

		if (!isExpanded)
			Icon(painterResource(drawable.expand_more), contentDescription = stringResource(string.more))
		else Icon(painterResource(drawable.expand_less), contentDescription = stringResource(string.less))
	}
}

@Composable
fun CategoriesDialog(
	onDismissRequest: () -> Unit,
	categories: List<CategoryUI>,
	novelCategories: List<Int>,
	setCategories: (IntArray) -> Unit
) {
	val selectedCategories = remember(novelCategories) {
		novelCategories.toMutableStateList()
	}
	AlertDialog(
		onDismissRequest = onDismissRequest,
		confirmButton = {
			TextButton(
				onClick = {
					setCategories(selectedCategories.toIntArray())
					onDismissRequest()
				}
			) {
				Text(stringResource(android.R.string.ok))
			}
		},
		dismissButton = {
			TextButton(onClick = onDismissRequest) {
				Text(stringResource(android.R.string.cancel))
			}
		},
		title = {
			Text(stringResource(string.set_categories))
		},
		text = {
			Column(Modifier.verticalScroll(rememberScrollState())) {
				categories.filterNot { it.id == 0 }.forEach {
					Row(
						Modifier
							.fillMaxWidth()
							.height(56.dp)
							.clickable {
								if (it.id in selectedCategories) {
									selectedCategories -= it.id
								} else {
									selectedCategories += it.id
								}
							},
						verticalAlignment = Alignment.CenterVertically
					) {
						Checkbox(
							checked = it.id in selectedCategories,
							onCheckedChange = null,
							modifier = Modifier.padding(horizontal = 8.dp)
						)
						Text(it.name)
					}
				}
			}
		}
	)
}