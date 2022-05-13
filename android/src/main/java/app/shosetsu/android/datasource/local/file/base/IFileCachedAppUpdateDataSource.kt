package app.shosetsu.android.datasource.local.file.base

import app.shosetsu.android.common.FileNotFoundException
import app.shosetsu.android.common.FilePermissionException
import app.shosetsu.android.domain.model.local.AppUpdateEntity
import app.shosetsu.common.domain.model.local.AppUpdateEntity
import kotlinx.coroutines.flow.Flow
import java.io.IOException

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
	val updateAvaLive: Flow<AppUpdateEntity?>

	/**
	 * Accessor method to read the current cached update
	 */
	@Throws(FileNotFoundException::class, FilePermissionException::class)
	suspend fun loadCachedAppUpdate(): AppUpdateEntity

	/** Puts an update into cache */
	@Throws(FilePermissionException::class, IOException::class)
	suspend fun putAppUpdateInCache(appUpdate: AppUpdateEntity, isUpdate: Boolean)

	/**
	 * Saves the APK bytes to the filesystem
	 *
	 * @return the path to the APK
	 */
	@Throws(IOException::class, FilePermissionException::class, FileNotFoundException::class)
	fun saveAPK(appUpdate: AppUpdateEntity, bytes: ByteArray): String
}