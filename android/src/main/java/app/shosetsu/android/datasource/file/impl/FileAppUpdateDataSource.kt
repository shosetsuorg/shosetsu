package app.shosetsu.android.datasource.file.impl

import app.shosetsu.android.common.consts.APP_UPDATE_CACHE_FILE
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.datasource.file.base.IFileCachedAppUpdateDataSource
import app.shosetsu.android.domain.model.remote.AppUpdateDTO
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.emptyResult
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.dto.transform
import app.shosetsu.common.enums.InternalFileDir.CACHE
import app.shosetsu.common.providers.file.base.IFileSystemProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
class FileAppUpdateDataSource(
	private val iFileSystemProvider: IFileSystemProvider
) : IFileCachedAppUpdateDataSource {

	override val updateAvaLive: MutableStateFlow<HResult<AppUpdateDTO>> by lazy {
		MutableStateFlow(emptyResult())
	}

	private fun write(debugAppUpdate: AppUpdateDTO): HResult<*> = try {
		iFileSystemProvider.writeInternalFile(
			CACHE,
			APP_UPDATE_CACHE_FILE,
			Json.encodeToString(debugAppUpdate)
		)
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun loadCachedAppUpdate(): HResult<AppUpdateDTO> =
		iFileSystemProvider.readInternalFile(
			CACHE,
			APP_UPDATE_CACHE_FILE
		).transform { Json.decodeFromString(it) }

	override suspend fun putAppUpdateInCache(
		debugAppUpdate: AppUpdateDTO,
		isUpdate: Boolean
	): HResult<*> {
		updateAvaLive.value = if (isUpdate) successResult(debugAppUpdate) else emptyResult()
		return write(debugAppUpdate)
	}
}