package com.github.doomsdayrs.apps.shosetsu.ui.search

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
/*

class SearchController(bundle: Bundle) : RecyclerController<SearchAdapter, Any>() {
	/** Class that handles querying */
	inner class InternalQuery
		: SearchView.OnQueryTextListener {
		override fun onQueryTextSubmit(query: String): Boolean {
			viewModel.query.postValue(query)
			return true
		}

		override fun onQueryTextChange(newText: String?): Boolean = true
	}

	override val layoutRes: Int = search_activity
	val viewModel: ISearchViewModel by viewModel()

	lateinit var searchView: SearchView

	init {
		setHasOptionsMenu(true)
		viewModel.setQuery(bundle.getString(BundleKeys.BUNDLE_QUERY, ""))
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_search, menu)
		searchView = menu.findItem(R.id.catalogues_search).actionView as SearchView
		searchView.setOnQueryTextListener(InternalQuery())

	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean = true

	override fun onViewCreated(view: View) {
		activity?.setActivityTitle(R.string.results)
		adapter = SearchAdapter(this)
		viewModel.query.observe(this, Observer {
			launchUI {
				Log.i(logID(), "Searching for $it")
				searchView.setQuery(it, false)
			}
		})
	}


	override fun difAreItemsTheSame(oldItem: Any, newItem: Any): Boolean = true
}
*/
