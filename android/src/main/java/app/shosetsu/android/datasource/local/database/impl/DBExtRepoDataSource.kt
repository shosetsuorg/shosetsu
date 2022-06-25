package app.shosetsu.android.datasource.local.database.impl

import app.shosetsu.android.datasource.local.database.base.IDBExtRepoDataSource
import app.shosetsu.android.domain.model.database.DBRepositoryEntity
import app.shosetsu.android.domain.model.local.RepositoryEntity
import app.shosetsu.android.dto.convertList
import app.shosetsu.android.providers.database.dao.RepositoryDao
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
 * shosetsu
 * 12 / 05 / 2020
 */
class DBExtRepoDataSource(
	private val repositoryDao: RepositoryDao,
) : IDBExtRepoDataSource {
	override fun loadRepositoriesLive(): Flow<List<RepositoryEntity>> =
		repositoryDao.loadRepositoriesLive().map { it.convertList() }

	override suspend fun loadRepositories(): List<RepositoryEntity> =
		(repositoryDao.loadRepositories().convertList())

	override suspend fun loadRepository(repoID: Int): RepositoryEntity? =
		repositoryDao.loadRepositoryFromID(repoID)?.convertTo()

	override suspend fun addRepository(url: String, name: String): Long =
		(repositoryDao.insertAbort(DBRepositoryEntity(null, url, name, true)))

	override suspend fun remove(entity: RepositoryEntity): Unit =
		(repositoryDao.delete(entity.toDB()))

	override suspend fun update(entity: RepositoryEntity): Unit =
		(repositoryDao.update(entity.toDB()))

	override suspend fun insert(entity: RepositoryEntity): Long =
		(repositoryDao.insertReplace(entity.toDB()))

	fun RepositoryEntity.toDB() = DBRepositoryEntity(id, url, name, isEnabled)

	fun List<RepositoryEntity>.toDB() = map { it.toDB() }
}