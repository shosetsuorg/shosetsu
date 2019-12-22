package com.github.doomsdayrs.apps.shosetsu.ui.reader

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.github.doomsdayrs.api.shosetsu.services.core.dep.Formatter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.reader.adapters.NewChapterReaderAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions.IndentChange
import com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions.ParaSpacingChange
import com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions.ReaderChange
import com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions.TextSizeChange
import com.github.doomsdayrs.apps.shosetsu.ui.reader.viewHolders.NewChapterView
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers
import com.github.doomsdayrs.apps.shosetsu.variables.Settings
import kotlinx.android.synthetic.main.new_chapter_reader.*

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
class NewChapterReader : AppCompatActivity() {
    private val demarkActions = arrayOf(TextSizeChange(this), ParaSpacingChange(this), IndentChange(this), ReaderChange(this))
    // Order of values. Small,Medium,Large
    private val textSizes = arrayOfNulls<MenuItem>(3)
    // Order of values. Non,Small,Medium,Large
    private val paragraphSpaces = arrayOfNulls<MenuItem>(4)
    // Order of values. Non,Small,Medium,Large
    private val indentSpaces = arrayOfNulls<MenuItem>(4)
    // Order of values. Default, Markdown
    private val readers = arrayOfNulls<MenuItem>(2)
    var bookmark: MenuItem? = null
    private var tapToScroll: MenuItem? = null
    @JvmField
    var toolbar: Toolbar? = null
    // NovelData
    @JvmField
    var chapterIDs: IntArray? = null
    @JvmField
    var formatter: Formatter? = null
    @JvmField
    var novelID = 0
    @JvmField
    var currentView: NewChapterView? = null
    private var currentChapterID = -1
    /**
     * Creates the option menu (on the top toolbar)
     *
     * @param menu Menu reference to fill
     * @return if made
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_chapter_view, menu)
        // Night mode
        menu.findItem(R.id.chapter_view_nightMode).isChecked = Utilities.isReaderNightMode()
        //  Bookmark
        run {
            bookmark = menu.findItem(R.id.chapter_view_bookmark)
            currentView!!.bookmarked = Database.DatabaseChapter.isBookMarked(currentView!!.chapterID)
            updateBookmark()
        }
        // Tap To Scroll
        run {
            tapToScroll = menu.findItem(R.id.tap_to_scroll)
            tapToScroll?.setChecked(Utilities.isTapToScroll())
        }
        // Text size
        run {
            textSizes[0] = menu.findItem(R.id.chapter_view_textSize_small)
            textSizes[1] = menu.findItem(R.id.chapter_view_textSize_medium)
            textSizes[2] = menu.findItem(R.id.chapter_view_textSize_large)
            when (Settings.ReaderTextSize.toInt()) {
                14 -> textSizes[0]?.setChecked(true)
                17 -> textSizes[1]?.setChecked(true)
                20 -> textSizes[2]?.setChecked(true)
                else -> {
                    Utilities.setTextSize(14)
                    textSizes[0]?.setChecked(true)
                }
            }
        }
        // Paragraph Space
        run {
            paragraphSpaces[0] = menu.findItem(R.id.chapter_view_paragraphSpace_none)
            paragraphSpaces[1] = menu.findItem(R.id.chapter_view_paragraphSpace_small)
            paragraphSpaces[2] = menu.findItem(R.id.chapter_view_paragraphSpace_medium)
            paragraphSpaces[3] = menu.findItem(R.id.chapter_view_paragraphSpace_large)
            paragraphSpaces[Settings.paragraphSpacing]?.setChecked(true)
        }
        // Indent Space
        run {
            indentSpaces[0] = menu.findItem(R.id.chapter_view_indent_none)
            indentSpaces[1] = menu.findItem(R.id.chapter_view_indent_small)
            indentSpaces[2] = menu.findItem(R.id.chapter_view_indent_medium)
            indentSpaces[3] = menu.findItem(R.id.chapter_view_indent_large)
            indentSpaces[Settings.indentSize]?.setChecked(true)
        }
        /* Reader
        {

            readers[0] = menu.findItem(R.id.reader_0);
            readers[1] = menu.findItem(R.id.reader_1);
            readerType = getReaderType(novelID);

            switch (readerType) {
                case 1:
                    demarkMenuItems(readers, 1, null);
                    break;
                case 0:
                case -1:
                    demarkMenuItems(readers, 0, null);
                    break;
                case -2:
                default:
                    throw new RuntimeException("Invalid chapter?!? How are you reading this without the novel loaded in");
            }
        }*/return true
    }

    fun updateBookmark() {
        if (bookmark != null) if (currentView!!.bookmarked) bookmark!!.setIcon(R.drawable.ic_bookmark_black_24dp) else bookmark!!.setIcon(R.drawable.ic_bookmark_border_black_24dp)
    }

    fun getViewPager(): ViewPager? {
        return viewpager
    }

    /**
     * What to do when an menu item is selected
     *
     * @param item item selected
     * @return true if processed
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("item", item.toString())
        return when (item.itemId) {
            R.id.chapter_view_nightMode -> {
                if (!item.isChecked) {
                    Utilities.swapReaderColor()
                    currentView!!.setUpReader()
                } else {
                    Utilities.swapReaderColor()
                    currentView!!.setUpReader()
                }
                item.isChecked = !item.isChecked
                true
            }
            R.id.tap_to_scroll -> {
                tapToScroll!!.isChecked = Utilities.toggleTapToScroll()
                true
            }
            R.id.chapter_view_bookmark -> {
                currentView!!.bookmarked = Utilities.toggleBookmarkChapter(currentView!!.chapterID)
                updateBookmark()
                true
            }
            R.id.chapter_view_textSize_small -> {
                Utilities.unmarkMenuItems(indentSpaces, 0, demarkActions[0])
                true
            }
            R.id.chapter_view_textSize_medium -> {
                Utilities.unmarkMenuItems(textSizes, 1, demarkActions[0])
                true
            }
            R.id.chapter_view_textSize_large -> {
                Utilities.unmarkMenuItems(textSizes, 2, demarkActions[0])
                true
            }
            R.id.chapter_view_paragraphSpace_none -> {
                Utilities.unmarkMenuItems(paragraphSpaces, 0, demarkActions[1])
                true
            }
            R.id.chapter_view_paragraphSpace_small -> {
                Utilities.unmarkMenuItems(paragraphSpaces, 1, demarkActions[1])
                true
            }
            R.id.chapter_view_paragraphSpace_medium -> {
                Utilities.unmarkMenuItems(paragraphSpaces, 2, demarkActions[1])
                true
            }
            R.id.chapter_view_paragraphSpace_large -> {
                Utilities.unmarkMenuItems(paragraphSpaces, 3, demarkActions[1])
                true
            }
            R.id.chapter_view_indent_none -> {
                Utilities.unmarkMenuItems(indentSpaces, 0, demarkActions[2])
                true
            }
            R.id.chapter_view_indent_small -> {
                Utilities.unmarkMenuItems(indentSpaces, 1, demarkActions[2])
                true
            }
            R.id.chapter_view_indent_medium -> {
                Utilities.unmarkMenuItems(indentSpaces, 2, demarkActions[2])
                true
            }
            R.id.chapter_view_indent_large -> {
                Utilities.unmarkMenuItems(indentSpaces, 3, demarkActions[2])
                true
            }
            R.id.browser -> {
                currentView!!.url?.let { Utilities.openInBrowser(this, it) }
                true
            }
            R.id.webview -> {
                currentView!!.url?.let { Utilities.openInWebview(this, it) }
                true
            }
            R.id.reader_0 -> {
                Utilities.unmarkMenuItems(readers, 0, demarkActions[3])
                true
            }
            R.id.reader_1 -> {
                Utilities.unmarkMenuItems(readers, 1, demarkActions[3])
                true
            }
            else -> false
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outPersistentState.putIntArray("chapters", chapterIDs)
        outPersistentState.putInt("novelID", novelID)
        outPersistentState.putInt("formatter", formatter!!.formatterID)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_chapter_reader)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (savedInstanceState != null) {
            formatter = DefaultScrapers.getByID(savedInstanceState.getInt("formatter"))
            novelID = savedInstanceState.getInt("novelID")
            chapterIDs = savedInstanceState.getIntArray("chapters")
        } else {
            chapterIDs = intent.getIntArrayExtra("chapters")
            run {
                val chapterID: Int = intent.getIntExtra("chapterID", -1)
                currentChapterID = chapterID
            }
            novelID = intent.getIntExtra("novelID", -1)
            formatter = DefaultScrapers.getByID(intent.getIntExtra("formatter", -1))
        }
        if (chapterIDs == null) {
            val integers = Database.DatabaseChapter.getChaptersOnlyIDs(novelID)
            chapterIDs = IntArray(integers.size)
            for (x in integers.indices) chapterIDs!![x] = integers[x]
        }
        val newChapterReaderAdapter = NewChapterReaderAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, this)
        viewpager.adapter = newChapterReaderAdapter
        if (currentChapterID != -1) viewpager.currentItem = findCurrentPosition(currentChapterID)
    }

    fun findCurrentPosition(id: Int): Int {
        for (x in chapterIDs!!.indices) if (chapterIDs!![x] == id) return x
        return -1
    }
}