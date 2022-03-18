package app.shosetsu.android.ui.reader

import android.annotation.SuppressLint
import android.graphics.Color
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.shosetsu.android.common.ext.launchUI
import app.shosetsu.android.common.ext.logD

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
 *
 * @since 18 / 03 / 2022
 * @author Doomsdayrs
 */

@Composable
fun WebViewPageContent(
	html: String,
	progress: Double,
	onScroll: (perc: Double) -> Unit,
	onFocusToggle: () -> Unit,
	onHitBottom: () -> Unit,
) {
	val scrollState = rememberScrollState()
	val state = rememberWebViewStateWithHTMLData(html)

	Box {
		WebView(
			state,
			captureBackPresses = false,
			onPageFinished = { webView, _ ->
				webView.evaluateJavascript(
					"""
						window.addEventListener("scroll",(event)=>{ shosetsuScript.onScroll(); });
						window.addEventListener("click",(event)=>{ shosetsuScript.onClick(); });
					""".trimIndent(), null
				)

				webView.evaluateJavascript(getMaxJson) { maxString ->
					maxString.toDoubleOrNull()?.let { maxY ->
						webView.evaluateJavascript(
							"window.scrollTo(0,${(maxY * (progress / 100)).toInt()})",
							null
						)
					}
				}
			},
			onCreated = { webView ->
				webView.setBackgroundColor(Color.BLACK)
				@SuppressLint("SetJavaScriptEnabled")
				webView.settings.javaScriptEnabled = true

				val inter = ShosetsuScript(
					webView,
					onHitBottom = onHitBottom,
					onScroll = onScroll
				)

				inter.onClickMethod = onFocusToggle

				webView.addJavascriptInterface(inter, "shosetsuScript")
			},
			modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
		)
	}
}

const val getMaxJson = """
   var innerh = window.innerHeight || ebody.clientHeight, yWithScroll = 0;
   yWithScroll = document.body.scrollHeight;
   yWithScroll-innerh; 
"""

class ShosetsuScript(
	private val webView: WebView,
	private val onHitBottom: () -> Unit,
	private val onScroll: (percentage: Double) -> Unit
) {
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
							onScroll(percentage)
						}
					} else {
						// Hit bottom
						onHitBottom()
					}
				}
			}
		}
	}
}