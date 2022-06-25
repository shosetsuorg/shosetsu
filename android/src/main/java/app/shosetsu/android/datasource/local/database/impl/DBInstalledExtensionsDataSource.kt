package app.shosetsu.android.datasource.local.database.impl

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.ext.toDB
import app.shosetsu.android.datasource.local.database.base.IDBInstalledExtensionsDataSource
import app.shosetsu.android.domain.model.local.InstalledExtensionEntity
import app.shosetsu.android.dto.convertList
import app.shosetsu.android.providers.database.dao.InstalledExtensionsDao
import kotlinx.coroutines.flow.Flow
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
class DBInstalledExtensionsDataSource(
	private val extensionsDao: InstalledExtensionsDao,
) : IDBInstalledExtensionsDataSource {
	override fun loadExtensionsFlow(): Flow<List<InstalledExtensionEntity>> =
		extensionsDao.loadExtensionsFlow().map { it.convertList() }

	override fun loadExtensionLive(formatterID: Int): Flow<InstalledExtensionEntity?> =
		extensionsDao.getExtensionFlow(formatterID).map { it?.convertTo() }

	@Throws(SQLiteException::class)
	override suspend fun updateExtension(extensionEntity: InstalledExtensionEntity): Unit =
		extensionsDao.update(extensionEntity.toDB())

	@Throws(SQLiteException::class)
	override suspend fun deleteExtension(extensionEntity: InstalledExtensionEntity): Unit =
		extensionsDao.delete(extensionEntity.toDB())

	@Throws(SQLiteException::class)
	override suspend fun loadExtension(formatterID: Int): InstalledExtensionEntity? =
		extensionsDao.getExtension(formatterID)?.convertTo()

	override suspend fun getExtensions(repoID: Int): List<InstalledExtensionEntity> =
		extensionsDao.getExtensions(repoID).convertList()

	@Throws(SQLiteException::class)
	override suspend fun loadExtensions(): List<InstalledExtensionEntity> =
		extensionsDao.loadExtensions().convertList()

	@Throws(SQLiteException::class)
	override suspend fun insert(extensionEntity: InstalledExtensionEntity): Long =
		extensionsDao.insertAbort(extensionEntity.toDB())
}