package com.github.doomsdayrs.apps.shosetsu.ui.search.async

import android.os.AsyncTask
import android.view.View
import app.shosetsu.lib.values
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.async.CatalogueLoader
import com.github.doomsdayrs.apps.shosetsu.ui.search.SearchController.StoredData
import com.github.doomsdayrs.apps.shosetsu.ui.search.adapters.SearchResultsAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.search.viewHolders.SearchViewHolder
import org.luaj.vm2.LuaError


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
 * ====================================================================
 */
/**
 * shosetsu
 * 20 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
class SearchLoader(private val searchViewHolder: SearchViewHolder) : AsyncTask<String, Void, Boolean>() {
	var array: List<Array<String>> = arrayListOf()

	override fun onPreExecute() {
		super.onPreExecute()
		searchViewHolder.itemView.post {
			searchViewHolder.progressBar.visibility = View.VISIBLE
		}
	}

	override fun doInBackground(vararg params: String?): Boolean {
		try {
			val a = CatalogueLoader(
					searchViewHolder.formatter,
					searchViewHolder.formatter.searchFilters.values(),
					searchViewHolder.query
			).execute()
			array = Utilities.convertNovelArrayToString2DArray(a)
		} catch (e: LuaError) {
			e.printStackTrace()
			return false
		}
		return true
	}

	override fun onPostExecute(result: Boolean?) {
		super.onPostExecute(result)
		searchViewHolder.itemView.post {
			searchViewHolder.progressBar.visibility = View.GONE
		}
		searchViewHolder.itemView.post {
			// Stores DATA
			with(StoredData(searchViewHolder.formatter.formatterID)) {
				novelArray = array
				searchViewHolder.searchController.array.add(this)
			}

			// Displays DATA
			searchViewHolder.searchResultsAdapter = SearchResultsAdapter(array, searchViewHolder)
			searchViewHolder.setAdapter()
		}
	}

}