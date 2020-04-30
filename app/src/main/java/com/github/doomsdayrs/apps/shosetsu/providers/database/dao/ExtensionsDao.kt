package com.github.doomsdayrs.apps.shosetsu.providers.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Ignore
import androidx.room.Query
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ExtensionEntity
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
	fun loadFormatters(): LiveData<List<ExtensionEntity>>

	@Query("SELECT * FROM extensions WHERE installed = 1 AND enabled = 1")
	fun loadPoweredFormatters(): LiveData<List<ExtensionEntity>>

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
