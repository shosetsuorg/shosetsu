package app.shosetsu.android.ui.updates

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.shosetsu.android.common.ext.displayOfflineSnackBar
import app.shosetsu.android.common.ext.openChapter
import app.shosetsu.android.common.ext.trimDate
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.view.compose.EmptyDataContent
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.view.controller.base.CollapsedToolBarController
import app.shosetsu.android.view.widget.EmptyDataView
import app.shosetsu.android.viewmodel.abstracted.AUpdatesViewModel
import app.shosetsu.common.domain.model.local.UpdateCompleteEntity
import coil.compose.rememberImagePainter
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.android.material.composethemeadapter.MdcTheme
import org.joda.time.DateTime
import java.util.*

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
 * @since 09 / 10 / 2021
 * @author Doomsdayrs
 */
@ExperimentalMaterialApi
@ExperimentalFoundationApi
class ComposeUpdatesController : CollapsedToolBarController, ShosetsuController() {
	override val viewTitleRes: Int = R.string.updates

	private val viewModel: AUpdatesViewModel by viewModel()

	override fun onViewCreated(view: View) {}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup,
		savedViewState: Bundle?
	): View = ComposeView(container.context).apply {
		setViewTitle()
		setContent {
			MdcTheme {
				UpdatesContent(
					this@ComposeUpdatesController,
					viewModel,
					onRefresh = this@ComposeUpdatesController::onRefresh
				) { (chapterID, novelID) ->
					activity?.openChapter(chapterID, novelID)
				}
			}
		}
	}

	fun onRefresh() {
		if (viewModel.isOnline())
			viewModel.startUpdateManager()
		else displayOfflineSnackBar(R.string.generic_error_cannot_update_library_offline)
	}
}

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun UpdatesContent(
	parent: ComposeUpdatesController,
	viewModel: AUpdatesViewModel,
	onRefresh: () -> Unit,
	openChapter: (UpdateCompleteEntity) -> Unit
) {
	val result: Map<DateTime, List<UpdateCompleteEntity>> by viewModel.items.collectAsState(emptyMap())
	val isRefreshing by viewModel.isRefreshing.collectAsState(false)

	// 		parent.logE("Error on handling result in updates", it.exception)
	//
	//				ACRA.errorReporter.handleException(it.exception, false)
	//

	SwipeRefresh(
		state = SwipeRefreshState(isRefreshing),
		onRefresh = onRefresh
	) {
		if (result.isEmpty()) {
			Column {
				EmptyDataContent(
					R.string.empty_updates_message,
					EmptyDataView.Action(R.string.empty_updates_refresh_action) {
						onRefresh()
					}
				)
			}
		}

		LazyColumn(
			contentPadding = PaddingValues(bottom = 112.dp)
		) {
			result.forEach { (header, updateItems) ->
				stickyHeader {
					UpdateHeaderItemContent(header)
				}

				items(updateItems, key = { it.chapterID }) {
					UpdateItemContent(it) {
						openChapter(it)
					}
				}
			}
		}
	}
}

@Preview
@Composable
fun PreviewUpdateHeaderItemContent() {
	UpdateHeaderItemContent(DateTime().trimDate())
}

@ExperimentalMaterialApi
@Preview
@Composable
fun PreviewUpdateItemContent() {
	UpdateItemContent(
		UpdateCompleteEntity(
			1,
			1,
			System.currentTimeMillis(),
			"This is a chapter",
			"This is a novel",
			""
		),
	) {
	}
}


@ExperimentalMaterialApi
@Composable
fun UpdateItemContent(updateUI: UpdateCompleteEntity, onClick: () -> Unit) {
	Card(
		modifier = Modifier.fillMaxWidth().height(72.dp),
		shape = RectangleShape,
		onClick = onClick
	) {
		Row {
			Image(
				if (updateUI.novelImageURL.isNotEmpty()) {
					rememberImagePainter(updateUI.novelImageURL)
				} else {
					painterResource(R.drawable.broken_image)
				},
				null,
				modifier = Modifier.aspectRatio(2f / 3f).height(IntrinsicSize.Min)
			)
			Column(
				verticalArrangement = Arrangement.Center,
				modifier = Modifier.fillMaxWidth(),
			) {
				Text(
					updateUI.chapterName,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis
				)
				Text(
					updateUI.novelName,
					fontSize = 14.sp,
					maxLines = 1,
					overflow = TextOverflow.Ellipsis,
					modifier = Modifier.alpha(.75f)
				)
				Text(
					DateFormat.format("hh:mm", Date(updateUI.time)).toString(),
					fontSize = 12.sp,
					maxLines = 1,
					modifier = Modifier.alpha(.5f)
				)
			}
		}
	}
}

@Composable
fun UpdateHeaderItemContent(dateTime: DateTime) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		shape = RectangleShape,
		elevation = 0.dp
	) {
		val text = when (dateTime) {
			DateTime(System.currentTimeMillis()).trimDate() ->
				stringResource(R.string.today)
			DateTime(System.currentTimeMillis()).trimDate().minusDays(1) ->
				stringResource(R.string.yesterday)
			else -> "${dateTime.dayOfMonth}/${dateTime.monthOfYear}/${dateTime.year}"
		}
		Text(text, modifier = Modifier.fillMaxWidth().padding(16.dp))
	}
}