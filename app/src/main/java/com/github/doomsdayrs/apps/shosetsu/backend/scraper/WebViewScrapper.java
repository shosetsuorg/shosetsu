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

import android.os.AsyncTask;
import android.util.Log;

import com.github.doomsdayrs.apps.shosetsu.backend.scraper.aria2.CloudFlareCallback;
import com.github.doomsdayrs.apps.shosetsu.backend.scraper.aria2.Cloudflare;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * shosetsu
 * 09 / 11 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class WebViewScrapper {

    private static String ua;
    String html;
    // private final WebView webView;
    //private Activity activity;
    public boolean completed = false;

    private WebViewScrapper() {
    }

    public static void setUa(String ua) {
        WebViewScrapper.ua = ua;
    }
    //private boolean working = false;

    /*
     * Constructor
     *
     * @param webView  Webview to use
     * @param activity How to handle Scraping
     */
            /*

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
*/

    /**
     * Put this in an async task, or you will have a bad time
     *
     * @param url URL to retrieve;
     * @return Document of the URL
     */
    public static Document docFromURL(String url, boolean cloudflare) {
        if (url != null) {
            Log.i("URL load", url);
            try {
                if (cloudflare) {
                    Cloudflare cf = new Cloudflare(url);
                    cf.setUser_agent(ua);
                    return Jsoup.connect(url).cookies(Cloudflare.List2Map(new GetCookies().execute(cf).get())).get();
                } else {
                    return Jsoup.connect(url).get();
                }
            } catch (IOException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    static class GetCookies extends AsyncTask<Cloudflare, Void, List<HttpCookie>> {
        List<HttpCookie> cookies = null;
        int status = 0;

        @Override
        protected List<HttpCookie> doInBackground(Cloudflare... cf) {
            cf[0].getCookies(new CloudFlareCallback() {
                @Override
                public void onSuccess(List<HttpCookie> cookieList) {
                    cookies = cookieList;
                    status = 1;
                }

                @Override
                public void onFail() {
                    status = -1;
                }
            });
            int a = 0;
            while (status == 0) {
                a++;
            }
            return cookies;
        }
    }
/*
    private void clear() {
        activity.runOnUiThread(() -> webView.loadUrl("about:blank"));
    }
*/
}
