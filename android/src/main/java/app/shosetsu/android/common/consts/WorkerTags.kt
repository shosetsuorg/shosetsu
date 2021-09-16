package app.shosetsu.android.common.consts

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
 * 18 / 06 / 2020
 */
object WorkerTags {
	const val DOWNLOAD_WORK_ID: String = "shosetsu_downloads"
	const val UPDATE_WORK_ID: String = "shosetsu_updates"
	const val APP_UPDATE_WORK_ID: String = "shosetsu_app_update"

	const val BACKUP_WORK_ID: String = "shosetsu_app_backup"
	const val RESTORE_WORK_ID: String = "shosetsu_app_restore"
	const val EXPORT_BACKUP_WORK_ID: String = "shosetsu_app_export_backup"

	const val REPOSITORY_UPDATE_TAG: String = "shosetsu_repository_update"
	const val EXTENSION_INSTALL_WORK_ID: String = "shosetsu_extension_installer"

	const val APP_UPDATE_INSTALL_WORK_ID: String = "shosetsu_app_update_install"

	const val UPDATE_CYCLE_WORK_ID: String = "shosetsu_updates_cycle"
	const val APP_UPDATE_CYCLE_WORK_ID: String = "shosetsu_app_update_cycle"
	const val BACKUP_CYCLE_WORK_ID: String = "shosetsu_backup_cycle"
}