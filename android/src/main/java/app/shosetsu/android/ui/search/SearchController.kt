package app.shosetsu.android.ui.search

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import app.shosetsu.android.common.consts.BundleKeys
import app.shosetsu.android.common.ext.collectLA
import app.shosetsu.android.common.ext.shosetsuPush
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.ui.novel.NovelController
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.android.view.uimodels.model.catlog.FullCatalogNovelUI
import app.shosetsu.android.view.uimodels.model.search.SearchRowUI
import app.shosetsu.android.viewmodel.abstracted.ASearchViewModel
import coil.compose.rememberImagePainter
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.android.material.composethemeadapter.MdcTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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
					rows,
					viewModel::getIsLoading,
					{
						if (it == -1)
							viewModel.searchLibrary()
						else viewModel.searchExtension(it)
					},
					viewModel::getException,
					{
						router.shosetsuPush(NovelController(bundleOf(BundleKeys.BUNDLE_NOVEL_ID to it.id)))
					},
					viewModel::refresh,
					viewModel::refresh
				)
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		viewModel.destroy()
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_search, menu)
	}

	override fun onPrepareOptionsMenu(menu: Menu) {
		val searchView = menu.findItem(R.id.search).actionView as SearchView
		searchView.setOnQueryTextListener(InternalQuery())
		searchView.setIconifiedByDefault(false)
		viewModel.query.collectLA(this, catch = {}) {
			searchView.setQuery(it, false)
		}
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean = true

	override fun onViewCreated(view: View) {
		viewModel.setQuery(args.getString(BundleKeys.BUNDLE_QUERY, ""))
	}

	/** Class that handles querying */
	inner class InternalQuery
		: SearchView.OnQueryTextListener {
		override fun onQueryTextSubmit(query: String): Boolean {
			viewModel.setQuery(query)
			return true
		}

		override fun onQueryTextChange(newText: String?): Boolean {
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
		getIsLoading = {
			flow { emit(false) }
		},
		getChildren = {
			flow {
				emit(List(5) {
					FullCatalogNovelUI(
						it,
						"Test",
						"",
						it % 2 == 0
					)
				})
			}
		},
		getException = {
			flow { emit(null) }
		},
		onClick = {},
		onRefresh = {},
		onRefreshAll = {}
	)
}

@Composable
fun SearchContent(
	rows: List<SearchRowUI>,
	getIsLoading: (id: Int) -> Flow<Boolean>,
	getChildren: (id: Int) -> Flow<List<ACatalogNovelUI>>,
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
			modifier = Modifier.fillMaxSize()
		) {

			items(rows, key = { it.extensionID }) { row ->
				val isLoading by getIsLoading(row.extensionID).collectAsState(false)
				val exception by getException(row.extensionID).collectAsState(null)
				val children by getChildren(row.extensionID).collectAsState(listOf())

				SearchRowContent(
					row,
					isLoading,
					children,
					onClick,
					exception
				) {
					onRefresh(row.extensionID)
				}

			}
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
		),
		isLoading = true,
		children = listOf(),
		onClick = {},
		exception = null,
		onRefresh = {}
	)
}

@Composable
fun SearchRowContent(
	row: SearchRowUI,
	isLoading: Boolean,
	children: List<ACatalogNovelUI>,
	onClick: (ACatalogNovelUI) -> Unit,
	exception: Throwable?,
	onRefresh: () -> Unit
) {
	Column(
		modifier = Modifier.fillMaxWidth().padding(top = 8.dp, start = 8.dp, end = 8.dp),
	) {
		Row(
			modifier = Modifier.padding(8.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Icon(
				if (!row.imageURL.isNullOrEmpty())
					rememberImagePainter(row.imageURL)
				else painterResource(R.drawable.library),
				contentDescription = row.name,
				modifier = Modifier.size(32.dp)
			)
			Text(row.name)
		}
		if (isLoading) {
			LinearProgressIndicator(
				modifier = Modifier.fillMaxWidth()
			)
		} else {
			LazyRow {
				items(children, key = { it.id }) {
					SearchResultContent(
						it,
						onClick
					)
				}
			}
		}

		if (exception != null) {
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
		Divider(modifier = Modifier.fillMaxWidth())
	}
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchResultContent(item: ACatalogNovelUI, onClick: (ACatalogNovelUI) -> Unit) {
	Card(
		onClick = { onClick(item) },
		border =
		if (item.isSelected) {
			BorderStroke(2.dp, colorResource(id = R.color.colorPrimary))
		} else {
			null
		},
		modifier = Modifier.aspectRatio(.70f).width(124.dp)
	) {
		val blackTrans = colorResource(id = R.color.black_trans)
		Box {
			Image(
				painter = if (item.imageURL.isNotEmpty()) {
					rememberImagePainter(item.imageURL)
				} else {
					painterResource(R.drawable.broken_image)
				},
				contentDescription = null,
				modifier = Modifier
					.fillMaxSize()
					.drawWithContent {

						drawContent()
						drawRect(
							Brush.verticalGradient(
								colors = listOf(
									Color.Transparent,
									blackTrans
								),
							)
						)
					},
			)

			Text(
				text = item.title,
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.padding(8.dp),
				fontWeight = FontWeight.Bold,
				textAlign = TextAlign.Center
			)
		}
	}
}