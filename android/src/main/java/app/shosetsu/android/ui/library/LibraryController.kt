package app.shosetsu.android.ui.library

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import app.shosetsu.android.activity.MainActivity
import app.shosetsu.android.common.SettingKey
import app.shosetsu.android.common.consts.BundleKeys
import app.shosetsu.android.common.enums.NovelCardType
import app.shosetsu.android.common.enums.NovelCardType.*
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.library.listener.LibrarySearchQuery
import app.shosetsu.android.ui.migration.MigrationController
import app.shosetsu.android.ui.novel.NovelController
import app.shosetsu.android.view.ComposeBottomSheetDialog
import app.shosetsu.android.view.compose.ErrorContent
import app.shosetsu.android.view.compose.NovelCardCompressedContent
import app.shosetsu.android.view.compose.NovelCardCozyContent
import app.shosetsu.android.view.compose.NovelCardNormalContent
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.view.controller.base.ExtendedFABController
import app.shosetsu.android.view.controller.base.syncFABWithCompose
import app.shosetsu.android.view.uimodels.model.LibraryNovelUI
import app.shosetsu.android.viewmodel.abstracted.ALibraryViewModel
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

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
	: ShosetsuController(), ExtendedFABController {

	private var fab: ExtendedFloatingActionButton? = null
	private var bsg: BottomSheetDialog? = null

	override val viewTitleRes: Int = R.string.my_library

	/***/
	val viewModel: ALibraryViewModel by viewModel()

	init {
		setHasOptionsMenu(true)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup,
		savedViewState: Bundle?
	): View = ComposeView(container.context).apply {
		setContent {
			MdcTheme {
				setViewTitle()
				val items by viewModel.liveData.collectAsState(listOf())
				val isEmpty by viewModel.isEmptyFlow.collectAsState(false)
				val hasSelected by viewModel.hasSelectionFlow.collectAsState(false)
				val type by viewModel.novelCardTypeFlow.collectAsState(NORMAL)

				val columnsInV by viewModel.columnsInV.collectAsState(SettingKey.ChapterColumnsInPortait.default)
				val columnsInH by viewModel.columnsInH.collectAsState(SettingKey.ChapterColumnsInLandscape.default)

				LibraryContent(
					items,
					isEmpty = isEmpty,
					type,
					columnsInV,
					columnsInH,
					hasSelected = hasSelected,
					onRefresh = {
						onRefresh()
					},
					onOpen = { item ->
						router.shosetsuPush(
							NovelController(
								bundleOf(BundleKeys.BUNDLE_NOVEL_ID to item.id)
							)
						)
					},
					toggleSelection = { item ->
						viewModel.toggleSelection(item)
					},
					toastNovel = { item ->
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
					},
					fab
				)
			}
		}
	}

	override fun onViewCreated(view: View) {
		startObservation()
	}

	private fun startObservation() {
		viewModel.isEmptyFlow.collectLA(this, catch = {}) {
			fab?.isVisible = !it
		}

		viewModel.hasSelectionFlow.collectLatestLA(this, catch = {}) {
			activity?.invalidateOptionsMenu()
		}
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		if (!viewModel.hasSelection) {
			inflater.inflate(R.menu.toolbar_library, menu)
		} else {
			inflater.inflate(R.menu.toolbar_library_selected, menu)
		}
	}

	private var searchView: SearchView? = null

	override fun onPrepareOptionsMenu(menu: Menu) {
		logI("Preparing options menu")
		searchView = (menu.findItem(R.id.library_search)?.actionView as? SearchView)
		searchView?.apply {
			setOnQueryTextListener(LibrarySearchQuery(viewModel))
		}
		viewModel.queryFlow.collectLA(this, catch = {}) {
			searchView?.setQuery(it, false)
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

	override fun handleBack(): Boolean =
		if (searchView != null && searchView!!.isIconified) {
			searchView!!.onActionViewCollapsed()
			true
		} else super.handleBack()

	/***/
	override fun onOptionsItemSelected(item: MenuItem): Boolean =
		when (item.itemId) {
			R.id.updater_now -> {
				if (viewModel.isOnline())
					viewModel.startUpdateManager()
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
					router.pushController(
						MigrationController(
							bundleOf(MigrationController.TARGETS_BUNDLE_KEY to it)
						).withFadeTransaction()
					)
				}

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

	override fun manipulateFAB(fab: ExtendedFloatingActionButton) {
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

	fun onRefresh() {
		if (viewModel.isOnline())
			viewModel.startUpdateManager()
		else displayOfflineSnackBar(R.string.generic_error_cannot_update_library_offline)
	}
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LibraryContent(
	items: List<LibraryNovelUI>,
	isEmpty: Boolean,
	cardType: NovelCardType,
	columnsInV: Int,
	columnsInH: Int,
	hasSelected: Boolean,
	onRefresh: () -> Unit,
	onOpen: (LibraryNovelUI) -> Unit,
	toggleSelection: (LibraryNovelUI) -> Unit,
	toastNovel: (LibraryNovelUI) -> Unit,
	fab: ExtendedFloatingActionButton?
) {
	if (!isEmpty) {
		SwipeRefresh(
			state = SwipeRefreshState(false),
			onRefresh = onRefresh
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
										Chip(
											onClick = {
												toastNovel(item)
											},
											modifier = Modifier
												.align(Alignment.TopStart)
												.height(24.dp)
										) {
											Text(
												item.unread.toString(),
												modifier = Modifier.padding(2.dp),
												fontSize = 14.sp
											)
										}
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
										Chip(
											onClick = {
												toastNovel(item)
											},
											modifier = Modifier
												.padding(8.dp)
												.height(24.dp)
										) {
											Text(
												item.unread.toString(),
												modifier = Modifier.padding(2.dp),
												fontSize = 14.sp
											)
										}
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
										Chip(
											onClick = {
												toastNovel(item)
											},
											modifier = Modifier
												.align(Alignment.TopStart)
												.height(24.dp)
										) {
											Text(
												item.unread.toString(),
												modifier = Modifier.padding(2.dp),
												fontSize = 14.sp
											)
										}
								},
								isSelected = item.isSelected
							)
						}
					}
				}
			}
		}
	} else {
		ErrorContent(
			stringResource(R.string.empty_library_message)
		)
	}

}