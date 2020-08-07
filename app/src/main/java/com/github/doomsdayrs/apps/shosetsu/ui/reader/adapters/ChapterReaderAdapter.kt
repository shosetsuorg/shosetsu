package com.github.doomsdayrs.apps.shosetsu.ui.reader.adapters

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings.MarkingTypes
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.common.ext.observe
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader
import com.github.doomsdayrs.apps.shosetsu.ui.reader.viewHolders.NewTextReader
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ReaderChapterUI

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
 * @param chapterReader ChapterReader
 */
class ChapterReaderAdapter(
		private val chapterReader: ChapterReader
) : RecyclerView.Adapter<NewTextReader>() {
	var textReaders = ArrayList<NewTextReader>()

	init {
		setHasStableIds(true)
	}

	private fun chapters() = chapterReader.chapters

	override fun onViewDetachedFromWindow(holder: NewTextReader) {
		Log.d(logID(), "Detaching ${holder.chapterID}")
		super.onViewDetachedFromWindow(holder)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewTextReader {
		Log.d(logID(), "Creating new view holder")
		val r = NewTextReader(LayoutInflater.from(parent.context).inflate(
				R.layout.chapter_reader_text_view,
				parent,
				false
		))
		textReaders.add(r)
		return r
	}

	override fun getItemCount(): Int = chapters().size

	override fun onBindViewHolder(holder: NewTextReader, position: Int) {
		val chapter = chapters()[position]
		Log.d(logID(), "Binding $position ${chapter.link}")
		holder.chapterID = chapter.id
		chapterReader.viewModel.getChapterPassage(chapter).observe(chapterReader) {
			when (it) {
				is HResult.Loading -> {
					Log.d(logID(), "Showing loading")
					holder.showProgress()
				}
				is HResult.Empty -> {
					Log.d(logID(), "Empty result")
				}
				is HResult.Error -> {
					Log.d(logID(), "Showing error")
					holder.setError(it.message, "Retry") {
						TODO("Figure out how to restart the liveData")
					}
				}
				is HResult.Success -> {
					Log.d(logID(), "Successfully loaded :D")
					holder.hideProgress()
					holder.setText(it.data)
					holder.bind()
					holder.textView.post {
						Log.d(logID(),"Reading position ${chapter.readingPosition}")
						holder.scrollView.scrollTo(0, chapter.readingPosition)
					}
				}
			}
		}


		holder.textView.apply {
			textSize = chapterReader.settings.readerTextSize

			setOnClickListener {
				chapterReader.animateToolbar()
				chapterReader.animateBottom()
			}
		}

		// Sets the scroll listener
		holder.scrollView.apply {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				setOnScrollChangeListener { _: View?, _: Int, _: Int, _: Int, _: Int ->
					Log.d(logID(), "Scrolled")
					scrollHitBottom(holder, chapter)
				}
			} else {
				viewTreeObserver.addOnScrollChangedListener {
					Log.d(logID(), "Scrolled")
					scrollHitBottom(holder, chapter)
				}
			}
		}
	}

	override fun onViewAttachedToWindow(holder: NewTextReader) {
		super.onViewAttachedToWindow(holder)
		Log.i(logID(), "Attaching ${holder.chapterID}")
	}

	override fun onViewRecycled(holder: NewTextReader) {
		super.onViewRecycled(holder)
		Log.i(logID(), "Recycling ${holder.chapterID}")
	}

	override fun getItemId(position: Int): Long = chapterReader.chapters[position].id.toLong()

	/**
	 * What to do when scroll hits bottom
	 */
	private fun scrollHitBottom(reader: NewTextReader, cUI: ReaderChapterUI) {
		val view = reader.scrollView
		val total = view.getChildAt(0).height - view.height
		val yPosition = view.scrollY
		if (yPosition / total.toFloat() < .99) {
			if (yPosition % 5 == 0) {
				Log.i(logID(), "Scrolling")
				// Mark as reading if on scroll
				if (chapterReader.settings.readerMarkingType == MarkingTypes.ONSCROLL)
					cUI.readingStatus = ReadingStatus.READING
				chapterReader.viewModel.updateChapter(cUI, readingPosition = yPosition)
			}
		} else {
			Log.i(logID(), "Hit the bottom")
			// Hit bottom
			chapterReader.viewModel.updateChapter(cUI, readingStatus = ReadingStatus.READ)
		}
	}
}