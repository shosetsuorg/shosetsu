package com.github.doomsdayrs.apps.shosetsu.providers.database.dao.base

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

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
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
interface BaseDao<T> {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(list: List<T>): Array<Long>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(data: T): Long

	@Update
	suspend fun update(data: T)

	@Delete
	suspend fun delete(data: T)
}