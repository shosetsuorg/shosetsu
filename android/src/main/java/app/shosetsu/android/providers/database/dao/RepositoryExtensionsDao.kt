package app.shosetsu.android.providers.database.dao

import android.database.sqlite.SQLiteException
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import app.shosetsu.android.domain.model.database.DBRepositoryExtensionEntity
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
 */

/**
 * shosetsu
 * 22 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
@Dao
interface RepositoryExtensionsDao : BaseDao<DBRepositoryExtensionEntity> {

	// All

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM repository_extension")
	fun loadExtensions(): List<DBRepositoryExtensionEntity>

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM repository_extension")
	fun loadExtensionsFlow(): Flow<List<DBRepositoryExtensionEntity>>

	// All specific

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM repository_extension WHERE repoId = :repoId")
	fun getExtensions(repoId: Int): List<DBRepositoryExtensionEntity>

	// Singular

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM repository_extension WHERE id = :extensionId AND repoId = :repoId LIMIT 1")
	fun getExtension(repoId: Int, extensionId: Int): DBRepositoryExtensionEntity?

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM repository_extension WHERE id = :extensionId AND repoId = :repoId LIMIT 1")
	fun getExtensionFlow(repoId: Int, extensionId: Int): Flow<DBRepositoryExtensionEntity>

	// Misc

	@Transaction
	@Throws(SQLiteException::class)
	suspend fun insertOrUpdate(data: DBRepositoryExtensionEntity) {
		if (getCount(data.repoId, data.id) > 0) {
			update(data)
		} else insertAbort(data)
	}

	@Throws(SQLiteException::class)
	@Query("SELECT COUNT(*) FROM repository_extension WHERE id = :extensionId AND repoId = :repoId")
	fun getCount(repoId: Int, extensionId: Int): Int
}
