package com.github.doomsdayrs.apps.shosetsu.datasource.remote.model

import app.shosetsu.lib.HTTPException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.doomsdayrs.apps.shosetsu.common.consts.ErrorKeys.ERROR_HTTP_ERROR
import com.github.doomsdayrs.apps.shosetsu.common.consts.SHOSETSU_UPDATE_URL
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.errorResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.quickie
import com.github.doomsdayrs.apps.shosetsu.datasource.remote.base.IRemoteAppUpdateDataSource
import com.github.doomsdayrs.apps.shosetsu.domain.model.remote.DebugAppUpdate
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
	override suspend fun loadGitAppUpdate(): HResult<DebugAppUpdate> {
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