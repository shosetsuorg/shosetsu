package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async

import android.os.AsyncTask
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogController
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
class CatalogueQuerySearch(private val catalogFragment: CatalogController) : AsyncTask<String?, Void?, ArrayList<NovelListingCard>>() {
    /**
     * Search catalogue
     *
     * @param strings ignored
     * @return List of results
     */
    override fun doInBackground(vararg strings: String?): ArrayList<NovelListingCard> {
        val result = ArrayList<NovelListingCard>()
        val novels = catalogFragment.formatter.search(
                (listOf(strings[0])+catalogFragment.filterValues).toTypedArray()
        ) {}.forEach {
            result.add(NovelListingCard(it.imageURL, it.title, Database.DatabaseIdentification.getNovelIDFromNovelURL(it.link), it.link))
        }
        return result
    }

}