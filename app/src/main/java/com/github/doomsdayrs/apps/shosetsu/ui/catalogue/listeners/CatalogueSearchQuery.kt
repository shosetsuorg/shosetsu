package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners

import android.os.Build
import android.util.Log
import android.widget.SearchView
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogController
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async.CatalogueQuerySearch
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelListingCard
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
 * Shosetsu
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
class CatalogueSearchQuery(private val catalogFragment: CatalogController) : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String): Boolean {
        catalogFragment.isQuery = false
        catalogFragment.isInSearch = true
        try {
            Log.d("CatalogueSearchQuery", "Query:\t$query")
            val searchResults = CatalogueQuerySearch(catalogFragment).execute(query).get()
            catalogFragment.setLibraryCards(searchResults)
            return true
        } catch (e: Exception) {
            Log.e("CatalogueSearchQuery", "Error on query", e)
        }
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        Log.d("Library search", newText)
        catalogFragment.isQuery = true
        val recycleCards = ArrayList(catalogFragment.recyclerArray)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recycleCards.removeIf { recycleListingCard: NovelListingCard? -> !recycleListingCard!!.title.toLowerCase(Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT)) }
        } else {
            for (x in recycleCards.indices.reversed()) {
                if (!recycleCards[x].title.toLowerCase(Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT))) {
                    recycleCards.removeAt(x)
                }
            }
        }
        catalogFragment.setLibraryCards(recycleCards)
        return recycleCards.size != 0
    }

}