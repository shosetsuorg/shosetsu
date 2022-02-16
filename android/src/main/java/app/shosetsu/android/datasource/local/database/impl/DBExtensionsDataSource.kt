package app.shosetsu.android.datasource.local.database.impl

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.ext.toDB
import app.shosetsu.android.providers.database.dao.InstalledExtensionsDao
import app.shosetsu.common.GenericSQLiteException
import app.shosetsu.common.datasource.database.base.IDBInstalledExtensionsDataSource
import app.shosetsu.common.domain.model.local.GenericExtensionEntity
import app.shosetsu.common.dto.convertList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

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
 * shosetsu
 * 12 / May / 2020
 */
class DBExtensionsDataSource(
	private val extensionsDao: InstalledExtensionsDao,
) : IDBInstalledExtensionsDataSource {
	override fun loadExtensionsFlow(): Flow<List<GenericExtensionEntity>> = flow {
		emitAll(extensionsDao.loadExtensionsFlow().map { it.convertList() })
	}

	override fun loadExtensionLive(formatterID: Int): Flow<GenericExtensionEntity> = flow {
		emitAll(extensionsDao.getExtensionFlow(formatterID).map { it.convertTo() })
	}

	override suspend fun updateExtension(extensionEntity: GenericExtensionEntity): Unit = try {
		extensionsDao.update(extensionEntity.toDB())
	} catch (e: SQLiteException) {
		throw GenericSQLiteException(e)
	}

	override suspend fun deleteExtension(extensionEntity: GenericExtensionEntity): Unit = try {
		extensionsDao.delete(extensionEntity.toDB())
	} catch (e: SQLiteException) {
		throw GenericSQLiteException(e)
	}

	override suspend fun loadExtension(formatterID: Int): GenericExtensionEntity? = try {
		extensionsDao.getExtension(formatterID)?.convertTo()
	} catch (e: SQLiteException) {
		throw GenericSQLiteException(e)
	}

	override suspend fun getExtensions(repoID: Int): List<GenericExtensionEntity> =
		extensionsDao.getExtensions(repoID).convertList()

	override suspend fun loadExtensions(): List<GenericExtensionEntity> = try {
		extensionsDao.loadExtensions().convertList()
	} catch (e: SQLiteException) {
		throw GenericSQLiteException(e)
	}

	@Throws(GenericSQLiteException::class)
	override suspend fun insert(extensionEntity: GenericExtensionEntity): Long = try {
		extensionsDao.insertAbort(extensionEntity.toDB())
	} catch (e: SQLiteException) {
		throw GenericSQLiteException(e)
	}
}