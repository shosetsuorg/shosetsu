package com.github.doomsdayrs.apps.shosetsu.providers.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Ignore
import androidx.room.Query
import androidx.room.Transaction
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ExtensionEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.IDNameImage
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.base.BaseDao

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
	@Query("SELECT * FROM extensions")
	fun loadExtensions(): LiveData<List<ExtensionEntity>>

	@Query("SELECT * FROM extensions WHERE installed = 1 AND enabled = 1")
	fun loadPoweredExtensions(): LiveData<List<ExtensionEntity>>

	@Query("SELECT id, name, imageURL FROM extensions WHERE installed = 1 AND enabled = 1")
	fun loadPoweredExtensionsBasic(): LiveData<List<IDNameImage>>

	@Query("SELECT fileName FROM extensions WHERE installed = 1 AND enabled = 1 ORDER BY name ASC")
	fun loadPoweredExtensionsFileNames(): Array<String>

	@Query("SELECT * FROM extensions WHERE id = :formatterID LIMIT 1")
	fun loadExtension(formatterID: Int): ExtensionEntity

	@Query("SELECT md5 FROM extensions WHERE id = :formatterID LIMIT 1")
	fun loadExtensionMD5(formatterID: Int): String

	@Query("SELECT COUNT(*) FROM extensions WHERE id= :formatterID")
	fun loadExtensionCountFromID(formatterID: Int): Int

	@Ignore
	fun doesExtensionExist(formatterID: Int): Boolean = loadExtensionCountFromID(formatterID) > 0

	@Transaction
	suspend fun insertOrUpdate(extensionEntity: ExtensionEntity) {
		if (doesExtensionExist(extensionEntity.id)) {
			blockingUpdate(extensionEntity)
		} else {
			insertReplace(extensionEntity)
		}
	}

}
