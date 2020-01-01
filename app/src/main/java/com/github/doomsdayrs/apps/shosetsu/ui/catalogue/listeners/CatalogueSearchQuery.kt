package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners

import android.os.Build
import android.util.Log
import android.widget.SearchView
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogueFragment
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async.CatalogueQuerySearch
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueNovelCard
import java.util.*
import java.util.concurrent.ExecutionException

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
class CatalogueSearchQuery(private val catalogueFragment: CatalogueFragment) : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String): Boolean {
        catalogueFragment.isQuery = false
        catalogueFragment.isInSearch = true
        try {
            val searchResults = CatalogueQuerySearch(catalogueFragment).execute(query).get()
            catalogueFragment.setLibraryCards(searchResults)
            return true
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        Log.d("Library search", newText)
        catalogueFragment.isQuery = true
        val recycleCards = ArrayList(catalogueFragment.catalogueNovelCards)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recycleCards.removeIf { recycleCard: CatalogueNovelCard? -> !recycleCard!!.title.toLowerCase().contains(newText.toLowerCase()) }
        } else {
            for (x in recycleCards.indices.reversed()) {
                if (!recycleCards[x]!!.title.toLowerCase().contains(newText.toLowerCase())) {
                    recycleCards.removeAt(x)
                }
            }
        }
        catalogueFragment.setLibraryCards(recycleCards)
        return recycleCards.size != 0
    }

}