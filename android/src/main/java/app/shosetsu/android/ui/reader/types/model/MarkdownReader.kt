package app.shosetsu.android.ui.reader.types.model

import android.view.View
import app.shosetsu.android.ui.reader.types.base.ReaderChapterViewHolder
import app.shosetsu.android.view.uimodels.model.reader.ReaderChapterUI

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
class MarkdownReader(itemView: View) : ReaderChapterViewHolder(itemView) {
	//private val markdownView: MarkdownView = itemView.findViewById(R.id.markdown_view)

	override fun setData(data: ByteArray) {
		TODO("Not yet implemented")
	}

	override fun syncTextColor() {
		TODO("Not yet implemented")
	}

	override fun syncBackgroundColor() {
		TODO("Not yet implemented")
	}

	override fun syncTextSize() {
		TODO("Not yet implemented")
	}

	override fun syncTextPadding() {
		TODO("Not yet implemented")
	}

	override fun syncParagraphSpacing() {
		TODO("Not yet implemented")
	}

	override fun syncParagraphIndent() {
		TODO("Not yet implemented")
	}

	override fun setProgress(progress: Double) {
		TODO("Not yet implemented")
	}

	override fun getFocusTarget(onFocus: () -> Unit) {
		TODO("Not yet implemented")
	}

	override fun hideLoadingProgress() {
		TODO("Not yet implemented")
	}

	override fun showLoadingProgress() {
		TODO("Not yet implemented")
	}

	override fun incrementScroll() {
		TODO("Not yet implemented")
	}

	override fun depleteScroll() {
		TODO("Not yet implemented")
	}

	override fun bindView(item: ReaderChapterUI, payloads: List<Any>) {
		TODO("Not yet implemented")
	}

	override fun unbindView(item: ReaderChapterUI) {
		TODO("Not yet implemented")
	}
}