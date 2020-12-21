package app.shosetsu.android.domain.repository.impl

import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.datasource.file.base.IFileCachedAppUpdateDataSource
import app.shosetsu.android.datasource.remote.base.IRemoteAppUpdateDataSource
import app.shosetsu.android.domain.model.remote.AppUpdateDTO
import app.shosetsu.common.consts.ErrorKeys
import app.shosetsu.common.consts.ErrorKeys.ERROR_DUPLICATE
import app.shosetsu.common.domain.model.local.AppUpdateEntity
import app.shosetsu.common.domain.repositories.base.IAppUpdatesRepository
import app.shosetsu.common.dto.*
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

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
class AppUpdatesRepository(
	private val iRemoteAppUpdateDataSource: IRemoteAppUpdateDataSource,
	private val iFileAppUpdateDataSource: IFileCachedAppUpdateDataSource,
) : IAppUpdatesRepository {
	private var running = false


	@ExperimentalCoroutinesApi
	override fun loadAppUpdateFlow(): Flow<HResult<AppUpdateEntity>> =
		iFileAppUpdateDataSource.updateAvaLive


	override suspend fun setAppUpdate(debugAppUpdate: AppUpdateEntity): HResult<*> {
		TODO("Not yet implemented")
	}


	private fun compareVersion(newVersion: AppUpdateDTO): HResult<AppUpdateEntity> {
		val currentV: Int
		val remoteV: Int

		if (newVersion.versionCode == -1) {
			currentV = BuildConfig.VERSION_NAME.substringAfter("r").toInt()
			remoteV = newVersion.version.toInt()
		} else {
			currentV = BuildConfig.VERSION_CODE
			remoteV = newVersion.versionCode
		}

		return when {
			remoteV < currentV -> {
				logI("This a future release compared to $newVersion")
				emptyResult()
			}
			remoteV > currentV -> {
				logI("Update found compared to $newVersion")
				successResult(newVersion.convertTo())
			}
			remoteV == currentV -> {
				logI("This the current release compared to $newVersion")
				emptyResult()
			}
			else -> emptyResult()
		}
	}

	@Synchronized
	override suspend fun loadGitAppUpdate(): HResult<AppUpdateEntity> {
		if (running) return errorResult(ERROR_DUPLICATE, "Cannot run duplicate")
		else running = true

		val rR: AppUpdateDTO = iRemoteAppUpdateDataSource.loadGitAppUpdate().unwrap(
			onEmpty = { return emptyResult().also { running = false } },
			onError = { return it.also { running = false } }
		)!!

		return compareVersion(rR).also {
			iFileAppUpdateDataSource.putAppUpdateInCache(rR.convertTo(), it is HResult.Success)
			running = false
		}
	}

	override suspend fun loadAppUpdate(): HResult<AppUpdateEntity> =
		iFileAppUpdateDataSource.loadCachedAppUpdate()

	override suspend fun downloadAppUpdate(appUpdateEntity: AppUpdateEntity): HResult<String> =
		iRemoteAppUpdateDataSource.downloadGitUpdate(appUpdateEntity).transform { response ->
			logV("Retrieved response")
			if (response.isSuccessful) {
				response.body?.let { body ->
					iFileAppUpdateDataSource.saveAPK(appUpdateEntity, body.source())
				} ?: errorResult(ErrorKeys.ERROR_NETWORK, "Empty response body")
			} else errorResult(ErrorKeys.ERROR_NETWORK, "Failed to download")
		}
}