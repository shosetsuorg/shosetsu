package app.shosetsu.android.ui.reader.types.model

import android.view.View
import android.webkit.WebView
import app.shosetsu.android.ui.reader.types.base.ReaderType
import com.github.doomsdayrs.apps.shosetsu.R

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
 */
class HTMLReader(itemView: View) : ReaderType(itemView) {
	private val webView: WebView = itemView.findViewById(R.id.web_view)

	override fun setData(data: String) {
		webView.loadData(data, "text/html", "UTF-8")
	}

	override fun setTextColor(int: Int) {
	}

	override fun setBackgroundColor(int: Int) {
	}

	override fun setTextSize(textSize: Float) {
	}

	override fun setTextPadding(padding: Int) {
	}

	override fun setParagraphSpacing(spacing: Int) {
	}

	override fun setParagraphIndent(indent: Int) {
	}

	override fun setProgress(progress: Int) {
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