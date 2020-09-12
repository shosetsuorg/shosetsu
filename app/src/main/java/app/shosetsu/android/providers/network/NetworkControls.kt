package app.shosetsu.android.providers.network

import android.util.Log
import app.shosetsu.android.common.utils.CookieJarSync
import okhttp3.OkHttpClient
import java.util.logging.Level
import java.util.logging.Logger

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
 */


/**
 * shosetsu
 * 04 / 05 / 2020
 */

fun createOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
		.addInterceptor {
			val r = it.request()
			Log.i("OkHttpClient request", r.url.toUrl().toExternalForm())
			return@addInterceptor it.proceed(r)
		}.apply {
			Logger.getLogger(OkHttpClient::class.java.name).level = Level.FINE
		}
		.cookieJar(CookieJarSync())
		.build()