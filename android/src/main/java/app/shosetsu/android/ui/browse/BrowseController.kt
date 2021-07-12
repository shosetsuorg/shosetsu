package app.shosetsu.android.ui.browse

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

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_EXTENSION
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.catalogue.CatalogController
import app.shosetsu.android.ui.extensionsConfigure.ConfigureExtension
import app.shosetsu.android.view.controller.FastAdapterRefreshableRecyclerController
import app.shosetsu.android.view.uimodels.model.ExtensionUI
import app.shosetsu.android.view.widget.EmptyDataView
import app.shosetsu.android.viewmodel.abstracted.ABrowseViewModel
import app.shosetsu.common.consts.REPOSITORY_HELP_URL
import app.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.R
import com.mikepenz.fastadapter.FastAdapter

/**
 * shosetsu
 * 18 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class BrowseController : FastAdapterRefreshableRecyclerController<ExtensionUI>() {
	override val viewTitleRes: Int = R.string.browse

	init {
		setHasOptionsMenu(true)
	}

	/***/
	val viewModel: ABrowseViewModel by viewModel()

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_browse, menu)
		(menu.findItem(R.id.search).actionView as SearchView)
			.setOnQueryTextListener(BrowseSearchQuery { router.shosetsuPush(it) })
	}

	private fun installExtension(extension: ExtensionUI) {
		var installed = false
		var update = false
		if (extension.installed && extension.isExtEnabled) {
			installed = true
			if (extension.updateState() == ExtensionUI.State.UPDATE) update = true
		}

		if (!installed || update) {
			if (viewModel.isOnline()) {
				viewModel.installExtension(extension)
			} else {
				displayOfflineSnackBar(R.string.controller_browse_snackbar_offline_no_install_extension)
			}
		}
	}

	override fun FastAdapter<ExtensionUI>.setupFastAdapter() {
		setOnClickListener { _, _, item, _ ->
			// First check if the user is online or not
			if (viewModel.isOnline()) {
				// If the extension is installed, push to it, otherwise prompt the user to install
				if (item.installed) {
					router.shosetsuPush(
						CatalogController(
							bundleOf(
								BUNDLE_EXTENSION to item.id
							)
						)
					)
				} else makeSnackBar(R.string.controller_browse_snackbar_not_installed)?.setAction(R.string.install) {
					installExtension(item)
				}?.show()
			} else displayOfflineSnackBar(R.string.controller_browse_snackbar_offline_no_extension)
			true
		}


		hookClickEvent(
			bind = { it: ExtensionUI.ViewHolder -> it.binding.installButton }
		) { _, _, _, item ->
			installExtension(item)
		}

		hookClickEvent(
			bind = { it: ExtensionUI.ViewHolder -> it.binding.settings }
		) { _, _, _, item ->
			router.shosetsuPush(ConfigureExtension(bundleOf(BUNDLE_EXTENSION to item.id)))
		}
	}


	override fun showEmpty() {
		super.showEmpty()
		binding.emptyDataView.show(
			R.string.empty_browse_message,
			EmptyDataView.Action(R.string.empty_browse_refresh_action) {
				onRefresh()
			})
	}

	override fun handleErrorResult(e: HResult.Error) {
		viewModel.reportError(e)
	}

	override fun onViewCreated(view: View) {
		super.onViewCreated(view)
		showEmpty()
		viewModel.liveData.observe(this) { handleRecyclerUpdate(it) }
	}

	override fun updateUI(newList: List<ExtensionUI>) {
		launchIO {
			val list = newList
				.sortedBy { it.name }
				.sortedBy { it.lang }
				.sortedBy { !it.installed }
				.sortedBy { it.updateState() != ExtensionUI.State.UPDATE }
			launchUI { super.updateUI(list) }
		}
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
		R.id.help -> {
			openHelpMenu()
			true
		}
		R.id.search -> true
		R.id.browse_import -> {
			makeSnackBar(R.string.regret)?.show()
			true
		}
		else -> false
	}

	private fun openHelpMenu() {
		startActivity(Intent(ACTION_VIEW, Uri.parse(REPOSITORY_HELP_URL)))
	}

	override fun onRefresh() {
		if (viewModel.isOnline())
			viewModel.refreshRepository()
		else displayOfflineSnackBar(R.string.controller_browse_snackbar_offline_no_update_extension)
	}
}