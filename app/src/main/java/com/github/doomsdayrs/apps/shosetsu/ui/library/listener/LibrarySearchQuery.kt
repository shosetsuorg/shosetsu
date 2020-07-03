package com.github.doomsdayrs.apps.shosetsu.ui.library.listener

import android.util.Log
import android.widget.SearchView
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.ui.library.LibraryController

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
 */

/**
 * shosetsu
 * 23 / 02 / 2020
 *
 * @author github.com/doomsdayrs
 */
class LibrarySearchQuery(private val libraryController: LibraryController)
	: SearchView.OnQueryTextListener {
	override fun onQueryTextSubmit(query: String): Boolean {
		libraryController.itemAdapter.filter(query)
		return true
	}

	override fun onQueryTextChange(newText: String): Boolean {
		Log.d(logID(), "Query:\t[$newText]")
		libraryController.itemAdapter.filter(newText)
		return true
	}
}