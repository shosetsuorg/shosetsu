package app.shosetsu.android.datasource.local.database.impl

import android.database.sqlite.SQLiteException
import app.shosetsu.android.datasource.local.database.base.IDBExtRepoDataSource
import app.shosetsu.android.domain.model.database.DBRepositoryEntity
import app.shosetsu.android.domain.model.local.RepositoryEntity
import app.shosetsu.android.dto.convertList
import app.shosetsu.android.providers.database.dao.RepositoryDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
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
	override fun loadRepositoriesLive(): Flow<List<RepositoryEntity>> = flow {
		try {
			emitAll(repositoryDao.loadRepositoriesLive().map { it.convertList() })
		} catch (e: SQLiteException) {
			throw e
		}
	}

	override suspend fun loadRepositories(): List<RepositoryEntity> = try {
		(repositoryDao.loadRepositories().convertList())
	} catch (e: SQLiteException) {
		throw e
	}

	override suspend fun loadRepository(repoID: Int): RepositoryEntity? = try {
		repositoryDao.loadRepositoryFromID(repoID)?.let { it.convertTo() }
	} catch (e: SQLiteException) {
		throw e
	}

	override suspend fun addRepository(url: String, name: String): Long =
		try {
			(repositoryDao.insertAbort(DBRepositoryEntity(null, url, name, true)))
		} catch (e: SQLiteException) {
			throw e
		}

	override suspend fun remove(entity: RepositoryEntity): Unit = try {
		(repositoryDao.delete(entity.toDB()))
	} catch (e: SQLiteException) {
		throw e
	}

	override suspend fun update(entity: RepositoryEntity): Unit = try {
		(repositoryDao.update(entity.toDB()))
	} catch (e: SQLiteException) {
		throw e
	}

	override suspend fun insert(entity: RepositoryEntity): Long = try {
		(repositoryDao.insertReplace(entity.toDB()))
	} catch (e: SQLiteException) {
		throw e
	}

	fun RepositoryEntity.toDB() = DBRepositoryEntity(id, url, name, isEnabled)

	fun List<RepositoryEntity>.toDB() = map { it.toDB() }
}