package app.shosetsu.android.datasource.local.database.impl

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.dto.errorResult
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.domain.model.database.DBRepositoryEntity
import app.shosetsu.android.providers.database.dao.RepositoryDao
import app.shosetsu.common.datasource.database.base.IDBExtRepoDataSource
import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.common.dto.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class DBExtRepoDataSource(
	private val repositoryDao: RepositoryDao,
) : IDBExtRepoDataSource {
	@ExperimentalCoroutinesApi
	override fun loadRepositoriesLive(): Flow<HResult<List<RepositoryEntity>>> = flow {
		try {
			emitAll(repositoryDao.loadRepositoriesLive().mapLatestListTo().mapLatestToSuccess())
		} catch (e: Exception) {
			emit(e.toHError())
		}
	}

	override suspend fun loadRepositories(): HResult<List<RepositoryEntity>> = try {
		successResult(repositoryDao.loadRepositories().convertList())
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun loadRepository(repoID: Int): HResult<RepositoryEntity> = try {
		successResult(
			repositoryDao.loadRepositoryFromID(repoID).convertTo()
		)
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun addRepository(repositoryEntity: RepositoryEntity): HResult<*> =
		try {
			successResult(repositoryDao.insertAbort(repositoryEntity.toDB()))
		} catch (e: SQLiteException) {
			errorResult(e)
		}

	override suspend fun remove(entity: RepositoryEntity): HResult<*> = try {
		successResult(repositoryDao.delete(entity.toDB()))
	} catch (e: SQLiteException) {
		errorResult(e)
	}

	override suspend fun update(entity: RepositoryEntity): HResult<*> = try {
		successResult(repositoryDao.update(entity.toDB()))
	} catch (e: SQLiteException) {
		errorResult(e)
	}

	override suspend fun insert(entity: RepositoryEntity): HResult<*> = try {
		successResult(repositoryDao.insertReplace(entity.toDB()))
	} catch (e: SQLiteException) {
		errorResult(e)
	}

	fun RepositoryEntity.toDB() = DBRepositoryEntity(id, url, name, isEnabled)

	fun List<RepositoryEntity>.toDB() = map { it.toDB() }
}