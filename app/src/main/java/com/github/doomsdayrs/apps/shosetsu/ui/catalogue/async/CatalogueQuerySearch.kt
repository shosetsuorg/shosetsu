package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async

import android.os.AsyncTask
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.scraper.WebViewScrapper.docFromURL
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogueFragment
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueNovelCard
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
class CatalogueQuerySearch(private val catalogueFragment: CatalogueFragment) : AsyncTask<String?, Void?, ArrayList<CatalogueNovelCard>>() {
    /**
     * Search catalogue
     *
     * @param strings ignored
     * @return List of results
     */
    override fun doInBackground(vararg strings: String?): ArrayList<CatalogueNovelCard> {
        val result = ArrayList<CatalogueNovelCard>()
        val novels = catalogueFragment.formatter.parseSearch(docFromURL(catalogueFragment.formatter.getSearchString(strings[0]!!), catalogueFragment.formatter.hasCloudFlare)!!)
        for (novel in novels) result.add(CatalogueNovelCard(novel.imageURL, novel.title, Database.DatabaseIdentification.getNovelIDFromNovelURL(novel.link), novel.link))
        return result
    }

}