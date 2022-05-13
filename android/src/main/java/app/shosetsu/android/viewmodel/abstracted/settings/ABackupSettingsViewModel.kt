package app.shosetsu.android.viewmodel.abstracted.settings

import android.net.Uri
import app.shosetsu.android.domain.repository.base.ISettingsRepository
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
 * 31 / 08 / 2020
 */
abstract class ABackupSettingsViewModel(iSettingsRepository: ISettingsRepository) :
	ASubSettingsViewModel(iSettingsRepository) {

	/** Order the app to create a new backup now */
	abstract fun startBackup()
	abstract fun loadInternalOptions(): Flow<List<String>>

	/**
	 * Load backup via a path
	 *
	 * For internal backups
	 */
	abstract fun restore(path: String)

	/**
	 * Load backup via the uri
	 *
	 * For external backups
	 */
	abstract fun restore(uri: Uri)

	/**
	 * The view model will hold the backup to export
	 */
	abstract fun holdBackupToExport(backupToExport: String)

	/**
	 * @return the backup file to export
	 */
	abstract fun getBackupToExport(): String?

	/**
	 * Cancel export process
	 */
	abstract fun clearExport()

	abstract fun exportBackup(uri: Uri)
}