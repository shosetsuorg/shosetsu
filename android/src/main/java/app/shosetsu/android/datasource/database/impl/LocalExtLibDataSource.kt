package app.shosetsu.android.datasource.database.impl

import app.shosetsu.android.common.ext.toDB
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.providers.database.dao.ExtensionLibraryDao
import app.shosetsu.common.datasource.database.base.ILocalExtLibDataSource
import app.shosetsu.common.domain.model.local.ExtLibEntity
import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.convertList
import app.shosetsu.common.dto.successResult

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
 * 12 / 05 / 2020
 */
class LocalExtLibDataSource(
	private val extensionLibraryDao: ExtensionLibraryDao,
) : ILocalExtLibDataSource {
	override suspend fun updateExtension(extLibEntity: ExtLibEntity): HResult<*> = try {
		successResult(extensionLibraryDao.suspendedUpdate(extLibEntity.toDB()))
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun updateOrInsert(extLibEntity: ExtLibEntity): HResult<*> = try {
		successResult(extensionLibraryDao.insertOrUpdateScriptLib(extLibEntity.toDB()))
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun loadExtLibByRepo(
		repositoryEntity: RepositoryEntity,
	): HResult<List<ExtLibEntity>> = try {
		successResult(extensionLibraryDao.loadLibByRepoID(repositoryEntity.id).convertList())
	} catch (e: Exception) {
		e.toHError()
	}
}