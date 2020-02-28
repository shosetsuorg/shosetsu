package com.github.doomsdayrs.apps.shosetsu.ui.reader.adapters

import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.support.RouterPagerAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader
import com.github.doomsdayrs.apps.shosetsu.ui.reader.fragments.ChapterView
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
 */ /**
 * shosetsu
 * 13 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
class ChapterReaderAdapter(private val chapterReader: ChapterReader) : RouterPagerAdapter(chapterReader) {
    val chapterViews = ArrayList<ChapterView>()

    init {
        for (i in chapterReader.chapterIDs) {
            val newChapterView = ChapterView()
            newChapterView.chapterID = (i)
            newChapterView.chapterReader = chapterReader
            chapterViews.add(newChapterView)
        }
    }

    override fun configureRouter(router: Router, position: Int) {
        if (!router.hasRootController()) router.setRoot(RouterTransaction.with(chapterViews[position]))
    }

    override fun getCount(): Int = chapterReader.chapterIDs.size

}