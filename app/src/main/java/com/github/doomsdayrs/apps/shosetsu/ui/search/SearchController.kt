package com.github.doomsdayrs.apps.shosetsu.ui.search

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.R.layout.search_activity
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities.setActivityTitle
import com.github.doomsdayrs.apps.shosetsu.common.consts.Bundle.BUNDLE_QUERY
import com.github.doomsdayrs.apps.shosetsu.common.ext.context
import com.github.doomsdayrs.apps.shosetsu.common.ext.getString
import com.github.doomsdayrs.apps.shosetsu.common.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.ui.search.adapters.SearchAdapter
import com.github.doomsdayrs.apps.shosetsu.view.base.ViewedController
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ISearchViewModel
import java.io.Serializable

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
class SearchController : ViewedController() {
	private class InternalQuery(val searchController: SearchController) : SearchView.OnQueryTextListener {

		override fun onQueryTextSubmit(query: String?): Boolean {
			return if (query != null) {
				searchController.query = query
				searchController.array = arrayListOf()
				searchController.adapter.notifyDataSetChanged()
				return true
			} else false
		}

		override fun onQueryTextChange(newText: String?): Boolean {
			return true
		}

	}

	class StoredData(val id: Int) : Serializable {
		//TODO This is dirty, Maybe replace with CatalogueNovelCard later
		var novelArray: List<Array<String>> = arrayListOf()
		var intArray: List<Int> = arrayListOf()
	}

	override val layoutRes: Int = search_activity

	//TODO replace with searchControllerViewModel
	val iSearchViewModel: ISearchViewModel by viewModel()

	var adapter: SearchAdapter = SearchAdapter(this)
	var query: String = ""
	var array: ArrayList<StoredData> = arrayListOf()

	@Attach(R.id.recyclerView)
	var recyclerView: RecyclerView? = null

	init {
		setHasOptionsMenu(true)
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_search, menu)
		val searchView = menu.findItem(R.id.catalogues_search).actionView as SearchView
		searchView.setQuery(query, false)
		searchView.setOnQueryTextListener(InternalQuery(this))
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return true
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		outState.putString(BUNDLE_QUERY, query)
		outState.putSerializable("data", array)
	}

	override fun onRestoreInstanceState(savedInstanceState: Bundle) {
		query = savedInstanceState.getString(BUNDLE_QUERY)!!
		array = savedInstanceState.getSerializable("data") as ArrayList<StoredData>
	}

	override fun onViewCreated(view: View) {
		setActivityTitle(activity, getString(R.string.results))
		Log.i("SearchQueryReceived", query)
		Log.d("SearchController", "Is view null?${recyclerView == null}")
		recyclerView?.layoutManager = LinearLayoutManager(context)
		adapter = SearchAdapter(this)
		recyclerView?.adapter = adapter
	}

	fun containsData(id: Int): Boolean {
		for (data in array)
			if (data.id == id)
				return true
		return false
	}

	fun getData(id: Int): StoredData {
		for (data in array)
			if (data.id == id)
				return data
		return StoredData(id)
	}
}