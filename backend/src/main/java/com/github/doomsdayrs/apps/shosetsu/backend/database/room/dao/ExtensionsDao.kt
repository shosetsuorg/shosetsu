package com.github.doomsdayrs.apps.shosetsu.backend.database.room.dao

import androidx.room.*
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.entities.ExtensionEntity

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
interface ExtensionsDao {
	@Insert(onConflict = OnConflictStrategy.ABORT, entity = ExtensionEntity::class)
	fun insertFormatter(extensionEntity: ExtensionEntity)

	@Update
	fun updateFormatter(extensionEntity: ExtensionEntity)

	@Delete
	fun deleteFormatter(extensionEntity: ExtensionEntity)

	@Query("SELECT * FROM extensions")
	fun loadFormatters(): Array<ExtensionEntity>

	@Query("SELECT fileName FROM extensions WHERE installed = 1 AND enabled = 1 ORDER BY name ASC")
	fun loadPoweredFormatterFileNames(): Array<String>

	@Query("SELECT * FROM extensions WHERE id = :formatterID LIMIT 1")
	fun loadFormatter(formatterID: Int): ExtensionEntity

	@Query("SELECT md5 FROM extensions WHERE id = :formatterID LIMIT 1")
	fun loadFormatterMD5(formatterID: Int): String

	@Query("SELECT COUNT(*) FROM extensions WHERE id= :formatterID")
	fun formatterCountFromID(formatterID: Int): Int

	@Ignore
	fun doesFormatterExist(formatterID: Int): Boolean = formatterCountFromID(formatterID) > 0

}
