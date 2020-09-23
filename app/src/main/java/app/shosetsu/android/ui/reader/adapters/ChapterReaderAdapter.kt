package app.shosetsu.android.ui.reader.adapters

import android.util.Log
import android.view.LayoutInflater.from
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.common.dto.handle
import app.shosetsu.android.common.ext.logD
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.ui.reader.ChapterReader
import app.shosetsu.android.ui.reader.types.base.ReaderType
import app.shosetsu.android.ui.reader.types.model.StringReader
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
 */
/**
 * shosetsu
 * 13 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 * @param chapterReader ChapterReader
 */
class ChapterReaderAdapter(
		private val chapterReader: ChapterReader,
) : RecyclerView.Adapter<ReaderType>() {
	var textReaders: ArrayList<ReaderType> = ArrayList()


	init {
		setHasStableIds(true)
	}

	private fun chapters() = chapterReader.chapters

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReaderType {
		Log.d(logID(), "Creating new view holder")
		return StringReader(
				itemView = from(parent.context).inflate(
						R.layout.chapter_reader_text_view,
						parent,
						false
				),
				defaultTextSize = chapterReader.viewModel.defaultTextSize,
				defaultParaSpacing = chapterReader.viewModel.defaultParaSpacing,
				defaultIndentSize = chapterReader.viewModel.defaultIndentSize,
				defaultForeground = chapterReader.viewModel.defaultForeground,
				defaultBackground = chapterReader.viewModel.defaultBackground
		).also { textReaders.add(it) }
	}

	override fun getItemCount(): Int = chapters().size

	override fun onBindViewHolder(holder: ReaderType, position: Int) {
		val chapter = chapters()[position]
		holder.attachData(chapter, chapterReader)

		chapterReader.viewModel.getChapterPassage(chapter).observe(chapterReader) { result ->
			result.handle(
					{ logD("Showing loading"); holder.showProgress() },
					{ logD("Empty result") },
					{
						logD("Showing error")
						//	holder.setError(it.message, "Retry") {
						//		TODO("Figure out how to restart the liveData")
						//		}
					}) {
				logD("Successfully loaded :D")
				holder.hideProgress()
				holder.setData(it)
				holder.itemView.post {
					holder.setProgress(chapter.readingPosition)
				}
			}
		}

		holder.setOnFocusListener {
			chapterReader.animateBottom()
			chapterReader.animateToolbar()
		}
	}

	override fun getItemId(position: Int): Long = chapterReader.chapters[position].id.toLong()
}