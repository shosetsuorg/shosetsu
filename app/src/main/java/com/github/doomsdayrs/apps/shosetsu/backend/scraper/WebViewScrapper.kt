package com.github.doomsdayrs.apps.shosetsu.backend.scraper

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
 * 09 / 11 / 2019
 *
 * @author github.com/doomsdayrs
 */
object WebViewScrapper {
    private var ua: String? = null
    fun setUa(ua: String?) {
        WebViewScrapper.ua = ua
    }
    /*

    //private boolean working = false;

    \/**
     */
     * Constructor
     *
     * @param webView  Webview to use
     * @param activity How to handle Scraping
     *\/

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
    @Deprecated("Useless with entire class", ReplaceWith("[LuaFormatter].method()"), DeprecationLevel.ERROR)
    fun docFromURL(url: String?, cloudflare: Boolean): Document? {
        if (url != null) {
            Log.i("URL load", url)
            try {
                return if (cloudflare) {
                    val cf = Cloudflare(url)
                    cf.user_agent = ua
                    Jsoup.connect(url).cookies(Cloudflare.List2Map(GetCookies().execute(cf).get())).get()
                } else {
                    Jsoup.connect(url).get()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }
        }
        return null
    }

    internal class GetCookies : AsyncTask<Cloudflare?, Void?, List<HttpCookie>?>() {
        var cookies: List<HttpCookie>? = null
        var status = 0
        override fun doInBackground(vararg cf: Cloudflare?): List<HttpCookie>? {
            cf[0]?.getCookies(object : cfCallback {
                override fun onSuccess(cookieList: List<HttpCookie>, hasNewUrl: Boolean, newUrl: String) {
                    cookies = cookieList
                    status = 1
                }

                override fun onFail() {
                    status = -1
                }
            })
            var a = 0
            while (status == 0) {
                a++
            }
            return cookies
        }
    }
    private void clear() {
        activity.runOnUiThread(() -> webView.loadUrl("about:blank"));
    }
*/
}