package app.shosetsu.android.datasource.local.database.impl

import app.shosetsu.android.datasource.local.database.base.IDBRepositoryExtensionsDataSource
import app.shosetsu.android.domain.model.database.DBRepositoryExtensionEntity
import app.shosetsu.android.domain.model.local.GenericExtensionEntity
import app.shosetsu.android.dto.convertList
import app.shosetsu.android.providers.database.dao.RepositoryExtensionsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
 * Shosetsu
 *
 * @since 18 / 02 / 2022
 * @author Doomsdayrs
 */
class DBRepositoryExtensionsDataSource(
	private val dao: RepositoryExtensionsDao
) : IDBRepositoryExtensionsDataSource {

	fun GenericExtensionEntity.toDB(): DBRepositoryExtensionEntity =
		DBRepositoryExtensionEntity(repoID, id, name, fileName, imageURL, lang, version, md5, type)

	override suspend fun loadExtensions(): List<GenericExtensionEntity> =
		dao.loadExtensions().map { it.convertTo() }

	override fun loadExtensionsFlow(): Flow<List<GenericExtensionEntity>> =
		dao.loadExtensionsFlow().map { it.convertList() }

	override suspend fun updateExtension(extensionEntity: GenericExtensionEntity) {
		dao.update(extensionEntity.toDB())
	}

	override suspend fun deleteExtension(extensionEntity: GenericExtensionEntity) {
		dao.delete(extensionEntity.toDB())
	}

	override suspend fun loadExtension(repoId: Int, extId: Int): GenericExtensionEntity? =
		dao.getExtension(repoId, extId)?.convertTo()

	override suspend fun getExtensions(repoID: Int): List<GenericExtensionEntity> =
		dao.getExtensions(repoID).map { it.convertTo() }

	override suspend fun insert(extensionEntity: GenericExtensionEntity): Long =
		dao.insertAbort(extensionEntity.toDB())
}