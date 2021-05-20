package app.shosetsu.android.ui.repository

import android.view.LayoutInflater
import android.view.View
import app.shosetsu.android.common.ext.hookClickEvent
import app.shosetsu.android.common.ext.logError
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.view.controller.GenericFastAdapterRecyclerController
import app.shosetsu.android.view.controller.base.FABController
import app.shosetsu.android.view.uimodels.model.RepositoryUI
import app.shosetsu.android.viewmodel.abstracted.ARepositoryViewModel
import app.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.RepositoryAddBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
class RepositoryController : GenericFastAdapterRecyclerController<RepositoryUI>(), FABController {
	private val viewModel: ARepositoryViewModel by viewModel()

	override val viewTitleRes: Int = R.string.repositories

	override fun onViewCreated(view: View) {
	}

	override fun showEmpty() {
		super.showEmpty()
		binding.emptyDataView.show("HOW DO YOU NOT HAVE ANY REPOSITORIES")
	}

	override fun setupRecyclerView() {
		super.setupRecyclerView()
		viewModel.liveData.observe(this) { handleRecyclerUpdate(it) }
	}

	override fun handleErrorResult(e: HResult.Error) {
		super.handleErrorResult(e)
		viewModel.reportError(e)
	}

	override fun FastAdapter<RepositoryUI>.setupFastAdapter() {
		hookClickEvent(
			bind = { it: RepositoryUI.ViewHolder -> it.binding.removeButton }
		) { _, _, _, item ->
			AlertDialogBuilder(binding.root.context)
				.setTitle(R.string.alert_dialog_title_warn_repo_removal)
				.setMessage(R.string.alert_dialog_message_warn_repo_removal)
				.setPositiveButton(android.R.string.ok) { d, w ->
					// Pass item to viewModel to remove, observe result
					viewModel.remove(item).handleObserve(
						onError = {
							toast(R.string.toast_repository_remove_fail)
						}
					) {
						toast(R.string.toast_repository_removed)
						showWarning()
					}
					d.dismiss()
				}.setNegativeButton(android.R.string.cancel) { d, w ->
					d.dismiss()
				}.show()

		}

		addClickListener<RepositoryUI.ViewHolder, RepositoryUI>({ it.binding.switchWidget }) { _, _, _, item ->
			viewModel.toggleIsEnabled(item).handleObserve(
				onError = {
					toast(R.string.toast_error_repository_toggle_enabled_failed)
				}
			) { newState ->
				toast(
					if (newState)
						R.string.toast_success_repository_toggled_enabled
					else
						R.string.toast_success_repository_toggled_disabled
				)
			}
		}
	}


	/**
	 * Warn the user that they need to refresh their extension list
	 */
	private fun showWarning() = AlertDialogBuilder(binding.root.context)
		.setTitle(R.string.repository_list_change_warning)
		.show()

	override fun manipulateFAB(fab: FloatingActionButton) {

		fab.setImageResource(R.drawable.add_circle_outline)

		// When the FAB is clicked, open a alert dialog to input a new repository
		fab.setOnClickListener {
			val addBinding: RepositoryAddBinding =
				RepositoryAddBinding.inflate(LayoutInflater.from(fab.context))

			AlertDialogBuilder(fab.context)
				.setView(addBinding.root)
				.setTitle(R.string.repository_add_title)
				.setPositiveButton(android.R.string.ok) { d, w ->
					addBinding.let {
						// Pass data to view model, observe result
						viewModel.addRepository(
							it.nameInput.text.toString(),
							it.urlInput.text.toString()
						).handleObserve(
							onError = {
								logError { it }
								toast(R.string.toast_repository_add_fail)
							}
						) {
							toast(R.string.toast_repository_added)
							showWarning()
						}
						d.dismiss()
					}
				}
				.setNegativeButton(android.R.string.cancel) { d, w ->
					d.dismiss()
				}
				.show()
		}
	}
}