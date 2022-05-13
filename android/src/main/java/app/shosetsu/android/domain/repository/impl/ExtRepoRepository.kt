package app.shosetsu.android.domain.repository.impl

import app.shosetsu.android.common.GenericSQLiteException
import app.shosetsu.android.datasource.local.database.base.IDBExtRepoDataSource
import app.shosetsu.android.datasource.remote.base.IRemoteExtRepoDataSource
import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.android.domain.repository.base.IExtensionRepoRepository
import app.shosetsu.lib.json.RepoIndex
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
 * 12 / 05 / 2020
 */
class ExtRepoRepository(
	private val databaseSource: IDBExtRepoDataSource,
	private val remoteSource: IRemoteExtRepoDataSource
) : IExtensionRepoRepository {
	override suspend fun getRepoData(entity: RepositoryEntity): RepoIndex =
		remoteSource.downloadRepoData(entity)

	@Throws(GenericSQLiteException::class)
	override suspend fun loadRepositories(): List<RepositoryEntity> =
		databaseSource.loadRepositories()

	/**
	 * TODO Create a direct to database call that cuts out the kotlin filtering
	 */
	@Throws(GenericSQLiteException::class)
	override suspend fun loadEnabledRepos(): List<RepositoryEntity> =
		loadRepositories().filter { it.isEnabled }

	override fun loadRepositoriesLive(): Flow<List<RepositoryEntity>> =
		databaseSource.loadRepositoriesLive()

	@Throws(GenericSQLiteException::class)
	override suspend fun addRepository(url: String, name: String): Long =
		databaseSource.addRepository(url, name)

	@Throws(GenericSQLiteException::class)
	override suspend fun remove(entity: RepositoryEntity): Unit =
		databaseSource.remove(entity)

	@Throws(GenericSQLiteException::class)
	override suspend fun update(entity: RepositoryEntity): Unit =
		databaseSource.update(entity)

	@Throws(GenericSQLiteException::class)
	override suspend fun insert(entity: RepositoryEntity): Long =
		databaseSource.insert(entity)

	@Throws(GenericSQLiteException::class)
	override suspend fun getRepo(id: Int): RepositoryEntity? =
		databaseSource.loadRepository(id)
}