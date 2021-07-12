package app.shosetsu.android.ui.search

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import app.shosetsu.android.common.consts.BundleKeys
import app.shosetsu.android.common.ext.shosetsuPush
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.ui.search.adapters.SearchRowAdapter
import app.shosetsu.android.view.controller.GenericFastAdapterRecyclerController
import app.shosetsu.android.view.uimodels.model.search.SearchRowUI
import app.shosetsu.android.viewmodel.abstracted.ASearchViewModel
import app.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.R
import com.mikepenz.fastadapter.FastAdapter

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
class SearchController(bundle: Bundle) : GenericFastAdapterRecyclerController<SearchRowUI>(bundle) {
	override val viewTitleRes: Int = R.string.search
	internal val viewModel: ASearchViewModel by viewModel()
	private var searchView: SearchView? = null

	init {
		setHasOptionsMenu(true)
	}

	override val fastAdapter: FastAdapter<SearchRowUI> by lazy {
		val adapter = SearchRowAdapter(this, { router.shosetsuPush(it) }, viewModel)
		adapter.addAdapter(0, itemAdapter)
		adapter
	}

	override fun onDestroy() {
		super.onDestroy()
		searchView = null
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_search, menu)
		searchView = menu.findItem(R.id.search).actionView as SearchView
		searchView?.setOnQueryTextListener(InternalQuery())
	}

	override fun handleErrorResult(e: HResult.Error) {
		viewModel.reportError(e)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean = true

	override fun onViewCreated(view: View) {
		viewModel.setQuery(args.getString(BundleKeys.BUNDLE_QUERY, ""))
		viewModel.listings.observe(this) {
			handleRecyclerUpdate(it)
		}
	}

	/** Class that handles querying */
	inner class InternalQuery
		: SearchView.OnQueryTextListener {
		override fun onQueryTextSubmit(query: String): Boolean {
			viewModel.setQuery(query)
			viewModel.loadQuery()
			return true
		}

		override fun onQueryTextChange(newText: String?): Boolean {
			newText?.let { viewModel.setQuery(it) }
			return true
		}
	}

}