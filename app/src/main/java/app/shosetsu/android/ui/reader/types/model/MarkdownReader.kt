package app.shosetsu.android.ui.reader.types.model

import android.view.View
import app.shosetsu.android.ui.reader.types.base.ReaderType
import com.github.doomsdayrs.apps.shosetsu.R
import us.feras.mdv.MarkdownView

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
class MarkdownReader(itemView: View) : ReaderType(itemView) {
	private val markdownView: MarkdownView = itemView.findViewById(R.id.markdown_view)

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