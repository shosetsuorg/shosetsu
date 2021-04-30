package app.shosetsu.android.datasource.remote.impl.update

import app.shosetsu.android.common.ext.ifSo
import app.shosetsu.android.common.ext.quickie
import app.shosetsu.android.datasource.remote.base.IRemoteAppUpdateDataSource
import app.shosetsu.android.domain.model.remote.AppUpdateDTO
import app.shosetsu.common.consts.ErrorKeys.ERROR_HTTP_ERROR
import app.shosetsu.common.consts.ErrorKeys.ERROR_NETWORK
import app.shosetsu.common.domain.model.local.AppUpdateEntity
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.errorResult
import app.shosetsu.common.dto.successResult
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
		"https://raw.githubusercontent.com/shosetsuorg/android-app/${
			DEBUG ifSo "development" ?: "master"
		}/android/src/${
			DEBUG ifSo "debug" ?: "master"
		}/assets/update.${
			DEBUG ifSo "json" ?: "xml"
		}"
	}

	override suspend fun loadAppUpdate(): HResult<AppUpdateEntity> {
		okHttpClient.quickie(SHOSETSU_GIT_UPDATE_URL)
			.use { gitResponse ->
				gitResponse.takeIf { it.code == 200 }?.use {
					return gitResponse.body?.use { responseBody ->
						successResult(
							Json.decodeFromString<AppUpdateDTO>(responseBody.string()).convertTo()
						)
					} ?: errorResult(ERROR_NETWORK, "Response body null")
				}
				return errorResult(ERROR_HTTP_ERROR, HTTPException(gitResponse.code))
			}
	}

	override suspend fun downloadAppUpdate(update: AppUpdateEntity): HResult<ByteArray> =
		okHttpClient.quickie(update.url).let { response ->
			if (response.isSuccessful) {
				response.body?.let { body ->
					// TODO One day have kotlin IO to handle this right here
					return successResult(body.bytes())
				} ?: errorResult(ERROR_NETWORK, "Empty response body")
			} else errorResult(ERROR_NETWORK, "Failed to download")
		}


}