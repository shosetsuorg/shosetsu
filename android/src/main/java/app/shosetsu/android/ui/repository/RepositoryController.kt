package app.shosetsu.android.ui.repository

import android.view.LayoutInflater
import android.view.View
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.view.controller.FastAdapterRefreshableRecyclerController
import app.shosetsu.android.view.controller.base.FABController
import app.shosetsu.android.view.uimodels.model.RepositoryUI
import app.shosetsu.android.view.widget.EmptyDataView
import app.shosetsu.android.viewmodel.abstracted.ARepositoryViewModel
import app.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.RepositoryAddBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_CONSECUTIVE
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.listeners.addClickListener
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
class RepositoryController : FastAdapterRefreshableRecyclerController<RepositoryUI>(),
	FABController {
	private val viewModel: ARepositoryViewModel by viewModel()

	override val viewTitleRes: Int = R.string.repositories

	override fun showEmpty() {
		super.showEmpty()
		binding.emptyDataView.show(
			R.string.empty_repositories_message,
			EmptyDataView.Action(R.string.empty_repositories_action) { launchAddRepositoryDialog(it) }
		)
	}

	override fun setupRecyclerView() {
		super.setupRecyclerView()
		viewModel.liveData.observe(this) { handleRecyclerUpdate(it) }
	}

	override fun handleErrorResult(e: HResult.Error) {
		viewModel.reportError(e)
	}

	override fun FastAdapter<RepositoryUI>.setupFastAdapter() {
		hookClickEvent(
			bind = { it: RepositoryUI.ViewHolder -> it.binding.removeButton }
		) { _, _, _, item ->
			AlertDialogBuilder(binding.root.context)
				.setTitle(R.string.alert_dialog_title_warn_repo_removal)
				.setMessage(R.string.alert_dialog_message_warn_repo_removal)
				.setPositiveButton(android.R.string.ok) { _, _ ->
					removeRepository(item)
				}.setNegativeButton(android.R.string.cancel) { _, _ ->
				}.show()

		}

		addClickListener<RepositoryUI.ViewHolder, RepositoryUI>({ it.binding.switchWidget }) { _, _, _, item ->
			toggleIsEnabled(item)
		}
	}

	private fun undoRemoveRepository(item: RepositoryUI) {
		viewModel.undoRemove(item).handleObserve(
			onError = {
				logError { it }
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
		viewModel.remove(item).handleObserve(
			onError = {
				logE("Failed to remove repository $item", it.exception)
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
		viewModel.toggleIsEnabled(item).handleObserve(
			onError = {
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
		viewModel.addRepository(name, url).handleObserve(
			onError = {
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

	override fun manipulateFAB(fab: FloatingActionButton) {
		fab.setImageResource(R.drawable.add_circle_outline)

		// When the FAB is clicked, open a alert dialog to input a new repository
		fab.setOnClickListener { launchAddRepositoryDialog(it) }
	}

	override fun onRefresh() {
		if (viewModel.isOnline()) {
			viewModel.updateRepositories()
		} else displayOfflineSnackBar(R.string.controller_repositories_snackbar_offline_no_update)
	}
}