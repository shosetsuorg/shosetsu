package app.shosetsu.android.domain.repository.impl

import app.shosetsu.android.common.FileNotFoundException
import app.shosetsu.android.common.FilePermissionException
import app.shosetsu.android.common.ext.onIO
import app.shosetsu.android.datasource.file.base.IFileBackupDataSource
import app.shosetsu.android.domain.model.local.BackupEntity
import app.shosetsu.android.domain.repository.base.IBackupRepository
import app.shosetsu.android.domain.repository.base.IBackupRepository.BackupProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
class BackupRepository(
	private val iFileBackupDataSource: IFileBackupDataSource
) : IBackupRepository {
	private val _backupProgress: MutableStateFlow<BackupProgress> by lazy {
		MutableStateFlow(
			BackupProgress.NOT_STARTED
		)
	}

	override val backupProgress: Flow<BackupProgress>
		get() = _backupProgress.onIO()

	override fun updateProgress(result: BackupProgress) {
		_backupProgress.tryEmit(result)
	}

	override suspend fun loadBackups(): List<String> =
		onIO { iFileBackupDataSource.loadBackups() }

	@Throws(FilePermissionException::class, FileNotFoundException::class)
	override suspend fun loadBackup(path: String, isExternal: Boolean): BackupEntity =
		onIO { iFileBackupDataSource.loadBackup(path, isExternal) }

	@Throws(FilePermissionException::class, IOException::class)
	override suspend fun saveBackup(backupEntity: BackupEntity): String =
		onIO { iFileBackupDataSource.saveBackup(backupEntity) }
}