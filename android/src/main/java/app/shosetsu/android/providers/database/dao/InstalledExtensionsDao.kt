package app.shosetsu.android.providers.database.dao

import android.database.sqlite.SQLiteException
import androidx.room.Dao
import androidx.room.Ignore
import androidx.room.Query
import app.shosetsu.android.domain.model.database.DBInstalledExtensionEntity
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
interface InstalledExtensionsDao : BaseDao<DBInstalledExtensionEntity> {

	// All

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM installed_extension")
	fun loadExtensions(): List<DBInstalledExtensionEntity>

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM installed_extension")
	fun loadExtensionsFlow(): Flow<List<DBInstalledExtensionEntity>>

	// All specific

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM installed_extension WHERE enabled = 1")
	fun loadEnabledExtensions(): Flow<List<DBInstalledExtensionEntity>>

	@Query("SELECT * FROM installed_extension WHERE repoID = :repoID")
	fun getExtensions(repoID: Int): List<DBInstalledExtensionEntity>

	// Singular

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM installed_extension WHERE id = :id LIMIT 1")
	fun getExtension(id: Int): DBInstalledExtensionEntity?

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM installed_extension WHERE id = :id LIMIT 1")
	fun getExtensionFlow(id: Int): Flow<DBInstalledExtensionEntity?>

	// Misc

	@Throws(SQLiteException::class)
	@Query("SELECT COUNT(*) FROM installed_extension WHERE id= :id")
	fun getCount(id: Int): Int

	@Throws(SQLiteException::class)
	@Ignore
	fun doesExtensionExist(id: Int): Boolean = getCount(id) > 0

}
