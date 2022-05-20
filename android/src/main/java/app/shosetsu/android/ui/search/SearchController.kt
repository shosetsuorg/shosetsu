package app.shosetsu.android.ui.search

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import app.shosetsu.android.common.consts.BundleKeys
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.novel.NovelController
import app.shosetsu.android.view.compose.NovelCardNormalContent
import app.shosetsu.android.view.compose.PlaceholderNovelCardNormalContent
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.android.view.uimodels.model.search.SearchRowUI
import app.shosetsu.android.viewmodel.abstracted.ASearchViewModel
import coil.compose.rememberAsyncImagePainter
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.material.composethemeadapter.MdcTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
 * @author github.com/doomsdayrs
 */
class SearchController(bundle: Bundle) : ShosetsuController(bundle) {
	override val viewTitleRes: Int = R.string.search
	internal val viewModel: ASearchViewModel by viewModel()

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
				val rows by viewModel.listings.collectAsState(listOf())
				SearchContent(
					rows = rows,
					getChildren = {
						if (it == -1)
							viewModel.searchLibrary()
						else
							viewModel.searchExtension(it)
					},
					getException = viewModel::getException,
					onClick = {
						router.shosetsuPush(NovelController(bundleOf(BundleKeys.BUNDLE_NOVEL_ID to it.id)))
					},
					onRefresh = viewModel::refresh,
					onRefreshAll = viewModel::refresh
				)
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		try {
			viewModel.destroy()
		} catch (e: DestroyFailedException) {
			logE("Failed to destroy", e)
			ACRA.errorReporter.handleSilentException(e)
		}
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_search, menu)
	}

	override fun onPrepareOptionsMenu(menu: Menu) {
		val searchView = menu.findItem(R.id.search).actionView as SearchView
		searchView.setOnQueryTextListener(InternalQuery())
		searchView.setIconifiedByDefault(false)
		viewModel.query.collectLA(this, catch = {
			makeSnackBar(it.message ?: "Unknown error loading app theme")
				?.setAction(R.string.report) { _ ->
					ACRA.errorReporter.handleSilentException(it)
				}?.show()
		}) {
			searchView.setQuery(it, false)
		}
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean = true

	override fun onViewCreated(view: View) {
		viewModel.initQuery(args.getString(BundleKeys.BUNDLE_QUERY, "")!!)
	}

	/** Class that handles querying */
	inner class InternalQuery
		: SearchView.OnQueryTextListener {
		override fun onQueryTextSubmit(query: String): Boolean {
			viewModel.applyQuery(query)
			return true
		}

		override fun onQueryTextChange(newText: String?): Boolean {
			viewModel.setQuery(newText ?: "")
			return true
		}
	}
}

@Preview
@Composable
fun PreviewSearchContent() {
	SearchContent(
		rows = listOf(SearchRowUI(-1, "Library", null)) + List(5) {
			SearchRowUI(
				it,
				"Test",
				null
			)
		},
		getException = {
			flow { emit(null) }
		},
		onClick = {},
		onRefresh = {},
		onRefreshAll = {},
		getChildren = {
			flow { }
		}
	)
}

@Composable
fun SearchContent(
	rows: List<SearchRowUI>,
	getChildren: (id: Int) -> Flow<PagingData<ACatalogNovelUI>>,
	getException: (id: Int) -> Flow<Throwable?>,
	onClick: (ACatalogNovelUI) -> Unit,
	onRefresh: (id: Int) -> Unit,
	onRefreshAll: () -> Unit
) {
	SwipeRefresh(
		rememberSwipeRefreshState(false),
		onRefresh = onRefreshAll
	) {
		LazyColumn(
			modifier = Modifier.fillMaxSize(),
			contentPadding = PaddingValues(top = 8.dp, bottom = 64.dp)
		) {
			items(rows, key = { row -> row.extensionID }) { row ->
				val children: LazyPagingItems<ACatalogNovelUI> =
					getChildren(row.extensionID).collectAsLazyPagingItems()

				SearchRowContent(
					row = row,
					loadingBar = {
						if (children.loadState.refresh == LoadState.Loading)
							LinearProgressIndicator(
								modifier = Modifier.fillMaxWidth()
							)
					},
					items = {
						itemsIndexed(
							children,
							key = { index, item -> item.hashCode() + index }) { _, novelUI ->
							Box(
								modifier = Modifier.width(105.dp)
							) {
								if (novelUI != null)
									NovelCardNormalContent(
										novelUI.title,
										novelUI.imageURL,
										onClick = {
											onClick(novelUI)
										},
										onLongClick = {},
									)
								else PlaceholderNovelCardNormalContent()
							}
						}
					},
					exception = {
						val exception by getException(row.extensionID).collectAsState(null)
						if (exception != null)
							ExceptionBar(
								exception!!,
								onRefresh = {
									onRefresh(row.extensionID)
								}
							)
						else {
							val refreshState = children.loadState.refresh
							if (refreshState is LoadState.Error) {
								ExceptionBar(
									refreshState.error,
									onRefresh = {
										children.refresh()
									}
								)
							}
						}
					}
				)
			}
		}
	}
}

@Composable
fun ExceptionBar(
	exception: Throwable,
	onRefresh: () -> Unit
) {
	Row(
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			exception.message ?: stringResource(R.string.unknown),
			modifier = Modifier.fillMaxWidth(.75f)
		)
		Button(onRefresh) {
			Text(stringResource(R.string.retry))
		}
	}
}


@Preview
@Composable
fun PreviewSearchRowContent() {
	SearchRowContent(
		SearchRowUI(
			extensionID = 0,
			name = "Name",
			imageURL = null
		), loadingBar = {}, items = {}, exception = {})
}

@Composable
fun SearchRowContent(
	row: SearchRowUI,
	loadingBar: @Composable () -> Unit,
	items: LazyListScope.() -> Unit,
	exception: @Composable () -> Unit,
) {
	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(top = 8.dp),
	) {
		Row(
			modifier = Modifier.padding(8.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Image(
				if (!row.imageURL.isNullOrEmpty()) {
					rememberAsyncImagePainter(row.imageURL)
				} else {
					painterResource(R.drawable.library)
				},
				contentDescription = row.name,
				modifier = Modifier.size(32.dp)
			)
			Text(row.name, modifier = Modifier.padding(start = 8.dp))
		}
		loadingBar()

		LazyRow(
			horizontalArrangement = Arrangement.spacedBy(4.dp)
		) {
			items()
		}

		exception()

		Divider(modifier = Modifier.fillMaxWidth())
	}
}