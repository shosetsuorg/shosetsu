package com.github.doomsdayrs.apps.shosetsu.backend.scraper;
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
 */

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

/**
 * shosetsu
 * 10 / 11 / 2019
 *
 * @author github.com/doomsdayrs
 */
class WebViewScrapperClient extends WebViewClient {
    private final WebViewScrapper webViewScrapper;

    WebViewScrapperClient(WebViewScrapper webViewScrapper) {
        this.webViewScrapper = webViewScrapper;
    }

    @Override
    public boolean shouldOverrideUrlLoading(@NonNull WebView view, String url) {
        view.loadUrl(url);
        return false;
    }

    public void onPageFinished(@NonNull WebView view, String url) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        view.evaluateJavascript(
                "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                html -> {
                    Log.d("HTML", html);
                    webViewScrapper.html = html;
                    webViewScrapper.completed = true;
                });
        // view.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");

    }
}
