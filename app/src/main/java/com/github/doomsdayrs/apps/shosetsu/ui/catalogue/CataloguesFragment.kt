package com.github.doomsdayrs.apps.shosetsu.ui.catalogue

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.adapters.CataloguesAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners.CataloguesSearchQuery
import com.github.doomsdayrs.apps.shosetsu.ui.extensionsConfigure.ConfigureExtensions
import com.github.doomsdayrs.apps.shosetsu.ui.main.MainActivity
import com.github.doomsdayrs.apps.shosetsu.variables.obj.DefaultScrapers.asFormatter
import com.github.doomsdayrs.apps.shosetsu.variables.obj.Settings
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.FormatterCard
import kotlinx.android.synthetic.main.fragment_catalogues.*
import java.util.*

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
class CataloguesFragment : Fragment(R.layout.fragment_catalogues) {
    private lateinit var cards: ArrayList<FormatterCard>

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utilities.setActivityTitle(activity, getString(R.string.catalogues))
        //TODO Conditional for turning formatter on and off
        // > Conditional for languages
        // > Conditional for categories, maybe
        cards = asFormatter
        recyclerView.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        val adapter = CataloguesAdapter(cards, fragmentManager!!)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }
}