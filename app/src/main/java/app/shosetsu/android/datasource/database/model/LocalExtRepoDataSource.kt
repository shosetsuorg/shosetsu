package app.shosetsu.android.datasource.database.model

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.errorResult
import app.shosetsu.android.common.dto.mapLatestToSuccess
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.datasource.database.base.ILocalExtRepoDataSource
import app.shosetsu.android.domain.model.local.RepositoryEntity
import app.shosetsu.android.providers.database.dao.RepositoryDao
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
			emitAll(repositoryDao.loadRepositoriesLive().mapLatestToSuccess())
		} catch (e: SQLiteException) {
			emit(errorResult(e))
		} catch (e: NullPointerException) {
			emit(errorResult(e))
		}
	}

	override fun loadRepositories(): HResult<List<RepositoryEntity>> = try {
		successResult(repositoryDao.loadRepositories())
	} catch (e: SQLiteException) {
		errorResult(e)
	} catch (e: NullPointerException) {
		errorResult(e)
	}

	override fun loadRepository(repoID: Int): HResult<RepositoryEntity> = try {
		successResult(repositoryDao.loadRepositoryFromID(repoID))
	} catch (e: SQLiteException) {
		errorResult(e)
	} catch (e: NullPointerException) {
		errorResult(e)
	}
}