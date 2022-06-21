package app.shosetsu.android.providers.database.dao.base

import android.database.sqlite.SQLiteException
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
	@Throws(SQLiteException::class)
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAllReplace(list: List<T>): Array<Long>

	@Throws(SQLiteException::class)
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertReplace(data: T): Long

	@Throws(SQLiteException::class)
	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insertAllIgnore(list: List<T>): Array<Long>

	@Throws(SQLiteException::class)
	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insertIgnore(data: T): Long


	@Throws(SQLiteException::class)
	@Insert(onConflict = OnConflictStrategy.ABORT)
	suspend fun insertAllAbort(list: List<T>): Array<Long>

	@Throws(SQLiteException::class)
	@Insert(onConflict = OnConflictStrategy.ABORT)
	suspend fun insertAbort(data: T): Long

	@Update
	@Throws(SQLiteException::class)
	suspend fun update(data: T)

	@Update
	@Throws(SQLiteException::class)
	fun blockingUpdate(data: T)

	@Delete
	@Throws(SQLiteException::class)
	suspend fun delete(data: T)

	@Delete
	@Throws(SQLiteException::class)
	suspend fun delete(data: List<T>)
}