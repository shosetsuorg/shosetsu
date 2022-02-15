package app.shosetsu.common.domain.repositories.base

import app.shosetsu.common.GenericSQLiteException
import app.shosetsu.common.domain.model.local.ExtLibEntity

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
	@Throws(GenericSQLiteException::class)
	suspend fun loadExtLibByRepo(repoID: Int): List<ExtLibEntity>

	/**
	 * Installs an extension library by its repository
	 */
	suspend fun installExtLibrary(
		repoURL: String,
		extLibEntity: ExtLibEntity,
	)

	/**
	 * @param name Name of the library requested
	 * @return Library ext content
	 */
	fun blockingLoadExtLibrary(name: String): String
}