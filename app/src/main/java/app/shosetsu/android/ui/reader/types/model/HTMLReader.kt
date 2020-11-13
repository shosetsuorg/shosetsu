package app.shosetsu.android.ui.reader.types.model

import android.view.View
import android.webkit.WebView
import app.shosetsu.android.ui.reader.types.base.TypedReaderViewHolder
import app.shosetsu.android.view.uimodels.model.reader.ReaderChapterUI
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
class HTMLReader(itemView: View) : TypedReaderViewHolder(itemView) {
	private val webView: WebView = itemView.findViewById(R.id.web_view)

	override fun setData(data: String) {
		webView.loadData(data, "text/html", "UTF-8")
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