package app.shosetsu.common.domain.repositories.base

import app.shosetsu.common.domain.model.local.BackupEntity
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
 * 17 / 01 / 2021
 *
 * Planned backup repository, handles saving and loading backups
 */
interface IBackupRepository {

	/**
	 * Reads the backup directory
	 *
	 * @return a list of filenames to select from
	 */
	suspend fun loadBackups(): HResult<List<String>>


	/**
	 * Loads a backup via its name
	 * @param path File name / Direct Path of a backup
	 * @param isExternal, if true then [path] is a direct path
	 *
	 * @return
	 * [HResult.Success] Backup entity
	 * [HResult.Empty] Such does not exist
	 * [HResult.Error] An exception occurred when loading
	 * [HResult.Loading] never
	 */
	suspend fun loadBackup(path: String, isExternal: Boolean): HResult<BackupEntity>


	suspend fun saveBackup(backupEntity: BackupEntity): HResult<*>
}