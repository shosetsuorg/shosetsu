package com.github.doomsdayrs.apps.shosetsu.backend.scraper.aria2;

import java.net.HttpCookie;
import java.util.List;


public interface CloudFlareCallback {
    void onSuccess(List<HttpCookie> cookieList);

    void onFail();
}
