package com.github.doomsdayrs.apps.shosetsu.backend.scraper

import android.util.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

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

	/**
	 * Put this in an async task, or you will have a bad time
	 *
	 * @param url URL to retrieve;
	 * @return Document of the URL
	 */
	fun docFromURL(url: String?, cloudflare: Boolean): Document? {
		if (url != null) {
			Log.i("URL load", url)
			return Jsoup.connect(url).get()
		}
		return null
	}
}