package com.github.doomsdayrs.apps.shosetsu.ui.reader.viewHolders

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification
import com.github.doomsdayrs.apps.shosetsu.ui.reader.NewChapterReader
import com.github.doomsdayrs.apps.shosetsu.ui.reader.async.NewChapterReaderViewLoader
import com.github.doomsdayrs.apps.shosetsu.ui.reader.listeners.NovelFragmentChapterViewHideBar
import com.github.doomsdayrs.apps.shosetsu.variables.Settings
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status
import kotlinx.android.synthetic.main.new_chapter_view.*
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
class NewChapterView @SuppressLint("ClickableViewAccessibility") constructor() : Fragment() {
    @JvmField
    var newChapterReader: NewChapterReader? = null
    @JvmField
    var url: String? = null
    @JvmField
    var chapterID: Int = 0
    @JvmField
    var bookmarked = false
    //public View coverView;
// public ViewPager2 viewPager2;
//public NewReader currentReader;
    @JvmField
    var ready = false
    @JvmField
    var unformattedText: String? = null
    var text: String? = null

    fun setNewChapterReader(newChapterReader: NewChapterReader?) {
        this.newChapterReader = newChapterReader
    }

    fun setChapterID(CHAPTER_ID: Int) {
        this.chapterID = CHAPTER_ID
        url = DatabaseIdentification.getChapterURLFromChapterID(CHAPTER_ID)
    }

    private fun updateParent() {
        newChapterReader!!.currentView = this
        newChapterReader!!.updateBookmark()
    }

    override fun onResume() {
        super.onResume()
        updateParent()
        val title = Database.DatabaseChapter.getTitle(chapterID)
        Log.i("Setting TITLE", title)
        newChapterReader!!.toolbar!!.title = title
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("id", chapterID)
        outState.putString("url", url)
        outState.putString("text", text)
        outState.putString("unform", unformattedText)
        outState.putBoolean("book", bookmarked)
        outState.putBoolean("ready", ready)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.new_chapter_view, container, false)
        if (savedInstanceState != null) {
            chapterID = savedInstanceState.getInt("id")
            url = savedInstanceState.getString("url")
            newChapterReader = activity as NewChapterReader?
            unformattedText = savedInstanceState.getString("unfom")
            text = savedInstanceState.getString("text")
            bookmarked = savedInstanceState.getBoolean("book")
            ready = savedInstanceState.getBoolean("ready")
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addBottomListener()
        textView.setOnClickListener(NovelFragmentChapterViewHideBar(newChapterReader!!.toolbar))
        textView.setBackgroundColor(Settings.ReaderTextBackgroundColor)
        textView.setTextColor(Settings.ReaderTextColor)
        textView.textSize = Settings.ReaderTextSize
        next_chapter.setOnClickListener {
            val position = newChapterReader!!.findCurrentPosition(chapterID)
            if (newChapterReader!!.chapterIDs != null && newChapterReader!!.getViewPager() != null) {
                if (position + 1 < newChapterReader!!.chapterIDs!!.size) {
                    next_chapter.visibility = View.GONE
                    newChapterReader!!.getViewPager()?.currentItem = position + 1
                } else Toast.makeText(newChapterReader!!.applicationContext, "No more chapters!", Toast.LENGTH_SHORT).show()
            }
        }
        updateParent()
        //holder.viewPager2.setUserInputEnabled(false);
//NewChapterReaderTypeAdapter newChapterReaderTypeAdapter = new NewChapterReaderTypeAdapter(newChapterReader);
//holder.viewPager2.setAdapter(newChapterReaderTypeAdapter);
//holder.viewPager2.setCurrentItem(getReaderType(newChapterReader.novelID));
        Log.i("Loading chapter", url)
        ready = false
        if (savedInstanceState == null) {
            if (Database.DatabaseChapter.isSaved(chapterID)) {
                unformattedText = Objects.requireNonNull(Database.DatabaseChapter.getSavedNovelPassage(chapterID))
                setUpReader()
                scrollView.post { scrollView.scrollTo(0, Database.DatabaseChapter.getY(chapterID)) }
                ready = true
            } else {
                unformattedText = ""
                setUpReader()
                NewChapterReaderViewLoader(this).execute()
            }
        } else {
            setUpReader()
        }
        Database.DatabaseChapter.setChapterStatus(chapterID, Status.READING)
    }


    fun getScrollView(): ScrollView? {
        return scrollView
    }

    fun setUpReader() {
        scrollView?.setBackgroundColor(Settings.ReaderTextBackgroundColor)
        textView?.setBackgroundColor(Settings.ReaderTextBackgroundColor)
        textView?.setTextColor(Settings.ReaderTextColor)
        textView?.textSize = Settings.ReaderTextSize
        if (unformattedText != null) {
            val replaceSpacing = StringBuilder("\n")
            for (x in 0 until Settings.paragraphSpacing) replaceSpacing.append("\n")
            for (x in 0 until Settings.indentSize) replaceSpacing.append("\t")
            text = unformattedText!!.replace("\n".toRegex(), replaceSpacing.toString())
            if (text!!.length > 100) Log.d("TextSet", text!!.substring(0, 100).replace("\n", "\\n")) else if (text!!.isNotEmpty()) Log.d("TextSet", text!!.substring(0, text!!.length - 1).replace("\n", "\\n"))
            textView?.text = text
            // viewPager2.post(() -> currentReader.setText(text));
        }
    }

    /**
     * What to do when scroll hits bottom
     */
    private fun bottom() {
        val total = scrollView!!.getChildAt(0).height - scrollView!!.height
        if (ready) if (scrollView!!.scrollY / total.toFloat() < .99) {
            val y = scrollView!!.scrollY
            if (y % 5 == 0) { // Log.d("YMAX", String.valueOf(total));
// Log.d("YC", String.valueOf(y));
// Log.d("YD", String.valueOf((scrollView.getScrollY() / (float) total)));
//   Log.d("TY", String.valueOf(textView.getScrollY()));
                if (Database.DatabaseChapter.getStatus(chapterID) != Status.READ) Database.DatabaseChapter.updateY(chapterID, y)
            }
        } else {
            Log.i("Scroll", "Marking chapter as READ")
            Database.DatabaseChapter.setChapterStatus(chapterID, Status.READ)
            Database.DatabaseChapter.updateY(chapterID, 0)
            next_chapter!!.visibility = View.VISIBLE
            //TODO Get total word count of passage, then add to a storage counter that memorizes the total (Chapters read, Chapters Unread, Chapters reading, Word count)
        }
    }

    /**
     * Sets up the hitting bottom listener
     */
    private fun addBottomListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView!!.setOnScrollChangeListener { _: View?, _: Int, _: Int, _: Int, _: Int -> bottom() }
        } else {
            scrollView!!.viewTreeObserver.addOnScrollChangedListener { bottom() }
        }
    }
}