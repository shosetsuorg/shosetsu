package app.shosetsu.android.domain.repository.impl

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.ext.onIO
import app.shosetsu.android.datasource.local.database.base.IDBExtRepoDataSource
import app.shosetsu.android.datasource.remote.base.IRemoteExtRepoDataSource
import app.shosetsu.android.domain.model.local.RepositoryEntity
import app.shosetsu.android.domain.repository.base.IExtensionRepoRepository
import app.shosetsu.lib.exceptions.HTTPException
import app.shosetsu.lib.json.RepoIndex
import kotlinx.coroutines.flow.Flow
import java.io.IOException

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
	@Throws(
		HTTPException::class,
		IOException::class,
	)
	override suspend fun getRepoData(entity: RepositoryEntity): RepoIndex =
		onIO { remoteSource.downloadRepoData(entity) }

	@Throws(SQLiteException::class)
	override suspend fun loadRepositories(): List<RepositoryEntity> =
		onIO { databaseSource.loadRepositories() }

	/**
	 * TODO Create a direct to database call that cuts out the kotlin filtering
	 */
	@Throws(SQLiteException::class)
	override suspend fun loadEnabledRepos(): List<RepositoryEntity> =
		onIO { loadRepositories().filter { it.isEnabled } }

	override fun loadRepositoriesLive(): Flow<List<RepositoryEntity>> =
		databaseSource.loadRepositoriesLive().onIO()

	@Throws(SQLiteException::class)
	override suspend fun addRepository(url: String, name: String): Long =
		onIO { databaseSource.addRepository(url, name) }

	@Throws(SQLiteException::class)
	override suspend fun remove(entity: RepositoryEntity): Unit =
		onIO { databaseSource.remove(entity) }

	@Throws(SQLiteException::class)
	override suspend fun update(entity: RepositoryEntity): Unit =
		onIO { databaseSource.update(entity) }

	@Throws(SQLiteException::class)
	override suspend fun insert(entity: RepositoryEntity): Long =
		onIO { databaseSource.insert(entity) }

	@Throws(SQLiteException::class)
	override suspend fun getRepo(id: Int): RepositoryEntity? =
		onIO { databaseSource.loadRepository(id) }
}