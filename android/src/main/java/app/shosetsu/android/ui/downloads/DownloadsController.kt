package app.shosetsu.android.ui.downloads

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

import android.os.Bundle
import android.view.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.shosetsu.android.common.consts.SELECTED_STROKE_WIDTH
import app.shosetsu.android.common.enums.DownloadStatus.*
import app.shosetsu.android.common.ext.collectLA
import app.shosetsu.android.common.ext.displayOfflineSnackBar
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.view.compose.ErrorContent
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.view.controller.base.ExtendedFABController
import app.shosetsu.android.view.controller.base.syncFABWithCompose
import app.shosetsu.android.view.uimodels.model.DownloadUI
import app.shosetsu.android.viewmodel.abstracted.ADownloadsViewModel
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class DownloadsController : ShosetsuController(),
	ExtendedFABController {

	override val viewTitleRes: Int = R.string.downloads
	private val viewModel: ADownloadsViewModel by viewModel()
	private var fab: ExtendedFloatingActionButton? = null
	private var actionMode: ActionMode? = null

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
				val items by viewModel.liveData.collectAsState(listOf())
				val hasSelected by viewModel.hasSelectedFlow.collectAsState(false)
				val isDownloadPaused by viewModel.isDownloadPaused.collectAsState(false)

				DownloadsContent(
					items = items,
					hasSelected = hasSelected,
					isPaused = isDownloadPaused,
					pauseSelection = {
						pauseSelection()
					},
					startSelection = { startSelection() },
					startFailedSelection = { startFailedSelection() },
					deleteSelected = {
						deleteSelected()
					},
					toggleSelection = {
						viewModel.toggleSelection(it)
					},
					fab
				)
			}
		}
	}

	private fun startSelectionAction(): Boolean {
		if (actionMode != null) return false
		hideFAB(fab!!)
		actionMode = activity?.startActionMode(SelectionActionMode())
		return true
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_downloads, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.set_all_pending -> {
				viewModel.setAllPending()
				return true
			}
			R.id.delete_all -> {
				viewModel.deleteAll()
				return true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	private fun togglePause() {
		if (viewModel.isOnline()) viewModel.togglePause() else displayOfflineSnackBar(R.string.controller_downloads_snackbar_offline_no_download)
	}

	override fun onViewCreated(view: View) {
		viewModel.isDownloadPaused.collectLA(this, catch = {}) {
			fab?.setText(
				if (it)
					R.string.resume
				else R.string.pause
			)
			fab?.setIconResource(
				if (it)
					R.drawable.play_arrow
				else R.drawable.ic_pause_circle_outline_24dp
			)
		}
		viewModel.hasSelectedFlow.collectLA(this, catch = {}) {
			if (it) {
				startSelectionAction()
			} else {
				actionMode?.finish()
			}
		}
	}

	override fun onDestroy() {
		actionMode?.finish()
		super.onDestroy()
	}

	override fun manipulateFAB(fab: ExtendedFloatingActionButton) {
		this.fab = fab
		fab.setOnClickListener { togglePause() }
		fab.setText(R.string.paused)
		fab.setIconResource(R.drawable.ic_pause_circle_outline_24dp)
	}

	override fun showFAB(fab: ExtendedFloatingActionButton) {
		if (actionMode == null) super.showFAB(fab)
	}

	private fun startSelection() {
		viewModel.startSelection()
	}

	private fun startFailedSelection() {
		viewModel.restartFailedSelection()
	}

	private fun pauseSelection() {
		viewModel.pauseSelection()
	}

	private fun deleteSelected() {
		viewModel.deleteSelected()
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

	private inner class SelectionActionMode : ActionMode.Callback {
		override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
			// Hides the original action bar
			// (activity as MainActivity?)?.supportActionBar?.hide()

			mode.menuInflater.inflate(R.menu.toolbar_downloads_selected, menu)
			mode.setTitle(R.string.selection)
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
				else -> false
			}

		override fun onDestroyActionMode(mode: ActionMode) {
			actionMode = null
			showFAB(fab!!)
			viewModel.deselectAll()
		}
	}
}

@Composable
fun DownloadsContent(
	items: List<DownloadUI>,
	hasSelected: Boolean,
	isPaused: Boolean,
	pauseSelection: () -> Unit,
	startSelection: () -> Unit,
	startFailedSelection: () -> Unit,
	deleteSelected: () -> Unit,
	toggleSelection: (DownloadUI) -> Unit,
	fab: ExtendedFloatingActionButton?
) {
	if (items.isNotEmpty()) {
		Box(
			modifier = Modifier.fillMaxSize()
		) {
			val state = rememberLazyListState()
			if (fab != null)
				syncFABWithCompose(state, fab)
			LazyColumn(
				modifier = Modifier.fillMaxSize(),
				contentPadding = PaddingValues(bottom = 140.dp),
				state = state
			) {
				items(items, key = { it.chapterID }) {
					DownloadContent(
						it,
						onClick = {
							if (hasSelected)
								toggleSelection(it)
						},
						onLongClick = {
							toggleSelection(it)
						}
					)
				}
			}

			if (hasSelected) {
				val selectedDownloads: List<DownloadUI> by remember {
					derivedStateOf {
						items.filter { it.isSelected }
					}
				}

				val pauseVisible by remember {
					derivedStateOf {
						selectedDownloads.any {
							it.status == PENDING
						}
					}
				}

				val restartVisible by remember {
					derivedStateOf {
						selectedDownloads.any {
							it.status == ERROR
						}
					}
				}


				val startVisible by remember {
					derivedStateOf {
						selectedDownloads.any {
							it.status == PAUSED
						}
					}
				}

				val deleteVisible by remember {
					derivedStateOf {
						selectedDownloads.any {
							it.status == PAUSED || it.status == PENDING || it.status == ERROR || (isPaused && it.status == DOWNLOADING)
						}
					}
				}

				Card(
					modifier = Modifier
						.align(BiasAlignment(0f, 0.7f))
				) {
					Row {
						IconButton(
							onClick = pauseSelection,
							enabled = pauseVisible
						) {
							Icon(
								painterResource(R.drawable.pause),
								stringResource(R.string.pause)
							)
						}
						IconButton(
							onClick = startSelection,
							enabled = startVisible
						) {
							Icon(
								painterResource(R.drawable.play_arrow),
								stringResource(R.string.start)
							)
						}
						IconButton(
							onClick = startFailedSelection,
							enabled = restartVisible
						) {
							Icon(
								painterResource(R.drawable.refresh),
								stringResource(R.string.restart)
							)
						}
						IconButton(
							onClick = deleteSelected,
							enabled = deleteVisible
						) {
							Icon(
								painterResource(R.drawable.trash),
								stringResource(R.string.delete)
							)
						}
					}
				}
			}
		}
	} else {
		ErrorContent(
			stringResource(R.string.empty_downloads_message)
		)
	}
}

@Preview
@Composable
fun PreviewDownloadContent() {
	MdcTheme {
		DownloadContent(
			DownloadUI(
				0,
				0,
				"aaa",
				"Chpater",
				"Novel",
				0,
				DOWNLOADING,
				false
			),
			{},
			{}
		)
	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DownloadContent(
	item: DownloadUI,
	onClick: () -> Unit,
	onLongClick: () -> Unit,
) {
	Card(
		border = if (item.isSelected) {
			BorderStroke(
				width = (SELECTED_STROKE_WIDTH / 2).dp,
				color = MaterialTheme.colors.primary
			)
		} else {
			null
		},
		modifier = Modifier
			.combinedClickable(
				onClick = onClick,
				onLongClick = onLongClick
			)
	) {
		Column(
			Modifier
				.padding(16.dp)
				.fillMaxWidth()
		) {
			Text(
				text = item.novelName,
				style = MaterialTheme.typography.body1
			)
			Text(
				text = item.chapterName,
				style = MaterialTheme.typography.body2
			)

			Row(
				Modifier
					.padding(top = 8.dp)
					.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				val status = item.status
				if (status == DOWNLOADING || status == WAITING) {
					LinearProgressIndicator(modifier = Modifier.fillMaxWidth(.7f))
				} else {
					LinearProgressIndicator(0.0f, modifier = Modifier.fillMaxWidth(.7f))
				}

				Text(
					text = stringResource(
						id = when (status) {
							PENDING -> {
								R.string.pending
							}
							DOWNLOADING -> {
								R.string.downloading
							}
							PAUSED -> {
								R.string.paused
							}
							ERROR -> {
								R.string.error
							}
							WAITING -> {
								R.string.waiting
							}
							else -> {
								R.string.completed
							}
						}
					),
					textAlign = TextAlign.End,
					style = MaterialTheme.typography.caption,
					modifier = Modifier
						.padding(start = 8.dp)
						.fillMaxWidth()
				)
			}
		}
	}
}