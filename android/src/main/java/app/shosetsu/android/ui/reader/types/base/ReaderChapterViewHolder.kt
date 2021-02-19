package app.shosetsu.android.ui.reader.types.base

import android.view.View
import app.shosetsu.android.ui.reader.ChapterReader
import app.shosetsu.android.view.uimodels.model.reader.ReaderChapterUI
import com.mikepenz.fastadapter.FastAdapter

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
 */

/**
 * shosetsu
 * 13 / 12 / 2019
 */
abstract class ReaderChapterViewHolder(
	itemView: View
) : FastAdapter.ViewHolder<ReaderChapterUI>(itemView) {
	lateinit var chapter: ReaderChapterUI
	lateinit var chapterReader: ChapterReader

	val viewModel
		get() = chapterReader.viewModel

	/**
	 * Passes in data
	 */
	abstract fun setData(data: String)

	abstract fun syncTextColor()
	abstract fun syncBackgroundColor()

	abstract fun syncTextSize()
	abstract fun syncTextPadding()

	abstract fun syncParagraphSpacing()
	abstract fun syncParagraphIndent()

	abstract fun setProgress(progress: Int)

	/**
	 * When the user double clicks the window
	 */
	abstract fun getFocusTarget(): View?

	abstract fun hideLoadingProgress()
	abstract fun showLoadingProgress()

	abstract fun incrementScroll()

	abstract fun depleteScroll()

	// abstract fun setTextFont()
}