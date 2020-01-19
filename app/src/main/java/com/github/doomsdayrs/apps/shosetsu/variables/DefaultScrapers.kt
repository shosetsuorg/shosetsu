package com.github.doomsdayrs.apps.shosetsu.variables

import com.github.doomsdayrs.api.shosetsu.services.core.dep.Formatter
import com.github.doomsdayrs.api.shosetsu.services.core.dep.ScrapeFormat
import com.github.doomsdayrs.api.shosetsu.services.core.objects.Novel
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelGenre
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelPage
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueCard
import org.jsoup.nodes.Document

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
 * 30 / May / 2019
 *
 * @author github.com/doomsdayrs
 */
/**
 * Moved from deprecated novelreader-core
 * Contains functional novels and their IDs
 */
// TODO Make this full dynamic, not needing to be predefined
// > Make IDs built into the formatter
object DefaultScrapers {
    class UnknownFormatter : ScrapeFormat() {
        override val genres: Array<NovelGenre> = arrayOf()
        override val imageURL: String = ""
        override val name: String = "UNKNOWN"

        override fun getLatestURL(page: Int): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getNovelPassage(document: Document): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getSearchString(query: String): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun novelPageCombiner(url: String, increment: Int): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun parseLatest(document: Document): List<Novel> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun parseNovel(document: Document): NovelPage {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun parseNovel(document: Document, increment: Int): NovelPage {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun parseSearch(document: Document): List<Novel> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    val unknown = UnknownFormatter()

    val formatters = ArrayList<Formatter>()

    @JvmStatic
    fun getByID(ID: Int): Formatter {
        for (formatter in formatters) {
            if (formatter.formatterID == ID) return formatter
        }
        return unknown
    }

    val asCatalogue: ArrayList<CatalogueCard>
        get() {
            val catalogueCards = ArrayList<CatalogueCard>()
            for (formatter in formatters) catalogueCards.add(CatalogueCard(formatter))
            catalogueCards.sortedWith(compareBy { it.title })
            return catalogueCards
        }
}