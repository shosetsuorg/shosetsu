package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.listeners

import android.os.Build
import android.util.Log
import android.widget.SearchView
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogController
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
 */

/**
 * Shosetsu
 * 18 / 06 / 2019
 */
class CatalogueSearchQuery(private val catalogFragment: CatalogController)
	: SearchView.OnQueryTextListener {
	override fun onQueryTextSubmit(query: String): Boolean {
		catalogFragment.isQuery = false
		catalogFragment.isInSearch = true
		catalogFragment.viewModel.loadQuery(query)
		return true
	}

	override fun onQueryTextChange(newText: String): Boolean {
		Log.d("Library search", newText)
		catalogFragment.isQuery = true
		val recycleCards = ArrayList(catalogFragment.recyclerArray)
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			recycleCards.removeIf { !it.title.contains(newText, true) }
		} else {
			for (x in recycleCards.indices.reversed()) {
				if (!recycleCards[x].title.contains(newText, true)) {
					recycleCards.removeAt(x)
				}
			}
		}
		return recycleCards.size != 0
	}

}