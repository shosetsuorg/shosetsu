package com.github.doomsdayrs.apps.shosetsu.ui.reader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.common.consts.Broadcasts.BC_CHAPTER_VIEW_THEME_CHANGE
import com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.ui.errorView.ErrorAlert
import com.github.doomsdayrs.apps.shosetsu.ui.reader.async.ChapterViewLoader
import com.github.doomsdayrs.apps.shosetsu.ui.reader.listeners.ToolbarHideOnClickListener
import kotlinx.android.synthetic.main.chapter_view.*

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
 */
/**
 * shosetsu
 * 13 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
class ChapterView : Fragment() {


	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.chapter_view, container, false)
		if (savedInstanceState != null) {
			chapterID = savedInstanceState.getInt("id")
			url = savedInstanceState.getString("url", "")
			chapterReader = activity as ChapterReader?
			unformattedText = savedInstanceState.getString("unform", "")
			text = savedInstanceState.getString("text")
			bookmarked = savedInstanceState.getBoolean("book")
			chapterLoaded = savedInstanceState.getBoolean("ready")
			Log.i("ChapterView", "Restored:${appendID()}")
		}
		Log.i("ChapterView", "Created:${appendID()}")
		return view
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		addBottomListener()
		chapterReader?.getToolbar()?.let { textView.setOnClickListener(ToolbarHideOnClickListener(it)) }

		textView.setBackgroundColor(getBackgroundColor())
		textView.setTextColor(getTextColor())

		textView.textSize = Settings.readerTextSize
		next_chapter.setOnClickListener {
			val next = chapterReader!!.getNextPosition(chapterID)
			if (chapterReader!!.chapterIDs.isNotEmpty() && chapterReader!!.getViewPager() != null) {
				if (next in chapterReader!!.chapterIDs.indices) {
					next_chapter.visibility = View.GONE
					chapterReader!!.getViewPager()?.currentItem = next
				} else chapterReader?.toast("No more chapters!")
			}
		}
		//holder.viewPager2.setUserInputEnabled(false);
		//NewChapterReaderTypeAdapter newChapterReaderTypeAdapter = new NewChapterReaderTypeAdapter(newChapterReader);
		//holder.viewPager2.setAdapter(newChapterReaderTypeAdapter);
		//holder.viewPager2.setCurrentItem(getReaderType(newChapterReader.novelID));

		chapterLoaded = false

		if (savedInstanceState == null) {
			dataSet()
		} else {
			Log.d("ChapterView", "Load, Data present${appendID()}")
			setUpReader()
		}

		val intentFilter = IntentFilter()
		intentFilter.addAction(BC_CHAPTER_VIEW_THEME_CHANGE)
		reciever = object : BroadcastReceiver() {
			override fun onReceive(context: Context?, intent: Intent?) {
				intent?.let {
					when (it.action) {
						BC_CHAPTER_VIEW_THEME_CHANGE -> {
							textView?.setBackgroundColor(getBackgroundColor())
							textView?.setTextColor(getTextColor())
						}
						else -> {
							Log.d(logID(), "Unknown action")
						}
					}
				}
			}
		}
		activity?.registerReceiver(reciever, intentFilter)
	}

	private fun dataSet() {
		if (Database.DatabaseChapter.isSaved(chapterID)) {
			Log.d("ChapterView", "Loading from storage${appendID()}")
			val r = Database.DatabaseChapter.getSavedNovelPassage(chapterID)
			if (r.succeeded) {
				unformattedText = r.value!!
				setUpReader()
				scrollView.post { scrollView.scrollTo(0, Database.DatabaseChapter.getY(chapterID)) }
				chapterLoaded = true
			} else {
				ErrorAlert(activity!!.parent)
						.setMessage(r.e?.message)
						.runOnUI()
			}
		} else {
			Log.d("ChapterView", "Loading from online${appendID()}")
			unformattedText = ""
			setUpReader()
			ChapterViewLoader(this).execute()
		}
	}

	fun setUpReader() {
		//scrollView!!.setBackgroundColor(Settings.ReaderTextBackgroundColor)
		textView!!.setBackgroundColor(getBackgroundColor())
		textView!!.setTextColor(getTextColor())


		textView!!.textSize = Settings.readerTextSize
		if (unformattedText.isNotEmpty()) {
			val replaceSpacing = StringBuilder("\n")
			for (x in 0 until Settings.readerParagraphSpacing) replaceSpacing.append("\n")
			for (x in 0 until Settings.ReaderIndentSize) replaceSpacing.append("\t")
			text = unformattedText.replace("\n".toRegex(), replaceSpacing.toString())

			if (text!!.length > 100)
				Log.d("ChapterView", "TextSet\t" +
						text!!.substring(0, 100).replace("\n", "\\n") +
						"\n" + appendID())
			else if (text!!.isNotEmpty())
				Log.d("ChapterView", "TextSet\t" +
						text!!.substring(0, text!!.length - 1).replace("\n", "\\n")
						+ "\n" + appendID())

			textView!!.text = text
			// viewPager2.post(() -> currentReader.setText(text));
		}
	}

	private var marked: Boolean = false

}