package com.github.doomsdayrs.apps.shosetsu.ui.search

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys
import com.github.doomsdayrs.apps.shosetsu.common.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.ui.search.adapters.SearchRowAdapter
import com.github.doomsdayrs.apps.shosetsu.view.base.FastAdapterRecyclerController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.search.SearchRowUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.abstracted.ISearchViewModel
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
 * ====================================================================
 */


/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class SearchController(bundle: Bundle) : FastAdapterRecyclerController<SearchRowUI>(bundle) {
	override val viewTitleRes: Int = R.string.search
	override val layoutRes: Int = R.layout.search_activity
	internal val viewModel: ISearchViewModel by viewModel()
	private var searchView: SearchView? = null

	init {
		setHasOptionsMenu(true)
	}

	override val fastAdapter: FastAdapter<SearchRowUI> by lazy {
		val adapter = SearchRowAdapter(this)
		adapter.addAdapter(0, itemAdapter)
		adapter
	}

	override fun onDestroy() {
		super.onDestroy()
		searchView = null
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_search, menu)
		searchView = menu.findItem(R.id.catalogues_search).actionView as SearchView
		searchView?.setOnQueryTextListener(InternalQuery())
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean = true

	override fun onViewCreated(view: View) {
		viewModel.setQuery(args.getString(BundleKeys.BUNDLE_QUERY, ""))
	}

	override fun setupFastAdapter() {
		super.setupFastAdapter()
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