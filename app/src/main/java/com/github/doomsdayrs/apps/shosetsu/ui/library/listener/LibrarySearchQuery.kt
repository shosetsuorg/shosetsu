package com.github.doomsdayrs.apps.shosetsu.ui.library.listener

import android.os.Build
import android.util.Log
import android.widget.SearchView
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.library.LibraryController
import java.util.*

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
 * 23 / 02 / 2020
 *
 * @author github.com/doomsdayrs
 */
class LibrarySearchQuery(private val libraryController: LibraryController)  : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        Log.d("Library search", newText)
        val novelIDs = ArrayList(libraryController.libraryNovelCards)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            novelIDs.removeIf { novelID: Int? -> !Database.DatabaseNovels.getNovelTitle(novelID!!).toLowerCase(Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT)) }
        } else {
            for (x in novelIDs.indices.reversed()) if (!Database.DatabaseNovels.getNovelTitle(novelIDs[x]).toLowerCase(Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT))) novelIDs.removeAt(x)
        }
        libraryController.setLibraryCards(novelIDs)
        return novelIDs.size != 0
    }
}