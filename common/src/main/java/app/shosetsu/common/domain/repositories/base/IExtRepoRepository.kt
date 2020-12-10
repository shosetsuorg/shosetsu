package app.shosetsu.common.domain.repositories.base

import app.shosetsu.common.com.dto.HResult
import app.shosetsu.common.domain.model.local.RepositoryEntity
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
interface IExtRepoRepository {
	/** Loads repository data */
    suspend fun loadRepoData(repositoryEntity: RepositoryEntity): HResult<RepoIndex>

	/** Loads all repositories present */
	suspend fun loadRepositories(): HResult<List<RepositoryEntity>>

	/** Loads all repositories present */
	suspend fun loadRepositoriesLive(): Flow<HResult<List<RepositoryEntity>>>
}