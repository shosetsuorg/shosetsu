package app.shosetsu.android.datasource.local.database.impl

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.ext.toDB
import app.shosetsu.android.datasource.local.database.base.IDBExtLibDataSource
import app.shosetsu.android.domain.model.local.ExtLibEntity
import app.shosetsu.android.dto.convertList
import app.shosetsu.android.providers.database.dao.ExtensionLibraryDao

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
class DBExtLibDataSource(
	private val extensionLibraryDao: ExtensionLibraryDao,
) : IDBExtLibDataSource {
	@Throws(SQLiteException::class)
	override suspend fun updateExtension(extLibEntity: ExtLibEntity): Unit =
		(extensionLibraryDao.update(extLibEntity.toDB()))

	@Throws(SQLiteException::class)
	override suspend fun updateOrInsert(extLibEntity: ExtLibEntity): Unit =
		(extensionLibraryDao.insertOrUpdateScriptLib(extLibEntity.toDB()))

	@Throws(SQLiteException::class)
	override suspend fun loadExtLibByRepo(
		repoID: Int,
	): List<ExtLibEntity> =
		(extensionLibraryDao.loadLibByRepoID(repoID).convertList())
}