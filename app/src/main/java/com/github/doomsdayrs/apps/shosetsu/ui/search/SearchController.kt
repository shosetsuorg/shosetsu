package com.github.doomsdayrs.apps.shosetsu.ui.search

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.R.layout.search_activity
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities.setActivityTitle
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys
import com.github.doomsdayrs.apps.shosetsu.common.ext.context
import com.github.doomsdayrs.apps.shosetsu.common.ext.getString
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.common.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.ui.search.adapters.SearchAdapter
import com.github.doomsdayrs.apps.shosetsu.view.base.RecyclerController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.NovelUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ISearchViewModel

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
 * TODO When opening a novel from here, Prevent reloading of already established DATA
 */
class SearchController(bundle: Bundle) : RecyclerController<SearchAdapter, NovelUI>() {
	internal class InternalQuery(val searchController: SearchController)
		: SearchView.OnQueryTextListener {
		override fun onQueryTextSubmit(query: String?): Boolean {
			return if (query != null) {
				searchController.viewModel.query = query
				searchController.recyclerArray = arrayListOf()
				searchController.adapter?.notifyDataSetChanged()
				return true
			} else false
		}

		override fun onQueryTextChange(newText: String?): Boolean {
			return true
		}

	}

	override val layoutRes: Int = search_activity

	val viewModel: ISearchViewModel by viewModel()

	init {
		setHasOptionsMenu(true)
		viewModel.query = bundle.getString(BundleKeys.BUNDLE_QUERY, "")
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_search, menu)
		val searchView = menu.findItem(R.id.catalogues_search).actionView as SearchView
		searchView.setQuery(viewModel.query, false)
		searchView.setOnQueryTextListener(InternalQuery(this))
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return true
	}

	override fun onViewCreated(view: View) {
		setActivityTitle(activity, getString(R.string.results))
		Log.i(logID(), viewModel.query)
		recyclerView?.layoutManager = LinearLayoutManager(context)
		adapter = SearchAdapter(this)
		recyclerView?.adapter = adapter
	}

	override fun difAreItemsTheSame(oldItem: NovelUI, newItem: NovelUI): Boolean {
		TODO("Not yet implemented")
	}
}