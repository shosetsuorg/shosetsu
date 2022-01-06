package app.shosetsu.common.domain.repositories.impl

import app.shosetsu.common.datasource.file.base.IFileBackupDataSource
import app.shosetsu.common.domain.model.local.BackupEntity
import app.shosetsu.common.domain.repositories.base.IBackupRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.empty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

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
	private val _backupProgress: MutableStateFlow<HResult<Unit>> by lazy { MutableStateFlow(empty) }

	override val backupProgress: Flow<HResult<Unit>>
		get() = _backupProgress

	override fun updateProgress(result: HResult<Unit>) {
		_backupProgress.tryEmit(result)
	}

	override suspend fun loadBackups(): HResult<List<String>> =
		iFileBackupDataSource.loadBackups()

	override suspend fun loadBackup(path: String, isExternal: Boolean): HResult<BackupEntity> =
		iFileBackupDataSource.loadBackup(path, isExternal)

	override suspend fun saveBackup(backupEntity: BackupEntity): HResult<String> =
		iFileBackupDataSource.saveBackup(backupEntity)
}