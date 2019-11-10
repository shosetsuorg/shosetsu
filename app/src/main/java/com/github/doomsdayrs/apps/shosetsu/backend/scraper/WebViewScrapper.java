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

import android.annotation.SuppressLint;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.concurrent.TimeUnit;

/**
 * shosetsu
 * 09 / 11 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class WebViewScrapper {
    private final WebView webView;
    private String returnedHTML = null;
    private boolean checked = false;

    @SuppressLint("SetJavaScriptEnabled")
    public WebViewScrapper(WebView webView) {
        this.webView = webView;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "HTMLOUT");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                returnedHTML = null;
                return false;
            }

            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                checked = !returnedHTML.contains("cf-browser-verification");
            }
        });
    }

    @JavascriptInterface
    public void processHTML(String html) {
        returnedHTML = html;
        //cf-browser-verification
    }


    public void loadUrl(String url) {
        webView.loadUrl(url);
    }


    /**
     * Put this in an async task, or you will have a bad time.
     *
     * @param url URL to retrieve;
     * @return Document of the URL
     * @throws InterruptedException This fucked up hard
     */
    public Document docFromURL(String url) {
        int count = 0;
        while (checked && returnedHTML == null) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
            if (count == 120) {
                return null;
            }
        }
        Document document = Jsoup.parse(returnedHTML);
        returnedHTML = null;
        return document;
    }
}
