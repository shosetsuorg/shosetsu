package app.shosetsu.android.providers.database.dao

import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Ignore
import androidx.room.Query
import androidx.room.Transaction
import app.shosetsu.android.domain.model.local.ExtensionEntity
import app.shosetsu.android.domain.model.local.IDNameImage
import app.shosetsu.android.providers.database.dao.base.BaseDao

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
 * 22 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
@Dao
interface ExtensionsDao : BaseDao<ExtensionEntity> {
	@Throws(SQLiteException::class)
	@Query("SELECT * FROM extensions")
	fun loadExtensions(): LiveData<List<ExtensionEntity>>

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM extensions WHERE installed = 1 AND enabled = 1")
	fun loadPoweredExtensions(): LiveData<List<ExtensionEntity>>

	@Throws(SQLiteException::class)
	@Query("SELECT id, name, imageURL FROM extensions WHERE installed = 1 AND enabled = 1")
	fun loadPoweredExtensionsBasic(): LiveData<List<IDNameImage>>

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM extensions WHERE id = :formatterID LIMIT 1")
	fun loadExtension(formatterID: Int): ExtensionEntity

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM extensions WHERE id = :formatterID LIMIT 1")
	fun loadExtensionLive(formatterID: Int): LiveData<ExtensionEntity>

	@Throws(SQLiteException::class)
	@Query("SELECT COUNT(*) FROM extensions WHERE id= :formatterID")
	fun loadExtensionCountFromID(formatterID: Int): Int

	@Throws(SQLiteException::class)
	@Ignore
	fun doesExtensionExist(formatterID: Int): Boolean = loadExtensionCountFromID(formatterID) > 0

	@Throws(SQLiteException::class)
	@Transaction
	suspend fun insertOrUpdate(extensionEntity: ExtensionEntity) {
		if (doesExtensionExist(extensionEntity.id)) {
			suspendedUpdate(loadExtension(extensionEntity.id).copy(
					name = extensionEntity.name,
					imageURL = extensionEntity.imageURL,
					repositoryVersion = extensionEntity.repositoryVersion,
					md5 = extensionEntity.md5
			))
		} else {
			insertReplace(extensionEntity)
		}
	}

}
