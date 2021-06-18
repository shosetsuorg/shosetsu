package app.shosetsu.common.domain.repositories.base

import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.common.dto.HResult
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
 * 30 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
interface IExtensionRepoRepository {
	/**
	 * Loads repository data
	 *
	 * @return
	 * [HResult.Success] Loaded
	 *
	 * [HResult.Error] Error Loading
	 *
	 * [HResult.Empty] No data found
	 *
	 * [HResult.Loading] never
	 */
	suspend fun getRepoData(entity: RepositoryEntity): HResult<RepoIndex>

	/**
	 * Loads all repositories present
	 *
	 * @return
	 * [HResult.Success] Loaded entities
	 *
	 * [HResult.Error] Something went wrong loading entities
	 *
	 * [HResult.Empty] Should never happen
	 *
	 * [HResult.Loading] never
	 */
	suspend fun loadRepositories(): HResult<List<RepositoryEntity>>


	/**
	 * Identical to [loadRepositories] except filters out all [RepositoryEntity]
	 *  where [RepositoryEntity.isEnabled]=false
	 * @see loadRepositories
	 */
	suspend fun loadEnabledRepos(): HResult<List<RepositoryEntity>>

	/**
	 * Loads all repositories present
	 *
	 * @return
	 * [HResult.Success] Successfully loaded entities
	 *
	 * [HResult.Error] Something went wrong loading
	 *
	 * [HResult.Empty] Should never happen
	 *
	 * [HResult.Loading] Initial value
	 */
	fun loadRepositoriesLive(): Flow<HResult<List<RepositoryEntity>>>


	suspend fun addRepository(entity: RepositoryEntity): HResult<*>

	suspend fun remove(entity: RepositoryEntity): HResult<*>

	suspend fun update(entity: RepositoryEntity): HResult<*>

	suspend fun insert(entity: RepositoryEntity): HResult<*>
}