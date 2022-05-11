package app.shosetsu.android.ui.catalogue

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.*
import android.widget.SearchView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataDiffer
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import app.shosetsu.android.activity.MainActivity
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_EXTENSION
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_NOVEL_ID
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.catalogue.listeners.CatalogueSearchQuery
import app.shosetsu.android.ui.novel.NovelController
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.view.controller.base.ExtendedFABController
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.android.viewmodel.abstracted.ACatalogViewModel
import app.shosetsu.android.viewmodel.abstracted.ACatalogViewModel.BackgroundNovelAddProgress.ADDED
import app.shosetsu.android.viewmodel.abstracted.ACatalogViewModel.BackgroundNovelAddProgress.ADDING
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.enums.NovelCardType
import app.shosetsu.common.enums.NovelCardType.COMPRESSED
import app.shosetsu.common.enums.NovelCardType.NORMAL
import app.shosetsu.lib.exceptions.HTTPException
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ComposeViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
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

				CatalogContent(
					items,
					type,
					columnsInV,
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

	override fun onDestroy() {
		logV("")
		super.onDestroy()
		viewModel.destroy()
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
					NovelCardType.COZY -> TODO()
				}
			}

		menu.findItem(R.id.search_item)?.let { searchItem ->
			if (!viewModel.hasSearch) {
				logV("Hiding search icon")
				menu.removeItem(R.id.search_item)
				return@let
			}
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
									items,
									viewModel::getFilterBooleanState,
									viewModel::setFilterBooleanState,
									viewModel::getFilterIntState,
									viewModel::setFilterIntState,
									viewModel::getFilterStringState,
									viewModel::setFilterStringState,
									viewModel::applyFilter,
									viewModel::resetFilter
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

/**
 * Adds the [LazyPagingItems] and their content to the scope where the content of an item is
 * aware of its local index. The range from 0 (inclusive) to [LazyPagingItems.itemCount] (exclusive)
 * always represents the full range of presentable items, because every event from
 * [PagingDataDiffer] will trigger a recomposition.
 *
 * @sample androidx.paging.compose.samples.ItemsIndexedDemo
 *
 * @param items the items received from a [Flow] of [PagingData].
 * @param key a factory of stable and unique keys representing the item. Using the same key
 * for multiple items in the list is not allowed. Type of the key should be saveable
 * via Bundle on Android. If null is passed the position in the list will represent the key.
 * When you specify the key the scroll position will be maintained based on the key, which
 * means if you add/remove items before the current visible item the item with the given key
 * will be kept as the first visible one.
 * @param itemContent the content displayed by a single item. In case the item is `null`, the
 * [itemContent] method should handle the logic of displaying a placeholder instead of the main
 * content displayed by an item which is not `null`.
 */
fun <T : Any> LazyGridScope.itemsIndexed(
	items: LazyPagingItems<T>,
	key: ((index: Int, item: T) -> Any)? = null,
	itemContent: @Composable LazyGridScope.(index: Int, value: T?) -> Unit
) {
	items(
		count = items.itemCount,
		key = if (key == null) null else { index ->
			val item = items.peek(index)
			if (item == null) {
				PagingPlaceholderKey(index)
			} else {
				key(index, item)
			}
		}
	) { index ->
		itemContent(index, items[index])
	}
}

data class PagingPlaceholderKey(private val index: Int) : Parcelable {
	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeInt(index)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object {
		@Suppress("unused")
		@JvmField
		val CREATOR: Parcelable.Creator<PagingPlaceholderKey> =
			object : Parcelable.Creator<PagingPlaceholderKey> {
				override fun createFromParcel(parcel: Parcel) =
					PagingPlaceholderKey(parcel.readInt())

				override fun newArray(size: Int) = arrayOfNulls<PagingPlaceholderKey?>(size)
			}
	}
}

@Composable
fun CatalogContent(
	items: LazyPagingItems<ACatalogNovelUI>,
	cardType: NovelCardType,
	columnsInV: Int,
	onClick: (ACatalogNovelUI) -> Unit,
	onLongClick: (ACatalogNovelUI) -> Unit
) {
	Column {
		if (items.loadState.refresh == LoadState.Loading)
			LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

		when (cardType) {
			NORMAL, NovelCardType.COZY -> {
				LazyVerticalGrid(columns = GridCells.Fixed(columnsInV)) {
					itemsIndexed(
						items,
						key = { index, item -> item.hashCode() + index }
					) { _, item ->
						if (cardType == NORMAL) {
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
						} else {
							TODO("Cozy Type type")
						}
					}
				}
			}
			COMPRESSED -> TODO("Compressed type")
		}

		if (items.loadState.append == LoadState.Loading)
			LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

		if (items.loadState.append is LoadState.NotLoading) {
			CatalogContentNoMore()
		}
	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NovelCardNormalContent(
	title: String,
	imageURL: String,
	onClick: () -> Unit,
	onLongClick: () -> Unit,
	overlay: (BoxScope.() -> Unit)? = null,
) {
	Card(
		modifier = Modifier.combinedClickable(
			onClick = onClick,
			onLongClick = onLongClick
		).padding(4.dp)
	) {
		Box {
			AsyncImage(
				ImageRequest.Builder(LocalContext.current)
					.data(imageURL)
					.placeholder(R.drawable.animated_refresh)
					.error(R.drawable.broken_image)
					.build(),
				stringResource(R.string.controller_novel_info_image),
				modifier = Modifier
					.fillMaxSize()
					.aspectRatio(.75f)
					.clickable(onClick = onClick)
			)

			Box(
				modifier = Modifier.aspectRatio(.75f)
					.fillMaxSize().drawWithCache {
						onDrawWithContent {

							drawRect(
								brush = Brush.linearGradient(
									listOf(
										Color.Transparent,
										Color.Black.copy(alpha = .75f),
									),
									Offset(0.0f, 0.0f),
									Offset(0.0f, Float.POSITIVE_INFINITY),
									TileMode.Clamp
								)
							)
						}
					}
			)
			Text(
				title,
				modifier = Modifier.align(Alignment.BottomCenter),
				textAlign = TextAlign.Center,
				color = Color.White
			)
			if (overlay != null)
				overlay()
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