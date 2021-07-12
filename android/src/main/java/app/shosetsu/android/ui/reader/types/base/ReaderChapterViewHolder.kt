package app.shosetsu.android.ui.reader.types.base

import android.view.View
import androidx.annotation.CallSuper
import app.shosetsu.android.ui.reader.ChapterReader
import app.shosetsu.android.view.uimodels.model.reader.ReaderChapterUI
import app.shosetsu.android.viewmodel.abstracted.AChapterReaderViewModel
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
	/**
	 * item this ViewHolder was created with
	 */
	lateinit var chapter: ReaderChapterUI

	/**
	 * reference to the chapter reader
	 */
	val chapterReader: ChapterReader
		get() = chapter.chapterReader!!

	val viewModel: AChapterReaderViewModel
		get() = chapterReader.viewModel

	val tapToScroll: Boolean
		get() = viewModel.tapToScroll

	/**
	 * Passes in data
	 */
	abstract fun setData(data: ByteArray)

	abstract fun syncTextColor()
	abstract fun syncBackgroundColor()

	abstract fun syncTextSize()
	abstract fun syncTextPadding()

	abstract fun syncParagraphSpacing()
	abstract fun syncParagraphIndent()

	abstract fun setProgress(progress: Double)

	/**
	 * When the user double clicks the window
	 */
	abstract fun getFocusTarget(onFocus: () -> Unit)

	abstract fun hideLoadingProgress()
	abstract fun showLoadingProgress()

	@CallSuper
	override fun bindView(item: ReaderChapterUI, payloads: List<Any>) {
		chapter = item // Save the item so the view can self reference
	}

	abstract fun incrementScroll()

	abstract fun depleteScroll()
}