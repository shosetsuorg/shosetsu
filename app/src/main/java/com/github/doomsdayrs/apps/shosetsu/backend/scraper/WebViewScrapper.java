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
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


/**
 * shosetsu
 * 09 / 11 / 2019
 *
 * @author github.com/doomsdayrs
 */
public class WebViewScrapper {
    private final WebView webView;
    private static Looper looper;
    private Activity activity;
    private ArrayList<String> toBeProcessed = new ArrayList<>();
    private ArrayList<StoredPage> storedPages = new ArrayList<>();
    private boolean running = false;
    private boolean next = false;

    @SuppressLint("SetJavaScriptEnabled")
    public WebViewScrapper(WebView webView, Activity activity) {
        this.webView = webView;
        this.activity = activity;

        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "HTMLOUT");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>','" + url + "');");
                StoredPage storedPage = getStoredPage(url);
                if (storedPage.html.contains("cf-browser-verification")) {
                    storedPage.html = null;
                } else {
                    next = true;
                }
            }
        });
    }

    public StoredPage getStoredPage(String url) {
        for (StoredPage storedPage : storedPages)
            if (storedPage.url.equals(url))
                return storedPage;
        return null;
    }

    @JavascriptInterface
    public void processHTML(String html, String url) {
        Log.i("ProcessingHTML", url);
        StoredPage storedPage = new StoredPage(url);
        storedPage.html = html;
        storedPages.add(storedPage);
    }

    public void loadUrl(String url) {
        toBeProcessed.add(url);
        if (!running) {
            looper = new Looper(this);
            looper.execute();
        }
    }

    /**
     * Put this in an async task, or you will have a bad time.
     *
     * @param url URL to retrieve;
     * @return Document of the URL
     */
    public Document docFromURL(String url) {
        activity.runOnUiThread(() -> loadUrl(url));
        int a = 0;
        Log.i("WaitingURL", url);
        while (getStoredPage(url) == null) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i("WaitLoop", String.valueOf(a));
            a++;
            if (a == 240) {
                return null;
            }
        }
        return Jsoup.parse(getStoredPage(url).html);
    }

    static class StoredPage {
        final String url;
        String html = null;

        StoredPage(String url) {
            this.url = url;
        }
    }

    static class Looper extends AsyncTask<Void, Void, Void> {
        final WebViewScrapper webViewScrapper;

        Looper(WebViewScrapper webViewScrapper) {
            this.webViewScrapper = webViewScrapper;
        }

        @Override
        protected void onPreExecute() {
            Log.i("Starting", "ScraperLoop");
            webViewScrapper.running = true;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i("Finished", "ScraperLoop");
            webViewScrapper.running = false;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (webViewScrapper.toBeProcessed.size() > 0) {
                String url = webViewScrapper.toBeProcessed.get(0);
                Log.i("ProcessingURL", url);
                webViewScrapper.activity.runOnUiThread(() -> webViewScrapper.webView.loadUrl(url));
                int a = 0;
                while (!webViewScrapper.next) {
                    a++;
                    Log.i("NextLoop", String.valueOf(a));
                }
            }
            return null;
        }
    }
}
