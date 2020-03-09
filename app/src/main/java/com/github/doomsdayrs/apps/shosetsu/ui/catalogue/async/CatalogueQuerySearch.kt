package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async

import android.os.AsyncTask
import com.github.doomsdayrs.api.shosetsu.services.core.ShosetsuLib
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogueController
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
class CatalogueQuerySearch(private val catalogueFragment: CatalogueController) : AsyncTask<String?, Void?, ArrayList<NovelListingCard>>() {
    /**
     * Search catalogue
     *
     * @param strings ignored
     * @return List of results
     */
    override fun doInBackground(vararg strings: String?): ArrayList<NovelListingCard> {
        val result = ArrayList<NovelListingCard>()
        val map = mapOf(Pair(ShosetsuLib.FILTER_ID_QUERY, strings[0]))
        val novels = catalogueFragment.formatter.search(map) {}
        try {
            for ((title, link, imageURL) in novels) result.add(NovelListingCard(imageURL, title, Database.DatabaseIdentification.getNovelIDFromNovelURL(link), link))
        } catch (e: MissingResourceException) {
            TODO("Add error handling here")
        }
        return result
    }

}