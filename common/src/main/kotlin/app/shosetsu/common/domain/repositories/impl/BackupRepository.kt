package app.shosetsu.common.domain.repositories.impl

import app.shosetsu.common.datasource.file.base.IFileBackupDataSource
import app.shosetsu.common.domain.model.local.BackupEntity
import app.shosetsu.common.domain.repositories.base.IBackupRepository
import app.shosetsu.common.dto.HResult

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
class BackupRepository(
	private val iFileBackupDataSource: IFileBackupDataSource
) : IBackupRepository {
	override suspend fun loadBackups(): HResult<List<String>> =
		iFileBackupDataSource.loadBackups()

	override suspend fun loadBackup(path: String, isExternal: Boolean): HResult<BackupEntity> =
		iFileBackupDataSource.loadBackup(path, isExternal)

	override suspend fun saveBackup(backupEntity: BackupEntity): HResult<*> =
		iFileBackupDataSource.saveBackup(backupEntity)
}