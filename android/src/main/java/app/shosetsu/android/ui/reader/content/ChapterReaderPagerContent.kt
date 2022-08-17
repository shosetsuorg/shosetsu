package app.shosetsu.android.ui.reader.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.shosetsu.android.R
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem
import com.google.accompanist.pager.*
import kotlinx.coroutines.flow.distinctUntilChanged

/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * Shosetsu
 *
 * @since 26 / 05 / 2022
 * @author Doomsdayrs
 * Content of pager itself
 */
@OptIn(ExperimentalPagerApi::class)
@Suppress("FunctionName", "DEPRECATION")
@Composable
fun ChapterReaderPagerContent(
	paddingValues: PaddingValues,

	items: List<ReaderUIItem>,
	isHorizontal: Boolean,

	isSwipeInverted: Boolean,

	currentPage: Int?,
	onPageChanged: (Int) -> Unit,

	markChapterAsCurrent: (item: ReaderUIItem.ReaderChapterUI) -> Unit,
	onChapterRead: (item: ReaderUIItem.ReaderChapterUI) -> Unit,

	onStopTTS: () -> Unit,

	createPage: @Composable PagerScope.(page: Int) -> Unit
) {
	// Do not create the pager if the currentPage has not been set yet
	if (currentPage == null) {
		Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
			Text(stringResource(R.string.loading))
		}
		return
	}

	val pagerState = rememberPagerState(currentPage)

	var curChapter: ReaderUIItem.ReaderChapterUI? by remember { mutableStateOf(null) }
	if (items.isNotEmpty())
		LaunchedEffect(pagerState) {
			snapshotFlow { pagerState.currentPage }.distinctUntilChanged().collect { newPage ->
				onStopTTS()
				val item = items.getOrNull(newPage) ?: return@collect

				when (item) {
					is ReaderUIItem.ReaderChapterUI -> {
						markChapterAsCurrent(item)
						curChapter = item
					}
					is ReaderUIItem.ReaderDividerUI -> {
						// Do not mark read backwards
						if (item.next?.id != curChapter?.id)
							item.prev.let(onChapterRead)
					}
				}
				onPageChanged(newPage)
			}
		}

	if (isHorizontal) {
		HorizontalPager(
			count = items.size,
			state = pagerState,
			modifier = Modifier
				.fillMaxSize()
				.padding(
					top = paddingValues.calculateTopPadding(),
					bottom = paddingValues.calculateBottomPadding()
				),
			reverseLayout = isSwipeInverted,
			content = {
				createPage(this, it)
			}
		)
	} else {
		VerticalPager(
			count = items.size,
			state = pagerState,
			modifier = Modifier
				.fillMaxSize()
				.padding(
					top = paddingValues.calculateTopPadding(),
					bottom = paddingValues.calculateBottomPadding()
				),
			content = {
				createPage(this, it)
			}
		)
	}
}