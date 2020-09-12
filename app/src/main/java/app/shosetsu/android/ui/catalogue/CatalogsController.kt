package app.shosetsu.android.ui.catalogue

import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import app.shosetsu.android.common.consts.BundleKeys
import app.shosetsu.android.common.ext.context
import app.shosetsu.android.common.ext.setOnClickListener
import app.shosetsu.android.common.ext.toast
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.ui.catalogue.listeners.CataloguesSearchQuery
import app.shosetsu.android.ui.extensionsConfigure.ConfigureExtensions
import app.shosetsu.android.view.base.FastAdapterRecyclerController.BasicFastAdapterRecyclerController
import app.shosetsu.android.view.base.PushCapableController
import app.shosetsu.android.view.uimodels.model.catlog.CatalogOptionUI
import app.shosetsu.android.viewmodel.abstracted.ICatalogOptionsViewModel
import com.bluelinelabs.conductor.Controller
import com.github.doomsdayrs.apps.shosetsu.R

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
//TODO Searching mechanics here
class CatalogsController : BasicFastAdapterRecyclerController<CatalogOptionUI>(),
		PushCapableController {
	private val viewModel: ICatalogOptionsViewModel by viewModel()
	override val viewTitleRes: Int = R.string.catalogues
	lateinit var pushController: (Controller) -> Unit

	init {
		setHasOptionsMenu(true)
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_catalogues, menu)
		(menu.findItem(R.id.catalogues_search).actionView as SearchView)
				.setOnQueryTextListener(CataloguesSearchQuery(pushController))
	}

	override fun acceptPushing(pushController: (Controller) -> Unit) {
		this.pushController = pushController
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.catalogues_search -> true
			R.id.configure_parsers -> {
				pushController(ConfigureExtensions())
				true
			}
			else -> false
		}
	}

	override fun onViewCreated(view: View) {
		viewModel.liveData.observe(this) { handleRecyclerUpdate(it) }
	}

	override fun showEmpty() {
		binding.emptyDataView.show("No sources, Install one from Extensions")
	}

	override fun setupFastAdapter() {
		fastAdapter.setOnClickListener { _, _, (identifier, title), _ ->
			Log.d("FormatterSelection", title)
			if (viewModel.isOnline()) {
				pushController(CatalogController(bundleOf(
						BundleKeys.BUNDLE_FORMATTER to identifier.toInt()
				)))
			} else context?.toast(R.string.you_not_online)
			true
		}
	}

	override fun updateUI(newList: List<CatalogOptionUI>) {
		if (newList.isEmpty()) showEmpty()
		super.updateUI(newList)
	}

	override fun setupRecyclerView() {
		recyclerView.setHasFixedSize(false)
		super.setupRecyclerView()
	}
}