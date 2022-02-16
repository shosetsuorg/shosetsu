package app.shosetsu.common.domain.repositories.base

import app.shosetsu.common.domain.model.local.AppUpdateEntity
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
 * 06 / 09 / 2020
 *
 * Source of truth for all app updates
 */
interface IAppUpdatesRepository {
	/**
	 * Flow of app updates
	 */
	fun loadAppUpdateFlow(): Flow<AppUpdateEntity>

	/**
	 * Load an app update if present
	 */
	suspend fun loadRemoteUpdate(): AppUpdateEntity?

	/**
	 * Load an app update if present
	 */
	suspend fun loadAppUpdate(): AppUpdateEntity?

	/**
	 * Can the app self update itself
	 */
	fun canSelfUpdate(): Boolean

	/**
	 * Downloads the app update specified by [appUpdateEntity]
	 *
	 * @return Path of the apk file, this is messy but it must be done so the intent can work
	 */
	suspend fun downloadAppUpdate(appUpdateEntity: AppUpdateEntity): String
}