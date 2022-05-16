package app.shosetsu.android.ui.catalogue

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import app.shosetsu.android.activity.MainActivity
import app.shosetsu.android.common.SettingKey
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_EXTENSION
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_NOVEL_ID
import app.shosetsu.android.common.enums.NovelCardType
import app.shosetsu.android.common.enums.NovelCardType.*
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.catalogue.listeners.CatalogueSearchQuery
import app.shosetsu.android.ui.novel.NovelController
import app.shosetsu.android.view.compose.ErrorContent
import app.shosetsu.android.view.compose.NovelCardCompressedContent
import app.shosetsu.android.view.compose.NovelCardNormalContent
import app.shosetsu.android.view.compose.itemsIndexed
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.view.controller.base.ExtendedFABController
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.android.view.widget.EmptyDataView
import app.shosetsu.android.viewmodel.abstracted.ACatalogViewModel
import app.shosetsu.android.viewmodel.abstracted.ACatalogViewModel.BackgroundNovelAddProgress.ADDED
import app.shosetsu.android.viewmodel.abstracted.ACatalogViewModel.BackgroundNovelAddProgress.ADDING
import app.shosetsu.lib.exceptions.HTTPException
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ComposeViewBinding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.Job
import org.acra.ACRA
import java.net.SocketTimeoutException

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
class CatalogController(
	/** data bundle uwu */
	val bundle: Bundle,
) : ShosetsuController(bundle), ExtendedFABController {
	private var bsg: BottomSheetDialog? = null

	/***/
	val viewModel: ACatalogViewModel by viewModel()
	//private val progressAdapter by lazy { ItemAdapter<ProgressItem>() }

	init {
		setHasOptionsMenu(true)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup,
		savedViewState: Bundle?
	): View = ComposeView(container.context).apply {
		setViewTitle()
		setContent {
			MdcTheme {
				val type by viewModel.novelCardTypeLive.collectAsState(NORMAL)

				val columnsInV by viewModel.columnsInV.collectAsState(SettingKey.ChapterColumnsInPortait.default)
				val columnsInH by viewModel.columnsInH.collectAsState(SettingKey.ChapterColumnsInLandscape.default)

				val items = viewModel.itemsLive.collectAsLazyPagingItems()

				val exception by viewModel.exceptionFlow.collectAsState(null)

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
						router.shosetsuPush(
							NovelController(
								bundleOf(
									BUNDLE_NOVEL_ID to it.id,
									BUNDLE_EXTENSION to bundle.getInt(BUNDLE_EXTENSION)
								)
							)
						)
					},
					onLongClick = {
						itemLongClicked(it)
					}
				)
			}
		}
	}

	/**
	 * A [ACatalogNovelUI] was long clicked, invoking a background add
	 */
	private fun itemLongClicked(item: ACatalogNovelUI): Boolean {
		logI("Adding novel to library in background: $item")

		if (item.bookmarked) {
			logI("Ignoring, already bookmarked: $item")
			return false
		}

		viewModel.backgroundNovelAdd(item.id).observe(
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

	override fun onViewCreated(view: View) {
		viewModel.setExtensionID(bundle.getInt(BUNDLE_EXTENSION))
		setupObservers()
	}

	/***/
	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		menu.clear()
		inflater.inflate(R.menu.toolbar_catalogue, menu)
	}

	private var optionSyncJob: Job? = null

	override fun onPrepareOptionsMenu(menu: Menu) {
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
					COZY -> TODO()
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

	override fun onOptionsItemSelected(item: MenuItem): Boolean =
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
			R.id.web_view -> {
				viewModel.getBaseURL().observe(
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
				}
				true
			}
			else -> false
		}

	fun handleRecyclerException(e: Throwable) {
		logE("Error occurred", e)
		val cause = e.cause

		when {
			e is HTTPException -> {
				makeSnackBar(e.code.toString())?.show()
			}
			cause is HTTPException -> {
				makeSnackBar(cause.code.toString())?.show()
			}
			e is SocketTimeoutException -> {
				makeSnackBar(e.message.toString())?.show()
			}
			cause is SocketTimeoutException -> {
				makeSnackBar(cause.message.toString())?.show()
			}
			else -> {
				logE("Exception", e.cause)
				makeSnackBar(
					getString(
						R.string.controller_library_error_recycler,
						e.message ?: "Unknown exception"
					)
				)?.setAction(R.string.report) {
					ACRA.errorReporter.handleSilentException(e)
				}?.show()
			}
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

	override fun manipulateFAB(fab: ExtendedFloatingActionButton) {
		fab.setIconResource(R.drawable.filter)
		fab.setOnClickListener {
			//bottomMenuRetriever.invoke()?.show()
			if (bsg == null)
				bsg = BottomSheetDialog(this.view!!.context)
			if (bsg?.isShowing == false) {
				bsg?.apply {
					val binding = ComposeViewBinding.inflate(
						this@CatalogController.activity!!.layoutInflater,
						null,
						false
					)

					this.window?.decorView?.let {
						ViewTreeLifecycleOwner.set(it, this@CatalogController)
						ViewTreeSavedStateRegistryOwner.set(it, activity as MainActivity)
					}

					binding.root.apply {
						setViewCompositionStrategy(
							ViewCompositionStrategy.DisposeOnLifecycleDestroyed(this@CatalogController)
						)
						setContent {
							MdcTheme(view!!.context) {
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

					setContentView(binding.root)

				}?.show()
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
	onLongClick: (ACatalogNovelUI) -> Unit
) {
	Column(
		modifier = Modifier.fillMaxSize(),
	) {
		val refreshState = items.loadState.refresh
		when (refreshState) {
			is LoadState.NotLoading -> {
			}
			LoadState.Loading ->
				LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

			is LoadState.Error ->
				ErrorContent(
					refreshState.error.message ?: "Unknown",
					EmptyDataView.Action(R.string.retry) {
						items.refresh()
					}
				)
		}
		SwipeRefresh(
			SwipeRefreshState(false),
			{
				items.refresh()
			},
		) {
			val w = LocalConfiguration.current.screenWidthDp
			val o = LocalConfiguration.current.orientation

			val size by remember {
				derivedStateOf {
					(w / when (o) {
						Configuration.ORIENTATION_LANDSCAPE -> columnsInH
						else -> columnsInV
					}).dp
				}
			}

			LazyVerticalGrid(
				columns = GridCells.Adaptive(if (cardType != COMPRESSED) size else 400.dp),
				contentPadding = PaddingValues(bottom = 200.dp)
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
									}
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
									}
								)
						}
						COZY -> {
							TODO("Cozy Type type")
						}
					}
				}
			}
		}

		if (items.loadState.append == LoadState.Loading)
			LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

		if (refreshState is LoadState.NotLoading &&
			items.loadState.append is LoadState.NotLoading
		) {
			CatalogContentNoMore()
		}
	}
}


@Preview
@Composable
fun PreviewCatalogContentNoMore() {
	MdcTheme {
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
			modifier = Modifier.padding(32.dp).align(Alignment.Center)
		)
	}
}