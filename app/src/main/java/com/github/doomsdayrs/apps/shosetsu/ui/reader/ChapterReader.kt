package com.github.doomsdayrs.apps.shosetsu.ui.reader

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.github.doomsdayrs.api.shosetsu.services.core.dep.Formatter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.reader.adapters.ChapterReaderAdapter
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers
import com.github.doomsdayrs.apps.shosetsu.variables.Settings
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status
import kotlinx.android.synthetic.main.chapter_reader.*

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
class ChapterReader : AppCompatActivity() {
    // NovelData
    var chapterIDs: ArrayList<Int> = arrayListOf()
    var formatter: Formatter? = null
    var novelID = 0

    private var currentChapterID = -1

    fun getViewPager(): ViewPager? {
        return viewpager
    }

    fun getToolbar(): Toolbar? {
        return toolbar as Toolbar
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outPersistentState.putIntArray("chapters", chapterIDs.toIntArray())
        outPersistentState.putInt("novelID", novelID)
        outPersistentState.putInt("formatter", formatter!!.formatterID)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chapter_reader)
        getToolbar()?.let { setSupportActionBar(it) }

        if (savedInstanceState != null) {
            // Sets default values
            formatter = DefaultScrapers.getByID(savedInstanceState.getInt("formatter"))
            novelID = savedInstanceState.getInt("novelID")
            val temp = savedInstanceState.getIntArray("chapters")
            for (x in temp!!.indices) chapterIDs.add(temp[x])
        } else {
            intent.getIntegerArrayListExtra("chapters")?.let {
                chapterIDs = it
            }
            run {
                val chapterID: Int = intent.getIntExtra("chapterID", -1)
                currentChapterID = chapterID
            }
            novelID = intent.getIntExtra("novelID", -1)
            formatter = DefaultScrapers.getByID(intent.getIntExtra("formatter", -1))
        }

        if (chapterIDs.isEmpty()) {
            val integers = Database.DatabaseChapter.getChaptersOnlyIDs(novelID)
            for (x in integers.indices) chapterIDs.add(integers[x])
        }
        val newChapterReaderAdapter = ChapterReaderAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, this)
        viewpager.adapter = newChapterReaderAdapter
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (Settings.ReaderMarkingType == Settings.MarkingTypes.ONVIEW.i) {
                    Log.d("ChapterReader","Marking as Reading")
                    Database.DatabaseChapter.setChapterStatus(newChapterReaderAdapter.chapterViews[position].chapterID, Status.READING)
                }
            }
        })
        if (currentChapterID != -1) viewpager.currentItem = findCurrentPosition(currentChapterID)
    }

    fun findCurrentPosition(id: Int): Int {
        for (x in chapterIDs.indices) if (chapterIDs[x] == id) return x
        return -1
    }
}