package app.shosetsu.android.common.utils

import android.webkit.CookieManager
import app.shosetsu.android.common.utils.CookieJarSync.androidCookieManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * shosetsu
 * 01 / 08 / 2019
 * Provides a synchronization point between [androidCookieManager] & [CookieJar]
 */
object CookieJarSync : CookieJar {
	private val androidCookieManager by lazy { CookieManager.getInstance() }

	override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
		//	logV("`$url` saving the following cookies: $cookies")
		val urlString = url.toString()
		cookies.forEach { androidCookieManager.setCookie(urlString, it.toString()) }
	}

	override fun loadForRequest(url: HttpUrl): List<Cookie> {
		//	logV("`$url` loading cookies...")
		return when (val cookies = androidCookieManager.getCookie(url.toString())) {
			null -> {
				//			logV("`$url` has no cookies")
				emptyList()
			}
			else -> {
				val result = cookies.split("; ").mapNotNull { Cookie.parse(url, it) }
				//			logV("`$url` has the following cookies: $result")
				result
			}
		}
	}
}