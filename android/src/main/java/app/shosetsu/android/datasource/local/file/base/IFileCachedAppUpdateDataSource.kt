package app.shosetsu.android.datasource.local.file.base

import app.shosetsu.common.domain.model.local.AppUpdateEntity
import app.shosetsu.common.dto.HResult
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
interface IFileCachedAppUpdateDataSource {
	/**
	 * Live data of the current update
	 */
	val updateAvaLive: Flow<HResult<AppUpdateEntity>>

	/**
	 * Accessor method to read the current cached update
	 */
	suspend fun loadCachedAppUpdate(): HResult<AppUpdateEntity>

	/** Puts an update into cache */
	suspend fun putAppUpdateInCache(appUpdate: AppUpdateEntity, isUpdate: Boolean): HResult<*>

	/**
	 * Saves the APK bytes to the filesystem
	 *
	 * @return the path to the APK
	 */
	fun saveAPK(appUpdate: AppUpdateEntity, bytes: ByteArray): HResult<String>
}