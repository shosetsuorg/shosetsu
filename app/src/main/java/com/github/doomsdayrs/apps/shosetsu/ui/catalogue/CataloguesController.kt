package com.github.doomsdayrs.apps.shosetsu.ui.catalogue

import android.view.*
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bluelinelabs.conductor.Controller
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.adapters.CataloguesCAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CataloguesSearchQuery
import com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure.ConfigureExtensions
import com.github.doomsdayrs.apps.shosetsu.ui.main.MainActivity
import com.github.doomsdayrs.apps.shosetsu.variables.obj.DefaultScrapers.asFormatter

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
class CataloguesController : Controller() {
    private val cards by lazy { asFormatter }
    private lateinit var recyclerView: RecyclerView


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
                val ce = ConfigureExtensions()
                ce.jsonArray = Settings.disabledFormatters
                (activity as MainActivity).transitionView(ce)
                true
            }
            else -> false
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.fragment_catalogues, container, false)
        Utilities.setActivityTitle(activity, applicationContext!!.getString(R.string.catalogues))
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.setHasFixedSize(true)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        val adapter = CataloguesCAdapter(cards, router)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        return view
    }
}