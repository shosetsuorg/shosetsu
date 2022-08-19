package app.shosetsu.android.datasource.remote.impl.update

import app.shosetsu.android.BuildConfig.DEBUG
import app.shosetsu.android.common.EmptyResponseBodyException
import app.shosetsu.android.common.ext.quickie
import app.shosetsu.android.common.utils.archURL
import app.shosetsu.android.datasource.remote.base.IRemoteAppUpdateDataSource
import app.shosetsu.android.domain.model.local.AppUpdateEntity
import app.shosetsu.android.domain.model.remote.AppUpdateDTO
import app.shosetsu.lib.exceptions.HTTPException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okhttp3.OkHttpClient
import java.io.IOException
import java.io.InputStream

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
	private val shosetsuGitUpdateURL: String by lazy {
		"https://raw.githubusercontent.com/shosetsuorg/shosetsu/${
			if (DEBUG) "development" else "main"
		}/android/src/${
			if (DEBUG) "debug" else "main"
		}/assets/update.json"
	}

	private val json by lazy {
		Json {
			ignoreUnknownKeys = true
		}
	}

	@OptIn(ExperimentalSerializationApi::class)
	@Throws(
		EmptyResponseBodyException::class,
		HTTPException::class,
		IOException::class
	)
	override suspend fun loadAppUpdate(): AppUpdateEntity {
		okHttpClient.quickie(shosetsuGitUpdateURL)
			.use { gitResponse ->
				if (gitResponse.isSuccessful) {
					return gitResponse.body?.use { responseBody ->
						responseBody.byteStream().use {
							json.decodeFromStream<AppUpdateDTO>(it).convertTo()
						}
					} ?: throw EmptyResponseBodyException(shosetsuGitUpdateURL)
				}
				throw HTTPException(gitResponse.code)
			}
	}

	@Throws(
		EmptyResponseBodyException::class,
		HTTPException::class,
		IOException::class
	)
	override suspend fun downloadAppUpdate(update: AppUpdateEntity): InputStream {
		okHttpClient.quickie(update.archURL()).let { response ->
			if (response.isSuccessful) {
				return response.body?.byteStream()
					?: throw EmptyResponseBodyException(update.archURL())
			} else throw HTTPException(response.code)
		}
	}


}
