package app.shosetsu.android.ui.reader.types.model

import android.util.Log
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import app.shosetsu.android.common.ext.logD
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.ui.reader.types.base.ReaderChapterViewHolder
import app.shosetsu.android.view.uimodels.model.reader.ReaderChapterUI
import app.shosetsu.common.enums.ReadingStatus
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
class HtmlReader(itemView: View) : ReaderChapterViewHolder(itemView) {
	private val webView: WebView = itemView.findViewById(R.id.web_view)

	/**
	 * key      : selector
	 * value    : style-key to value
	 */
	private val shosetsuStyle: HashMap<String, HashMap<String, String>> = hashMapOf()

	/**
	 * HTML page provided by extension
	 */
	private var html: String = ""


	private inner class ScrollListener() {

		@JavascriptInterface
		public fun scrolled() {
			webView.evaluateJavascript("window.pageYOffset") { _yPosition ->
				val yPosition = _yPosition.toInt()
				webView.evaluateJavascript("window.scrollMaxY") { _scrollMaxY ->
					val scrollMaxY = _scrollMaxY.toInt()
					val percentage = (yPosition / scrollMaxY) * 100
					if (percentage < 99) {
						if (yPosition % 5 == 0) {
							Log.i(logID(), "Scrolling")
							// Mark as reading if on scroll
							chapterReader.viewModel.markAsReadingOnScroll(chapter, yPosition)
						}
					} else {
						Log.i(logID(), "Hit the bottom")
						// Hit bottom
						chapterReader.viewModel.updateChapter(
							chapter,
							readingStatus = ReadingStatus.READ,
							readingPosition = 0
						)
					}
				}
			}
		}
	}

	init {
		webView.settings.javaScriptEnabled = true
		webView.addJavascriptInterface(ScrollListener(), "scroll_listener")
		webView.evaluateJavascript(
			"""
				window.addEventListener("scroll",scrolled);
			""".trimIndent(), null
		)
	}

	private fun syncStylesWithViewModel() {
		setShosetsuStyle("body")["backgroundColor"] = "${viewModel.defaultBackground}"
		setShosetsuStyle("p1")["color"] = "${viewModel.defaultForeground}"
		setShosetsuStyle("p1")["size"] = "${viewModel.defaultTextSize}"
	}

	private fun setShosetsuStyle(elem: String): HashMap<String, String> =
		shosetsuStyle.getOrPut(elem) { hashMapOf() }

	/**
	 * Converts [shosetsuStyle] into a valid css styles sheet to be inserted
	 */
	private fun generateShosetsuCss(): String =
		shosetsuStyle.map { elem ->
			"${elem.key} {" + elem.value.map { rule -> "${rule.key}:${rule.value}" }
				.joinToString(";", postfix = ";") + "}"
		}.joinToString("\n")


	private fun injectShosetsuCss() {
		// READ AND INJECT STYLE
		webView.evaluateJavascript(
			"""
				style = document.getElementById("shosetsu-style");
				if(!style){
					style = document.createElement("style");
					style.id = "shosetsu-style";
					document.head.append(style);
				}
				style.innerHtml = "${generateShosetsuCss().replace("\"", "\\\"")}";
			""".trimIndent()
		) {
			logD("Shosetsu injection: $it")
		}
	}

	private fun bind() {
		webView.loadData(html, "text/html", "UTF-8")
	}

	override fun setData(data: String) {
		syncStylesWithViewModel()
		html = data
		bind()
	}

	override fun syncTextColor() {
		injectShosetsuCss()
	}

	override fun syncBackgroundColor() {
		injectShosetsuCss()
	}

	override fun syncTextSize() {
		injectShosetsuCss()
	}

	override fun syncTextPadding() {
		injectShosetsuCss()
	}

	override fun syncParagraphSpacing() {
		injectShosetsuCss()
	}

	override fun syncParagraphIndent() {
		injectShosetsuCss()
	}

	override fun setProgress(progress: Int) {
		webView.evaluateJavascript("window.scroll(0,$progress)", null)
	}

	override fun getFocusTarget(): View = webView

	override fun hideLoadingProgress() {}
	override fun showLoadingProgress() {}

	override fun incrementScroll() {
		webView.evaluateJavascript("window.scroll(0,50)", null)
	}

	override fun depleteScroll() {
		webView.evaluateJavascript("window.scroll(0,-50)", null)
	}

	override fun bindView(item: ReaderChapterUI, payloads: List<Any>) {
		syncStylesWithViewModel()
		injectShosetsuCss()

		viewModel.loadChapterCss().observe(chapterReader) { css ->
			webView.evaluateJavascript(
				"""
				document.getElementById("shosetsu-style").innerHtml += "$css";
			""".trimIndent()
			) {
				logD("User injection: $it")
			}
			webView.evaluateJavascript(
				"""
				document.getElementById("shosetsu-style").innerHtml;
			""".trimIndent()
			) {
				logD("Inner html: $it")
			}
			webView.evaluateJavascript(
				"""
				document.getElementsByTagName("html")[0].innerHtml;
			""".trimIndent()
			) {
				logD("Html: $it")
			}
		}
	}

	override fun unbindView(item: ReaderChapterUI) {
	}
}