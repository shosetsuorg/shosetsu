package app.shosetsu.android.ui.reader

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.shosetsu.android.ui.reader.types.model.ShosetsuScript
import app.shosetsu.android.ui.reader.types.model.getMaxJson

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