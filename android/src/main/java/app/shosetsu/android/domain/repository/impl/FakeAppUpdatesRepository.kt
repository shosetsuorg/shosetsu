package app.shosetsu.android.domain.repository.impl

import app.shosetsu.android.datasource.local.file.base.IFileCachedAppUpdateDataSource
import app.shosetsu.android.datasource.remote.base.IRemoteAppUpdateDataSource
import app.shosetsu.common.consts.ErrorKeys
import app.shosetsu.common.domain.model.local.AppUpdateEntity
import app.shosetsu.common.domain.repositories.base.IAppUpdatesRepository
import app.shosetsu.common.dto.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

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
class FakeAppUpdatesRepository(
	private val iFileAppUpdateDataSource: IFileCachedAppUpdateDataSource,
	private val iRemoteAppUpdateDataSource: IRemoteAppUpdateDataSource,
) : IAppUpdatesRepository {
	private val _appUpdateFlow = MutableStateFlow<HResult<AppUpdateEntity>>(empty)

	override fun loadAppUpdateFlow(): Flow<HResult<AppUpdateEntity>> =
		_appUpdateFlow

	override suspend fun loadRemoteUpdate(): HResult<AppUpdateEntity> =
		loadAppUpdate()

	override suspend fun loadAppUpdate(): HResult<AppUpdateEntity> =
		successResult(
			AppUpdateEntity(
				"v3.0.0",
				999,
				"https://github.com/shosetsuorg/shosetsu-preview/releases/download/r1136/shosetsu-r1136.apk",
				listOf("This is a fake update")
			)
		).also {
			_appUpdateFlow.emit(loading)
			_appUpdateFlow.emit(it)
		}

	override fun canSelfUpdate(): HResult<Boolean> =
		successResult(true)

	override suspend fun downloadAppUpdate(appUpdateEntity: AppUpdateEntity): HResult<String> =
		if (iRemoteAppUpdateDataSource is IRemoteAppUpdateDataSource.Downloadable)
			iRemoteAppUpdateDataSource.downloadAppUpdate(appUpdateEntity).transform { response ->
				iFileAppUpdateDataSource.saveAPK(appUpdateEntity, response).also {
					// Call GC to clean up the chunky objects
					System.gc()
				}
			}
		else errorResult(ErrorKeys.ERROR_INVALID_FEATURE, "This flavor cannot self update")
}