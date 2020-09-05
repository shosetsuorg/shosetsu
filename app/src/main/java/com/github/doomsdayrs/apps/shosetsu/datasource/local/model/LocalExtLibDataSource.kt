package com.github.doomsdayrs.apps.shosetsu.datasource.local.model

import android.database.sqlite.SQLiteException
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.errorResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.datasource.local.base.ILocalExtLibDataSource
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ExtLibEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.RepositoryEntity
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.ExtensionLibraryDao

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
		successResult(extensionLibraryDao.suspendedUpdate(extLibEntity))
	} catch (e: SQLiteException) {
		errorResult(e)
	}

	override suspend fun updateOrInsert(extLibEntity: ExtLibEntity): HResult<*> = try {
		successResult(extensionLibraryDao.insertOrUpdateScriptLib(extLibEntity))
	} catch (e: SQLiteException) {
		errorResult(e)
	}

	override suspend fun loadExtLibByRepo(
			repositoryEntity: RepositoryEntity,
	): HResult<List<ExtLibEntity>> = try {
		successResult(extensionLibraryDao.loadLibByRepoID(repositoryEntity.id))
	} catch (e: SQLiteException) {
		errorResult(e)
	}
}