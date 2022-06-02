package app.shosetsu.android.datasource.local.file.base

import app.shosetsu.android.common.FileNotFoundException
import app.shosetsu.android.common.FilePermissionException
import app.shosetsu.android.domain.model.local.BackupEntity
import java.io.IOException

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * 18 / 01 / 2021
 */
interface IFileBackupDataSource {

	@Throws(FilePermissionException::class, FileNotFoundException::class)
	suspend fun loadBackup(backupName: String, isExternal: Boolean): BackupEntity

	@Throws(FilePermissionException::class, IOException::class)
	suspend fun saveBackup(backupEntity: BackupEntity): String

	suspend fun loadBackups(): List<String>
}