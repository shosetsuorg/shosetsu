package app.shosetsu.android.datasource.local.file.impl

import app.shosetsu.android.common.FileNotFoundException
import app.shosetsu.android.common.FilePermissionException
import app.shosetsu.android.common.enums.ExternalFileDir.APP
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.datasource.local.file.base.IFileBackupDataSource
import app.shosetsu.android.domain.model.local.BackupEntity
import app.shosetsu.android.providers.file.base.IFileSystemProvider
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
class FileBackupDataSource(
	private val iFileSystemProvider: IFileSystemProvider
) : IFileBackupDataSource {

	init {
		try {
			iFileSystemProvider.createDirectory(APP, BACKUP_DIRECTORY)
			logV("Created directory: `$BACKUP_DIRECTORY`")
		} catch (e: Exception) {
			logE("Failed to create directory", e)
		}
	}

	@Throws(FilePermissionException::class, FileNotFoundException::class)
	override suspend fun loadBackup(
		backupName: String,
		isExternal: Boolean
	): BackupEntity {
		logI("Reading backup: $backupName")
		val result = if (!isExternal)
			iFileSystemProvider.readFile(APP, "$BACKUP_DIRECTORY/${backupName}")
		else iFileSystemProvider.readFile(backupName)

		return BackupEntity(result)
	}

	@Throws(FilePermissionException::class, IOException::class)
	override suspend fun saveBackup(backupEntity: BackupEntity): String {
		val path = "$BACKUP_DIRECTORY/${backupEntity.fileName}"
		iFileSystemProvider.writeFile(
			APP,
			path,
			backupEntity.content
		)
		return path
	}

	override suspend fun loadBackups(): List<String> =
		iFileSystemProvider.listFiles(APP, BACKUP_DIRECTORY)

	companion object {
		private const val BACKUP_DIRECTORY = "Backups"
	}
}