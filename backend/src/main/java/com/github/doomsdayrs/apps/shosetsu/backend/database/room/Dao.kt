package com.github.doomsdayrs.apps.shosetsu.backend.database.room

import androidx.room.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

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
interface FormatterDao {
	@Insert(onConflict = OnConflictStrategy.ABORT, entity = FormatterEntity::class)
	fun insertFormatter(formatterEntity: FormatterEntity)

	@Update
	fun updateFormatter(formatterEntity: FormatterEntity)

	@Delete
	fun deleteFormatter(formatterEntity: FormatterEntity)

	@Query("SELECT * FROM formatters")
	fun loadFormatters(): Array<FormatterEntity>

	@Query("SELECT * FROM formatters")
	fun loadFormattersOnFlow(): Flow<Array<FormatterEntity>>

	@ExperimentalCoroutinesApi
	fun loadFormattersOnFlowDistinctly() =
			loadFormattersOnFlow().distinctUntilChanged()

	@Query("SELECT * FROM formatters WHERE formatterID=:id")
	fun loadFormatter(id: Int)
}

@Dao
interface FRepositoryDao {
}