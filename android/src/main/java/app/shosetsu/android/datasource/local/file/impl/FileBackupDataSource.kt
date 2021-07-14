package app.shosetsu.android.datasource.local.file.impl

import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.common.datasource.file.base.IFileBackupDataSource
import app.shosetsu.common.domain.model.local.BackupEntity
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.transformToSuccess
import app.shosetsu.common.enums.ExternalFileDir.APP
import app.shosetsu.common.providers.file.base.IFileSystemProvider

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
class FileBackupDataSource(
	private val iFileSystemProvider: IFileSystemProvider
) : IFileBackupDataSource {

	init {
		launchIO {
			iFileSystemProvider.createDirectory(APP, BACKUP_DIRECTORY)
		}
	}

	override suspend fun loadBackup(
		backupName: String,
		isExternal: Boolean
	): HResult<BackupEntity> {
		val result = if (!isExternal)
			iFileSystemProvider.readFile(APP, "$BACKUP_DIRECTORY/${backupName}")
		else iFileSystemProvider.readFile(backupName)

		return result.transformToSuccess { BackupEntity(it) }
	}

	override suspend fun saveBackup(backupEntity: BackupEntity): HResult<*> =
		iFileSystemProvider.writeFile(
			APP,
			"$BACKUP_DIRECTORY/${backupEntity.fileName}",
			backupEntity.content
		)

	override suspend fun loadBackups(): HResult<List<String>> =
		iFileSystemProvider.listFiles(APP, BACKUP_DIRECTORY)

	companion object {
		private const val BACKUP_DIRECTORY = "Backups"
	}
}