package com.github.doomsdayrs.apps.shosetsu.ui.search

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.doomsdayrs.api.shosetsu.services.core.objects.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.R.layout.search_activity
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities.setActivityTitle
import com.github.doomsdayrs.apps.shosetsu.ui.search.adapters.SearchAdapter
import kotlinx.android.synthetic.main.search_activity.*

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
class SearchFragment : Fragment() {
    var array: ArrayList<StoredData> = arrayListOf()

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

    class StoredData(val id: Int) {
        var novelArray: List<Novel> = arrayListOf()
        var intArray: List<Int> = arrayListOf()
    }

    private class InternalQuery(val searchFragment: SearchFragment) : SearchView.OnQueryTextListener {

        override fun onQueryTextSubmit(query: String?): Boolean {
            return if (query != null) {
                searchFragment.query = query
                searchFragment.array = arrayListOf()
                searchFragment.adapter.notifyDataSetChanged()
                return true
            } else false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return true
        }

    }

    var adapter: SearchAdapter = SearchAdapter(this)
    var query: String = ""

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
        outState.putString("query", query)
        outState.putSerializable("data", array)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(search_activity, container, false)
        setActivityTitle(activity, "Results")
        if (savedInstanceState != null) {
            query = savedInstanceState.getString("query")!!
            array = savedInstanceState.getSerializable("data") as ArrayList<StoredData>
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("SearchQueryReceived", query)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = SearchAdapter(this)
        recyclerView.adapter = adapter
    }

}