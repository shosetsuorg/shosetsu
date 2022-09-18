package app.shosetsu.android.ui.catalogue

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import app.shosetsu.android.R
import app.shosetsu.android.activity.MainActivity
import app.shosetsu.android.common.SettingKey
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_EXTENSION
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_NOVEL_ID
import app.shosetsu.android.common.enums.NovelCardType
import app.shosetsu.android.common.enums.NovelCardType.*
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.catalogue.listeners.CatalogueSearchQuery
import app.shosetsu.android.ui.novel.CategoriesDialog
import app.shosetsu.android.view.ComposeBottomSheetDialog
import app.shosetsu.android.view.compose.*
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.view.controller.base.ExtendedFABController
import app.shosetsu.android.view.controller.base.ExtendedFABController.EFabMaintainer
import app.shosetsu.android.view.controller.base.syncFABWithCompose
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.android.viewmodel.abstracted.ACatalogViewModel
import app.shosetsu.android.viewmodel.abstracted.ACatalogViewModel.BackgroundNovelAddProgress.ADDED
import app.shosetsu.android.viewmodel.abstracted.ACatalogViewModel.BackgroundNovelAddProgress.ADDING
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import org.acra.ACRA

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
 */
class CatalogController : ShosetsuController(), ExtendedFABController, MenuProvider {
	private var bsg: BottomSheetDialog? = null

	/***/
	val viewModel: ACatalogViewModel by viewModel()
	//private val progressAdapter by lazy { ItemAdapter<ProgressItem>() }

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedViewState: Bundle?
	): View {
		activity?.addMenuProvider(this, viewLifecycleOwner)
		setViewTitle()
		return ComposeView(requireContext()).apply {
			setContent {
				ShosetsuCompose {
					val type by viewModel.novelCardTypeLive.collectAsState(NORMAL)

					val columnsInV by viewModel.columnsInV.collectAsState(SettingKey.ChapterColumnsInPortait.default)
					val columnsInH by viewModel.columnsInH.collectAsState(SettingKey.ChapterColumnsInLandscape.default)

					val items = viewModel.itemsLive.collectAsLazyPagingItems()

					val exception by viewModel.exceptionFlow.collectAsState(null)
					val hasFilters by viewModel.hasFilters.collectAsState(false)

					val categories by viewModel.categories.collectAsState(emptyList())
					var categoriesDialogItem by remember { mutableStateOf<ACatalogNovelUI?>(null) }

					if (exception != null)
						LaunchedEffect(Unit) {
							launchUI {
								makeSnackBar(exception!!.message ?: "Unknown error")
									?.setAction(R.string.retry) {
										viewModel.resetView()
									}
									?.show()
							}
						}

					val prepend = items.loadState.prepend
					if (prepend is LoadState.Error) {
						LaunchedEffect(Unit) {
							launchUI {
								makeSnackBar(prepend.error.message ?: "Unknown error")
									?.setAction(R.string.retry) {
										items.retry()
									}
									?.show()
							}
						}
					}
					val append = items.loadState.prepend
					if (append is LoadState.Error) {
						LaunchedEffect(Unit) {
							launchUI {
								makeSnackBar(append.error.message ?: "Unknown error")
									?.setAction(R.string.retry) {
										items.retry()
									}
									?.show()
							}
						}
					}

					CatalogContent(
						items,
						type,
						columnsInV,
						columnsInH,
						onClick = {
							try {
								findNavController().navigateSafely(
									R.id.action_catalogController_to_novelController, bundleOf(
										BUNDLE_NOVEL_ID to it.id,
										BUNDLE_EXTENSION to arguments!!.getInt(BUNDLE_EXTENSION)
									),
									navOptions {
										setShosetsuTransition()
									}
								)
							} catch (ignored: Exception) {
								// ignore dup
							}
						},
						onLongClick = {
							if (categories.isNotEmpty() && !it.bookmarked) {
								categoriesDialogItem = it
							} else {
								itemLongClicked(it)
							}
						},
						hasFilters = hasFilters,
						fab
					)
					if (categoriesDialogItem != null) {
						CategoriesDialog(
							onDismissRequest = { categoriesDialogItem = null },
							categories = categories,
							setCategories = {
								itemLongClicked(
									item = categoriesDialogItem ?: return@CategoriesDialog,
									categories = it
								)
							},
							novelCategories = emptyList()
						)
					}
				}
			}
		}
	}

	/**
	 * A [ACatalogNovelUI] was long clicked, invoking a background add
	 */
	private fun itemLongClicked(item: ACatalogNovelUI, categories: IntArray = intArrayOf()): Boolean {
		logI("Adding novel to library in background: $item")

		if (item.bookmarked) {
			logI("Ignoring, already bookmarked: $item")
			return false
		}

		viewModel.backgroundNovelAdd(item.id, categories).observe(
			catch = {
				makeSnackBar(
					getString(
						R.string.controller_catalogue_toast_background_add_fail,
						it.message ?: "Unknown exception"
					)
				)?.setAction(R.string.report) { _ ->
					ACRA.errorReporter.handleSilentException(it)
				}?.show()
			}
		) { result ->
			when (result) {
				ADDING -> {
					makeSnackBar(R.string.controller_catalogue_toast_background_add)?.show()
				}
				ADDED -> {
					makeSnackBar(
						getString(
							R.string.controller_catalogue_toast_background_add_success,
							item.title.let {
								if (it.length > 20)
									it.substring(0, 20) + "..."
								else it
							}
						)
					)?.show()
				}
			}
		}

		return true
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		viewModel.setExtensionID(arguments!!.getInt(BUNDLE_EXTENSION))
		setupObservers()
	}

	override fun onDestroy() {
		super.onDestroy()
		viewModel.destroy()
	}

	/***/
	override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
		menu.clear()
		inflater.inflate(R.menu.toolbar_catalogue, menu)
	}

	private var optionSyncJob: Job? = null

	override fun onPrepareMenu(menu: Menu) {
		logI("Preparing option menu")
		optionSyncJob?.cancel()
		optionSyncJob =
			viewModel.novelCardTypeLive.collectLA(this@CatalogController, catch = {}) {
				when (it) {
					NORMAL -> {
						menu.findItem(R.id.view_type_normal)?.isChecked = true
					}
					COMPRESSED -> {
						menu.findItem(R.id.view_type_comp)?.isChecked = true
					}
					COZY -> menu.findItem(R.id.view_type_cozy)?.isChecked = true
				}
			}

		menu.findItem(R.id.search_item)?.let { searchItem ->
			viewModel.hasSearchLive.collectLA(this, catch = {}) {
				if (!it) {
					logV("Hiding search icon")
					menu.removeItem(R.id.search_item)
				} else {
					logV("Showing search icon")
					(searchItem.actionView as SearchView).apply {
						setOnQueryTextListener(CatalogueSearchQuery(this@CatalogController))
						setOnCloseListener {
							logV("closing search view")
							viewModel.applyQuery("")
							viewModel.resetView()
							true
						}
					}
				}
			}

		}
	}

	override fun onMenuItemSelected(item: MenuItem): Boolean =
		when (item.itemId) {
			R.id.view_type_normal -> {
				item.isChecked = true
				viewModel.setViewType(NORMAL)
				true
			}
			R.id.view_type_comp -> {
				item.isChecked = true
				viewModel.setViewType(COMPRESSED)
				true
			}
			R.id.view_type_cozy -> {
				item.isChecked = true
				viewModel.setViewType(COZY)
				true
			}
			R.id.web_view -> {
				openInWebView()
				true
			}
			else -> false
		}

	private fun openInWebView() {
		var job: Job? = null
		job = viewModel.getBaseURL().firstLa(
			this,
			catch = {
				makeSnackBar(
					getString(
						R.string.controller_catalogue_error_base_url,
						it.message ?: "Unknown exception"
					)
				)?.setAction(R.string.report) { _ ->
					ACRA.errorReporter.handleSilentException(it)
				}?.show()
			}
		) {
			activity?.openInWebView(it)
			job?.cancel("Done")
		}
	}

	private fun setupObservers() {
		setViewTitle(getString(R.string.loading))
		viewModel.extensionName.observe(catch = {
			makeSnackBar(
				getString(
					R.string.controller_catalogue_error_name,
					it.message ?: "Unknown exception"
				)
			)?.setAction(R.string.report) { _ ->
				ACRA.errorReporter.handleSilentException(it)
			}?.show()
		}) {
			setViewTitle(it)
		}

		viewModel.hasSearchLive.observe(catch = {
			makeSnackBar(
				getString(
					R.string.controller_catalogue_error_has_search,
					it.message ?: "Unknown exception"
				)
			)?.setAction(R.string.report) { _ ->
				ACRA.errorReporter.handleSilentException(it)
			}?.show()
		}) {
			activity?.invalidateOptionsMenu()
		}
	}

	private lateinit var fab: EFabMaintainer
	override fun manipulateFAB(fab: EFabMaintainer) {
		this.fab = fab
		fab.setIconResource(R.drawable.filter)
		fab.setText(R.string.filter)
		fab.setOnClickListener {
			//bottomMenuRetriever.invoke()?.show()
			if (bsg == null)
				bsg = ComposeBottomSheetDialog(
					(this.view ?: return@setOnClickListener).context,
					this,
					activity as MainActivity
				)
			if (bsg?.isShowing == false) {
				bsg?.apply {
					setContentView(
						ComposeView((view ?: return@apply).context).apply {
							setContent {
								ShosetsuCompose((view ?: return@setContent).context) {
									val items by viewModel.filterItemsLive.collectAsState(emptyList())
									CatalogFilterMenu(
										items = items,
										getBoolean = viewModel::getFilterBooleanState,
										setBoolean = viewModel::setFilterBooleanState,
										getInt = viewModel::getFilterIntState,
										setInt = viewModel::setFilterIntState,
										getString = viewModel::getFilterStringState,
										setString = viewModel::setFilterStringState,
										applyFilter = viewModel::applyFilter,
										resetFilter = viewModel::resetFilter
									)
								}
							}
						}
					)

				}?.show()
			}
		}
		viewModel.hasFilters.collectLatestLA(this, catch = {}) {
			if (it)
				fab.show()
			else {
				fab.hide()
			}
		}
	}
}

@Composable
fun CatalogContent(
	items: LazyPagingItems<ACatalogNovelUI>,
	cardType: NovelCardType,
	columnsInV: Int,
	columnsInH: Int,
	onClick: (ACatalogNovelUI) -> Unit,
	onLongClick: (ACatalogNovelUI) -> Unit,
	hasFilters: Boolean,
	fab: EFabMaintainer
) {
	Box(
		modifier = Modifier.fillMaxSize(),
	) {
		Column(Modifier.fillMaxHeight()) {
			SwipeRefresh(
				state = SwipeRefreshState(false),
				onRefresh = {
					items.refresh()
				},
			) {
				val w = LocalConfiguration.current.screenWidthDp
				val o = LocalConfiguration.current.orientation

				val size =
					(w / when (o) {
						Configuration.ORIENTATION_LANDSCAPE -> columnsInH
						else -> columnsInV
					}).dp - 8.dp

				val state = rememberLazyGridState()
				if (hasFilters)
					syncFABWithCompose(state, fab)
				LazyVerticalGrid(
					columns = GridCells.Adaptive(if (cardType != COMPRESSED) size else 400.dp),
					contentPadding = PaddingValues(
						bottom = 200.dp,
						start = 8.dp,
						end = 8.dp,
						top = 4.dp
					),
					state = state,
					horizontalArrangement = Arrangement.spacedBy(4.dp),
					verticalArrangement = Arrangement.spacedBy(4.dp)
				) {
					itemsIndexed(
						items,
						key = { index, item -> item.hashCode() + index }
					) { _, item ->
						when (cardType) {
							NORMAL -> {
								if (item != null)
									NovelCardNormalContent(
										item.title,
										item.imageURL,
										onClick = {
											onClick(item)
										},
										onLongClick = {
											onLongClick(item)
										},
										isBookmarked = item.bookmarked
									)
							}
							COMPRESSED -> {
								if (item != null)
									NovelCardCompressedContent(
										item.title,
										item.imageURL,
										onClick = {
											onClick(item)
										},
										onLongClick = {
											onLongClick(item)
										},
										isBookmarked = item.bookmarked
									)
							}
							COZY -> {
								if (item != null)
									NovelCardCozyContent(
										item.title,
										item.imageURL,
										onClick = {
											onClick(item)
										},
										onLongClick = {
											onLongClick(item)
										},
										isBookmarked = item.bookmarked
									)
							}
						}
					}
				}
			}

			if (items.loadState.append == LoadState.Loading)
				LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

			if (items.loadState.refresh.endOfPaginationReached && items.loadState.append.endOfPaginationReached) {
				CatalogContentNoMore()
			}

			val errorState = items.loadState.refresh
			if (errorState is LoadState.Error) {
				ErrorContent(
					errorState.error.message ?: "Unknown",
					ErrorAction(R.string.retry) {
						items.refresh()
					},
					stackTrace = errorState.error.stackTraceToString()
				)
			}
		}

		if (items.loadState.refresh == LoadState.Loading)
			LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
	}
}


@Preview
@Composable
fun PreviewCatalogContentNoMore() {
	ShosetsuCompose {
		CatalogContentNoMore()
	}
}

@Composable
fun CatalogContentNoMore() {
	Box(
		modifier = Modifier.fillMaxWidth()
	) {
		Text(
			stringResource(R.string.controller_catalogue_no_more),
			modifier = Modifier
				.padding(32.dp)
				.align(Alignment.Center)
		)
	}
}