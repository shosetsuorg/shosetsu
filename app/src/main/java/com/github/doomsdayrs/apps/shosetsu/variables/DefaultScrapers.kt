package com.github.doomsdayrs.apps.shosetsu.variables

import com.github.doomsdayrs.api.shosetsu.extensions.lang.en.bestlightnovel.BestLightNovel
import com.github.doomsdayrs.api.shosetsu.extensions.lang.en.box_novel.BoxNovel
import com.github.doomsdayrs.api.shosetsu.extensions.lang.en.novel_full.NovelFull
import com.github.doomsdayrs.api.shosetsu.extensions.lang.en.syosetu.Syosetu
import com.github.doomsdayrs.api.shosetsu.services.core.dep.Formatter
import com.github.doomsdayrs.api.shosetsu.services.core.objects.Novel
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelGenre
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelPage
import com.github.doomsdayrs.api.shosetsu.services.core.objects.Ordering
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueCard
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.nodes.Document
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
enum class DefaultScrapers(private val formatter: Formatter) : Formatter {
    NOVELFULL(NovelFull()), BOXNOVEL(BoxNovel()), SYOSETU(Syosetu()),  //NOVELPLANENT(new NovelPlanet(4)),
    BESTLIGHTNOVEL(BestLightNovel());

    companion object {
        val formatters = ArrayList<Formatter>()
        fun getByID(ID: Int): Formatter? {
            for (formatter in formatters) {
                if (formatter.formatterID == ID) return formatter
            }
            return null
        }

        val asCatalogue: ArrayList<CatalogueCard>
            get() {
                val catalogueCards = ArrayList<CatalogueCard>()
                for (formatter in formatters) catalogueCards.add(CatalogueCard(formatter))
                return catalogueCards
            }

        init {
            formatters.add(NOVELFULL)
            formatters.add(BOXNOVEL)
            formatters.add(SYOSETU)
            // formatters.add(NOVELPLANENT);
            formatters.add(BESTLIGHTNOVEL)
        }
    }

    override val imageURL: String = formatter.imageURL

    override val isIncrementingChapterList: Boolean = formatter.isIncrementingChapterList

    override val isIncrementingPassagePage: Boolean = formatter.isIncrementingPassagePage

    override fun getNovelPassage(document: Document): String {
        return formatter.getNovelPassage(document)
    }

    override fun parseNovel(document: Document): NovelPage {
        return formatter.parseNovel(document)
    }

    override fun parseNovel(document: Document, increment: Int): NovelPage {
        return formatter.parseNovel(document, increment)
    }

    override fun novelPageCombiner(url: String, increment: Int): String {
        return formatter.novelPageCombiner(url, increment)
    }

    override fun getLatestURL(page: Int): String {
        return formatter.getLatestURL(page)
    }

    override fun parseLatest(document: Document): List<Novel> {
        return formatter.parseLatest(document)
    }

    override fun getSearchString(query: String): String {
        return formatter.getSearchString(query)
    }

    override fun parseSearch(document: Document): List<Novel> {
        return formatter.parseSearch(document)
    }

    override val genres: Array<NovelGenre>
        get() = formatter.genres

    override var builder: Request.Builder = formatter.builder


    override val chapterOrder: Ordering = formatter.chapterOrder

    override var client: OkHttpClient = formatter.client


    override val formatterID: Int = formatter.formatterID

    override val hasCloudFlare: Boolean = formatter.hasCloudFlare

    override val hasGenres: Boolean = formatter.hasGenres

    override val hasSearch: Boolean = formatter.hasSearch

    override val latestOrder: Ordering = formatter.latestOrder

}