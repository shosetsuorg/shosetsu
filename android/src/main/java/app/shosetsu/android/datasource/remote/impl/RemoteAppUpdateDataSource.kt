package app.shosetsu.android.datasource.remote.impl

import app.shosetsu.android.common.consts.SHOSETSU_UPDATE_URL
import app.shosetsu.android.common.ext.quickie
import app.shosetsu.android.datasource.remote.base.IRemoteAppUpdateDataSource
import app.shosetsu.android.domain.model.remote.AppUpdateDTO
import app.shosetsu.common.consts.ErrorKeys.ERROR_HTTP_ERROR
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.errorResult
import app.shosetsu.common.dto.successResult
import app.shosetsu.lib.exceptions.HTTPException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
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
 */
class RemoteAppUpdateDataSource(
	private val okHttpClient: OkHttpClient
) : IRemoteAppUpdateDataSource {
	override suspend fun loadGitAppUpdate(): HResult<AppUpdateDTO> {
		val response = okHttpClient.quickie(SHOSETSU_UPDATE_URL)
		response.takeIf { it.code == 200 }?.let { r ->
			@Suppress("BlockingMethodInNonBlockingContext")
			return successResult(
				ObjectMapper().registerKotlinModule()
					.readValue(r.body!!.string())
			)
		}
		return errorResult(ERROR_HTTP_ERROR, HTTPException(response.code))
	}
}