package app.shosetsu.android.domain.repository.base

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.FileNotFoundException
import app.shosetsu.android.common.FilePermissionException
import app.shosetsu.android.domain.model.local.ExtLibEntity
import app.shosetsu.lib.exceptions.HTTPException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

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
 * 01 / 05 / 2020
 */
interface IExtensionLibrariesRepository {

	/**
	 * Loads extension libraries by its repository
	 *
	 * @return entity from repo
	 */
	@Throws(SQLiteException::class)
	suspend fun loadExtLibByRepo(repoID: Int): List<ExtLibEntity>

	/**
	 * Installs an extension library by its repository
	 */
	@Throws(
		SQLiteException::class,
		HTTPException::class,
		SocketTimeoutException::class,
		UnknownHostException::class,
	)
	suspend fun installExtLibrary(
		repoURL: String,
		extLibEntity: ExtLibEntity,
	)

	/**
	 * @param name Name of the library requested
	 * @return Library ext content
	 */
	@Throws(FileNotFoundException::class, FilePermissionException::class)
	suspend fun loadExtLibrary(name: String): String
}