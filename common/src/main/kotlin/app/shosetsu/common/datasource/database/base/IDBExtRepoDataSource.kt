package app.shosetsu.common.datasource.database.base

import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.common.dto.HResult
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
 * 04 / 05 / 2020
 */
interface IDBExtRepoDataSource {
	/** Loads LiveData of the repositories */
	fun loadRepositoriesLive(): Flow<HResult<List<RepositoryEntity>>>

	/** Loads a list of the repositories */
	suspend fun loadRepositories(): HResult<List<RepositoryEntity>>

	/** Loads a [RepositoryEntity] by its [repoID] */
	suspend fun loadRepository(repoID: Int): HResult<RepositoryEntity>

	suspend fun addRepository(repositoryEntity: RepositoryEntity): HResult<*>

	suspend fun remove(entity: RepositoryEntity): HResult<*>
	suspend fun update(entity: RepositoryEntity): HResult<*>
	suspend fun insert(entity: RepositoryEntity):HResult<*>
}