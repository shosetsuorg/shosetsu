package app.shosetsu.android.datasource.remote.impl.update

import app.shosetsu.android.common.ext.quickie
import app.shosetsu.android.datasource.remote.base.IRemoteAppUpdateDataSource
import app.shosetsu.android.domain.model.remote.AppUpdateDTO
import app.shosetsu.common.EmptyResponseBodyException
import app.shosetsu.common.domain.model.local.AppUpdateEntity
import app.shosetsu.lib.exceptions.HTTPException
import com.github.doomsdayrs.apps.shosetsu.BuildConfig.DEBUG
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient

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
 * 07 / 09 / 2020
 *
 * For standard releases
 */
class GithubAppUpdateDataSource(
	private val okHttpClient: OkHttpClient
) : IRemoteAppUpdateDataSource, IRemoteAppUpdateDataSource.Downloadable {
	private val SHOSETSU_GIT_UPDATE_URL: String by lazy {
		"https://raw.githubusercontent.com/shosetsuorg/shosetsu/${
			if (DEBUG) "development" else "master"
		}/android/src/${
			if (DEBUG) "debug" else "master"
		}/assets/update.json"
	}

	private val json by lazy {
		Json {
			ignoreUnknownKeys = true
		}
	}

	@Throws(EmptyResponseBodyException::class, HTTPException::class)
	override suspend fun loadAppUpdate(): AppUpdateEntity {
		okHttpClient.quickie(SHOSETSU_GIT_UPDATE_URL)
			.use { gitResponse ->
				if (gitResponse.isSuccessful) {
					return gitResponse.body?.use { responseBody ->
						json.decodeFromString<AppUpdateDTO>(responseBody.string()).convertTo()
					} ?: throw EmptyResponseBodyException(SHOSETSU_GIT_UPDATE_URL)
				}
				throw HTTPException(gitResponse.code)
			}
	}

	@Throws(EmptyResponseBodyException::class, HTTPException::class)
	override suspend fun downloadAppUpdate(update: AppUpdateEntity): ByteArray {
		/**
		 * Attempts to figure out the correct download URL to use
		 */
		val url = if (update.archURLs != null) {
			when (System.getProperty("os.arch")) {
				"armeabi-v7a" -> update.archURLs!!.`armeabi-v7a`
				"arm64-v8a" -> update.archURLs!!.`arm64-v8a`
				"x86" -> update.archURLs!!.x86
				"x86_64" -> update.archURLs!!.x86_64
				else -> update.url // default to using the universal APK
			}
		} else update.url
		okHttpClient.quickie(url).let { response ->
			if (response.isSuccessful) {
				// TODO One day have kotlin IO to handle this right here
				response.body?.bytes() ?: throw EmptyResponseBodyException(update.url)
			}
			throw HTTPException(response.code)
		}
	}


}
