package com.github.doomsdayrs.apps.shosetsu.ui.reader.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.reader.ChapterView
import com.github.doomsdayrs.apps.shosetsu.ui.reader.viewHolders.NewMarkdownReader
import com.github.doomsdayrs.apps.shosetsu.ui.reader.viewHolders.NewReader
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
 */ /**
 * shosetsu
 * 13 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
@Suppress("unused")
internal class ChapterViewTypeAdapter(private val chapterReader: ChapterView) : RecyclerView.Adapter<NewReader>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewReader {
        val view: View
        val newReader: NewReader
        when (viewType) {
            0 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.chapter_reader_text_view, parent, false)
                newReader = NewTextReader(view, chapterReader)
            }
            1 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.chapter_reader_mark_down, parent, false)
	            newReader = NewMarkdownReader(view)
            }
            else -> throw IllegalStateException("Unexpected value: $viewType")
        }
        return newReader
    }

    override fun onBindViewHolder(holder: NewReader, position: Int) {
        Log.i("LoadingReader", position.toString())
        //newChapterReader.currentView.currentReader = holder;
        holder.bind()
        // newChapterReader.currentView.setUpReader();
    }

    override fun getItemCount(): Int {
        return 2
    }

}