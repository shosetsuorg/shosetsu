package app.shosetsu.android.datasource.cache.model

import android.app.Application
import androidx.lifecycle.MutableLiveData
import app.shosetsu.android.common.consts.ErrorKeys
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.emptyResult
import app.shosetsu.android.common.dto.errorResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.datasource.cache.base.ICacheAppUpdateDataSource
import app.shosetsu.android.domain.model.remote.DebugAppUpdate
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
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