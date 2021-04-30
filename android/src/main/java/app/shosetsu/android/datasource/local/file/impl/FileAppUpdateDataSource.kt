package app.shosetsu.android.datasource.local.file.impl

import app.shosetsu.android.common.consts.APP_UPDATE_CACHE_FILE
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.datasource.local.file.base.IFileCachedAppUpdateDataSource
import app.shosetsu.android.domain.model.remote.AppUpdateDTO
import app.shosetsu.common.domain.model.local.AppUpdateEntity
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
	override val updateAvaLive: MutableStateFlow<HResult<AppUpdateEntity>> by lazy {
		MutableStateFlow(emptyResult())
	}

	init {
		launchIO {
			iFileSystemProvider.createDirectory(CACHE, updatesPath)
		}
	}

	private fun write(debugAppUpdate: AppUpdateDTO): HResult<*> = try {
		iFileSystemProvider.writeFile(
			CACHE,
			APP_UPDATE_CACHE_FILE,
			Json.encodeToString(debugAppUpdate).encodeToByteArray()
		)
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun loadCachedAppUpdate(): HResult<AppUpdateEntity> =
		updateAvaLive.value.takeIf { it is HResult.Success }
			?: iFileSystemProvider.readFile(
				CACHE,
				APP_UPDATE_CACHE_FILE
			).transform {
				successResult(
					Json.decodeFromString<AppUpdateDTO>(it.decodeToString()).convertTo()
				)
			}

	override suspend fun putAppUpdateInCache(
		appUpdate: AppUpdateEntity,
		isUpdate: Boolean
	): HResult<*> {
		updateAvaLive.value = if (isUpdate) successResult(appUpdate) else emptyResult()
		return write(AppUpdateDTO.fromEntity(appUpdate))
	}

	override fun saveAPK(
		appUpdate: AppUpdateEntity,
		bufferedSource: ByteArray
	): HResult<String> = iFileSystemProvider.doesFileExist(CACHE, updatesCPath)
		.transform { doesFileExist ->
			if (doesFileExist) iFileSystemProvider.deleteFile(CACHE, updatesCPath)
			iFileSystemProvider.createFile(CACHE, updatesCPath)
			iFileSystemProvider.writeFile(CACHE, updatesCPath, bufferedSource)
			iFileSystemProvider.retrievePath(CACHE, updatesCPath)
		}

	companion object {
		const val updatesPath = "/updates/"
		const val updatesFile = "/update.apk"
		const val updatesCPath = "$updatesPath$updatesFile"
	}


}