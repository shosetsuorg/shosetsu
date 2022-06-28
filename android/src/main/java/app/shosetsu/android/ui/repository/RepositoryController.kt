package app.shosetsu.android.ui.repository

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.view.compose.ErrorAction
import app.shosetsu.android.view.compose.ErrorContent
import app.shosetsu.android.view.compose.ShosetsuCompose
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.view.controller.base.ExtendedFABController
import app.shosetsu.android.view.controller.base.syncFABWithCompose
import app.shosetsu.android.view.uimodels.model.RepositoryUI
import app.shosetsu.android.viewmodel.abstracted.ARepositoryViewModel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.RepositoryAddBinding
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_CONSECUTIVE
import com.google.android.material.snackbar.Snackbar
import org.acra.ACRA
import androidx.appcompat.app.AlertDialog.Builder as AlertDialogBuilder

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
 * shosetsu
 * 16 / 09 / 2020
 */
class RepositoryController : ShosetsuController(),
	ExtendedFABController {
	private val viewModel: ARepositoryViewModel by viewModel()

	override val viewTitleRes: Int = R.string.repositories

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup,
		savedViewState: Bundle?
	): View =
		ComposeView(container.context).apply {
			setViewTitle()
			setContent {
				ShosetsuCompose {
					val items by viewModel.liveData.collectAsState(listOf())

					RepositoriesContent(
						items = items,
						toggleEnabled = {
							toggleIsEnabled(it)
						},
						onRemove = {
							onRemove(it, container.context)
						},
						addRepository = {
							launchAddRepositoryDialog(container)
						},
						onRefresh = {
							onRefresh()
						},
						fab
					)
				}
			}
		}

	override fun onViewCreated(view: View) {

	}

	private fun onRemove(item: RepositoryUI, context: Context) {
		AlertDialogBuilder(context)
			.setTitle(R.string.alert_dialog_title_warn_repo_removal)
			.setMessage(R.string.alert_dialog_message_warn_repo_removal)
			.setPositiveButton(android.R.string.ok) { _, _ ->
				removeRepository(item)
			}.setNegativeButton(android.R.string.cancel) { _, _ ->
			}.show()
	}

	private fun undoRemoveRepository(item: RepositoryUI) {
		viewModel.undoRemove(item).observe(

			catch = {
				it.printStackTrace()
				ACRA.errorReporter.handleSilentException(it)

				// Warn the user that there was an error
				makeSnackBar(R.string.controller_repositories_snackbar_fail_undo_repo_removal)
					// Ask if the user wants to retry
					?.setAction(R.string.generic_question_retry) {
						undoRemoveRepository(item)
					}
					// If the user doesn't want to retry, ask to refresh
					?.setOnDismissedNotByAction { _, _ ->
						showWarning()
					}
					?.show()
			}
		) {
			// Success, ask to refresh
			showWarning()
		}
	}

	private fun removeRepository(item: RepositoryUI) {
		// Pass item to viewModel to remove, observe result
		viewModel.remove(item).observe(
			catch = {
				logE("Failed to remove repository $item", it)
				makeSnackBar(R.string.toast_repository_remove_fail)
					?.setAction(R.string.generic_question_retry) {
						removeRepository(item)
					}?.show()
			}
		) {
			// Inform user of the repository being removed
			makeSnackBar(
				R.string.controller_repositories_snackbar_repo_removed,
			)
				// Ask the user if they want to undo
				?.setAction(R.string.generic_undo) {
					undoRemoveRepository(item)
				}
				// If they don't, ask to refresh
				?.setOnDismissedNotByAction { _, _ ->
					showWarning()
				}?.show()
		}
	}

	private fun toggleIsEnabled(item: RepositoryUI) {
		viewModel.toggleIsEnabled(item).observe(
			catch = {
				// Inform the user of an error
				makeSnackBar(R.string.toast_error_repository_toggle_enabled_failed)
					// Ask the user if they want to retry
					?.setAction(R.string.generic_question_retry) {
						toggleIsEnabled(item)
					}?.show()
			}
		) { newState ->
			// Inform the user of the new state
			makeSnackBar(
				if (newState)
					R.string.toast_success_repository_toggled_enabled
				else
					R.string.toast_success_repository_toggled_disabled
			)
				// After, ask the user if they want to refresh
				?.setOnDismissed { _, event ->
					if (event != DISMISS_EVENT_CONSECUTIVE)
						showWarning()
				}?.show()
		}
	}

	private fun addRepository(name: String, url: String) {
		viewModel.addRepository(name, url).observe(
			catch = {
				// Inform the user the repository couldn't be added
				makeSnackBar(R.string.toast_repository_add_fail)
					// Ask the user if they want to retry
					?.setAction(R.string.generic_question_retry) {
						addRepository(name, url)
					}?.show()
			}
		) {
			// Inform the user that the repository was added
			makeSnackBar(R.string.toast_repository_added)
				// Ask if the user wants to refresh the UI
				?.setOnDismissed { _, _ ->
					showWarning()
				}?.show()
		}
	}

	private fun launchAddRepositoryDialog(view: View) {
		val addBinding: RepositoryAddBinding =
			RepositoryAddBinding.inflate(LayoutInflater.from(view.context))

		AlertDialogBuilder(view.context)
			.setView(addBinding.root)
			.setTitle(R.string.repository_add_title)
			.setPositiveButton(android.R.string.ok) { _, _ ->
				with(addBinding) {
					// Pass data to view model, observe result
					addRepository(
						nameInput.text.toString(),
						urlInput.text.toString()
					)
				}
			}
			.setNegativeButton(android.R.string.cancel) { _, _ -> }
			.show()
	}

	/**
	 * Warn the user that they need to refresh their extension list
	 */
	private fun showWarning() {
		makeSnackBar(
			R.string.controller_repositories_snackbar_repo_changed,
			length = Snackbar.LENGTH_LONG
		)
			// Ask the user if they want to refresh
			?.setAction(R.string.controller_repositories_action_repo_update) {
				onRefresh()
			}?.show()
	}

	private lateinit var fab: ExtendedFloatingActionButton
	override fun manipulateFAB(fab: ExtendedFloatingActionButton) {
		this.fab = fab
		fab.setIconResource(R.drawable.add_circle_outline)
		fab.setText(R.string.controller_repositories_action_add)

		// When the FAB is clicked, open a alert dialog to input a new repository
		fab.setOnClickListener { launchAddRepositoryDialog(it) }
	}

	private fun onRefresh() {
		if (viewModel.isOnline()) {
			viewModel.updateRepositories()
		} else displayOfflineSnackBar(R.string.controller_repositories_snackbar_offline_no_update)
	}
}

@Composable
fun RepositoriesContent(
	items: List<RepositoryUI>,
	toggleEnabled: (RepositoryUI) -> Unit,
	onRemove: (RepositoryUI) -> Unit,
	addRepository: () -> Unit,
	onRefresh: () -> Unit,
	fab: ExtendedFloatingActionButton
) {
	if (items.isNotEmpty()) {
		SwipeRefresh(SwipeRefreshState(false), onRefresh) {
			val state = rememberLazyListState()
			syncFABWithCompose(state, fab)
			LazyColumn(
				contentPadding = PaddingValues(
					start = 8.dp,
					top = 8.dp,
					end = 8.dp,
					bottom = 64.dp
				),
				state = state
			) {
				items(items, key = { it.id }) { item ->
					RepositoryContent(
						item,
						onCheckedChange = {
							toggleEnabled(item)
						},
						onRemove = {
							onRemove(item)
						}
					)
				}
			}
		}
	} else {
		ErrorContent(
			stringResource(R.string.empty_repositories_message),
			ErrorAction(R.string.empty_repositories_action) { addRepository() }
		)
	}
}

@Composable
fun RepositoryContent(
	item: RepositoryUI,
	onCheckedChange: () -> Unit,
	onRemove: () -> Unit
) {
	Card(Modifier.padding(bottom = 8.dp)) {
		Row(
			Modifier
				.padding(8.dp)
				.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Column(
				Modifier.fillMaxWidth(.7f)
			) {
				Text(text = item.name)

				Row(
					modifier = Modifier.padding(start = 16.dp)
				) {
					Row {
						Text(
							text = stringResource(id = R.string.id_label),
							style = MaterialTheme.typography.caption
						)
						Text(text = "${item.id}", style = MaterialTheme.typography.caption)
					}
					SelectionContainer {
						Text(
							text = item.url,
							style = MaterialTheme.typography.caption,
							modifier = Modifier.padding(start = 8.dp)
						)
					}
				}
			}

			Row {
				IconButton(onClick = onRemove) {
					Icon(
						painter = painterResource(R.drawable.close),
						contentDescription =
						stringResource(R.string.controller_repositories_action_remove)
					)
				}

				Switch(
					checked = item.isRepoEnabled,
					onCheckedChange = {
						onCheckedChange()
					},
					colors = SwitchDefaults.colors(
						checkedThumbColor = MaterialTheme.colors.secondary
					)
				)
			}
		}
	}
}