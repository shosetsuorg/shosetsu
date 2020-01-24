package com.github.doomsdayrs.apps.shosetsu.ui.webView

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.android.synthetic.main.webview.*

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
 * ====================================================================
 * shosetsu
 * 31 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
class WebViewApp : AppCompatActivity(R.layout.webview) {
    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val action = Actions.actions[intent.getIntExtra("action", 0)]
        webview.settings.javaScriptEnabled = true
        when (action) {
            Actions.VIEW -> webview.webViewClient = WebViewClient()
            Actions.CLOUD_FLARE -> {
                // webview.addJavascriptInterface(JSInterface(), "HtmlViewer")
                webview.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        webview.loadUrl("javascript:window.HtmlViewer.showHTML" +
                                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")
                        finish()
                    }
                }
            }
        }
        webview.loadUrl(intent.getStringExtra("url"))
    }
}