package app.shosetsu.android.ui.reader.types.model

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import app.shosetsu.android.common.ext.launchUI
import app.shosetsu.android.common.ext.logD
import app.shosetsu.android.ui.reader.types.base.ReaderChapterViewHolder
import app.shosetsu.android.view.uimodels.model.reader.ReaderChapterUI
import app.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.databinding.ChapterReaderHtmlBinding

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
	private val binding = ChapterReaderHtmlBinding.bind(itemView)
	private val webView: WebView = binding.webView
	private val host = binding.nestScrollableHost

	private var isPageLoaded = false
	private var onPageLoaded: (() -> Unit)? = null

	/**
	 * key      : selector
	 * value    : style-key to value
	 */
	private val shosetsuStyle: HashMap<String, HashMap<String, String>> = hashMapOf()
	private val shosetsuScript by lazy { ShosetsuScript() }

	private inner class ShosetsuScript {
		var onClickMethod: () -> Unit = {}

		@Suppress("unused")
		@JavascriptInterface
		fun onClick() {
			launchUI {
				onClickMethod()
			}
		}

		@Suppress("unused", "RedundantVisibilityModifier")
		@JavascriptInterface
		public fun onScroll() {
			launchUI {
				webView.evaluateJavascript("window.pageYOffset") { _yPosition ->
					val yPosition: Double? = _yPosition.toDoubleOrNull()
					yPosition ?: logD("Null Y position")
					yPosition ?: return@evaluateJavascript

					webView.evaluateJavascript(getMaxJson) { _scrollMaxY ->
						val scrollMaxY: Double? = _scrollMaxY.toDoubleOrNull()
						scrollMaxY ?: logD("Null Y max")
						scrollMaxY ?: return@evaluateJavascript


						val percentage = ((yPosition / scrollMaxY) * 100)
						if (percentage < 99) {
							if (yPosition.toInt() % 5 == 0) {
								// Mark as reading if on scroll
								chapterReader.viewModel.markAsReadingOnScroll(chapter, percentage)
							}
						} else {
							// Hit bottom
							chapterReader.viewModel.updateChapter(
								chapter.copy(
									readingStatus = ReadingStatus.READ,
									readingPosition = 0.0
								),
							)
						}
					}
				}
			}
		}
	}

	init {
		@SuppressLint("SetJavaScriptEnabled") // kotlin-lib should implement xml scrubbing
		webView.settings.javaScriptEnabled = true
		webView.addJavascriptInterface(shosetsuScript, "shosetsuScript")

		webView.webViewClient = object : WebViewClient() {
			override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
				isPageLoaded = false
				super.onPageStarted(view, url, favicon)
			}

			override fun onPageFinished(view: WebView?, url: String?) {
				injectCss()
				super.onPageFinished(view, url)
				webView.evaluateJavascript(
					"""
						window.addEventListener("scroll",(event)=>{ shosetsuScript.onScroll(); });
						window.addEventListener("click",(event)=>{ shosetsuScript.onClick(); });
					""".trimIndent(), null
				)
				isPageLoaded = true
				onPageLoaded?.invoke()
			}
		}
	}

	private var userCss: String = ""

	private fun Int.cssColor(): String = "rgb($red,$green,$blue)"

	/**
	 * Get CSS from shosetsu normal settings and insert them into [shosetsuStyle]
	 */
	private fun syncStylesWithViewModel() {
		setShosetsuStyle("body") {
			this["background-color"] = viewModel.defaultBackground.cssColor()
			this["color"] = viewModel.defaultForeground.cssColor()
			this["font-size"] = "${viewModel.defaultTextSize}pt"
			this["scroll-behavior"] = "smooth"
			this["text-indent"] = "${viewModel.defaultIndentSize * 10}px"
		}

		setShosetsuStyle("p") {
			this["margin"] = "${viewModel.defaultParaSpacing * 10}px 0"
		}

		setShosetsuStyle("img") {
			this["max-width"] = "100%"
			this["height"] = "initial !important"
		}
	}


	/**
	 * Get a specific style for an element out of [shosetsuStyle], otherwise put and get emptyMap
	 */
	private inline fun setShosetsuStyle(elem: String, action: HashMap<String, String>.() -> Unit) =
		shosetsuStyle.getOrPut(elem) { hashMapOf() }.apply(action)

	/**
	 * Converts [shosetsuStyle] into a valid css styles sheet to be inserted
	 */
	private fun generateShosetsuCss(): String =
		shosetsuStyle.map { elem ->
			"${elem.key} {" + elem.value.map { rule -> "${rule.key}:${rule.value}" }
				.joinToString(";", postfix = ";") + "}"
		}.joinToString("")

	private fun injectCss() {
		syncStylesWithViewModel()

		// READ AND INJECT STYLE
		val css = generateShosetsuCss() + userCss

		webView.evaluateJavascript(
			"""
				style = document.getElementById('shosetsu-style');
				if(!style){
					style = document.createElement('style');
					style.id = 'shosetsu-style';
					style.type = 'text/css';
					document.head.appendChild(style);
				}
				style.textContent = '$css';
			""".trimIndent(),
			null
		)


	}

	override fun setData(data: ByteArray) {
		syncStylesWithViewModel()
		webView.loadData(data.decodeToString(), "text/html", "UTF-8")
	}

	override fun syncTextColor() {
		injectCss()
	}

	override fun syncBackgroundColor() {
		injectCss()
	}

	override fun syncTextSize() {
		injectCss()
	}

	override fun syncTextPadding() {
		injectCss()
	}

	override fun syncParagraphSpacing() {
		injectCss()
	}

	override fun syncParagraphIndent() {
		injectCss()
	}

	override fun setProgress(progress: Double) {
		val call = {

			webView.evaluateJavascript(getMaxJson) { maxString ->
				maxString.toDoubleOrNull()?.let { maxY ->
					webView.evaluateJavascript(
						"window.scrollTo(0,${(maxY * (progress / 100)).toInt()})",
						null
					)
				}
			}
		}

		if (isPageLoaded) call()
		else onPageLoaded = call
	}

	override fun getFocusTarget(onFocus: () -> Unit) {
		shosetsuScript.onClickMethod = onFocus
	}

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
		injectCss()
		viewModel.loadChapterCss().observe(chapterReader) { userCss = it;injectCss() }
	}

	override fun unbindView(item: ReaderChapterUI) {
	}

	companion object {
		private val getMaxJson = """
					    var innerh = window.innerHeight || ebody.clientHeight, yWithScroll = 0;
					    yWithScroll = document.body.scrollHeight;
					    yWithScroll-innerh; 
				""".trimIndent()
	}
}