package com.github.doomsdayrs.apps.shosetsu.datasource.cache.model

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.doomsdayrs.apps.shosetsu.common.consts.ErrorKeys
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.emptyResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.errorResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.datasource.cache.base.ICacheAppUpdateDataSource
import com.github.doomsdayrs.apps.shosetsu.domain.model.remote.DebugAppUpdate
import java.io.File

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
class CacheAppUpdateDataSource(
		private val application: Application
) : ICacheAppUpdateDataSource {
	private val cachedUpdate: File by lazy {
		File(application.cacheDir.absolutePath + "/SHOSETSU_APP_UPDATE.json")
	}

	override val updateAvaLive: MutableLiveData<HResult<DebugAppUpdate>> by lazy {
		MutableLiveData(emptyResult())
	}

	private fun write(debugAppUpdate: DebugAppUpdate): HResult<*> = try {
		cachedUpdate.writeText(
				ObjectMapper().registerKotlinModule().writeValueAsString(debugAppUpdate)
		)
		successResult("")
	} catch (e: JsonProcessingException) {
		errorResult(ErrorKeys.ERROR_IO, e)
	}

	override suspend fun loadCachedAppUpdate(): HResult<DebugAppUpdate> =
			successResult(ObjectMapper().registerKotlinModule()
					.readValue(cachedUpdate.readText())
			)

	override suspend fun putAppUpdateInCache(
			debugAppUpdate: DebugAppUpdate,
			isUpdate: Boolean
	): HResult<*> {
		updateAvaLive.postValue(if (isUpdate) successResult(debugAppUpdate) else emptyResult())
		return write(debugAppUpdate)
	}
}