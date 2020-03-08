package com.github.doomsdayrs.apps.shosetsu.ui.reader

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.github.doomsdayrs.api.shosetsu.services.core.Formatter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.reader.adapters.ChapterReaderAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.reader.listeners.ChapterViewChange
import com.github.doomsdayrs.apps.shosetsu.variables.obj.DefaultScrapers
import kotlinx.android.synthetic.main.chapter_reader.*
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
class ChapterReader : AppCompatActivity(R.layout.chapter_reader) {
    // NovelData
    private var _chapterIDs: MutableList<Int> = arrayListOf()
    var chapterIDs: MutableList<Int>
        get() = if (Utilities.isInvertedSwipe) _chapterIDs.reversed().toMutableList() else _chapterIDs
        set(value) { _chapterIDs = value; Log.v("ChapterReader", "set chapters - ${value.size}") }


    var formatter: Formatter? = null
    var novelID = 0

    private lateinit var chapterReaderAdapter: ChapterReaderAdapter

    private var currentChapterID = -1

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntArray("chapters", chapterIDs.toIntArray())
        outState.putInt("novelID", novelID)
        outState.putInt("formatter", formatter!!.formatterID)
        outState.putParcelable("adapter", chapterReaderAdapter.saveState())
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getToolbar()?.let { setSupportActionBar(it) }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (savedInstanceState != null) {
            // Sets default values
            formatter = DefaultScrapers.getByID(savedInstanceState.getInt("formatter"))
            novelID = savedInstanceState.getInt("novelID")

            val temp = savedInstanceState.getIntArray("chapters")
            for (x in temp!!.indices) chapterIDs.add(temp[x])
        } else {
            chapterIDs = intent.getIntegerArrayListExtra("chapters")!!
            run {
                val chapterID: Int = intent.getIntExtra("chapterID", -1)
                currentChapterID = chapterID
            }
            novelID = intent.getIntExtra("novelID", -1)
            formatter = DefaultScrapers.getByID(intent.getIntExtra("formatter", -1))
        }

        if (chapterIDs.isEmpty()) {
            try {
                val integers = Database.DatabaseChapter.getChaptersOnlyIDs(novelID)
                for (x in integers.indices) chapterIDs.add(integers[x])
            } catch (e: MissingResourceException) {
                TODO("Add error handling here")
            }
        }

        setupViewPager(savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return false
    }

    private fun setupViewPager(savedInstanceState: Bundle?) {
        chapterReaderAdapter = ChapterReaderAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, this)

        if (savedInstanceState != null) {
            chapterReaderAdapter.restoreState(savedInstanceState.getParcelable("adapter"), classLoader)
        }

        viewpager.adapter = chapterReaderAdapter
        viewpager.addOnPageChangeListener(ChapterViewChange(chapterReaderAdapter))
        if (currentChapterID != -1)
            viewpager.currentItem = chapterIDs.indexOf(currentChapterID)
        else if (Utilities.isInvertedSwipe)
            viewpager.currentItem = chapterIDs.lastIndex

        Log.v("ChapterReader", "#ids ${_chapterIDs.size} - ${chapterIDs.size}")
        Log.v("ChapterReader", "currentItem ${viewpager.currentItem}")
    }

    fun getNextPosition(id: Int) = chapterIDs.indexOf(id) + if (Utilities.isInvertedSwipe) 1 else -1

    fun getViewPager(): ViewPager? = viewpager

    fun getToolbar(): Toolbar? = toolbar as Toolbar
}