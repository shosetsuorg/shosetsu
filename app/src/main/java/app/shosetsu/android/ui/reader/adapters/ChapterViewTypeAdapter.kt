package app.shosetsu.android.ui.reader.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.ui.reader.ChapterReader
import app.shosetsu.android.ui.reader.types.model.MarkdownReader
import app.shosetsu.android.ui.reader.types.model.StringReader
import app.shosetsu.android.ui.reader.types.base.ReaderType
import com.github.doomsdayrs.apps.shosetsu.R

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
internal class ChapterViewTypeAdapter(private val chapterReader: ChapterReader) : RecyclerView.Adapter<ReaderType<*>>() {
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReaderType<*> {
		val view: View
		val newReader: ReaderType<*>
		app.shosetsu.android.common.ext.launchUI {

		}
		when (viewType) {
			0 -> {
				view = LayoutInflater.from(parent.context).inflate(R.layout.chapter_reader_text_view, parent, false)
				newReader = StringReader(view)
			}
			1 -> {
				view = LayoutInflater.from(parent.context).inflate(R.layout.chapter_reader_mark_down, parent, false)
				newReader = MarkdownReader(view)
			}
			else -> throw IllegalStateException("Unexpected value: $viewType")
		}
		return newReader
	}

	override fun onBindViewHolder(holder: ReaderType<*>, position: Int) {
		Log.i("LoadingReader", position.toString())
		//newChapterReader.currentView.currentReader = holder;
		// newChapterReader.currentView.setUpReader();
	}

	override fun getItemCount(): Int {
		return 2
	}

}