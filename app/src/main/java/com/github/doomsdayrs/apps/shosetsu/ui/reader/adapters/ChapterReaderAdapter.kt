package com.github.doomsdayrs.apps.shosetsu.ui.reader.adapters

import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader
import com.github.doomsdayrs.apps.shosetsu.ui.reader.fragments.ChapterView
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status
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
class ChapterReaderAdapter(fm: FragmentManager, behavior: Int, private val chapterReader: ChapterReader) : FragmentStatePagerAdapter(fm, behavior) {
    val chapterViews = ArrayList<ChapterView>()

    init {
        for (i in chapterReader.chapterIDs) {
            val newChapterView = ChapterView()
            newChapterView.chapterID = (i)
            newChapterView.chapterReader = chapterReader
            chapterViews.add(newChapterView)
        }
    }

    override fun getItem(position: Int): Fragment {
        return chapterViews[position]
    }

    override fun getCount(): Int {
        Log.i("size", chapterReader.chapterIDs.size.toString())
        return chapterReader.chapterIDs.size
    }

}