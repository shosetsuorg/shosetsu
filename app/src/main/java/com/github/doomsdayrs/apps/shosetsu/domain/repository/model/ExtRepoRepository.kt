package com.github.doomsdayrs.apps.shosetsu.domain.repository.model

import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.datasource.local.base.ILocalExtRepoDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.remote.base.IRemoteExtRepoDataSource
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.RepositoryEntity
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IExtRepoRepository
import org.json.JSONObject

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
		private val databaseSource: ILocalExtRepoDataSource,
		private val remoteSource: IRemoteExtRepoDataSource
) : IExtRepoRepository {
	override suspend fun loadRepoDataJSON(repositoryEntity: RepositoryEntity): HResult<JSONObject> =
			remoteSource.downloadRepoData(repositoryEntity)

	override suspend fun loadRepositories(): HResult<List<RepositoryEntity>> =
			databaseSource.loadRepositories()
}