package com.github.doomsdayrs.apps.shosetsu.domain.usecases

import android.util.Log
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import com.github.doomsdayrs.apps.shosetsu.common.consts.SHOSETSU_UPDATE_URL
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.emptyResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.common.ext.quickie
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
 * 20 / 06 / 2020
 */
class LoadAppUpdateUseCase(
		private val okHttpClient: OkHttpClient
) {
	data class DebugAppUpdate(
			@JsonProperty("latestVersion")
			val version: String,
			@JsonProperty("versionCode")
			val versionCode: Int = -1,
			@JsonProperty("url")
			val url: String,
			@JsonProperty("releaseNotes")
			val notes: List<String>,
	)

	suspend operator fun invoke(): HResult<DebugAppUpdate> {
		okHttpClient.quickie(SHOSETSU_UPDATE_URL).takeIf { it.code == 200 }?.let { r ->
			val update = ObjectMapper().registerKotlinModule()
					.readValue<DebugAppUpdate>(r.body!!.string())
			val currentV: Int
			val remoteV: Int

			if (update.versionCode == -1) {
				currentV = BuildConfig.VERSION_NAME.substringAfter("r").toInt()
				remoteV = update.version.toInt()
			} else {
				currentV = BuildConfig.VERSION_CODE
				remoteV = update.versionCode
			}

			when {
				remoteV < currentV -> {
					Log.i(logID(), "This a future release")
					return emptyResult()
				}
				remoteV > currentV -> {
					Log.i(logID(), "Update found")
					return successResult(update)
				}
				remoteV == currentV -> {
					Log.i(logID(), "This the current release")
					return emptyResult()
				}
			}
			r.close()
		}
		return emptyResult()
	}
}