package app.shosetsu.android.ui.library

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import app.shosetsu.android.R
import app.shosetsu.android.activity.MainActivity
import app.shosetsu.android.common.SettingKey
import app.shosetsu.android.common.consts.BundleKeys
import app.shosetsu.android.common.enums.NovelCardType
import app.shosetsu.android.common.enums.NovelCardType.*
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.library.listener.LibrarySearchQuery
import app.shosetsu.android.ui.migration.MigrationController
import app.shosetsu.android.ui.novel.CategoriesDialog
import app.shosetsu.android.view.ComposeBottomSheetDialog
import app.shosetsu.android.view.compose.*
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.view.controller.base.ExtendedFABController
import app.shosetsu.android.view.controller.base.ExtendedFABController.EFabMaintainer
import app.shosetsu.android.view.controller.base.HomeFragment
import app.shosetsu.android.view.controller.base.syncFABWithCompose
import app.shosetsu.android.view.uimodels.model.LibraryNovelUI
import app.shosetsu.android.view.uimodels.model.LibraryUI
import app.shosetsu.android.viewmodel.abstracted.ALibraryViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
 * @author github.com/doomsdayrs
 */
class LibraryController
	: ShosetsuController(), ExtendedFABController, HomeFragment, MenuProvider {

	private var fab: EFabMaintainer? = null
	private var bsg: BottomSheetDialog? = null

	override val viewTitleRes: Int = R.string.my_library

	/***/
	val viewModel: ALibraryViewModel by viewModel()
	var categoriesDialogOpen by mutableStateOf(false)

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
					val items by viewModel.liveData.collectAsState(null)
					val isEmpty by viewModel.isEmptyFlow.collectAsState(false)
					val hasSelected by viewModel.hasSelectionFlow.collectAsState(false)
					val type by viewModel.novelCardTypeFlow.collectAsState(NORMAL)
					val badgeToast by viewModel.badgeUnreadToastFlow.collectAsState(true)

					val columnsInV by viewModel.columnsInV.collectAsState(SettingKey.ChapterColumnsInPortait.default)
					val columnsInH by viewModel.columnsInH.collectAsState(SettingKey.ChapterColumnsInLandscape.default)

					BackHandler(hasSelected) {
						viewModel.deselectAll()
					}

					LibraryContent(
						items,
						isEmpty = isEmpty,
						setActiveCategory = viewModel::setActiveCategory,
						type,
						columnsInV,
						columnsInH,
						hasSelected = hasSelected,
						onRefresh = {
							onRefresh(it)
						},
						onOpen = { (id) ->
							try {
								findNavController().navigateSafely(
									R.id.action_libraryController_to_novelController,
									bundleOf(BundleKeys.BUNDLE_NOVEL_ID to id),
									navOptions = navOptions {
										launchSingleTop = true
										setShosetsuTransition()
									}
								)
							} catch (ignored: Exception) {
								// ignore dup
							}
						},
						toggleSelection = viewModel::toggleSelection,
						toastNovel = if (badgeToast) {
							{ item ->
								try {
									makeSnackBar(
										resources!!.getQuantityString(
											R.plurals.toast_unread_count,
											item.unread,
											item.unread
										)
									)?.show()
								} catch (e: Resources.NotFoundException) {
								}
							}
						} else null,
						fab
					)
					if (categoriesDialogOpen) {
						CategoriesDialog(
							onDismissRequest = { categoriesDialogOpen = false },
							categories = items?.categories.orEmpty(),
							novelCategories = emptyList(),
							setCategories = viewModel::setCategories
						)
					}
				}
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		startObservation()
	}

	private fun startObservation() {
		viewModel.isEmptyFlow.collectLA(this, catch = {}) {
			if (it)
				fab?.hide()
			else fab?.show()
		}

		viewModel.hasSelectionFlow.collectLatestLA(this, catch = {}) {
			activity?.invalidateOptionsMenu()
		}
	}

	override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
		if (!viewModel.hasSelection) {
			inflater.inflate(R.menu.toolbar_library, menu)
		} else {
			inflater.inflate(R.menu.toolbar_library_selected, menu)
		}
	}

	private var searchView: SearchView? = null

	override fun onPrepareMenu(menu: Menu) {
		logI("Preparing options menu")
		searchView = (menu.findItem(R.id.library_search)?.actionView as? SearchView)
		searchView?.apply {
			setOnQueryTextListener(LibrarySearchQuery(viewModel))
		}
		runBlocking {
			val string = viewModel.queryFlow.first()
			searchView?.setQuery(string, false)
			if (string.isNotEmpty()) {
				searchView?.isIconified = false
			}
		}

		viewModel.isEmptyFlow.collectLA(this, catch = {
			// IGNORE, Main observer will handle
		}) { visible ->

			menu.findItem(R.id.library_search)?.isVisible = !visible
			menu.findItem(R.id.view_type)?.isVisible = !visible
			menu.findItem(R.id.updater_now)?.isVisible = !visible
		}

		viewModel.novelCardTypeFlow.collectLA(this, catch = {}) {
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
	}

	/*&
	TODO BACK
	override fun handleBack(): Boolean =
		if (searchView != null && searchView!!.isIconified) {
			searchView!!.onActionViewCollapsed()
			true
		} else super.handleBack()
	 */

	/***/
	override fun onMenuItemSelected(item: MenuItem): Boolean =
		when (item.itemId) {
			R.id.updater_now -> {
				if (viewModel.isOnline())
					viewModel.startUpdateManager(-1)
				else displayOfflineSnackBar()
				true
			}
			R.id.library_select_all -> {
				selectAll()
				true
			}
			R.id.library_deselect_all -> {
				deselectAll()
				true
			}
			R.id.library_inverse_selection -> {
				invertSelection()
				true
			}
			R.id.library_select_between -> {
				selectBetween()
				true
			}
			R.id.remove_from_library -> {
				viewModel.removeSelectedFromLibrary()
				true
			}
			R.id.source_migrate -> {
				viewModel.getSelectedIds().firstLa(this, catch = {}) {
					findNavController().navigateSafely(
						R.id.action_libraryController_to_migrationController,
						bundleOf(MigrationController.TARGETS_BUNDLE_KEY to it),
						navOptions {
							setShosetsuTransition()
						}
					)
				}

				true
			}
			R.id.set_categories -> {
				categoriesDialogOpen = true
				true
			}
			R.id.view_type_normal -> {
				item.isChecked = !item.isChecked
				viewModel.setViewType(NORMAL)
				true
			}
			R.id.view_type_comp -> {
				item.isChecked = !item.isChecked
				viewModel.setViewType(COMPRESSED)
				true
			}
			R.id.view_type_cozy -> {
				item.isChecked = !item.isChecked
				viewModel.setViewType(COZY)
				true
			}
			else -> false
		}

	private fun deselectAll() {
		viewModel.deselectAll()
	}

	private fun selectAll() {
		viewModel.selectAll()
	}

	private fun invertSelection() {
		viewModel.invertSelection()
	}

	private fun selectBetween() {
		viewModel.selectBetween()
	}

	override fun manipulateFAB(fab: EFabMaintainer) {
		this.fab = fab
		fab.setOnClickListener {
			//bottomMenuRetriever.invoke()?.show()
			if (bsg == null)
				bsg = ComposeBottomSheetDialog(view!!.context, this, activity as MainActivity)
			if (bsg?.isShowing == false) {
				bsg?.apply {
					setContentView(
						LibraryFilterMenuBuilder(this.context, viewModel).build()
					)
				}?.show()
			}
		}
		fab.setText(R.string.filter)
		fab.setIconResource(R.drawable.filter)
	}

	fun onRefresh(categoryID: Int) {
		if (viewModel.isOnline())
			viewModel.startUpdateManager(categoryID)
		else displayOfflineSnackBar(R.string.generic_error_cannot_update_library_offline)
	}
}

@Composable
fun LibraryContent(
	items: LibraryUI?,
	isEmpty: Boolean,
	setActiveCategory: (Int) -> Unit,
	cardType: NovelCardType,
	columnsInV: Int,
	columnsInH: Int,
	hasSelected: Boolean,
	onRefresh: (Int) -> Unit,
	onOpen: (LibraryNovelUI) -> Unit,
	toggleSelection: (LibraryNovelUI) -> Unit,
	toastNovel: ((LibraryNovelUI) -> Unit)?,
	fab: EFabMaintainer?
) {
	if (!isEmpty) {
		if (items == null) {
			LinearProgressIndicator(Modifier.fillMaxWidth())
		} else {
			LibraryPager(
				library = items,
				setActiveCategory = setActiveCategory,
				cardType = cardType,
				columnsInV = columnsInV,
				columnsInH = columnsInH,
				hasSelected = hasSelected,
				onRefresh = onRefresh,
				onOpen = onOpen,
				toggleSelection = toggleSelection,
				toastNovel = toastNovel,
				fab = fab
			)
		}
	} else {
		ErrorContent(
			stringResource(R.string.empty_library_message)
		)
	}
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun LibraryPager(
	library: LibraryUI,
	setActiveCategory: (Int) -> Unit,
	cardType: NovelCardType,
	columnsInV: Int,
	columnsInH: Int,
	hasSelected: Boolean,
	onRefresh: (Int) -> Unit,
	onOpen: (LibraryNovelUI) -> Unit,
	toggleSelection: (LibraryNovelUI) -> Unit,
	toastNovel: ((LibraryNovelUI) -> Unit)?,
	fab: EFabMaintainer?
) {
	val scope = rememberCoroutineScope()
	val state = rememberPagerState()
	LaunchedEffect(state.currentPage) {
		setActiveCategory(library.categories[state.currentPage].id)
	}

	Column(Modifier.fillMaxWidth()) {
		if (!(library.categories.size == 1 && library.categories.first().id == 0)) {
			ScrollableTabRow(
				selectedTabIndex = state.currentPage,
				indicator = { tabPositions ->
					TabRowDefaults.Indicator(
						Modifier.pagerTabIndicatorOffset(state, tabPositions)
					)
				},
				backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1F),
				edgePadding = 0.dp,
			) {
				library.categories.forEachIndexed { index, category ->
					Tab(
						text = { Text(category.name) },
						selected = state.currentPage == index,
						onClick = {
							scope.launch {
								state.animateScrollToPage(index)
							}
						},
					)
				}
			}
		}
		HorizontalPager(
			count = library.categories.size,
			state = state,
			modifier = Modifier.fillMaxSize()
		) {
			val id by derivedStateOf {
				library.categories[it].id
			}
			val items by produceState(emptyList(), library, it, id) {
				value = onIO {
					library.novels[id].orEmpty()
				}
			}
			LibraryCategory(
				items = items,
				cardType = cardType,
				columnsInV = columnsInV,
				columnsInH = columnsInH,
				hasSelected = hasSelected,
				onRefresh = { onRefresh(id) },
				onOpen = onOpen,
				toggleSelection = toggleSelection,
				toastNovel = toastNovel,
				fab = fab
			)
		}
	}
}

@Composable
fun LibraryCategory(
	items: List<LibraryNovelUI>,
	cardType: NovelCardType,
	columnsInV: Int,
	columnsInH: Int,
	hasSelected: Boolean,
	onRefresh: () -> Unit,
	onOpen: (LibraryNovelUI) -> Unit,
	toggleSelection: (LibraryNovelUI) -> Unit,
	toastNovel: ((LibraryNovelUI) -> Unit)?,
	fab: EFabMaintainer?
) {
	SwipeRefresh(
		state = SwipeRefreshState(false),
		onRefresh = onRefresh,
		modifier = Modifier.fillMaxSize()
	) {
		val w = LocalConfiguration.current.screenWidthDp
		val o = LocalConfiguration.current.orientation

		val size =
			(w / when (o) {
				Configuration.ORIENTATION_LANDSCAPE -> columnsInH
				else -> columnsInV
			}).dp - 16.dp


		val state = rememberLazyGridState()
		if (fab != null)
			syncFABWithCompose(state, fab)

		LazyVerticalGrid(
			columns = GridCells.Adaptive(if (cardType != COMPRESSED) size else 400.dp),
			contentPadding = PaddingValues(
				bottom = 300.dp,
				start = 8.dp,
				end = 8.dp,
				top = 4.dp
			),
			state = state,
			horizontalArrangement = Arrangement.spacedBy(4.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp)
		) {
			fun onClick(item: LibraryNovelUI) {
				if (hasSelected)
					toggleSelection(item)
				else onOpen(item)
			}

			fun onLongClick(item: LibraryNovelUI) {
				if (!hasSelected)
					toggleSelection(item)
			}
			items(
				items,
				key = { it.hashCode() }
			) { item ->
				val onClickBadge = if (toastNovel != null) {
					{ toastNovel(item) }
				} else null
				when (cardType) {
					NORMAL -> {
						NovelCardNormalContent(
							item.title,
							item.imageURL,
							onClick = {
								onClick(item)
							},
							onLongClick = {
								onLongClick(item)
							},
							overlay = {
								if (item.unread > 0)
									Badge(
										Modifier
											.align(Alignment.TopStart)
											.padding(top = 4.dp, start = 4.dp),
										text = item.unread.toString(),
										onClick = onClickBadge
									)
							},
							isSelected = item.isSelected
						)
					}
					COMPRESSED -> {
						NovelCardCompressedContent(
							item.title,
							item.imageURL,
							onClick = {
								onClick(item)
							},
							onLongClick = {
								onLongClick(item)
							},
							overlay = {
								if (item.unread > 0)
									Badge(
										Modifier.padding(8.dp),
										text = item.unread.toString(),
										onClick = onClickBadge
									)
							},
							isSelected = item.isSelected
						)
					}
					COZY -> {
						NovelCardCozyContent(
							item.title,
							item.imageURL,
							onClick = {
								onClick(item)
							},
							onLongClick = {
								onLongClick(item)
							},
							overlay = {
								if (item.unread > 0)
									Badge(
										Modifier
											.align(Alignment.TopStart)
											.padding(top = 4.dp, start = 4.dp),
										text = item.unread.toString(),
										onClick = onClickBadge
									)
							},
							isSelected = item.isSelected
						)
					}
				}
			}
		}
	}
}

@Composable
fun Badge(modifier: Modifier, text: String, onClick: (() -> Unit)? = null) {
	Box(
		modifier = modifier then Modifier
			.height(20.dp)
			.background(MaterialTheme.colors.secondary, MaterialTheme.shapes.medium)
			.clip(MaterialTheme.shapes.medium)
			.let {
				 if (onClick != null) {
					 it.clickable(onClick = onClick)
				 } else it
			},
		contentAlignment = Alignment.Center
	) {
		Text(
			text,
			fontSize = 12.sp,
			color = MaterialTheme.colors.onSecondary,
			modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)
		)
	}
}