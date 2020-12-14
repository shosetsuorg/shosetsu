package app.shosetsu.android.datasource.database.impl

import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.providers.database.dao.RepositoryDao
import app.shosetsu.common.datasource.database.base.ILocalExtRepoDataSource
import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.convertList
import app.shosetsu.common.dto.mapLatestListTo
import app.shosetsu.common.dto.mapLatestToSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

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
class LocalExtRepoDataSource(
	private val repositoryDao: RepositoryDao,
) : ILocalExtRepoDataSource {
	override fun loadRepositoriesLive(): Flow<HResult<List<RepositoryEntity>>> = flow {
		try {
			emitAll(repositoryDao.loadRepositoriesLive().mapLatestListTo().mapLatestToSuccess())
		} catch (e: Exception) {
			emit(e.toHError())
		}
	}

	override fun loadRepositories(): HResult<List<RepositoryEntity>> = try {
		app.shosetsu.common.dto.successResult(repositoryDao.loadRepositories().convertList())
	} catch (e: Exception) {
		e.toHError()
	}

	override fun loadRepository(repoID: Int): HResult<RepositoryEntity> = try {
		app.shosetsu.common.dto.successResult(
			repositoryDao.loadRepositoryFromID(repoID).convertTo()
		)
	} catch (e: Exception) {
		e.toHError()
	}
}