package app.shosetsu.android.providers.database.dao

import android.database.sqlite.SQLiteException
import androidx.room.Dao
import androidx.room.Ignore
import androidx.room.Query
import androidx.room.Transaction
import app.shosetsu.android.common.enums.ProductFlavors
import app.shosetsu.android.common.utils.flavor
import app.shosetsu.android.domain.model.database.DBRepositoryEntity
import app.shosetsu.android.domain.model.local.CountIDTuple
import app.shosetsu.android.providers.database.dao.base.BaseDao
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
 * ====================================================================
 */

/**
 * shosetsu
 * 18 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
@Dao
interface RepositoryDao : BaseDao<DBRepositoryEntity> {
	@Throws(SQLiteException::class)
	@Transaction
	suspend fun insertRepositoryAndReturn(DBRepositoryEntity: DBRepositoryEntity): DBRepositoryEntity =
		loadRepositoryFromROWID(insertReplace(DBRepositoryEntity))

	/**
	 * Run only if you know for sure the data exists
	 */
	@Throws(SQLiteException::class)
	@Query("SELECT * FROM repositories WHERE url = :url LIMIT 1")
	fun loadRepositoryFromURL(url: String): DBRepositoryEntity

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM repositories WHERE id = :rowID LIMIT 1")
	fun loadRepositoryFromROWID(rowID: Long): DBRepositoryEntity

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM repositories WHERE id = :repositoryID LIMIT 1")
	fun loadRepositoryFromID(repositoryID: Int): DBRepositoryEntity

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM repositories ORDER BY id ASC")
	fun loadRepositoriesLive(): Flow<List<DBRepositoryEntity>>

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM repositories ORDER BY id ASC")
	fun loadRepositories(): List<DBRepositoryEntity>

	@Throws(SQLiteException::class)
	@Query("SELECT COUNT(*) FROM repositories WHERE url = :url")
	fun repositoryCountFromURL(url: String): Int

	@Throws(SQLiteException::class)
	@Query("SELECT COUNT(*), id FROM repositories WHERE url = :url LIMIT 1")
	fun repositoryCountAndROWIDFromURL(url: String): CountIDTuple

	@Ignore
	@Throws(SQLiteException::class)
	fun doesRepositoryExist(url: String): Boolean = repositoryCountFromURL(url) > 0

	@Transaction
	@Throws(SQLiteException::class)
	suspend fun initializeData() {
		// Create the Main repository with supportive code
		createIfNotExist(
			DBRepositoryEntity(
				null,
				url = "https://raw.githubusercontent.com/shosetsuorg/extensions-main/main",
				name = "Main",
				isEnabled = true
			)
		)

		// Create the Universe repository
		if (flavor() != ProductFlavors.PLAY_STORE)
			createIfNotExist(
				DBRepositoryEntity(
					null,
					url = "https://raw.githubusercontent.com/shosetsuorg/extensions/dev",
					//url = "https://raw.githubusercontent.com/shosetsuorg/extensions/dev/src/main/resources/",
					name = "Universe",
					isEnabled = true
				)
			)
	}

	@Transaction
	@Throws(SQLiteException::class)
	suspend fun createIfNotExist(DBRepositoryEntity: DBRepositoryEntity): Int {
		val tuple = repositoryCountAndROWIDFromURL(DBRepositoryEntity.url)
		if (tuple.count == 0)
			return insertRepositoryAndReturn(DBRepositoryEntity).id!!
		return tuple.id
	}
}