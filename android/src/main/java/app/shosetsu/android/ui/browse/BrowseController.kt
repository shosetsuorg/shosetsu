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
import app.shosetsu.android.view.controller.base.PushCapableController
import app.shosetsu.android.view.uimodels.model.ExtensionUI
import app.shosetsu.android.view.widget.EmptyDataView
import app.shosetsu.android.viewmodel.abstracted.IBrowseViewModel
import app.shosetsu.common.consts.REPOSITORY_HELP_URL
import app.shosetsu.common.dto.HResult
import com.bluelinelabs.conductor.Controller
import com.github.doomsdayrs.apps.shosetsu.R
import com.mikepenz.fastadapter.FastAdapter

/**
 * shosetsu
 * 18 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class BrowseController : FastAdapterRefreshableRecyclerController<ExtensionUI>(),
	PushCapableController {
	override val viewTitleRes: Int = R.string.browse

	init {
		setHasOptionsMenu(true)
	}

	override var pushController: (Controller) -> Unit = {}


	/***/
	val viewModel: IBrowseViewModel by viewModel()

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_browse, menu)
		(menu.findItem(R.id.search).actionView as SearchView)
			.setOnQueryTextListener(BrowseSearchQuery(pushController))
	}

	override fun FastAdapter<ExtensionUI>.setupFastAdapter() {
		setOnClickListener { _, _, item, _ ->
			if (item.installed)
				if (viewModel.isOnline()) {
					pushController(
						CatalogController(
							bundleOf(
								BUNDLE_EXTENSION to item.id
							)
						)
					)
				} else context?.toast(R.string.you_not_online)
			else toast(R.string.ext_not_installed)
			true
		}


		hookClickEvent(
			bind = { it: ExtensionUI.ViewHolder -> it.binding.button }
		) { _, _, _, item ->
			var installed = false
			var update = false
			if (item.installed && item.isExtEnabled) {
				installed = true
				if (item.updateState() == ExtensionUI.State.UPDATE) update = true
			}

			if (!installed || update) viewModel.installExtension(item)
		}

		hookClickEvent(
			bind = { it: ExtensionUI.ViewHolder -> it.binding.settings }
		) { _, _, _, item ->
			pushController(ConfigureExtension(bundleOf(BUNDLE_EXTENSION to item.id)))
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
		super.handleErrorResult(e)
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
			toast(R.string.regret)
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
		else toast(R.string.you_not_online)
	}
}