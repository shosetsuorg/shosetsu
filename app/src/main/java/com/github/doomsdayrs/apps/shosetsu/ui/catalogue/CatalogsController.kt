package com.github.doomsdayrs.apps.shosetsu.ui.catalogue

import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.lifecycle.Observer
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.common.ext.setActivityTitle
import com.github.doomsdayrs.apps.shosetsu.common.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.common.ext.withFadeTransaction
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.adapters.CataloguesAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CataloguesSearchQuery
import com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure.ConfigureExtensions
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.FormatterCard
import com.github.doomsdayrs.apps.shosetsu.view.base.RecyclerController
import com.github.doomsdayrs.apps.shosetsu.viewmodel.CatalogsViewModel

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
 * ====================================================================
 */
/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
//TODO Searching mechanics here
class CatalogsController : RecyclerController<CataloguesAdapter, FormatterCard>() {


	val viewModel: CatalogsViewModel by viewModel()

	init {
		setHasOptionsMenu(true)
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_catalogues, menu)
		val searchView = menu.findItem(R.id.catalogues_search).actionView as SearchView
		searchView.setOnQueryTextListener(CataloguesSearchQuery(activity))
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.catalogues_search -> true
			R.id.configure_parsers -> {
				router.pushController(ConfigureExtensions().withFadeTransaction())
				true
			}
			else -> false
		}
	}

	override fun onViewCreated(view: View) {
		activity?.setActivityTitle(R.string.catalogues)
		recyclerView?.setHasFixedSize(true)
		adapter = CataloguesAdapter(recyclerArray, router)
		viewModel.liveData.observe(this, Observer(::handleFormatterRepository))
	}

	/**
	 *
	 */
	private fun handleFormatterRepository(result: HResult<List<FormatterCard>>) {
		when (result) {
			is HResult.Loading -> {
				Log.i(logID(), "Loading UWU")
			}
			is HResult.Success -> updateUI(result.data)
			is HResult.Error -> {
				Log.i(logID(), "ERROR OWO ${result.message}")
			}
		}
	}

	override fun difAreItemsTheSame(oldItem: FormatterCard, newItem: FormatterCard): Boolean =
			oldItem.formatter.formatterID == newItem.formatter.formatterID
}