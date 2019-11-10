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
import android.app.Activity;
import android.webkit.WebView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;


/**
 * shosetsu
 * 09 / 11 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class WebViewScrapper {

    private final WebView webView;
    private Activity activity;
    public boolean completed = false;
    String html;
    private boolean working = false;


    /**
     * Constructor
     *
     * @param webView  Webview to use
     * @param activity How to handle Scraping
     */
    @SuppressLint("SetJavaScriptEnabled")
    public WebViewScrapper(WebView webView, Activity activity) {
        this.webView = webView;
        this.activity = activity;
        webView.getSettings().setJavaScriptEnabled(true);
        // webView.addJavascriptInterface(this, "HTMLOUT");
        webView.setWebViewClient(new WebViewScrapperClient(this));
    }

    // @JavascriptInterface
    // public void processHTML(String html) {
    //     Log.i("ProcessingHTML", "of latestURL");
    //     this.html = html;
    // }

    /**
     * Put this in an async task, or you will have a bad time
     *
     * @param url URL to retrieve;
     * @return Document of the URL
     */
    public Document docFromURL(String url, boolean cloudflare) {

        try {
            if (!cloudflare)
                return Jsoup.connect(url).get();
            else {
                // Cloudflare cf = new Cloudflare(url);
                //  cf.setUser_agent(webView.getSettings().getUserAgentString());

                return Jsoup.connect(url).get();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void clear() {
        activity.runOnUiThread(() -> webView.loadUrl("about:blank"));
    }

}
