package com.github.doomsdayrs.apps.shosetsu.backend.scraper

import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import java.util.concurrent.TimeUnit

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
 * ====================================================================
 */ /**
 * shosetsu
 * 10 / 11 / 2019
 *
 * @author github.com/doomsdayrs
 */
internal class WebViewScrapperClient : WebViewClient() {
    //  private final WebViewScrapper webViewScrapper;
// --Commented out by Inspection START (12/22/19 11:10 AM):
//    WebViewScrapperClient(WebViewScrapper webViewScrapper) {
//        this.webViewScrapper = webViewScrapper;
//    }
// --Commented out by Inspection STOP (12/22/19 11:10 AM)
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        view.loadUrl(url)
        return false
    }

    override fun onPageFinished(view: WebView, url: String) {
        try {
            TimeUnit.SECONDS.sleep(1)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        view.evaluateJavascript(
                "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();"
        ) { html: String? -> Log.d("HTML", html) }
        // view.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
    }
}