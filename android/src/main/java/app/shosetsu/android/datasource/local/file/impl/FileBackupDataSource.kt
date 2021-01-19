package app.shosetsu.android.datasource.local.file.impl

import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.common.datasource.file.base.IFileBackupDataSource
import app.shosetsu.common.domain.model.local.BackupEntity
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.dto.transform
import app.shosetsu.common.enums.ExternalFileDir.APP
import app.shosetsu.common.providers.file.base.IFileSystemProvider
import java.text.SimpleDateFormat
import java.util.*

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
	private val currentDate
		get() = SimpleDateFormat("yyyy-MM-dd-hh-mm-ss", Locale.ROOT).format(Date())

	init {
		launchIO {
			iFileSystemProvider.createExternalDirectory(APP, BACKUP_DIRECTORY)
		}
	}

	override suspend fun loadBackup(backupName: String): HResult<BackupEntity> =
		iFileSystemProvider.readExternalFile(APP, "$BACKUP_DIRECTORY/${backupName}.sbk")
			.transform {
				successResult(BackupEntity(it))
			}

	override suspend fun saveBackup(backupEntity: BackupEntity): HResult<*> =
		iFileSystemProvider.writeExternalFile(
			APP,
			"$BACKUP_DIRECTORY/shosetsu-backup-${currentDate}.sbk",
			backupEntity.content
		)

	override suspend fun loadBackups(): HResult<List<String>> =
		iFileSystemProvider.listExternalFiles(APP, BACKUP_DIRECTORY)

	companion object {
		private const val BACKUP_DIRECTORY = "Backups"
	}
}