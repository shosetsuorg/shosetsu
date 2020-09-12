package app.shosetsu.android.ui.reader.types.model

import android.view.View
import app.shosetsu.android.ui.reader.types.base.ReaderType

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
class EPUBReader(itemView: View) : ReaderType(itemView) {

	override fun setData(data: String) {
		TODO("Not yet implemented")
	}

	override fun setTextColor(int: Int) {
		TODO("Not yet implemented")
	}

	override fun setBackgroundColor(int: Int) {
		TODO("Not yet implemented")
	}

	override fun setTextSize(textSize: Float) {
		TODO("Not yet implemented")
	}

	override fun setTextPadding(padding: Int) {
		TODO("Not yet implemented")
	}

	override fun setParagraphSpacing(spacing: Int) {
		TODO("Not yet implemented")
	}

	override fun setParagraphIndent(indent: Int) {
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

}