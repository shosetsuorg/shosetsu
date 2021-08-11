package app.shosetsu.android.providers.database.dao

import android.database.sqlite.SQLiteException
import androidx.room.Dao
import androidx.room.Ignore
import androidx.room.Query
import androidx.room.Transaction
import app.shosetsu.android.domain.model.database.DBExtensionEntity
import app.shosetsu.android.domain.model.database.DBStrippedExtensionEntity
import app.shosetsu.android.providers.database.dao.base.BaseDao
import app.shosetsu.lib.Version
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
interface ExtensionsDao : BaseDao<DBExtensionEntity> {
	@Throws(SQLiteException::class)
	@Query("SELECT * FROM extensions")
	fun loadExtensionsFlow(): Flow<List<DBExtensionEntity>>

	@Query("SELECT * FROM extensions")
	fun loadExtensions(): List<DBExtensionEntity>

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM extensions WHERE installed = 1 AND enabled = 1")
	fun loadPoweredExtensions(): Flow<List<DBExtensionEntity>>

	@Throws(SQLiteException::class)
	@Query("SELECT id, name, imageURL FROM extensions WHERE installed = 1 AND enabled = 1")
	fun loadPoweredExtensionsBasic(): Flow<List<DBStrippedExtensionEntity>>

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM extensions WHERE id = :formatterID LIMIT 1")
	fun getExtension(formatterID: Int): DBExtensionEntity?

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM extensions WHERE id = :formatterID LIMIT 1")
	fun getExtensionLive(formatterID: Int): Flow<DBExtensionEntity>

	@Throws(SQLiteException::class)
	@Query("SELECT COUNT(*) FROM extensions WHERE id= :formatterID")
	fun getExtensionCountFromID(formatterID: Int): Int

	@Throws(SQLiteException::class)
	@Ignore
	fun doesExtensionExist(formatterID: Int): Boolean = getExtensionCountFromID(formatterID) > 0

	@Query("SELECT * FROM extensions WHERE repoID = :repoID")
	fun getExtensions(repoID: Int): List<DBExtensionEntity>

	/**
	 * @return
	 * 1 if extension updated (update ava),
	 * 0 if inserted,
	 */
	@Throws(SQLiteException::class)
	@Transaction
	suspend fun insertOrUpdate(newEntity: DBExtensionEntity): Int =
		if (doesExtensionExist(newEntity.id)) {
			var isInstalled: Boolean
			var oldVersion = Version(0, 0, 0)
			update(
				getExtension(newEntity.id)!!.also {
					isInstalled = it.installed
					if (isInstalled)
						oldVersion = it.installedVersion!!
				}.copy(
					repoID = newEntity.repoID,
					name = newEntity.name,
					fileName = newEntity.fileName,
					imageURL = newEntity.imageURL,
					lang = newEntity.lang,
					// Ignore enabled, installed, installedVersion as those are independent
					repositoryVersion = newEntity.repositoryVersion,
					md5 = newEntity.md5,
					type = newEntity.type
				).let {
					if (!it.installed) // Only do this if the extension is not installed
						it.copy(
							chapterType = newEntity.chapterType
						)
					else
						it
				}
			)

			if (isInstalled && oldVersion < newEntity.repositoryVersion)
				1
			else
				0
		} else {
			insertReplace(newEntity)
			0
		}
}
