package app.shosetsu.android.ui.catalogue.listeners

import android.widget.SearchView
import app.shosetsu.android.ui.catalogue.CatalogController

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
class CatalogueSearchQuery(private val catalogFragment: CatalogController) :
	SearchView.OnQueryTextListener {
	override fun onQueryTextSubmit(query: String): Boolean {
		catalogFragment.viewModel.applyQuery(query)
		return true
	}

	override fun onQueryTextChange(newText: String): Boolean {
		catalogFragment.viewModel.applyQuery(newText)
		return true
	}

}