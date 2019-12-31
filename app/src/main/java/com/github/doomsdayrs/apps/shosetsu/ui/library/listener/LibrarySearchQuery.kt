package com.github.doomsdayrs.apps.shosetsu.ui.library.listener

import android.os.Build
import android.util.Log
import android.widget.SearchView
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseNovels
import com.github.doomsdayrs.apps.shosetsu.ui.library.LibraryFragment
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
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
class LibrarySearchQuery(private val libraryFragment: LibraryFragment) : SearchView.OnQueryTextListener {
    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        Log.d("Library search", newText)
        val novelIDs = ArrayList(libraryFragment.libraryNovelCards)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            novelIDs.removeIf { novelID: Int? -> !DatabaseNovels.getNovelTitle(novelID!!).toLowerCase(Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT)) }
        } else {
            for (x in novelIDs.indices.reversed()) if (!DatabaseNovels.getNovelTitle(novelIDs[x]).toLowerCase(Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT))) novelIDs.removeAt(x)
        }
        libraryFragment.setLibraryCards(novelIDs)
        return novelIDs.size != 0
    }

}