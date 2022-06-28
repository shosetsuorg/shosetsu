package app.shosetsu.android.domain.repository.base

import android.database.sqlite.SQLiteException
import app.shosetsu.android.domain.model.local.RepositoryEntity
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
 * 30 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
interface IExtensionRepoRepository {
	/**
	 * Loads repository data
	 */
	@Throws(
		HTTPException::class,
		IOException::class,
	)
	suspend fun getRepoData(entity: RepositoryEntity): RepoIndex

	/**
	 * Loads all repositories present
	 */
	@Throws(SQLiteException::class)
	suspend fun loadRepositories(): List<RepositoryEntity>


	/**
	 * Identical to [loadRepositories] except filters out all [RepositoryEntity]
	 *  where [RepositoryEntity.isEnabled]=false
	 * @see loadRepositories
	 */
	@Throws(SQLiteException::class)
	suspend fun loadEnabledRepos(): List<RepositoryEntity>

	/**
	 * Loads all repositories present
	 */
	fun loadRepositoriesLive(): Flow<List<RepositoryEntity>>

	@Throws(SQLiteException::class)
	suspend fun addRepository(url: String, name: String): Long

	@Throws(SQLiteException::class)
	suspend fun remove(entity: RepositoryEntity)

	@Throws(SQLiteException::class)
	suspend fun update(entity: RepositoryEntity)

	@Throws(SQLiteException::class)
	suspend fun insert(entity: RepositoryEntity): Long

	@Throws(SQLiteException::class)
	suspend fun getRepo(id: Int): RepositoryEntity?
}