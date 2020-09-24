package app.shosetsu.android.ui.reader.types.base

import android.view.View
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.ui.reader.ChapterReader
import app.shosetsu.android.view.uimodels.model.ReaderChapterUI

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
abstract class ReaderType(
		itemView: View
) : RecyclerView.ViewHolder(itemView) {
	lateinit var chapter: ReaderChapterUI
	lateinit var chapterReader: ChapterReader

	fun attachData(chapterUI: ReaderChapterUI, chapterReader: ChapterReader) {
		this.chapter = chapterUI
		this.chapterReader = chapterReader
	}

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
	abstract fun setOnFocusListener(focus: () -> Unit)

	abstract fun hideProgress()
	abstract fun showProgress()

	// abstract fun setTextFont()
}