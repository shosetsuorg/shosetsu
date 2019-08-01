package com.github.doomsdayrs.apps.shosetsu.backend;

import android.webkit.CookieManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 * shosetsu
 * 01 / 08 / 2019
 *
 */

/**
 * Provides a synchronization point between the webview cookie store and okhttp3.OkHttpClient cookie store
 */
public final class WebviewCookieHandler implements CookieJar {
    private CookieManager webviewCookieManager = CookieManager.getInstance();

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        String urlString = url.toString();
        for (Cookie cookie : cookies) {
            webviewCookieManager.setCookie(urlString, cookie.toString());
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        String urlString = url.toString();
        String cookiesString = webviewCookieManager.getCookie(urlString);

        if (cookiesString != null && !cookiesString.isEmpty()) {
            //We can split on the ';' char as the cookie manager only returns cookies
            //that match the url and haven't expired, so the cookie attributes aren't included
            String[] cookieHeaders = cookiesString.split(";");
            List<Cookie> cookies = new ArrayList<>(cookieHeaders.length);

            for (String header : cookieHeaders) {
                Cookie c = Cookie.parse(url, header);
                if (c != null) {
                    cookies.add(c);
                }
            }
            return cookies;
        }

        return Collections.emptyList();
    }
}