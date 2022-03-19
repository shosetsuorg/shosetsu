package app.shosetsu.android.ui.reader

import android.annotation.SuppressLint
import android.graphics.Color
import android.webkit.JavascriptInterface
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import app.shosetsu.android.common.ext.launchUI
import app.shosetsu.android.common.ext.logV
import kotlinx.coroutines.launch

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

	if (scrollState.isScrollInProgress)
		DisposableEffect(Unit) {
			onDispose {
				if (scrollState.value != 0)
					onScroll((scrollState.value.toDouble() / scrollState.maxValue))
				else onScroll(0.0)
			}
		}

	Box {
		WebView(
			state,
			captureBackPresses = false,
			onPageFinished = { webView, _ ->
				webView.evaluateJavascript(
					"""
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

				val inter = ShosetsuScript(onFocusToggle)

				webView.addJavascriptInterface(inter, "shosetsuScript")
			},
			modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
		)
	}

	// Avoid scrolling when the state has not fully loaded
	if (scrollState.maxValue != 0 && scrollState.maxValue != Int.MAX_VALUE)
		LaunchedEffect(Unit) {
			launch {
				val result = (scrollState.maxValue * progress).toInt()
				logV("Scrolling to $result from $progress with max of ${scrollState.maxValue}")
				scrollState.scrollTo(result)
			}
		}
}

const val getMaxJson = """
   var innerh = window.innerHeight || ebody.clientHeight, yWithScroll = 0;
   yWithScroll = document.body.scrollHeight;
   yWithScroll-innerh; 
"""

class ShosetsuScript(
	val onClickMethod: () -> Unit
) {

	@Suppress("unused")
	@JavascriptInterface
	fun onClick() {
		launchUI {
			onClickMethod()
		}
	}
}