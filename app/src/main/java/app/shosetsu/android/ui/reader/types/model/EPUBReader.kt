package app.shosetsu.android.ui.reader.types.model

import android.view.View
import app.shosetsu.android.ui.reader.types.base.TypedReaderViewHolder
import app.shosetsu.android.view.uimodels.model.reader.ReaderChapterUI

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * shosetsu
 * 11 / 09 / 2020
 *
 * <p>
 *     Concept
 *     Epubs are a series of images, links, videos, and more
 *     To properly fit all of this in, Use a webview to handle interactions
 *     A series of webviews can be used to represent different things (possible)
 * </p>
 */
class EPUBReader(itemView: View) : TypedReaderViewHolder(itemView) {
	override fun setData(data: String) {
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


	override fun setProgress(progress: Int) {
		TODO("Not yet implemented")
	}

	override fun setOnFocusListener(focus: () -> Unit) {
		TODO("Not yet implemented")
	}

	override fun hideProgress() {
		TODO("Not yet implemented")
	}

	override fun showProgress() {
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