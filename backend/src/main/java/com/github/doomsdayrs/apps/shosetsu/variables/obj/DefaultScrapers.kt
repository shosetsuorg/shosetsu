package com.github.doomsdayrs.apps.shosetsu.variables.obj

import com.github.doomsdayrs.api.shosetsu.services.core.Filter
import com.github.doomsdayrs.api.shosetsu.services.core.Formatter
import com.github.doomsdayrs.api.shosetsu.services.core.Novel
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.FormatterCard

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
 * 30 / May / 2019
 *
 * @author github.com/doomsdayrs
 */
object DefaultScrapers {
    val unknown = object : Formatter {
        override val baseURL: String
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        override val filters: Array<Filter<*>>
            get() = TODO("Not yet implemented")
        override val formatterID: Int = -1
        override val hasCloudFlare: Boolean
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        override val hasSearch: Boolean
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        override val imageURL: String
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        override val listings: Array<Formatter.Listing>
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        override val name: String
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        override val settings: Array<Filter<*>>
            get() = TODO("Not yet implemented")

        override fun getPassage(chapterURL: String): String {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun parseNovel(novelURL: String, loadChapters: Boolean, reporter: (status: String) -> Unit): Novel.Info {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun search(data: Array<*>, reporter: (status: String) -> Unit): Array<Novel.Listing> {
            TODO("Not yet implemented")
        }

        override fun updateSetting(id: Int, value: Any?) {
            TODO("Not yet implemented")
        }
    }

    val formatters = ArrayList<Formatter>()

    @JvmStatic
    fun getByID(ID: Int): Formatter = formatters.firstOrNull { it.formatterID == ID } ?: unknown

    val asFormatter: ArrayList<FormatterCard>
        get() {
            val catalogueCards = ArrayList<FormatterCard>()
            for (formatter in formatters) catalogueCards.add(FormatterCard(formatter))
            catalogueCards.sortedWith(compareBy { it.title })
            return catalogueCards
        }
}