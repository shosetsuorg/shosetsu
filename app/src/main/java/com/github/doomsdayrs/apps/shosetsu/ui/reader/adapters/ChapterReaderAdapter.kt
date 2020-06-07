package com.github.doomsdayrs.apps.shosetsu.ui.reader.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterReader
import com.github.doomsdayrs.apps.shosetsu.ui.reader.viewHolders.NewTextReader

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

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewTextReader {
		Log.d(logID(), "Creating new view holder")
		return NewTextReader(LayoutInflater.from(parent.context).inflate(
				R.layout.chapter_reader_text_view,
				parent,
				false
		))
	}

	override fun getItemCount(): Int = chapterReader.chapters.size

	override fun onBindViewHolder(holder: NewTextReader, position: Int) {
		val chapter = chapterReader.chapters[position]
		Log.d(logID(), "Binding $position ${chapter.link}")
	}
}