package app.shosetsu.android.datasource.local.database.base

import android.database.sqlite.SQLiteException
import app.shosetsu.android.domain.model.local.UpdateCompleteEntity
import app.shosetsu.android.domain.model.local.UpdateEntity
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
 * 04 / 05 / 2020
 */
interface IDBUpdatesDataSource {

	/** Loads [Flow] of a [List] of [UpdateEntity] */
	suspend fun getUpdates(): Flow<List<UpdateEntity>>

	/** Insert a [List] of [UpdateEntity] and returns an [HResult] of [Array] of [Long] */
	@Throws(SQLiteException::class)
	suspend fun insertUpdates(list: List<UpdateEntity>): Array<Long>

	/** Loads [Flow] of a [List] of [UpdateCompleteEntity] */
	suspend fun getCompleteUpdates(): Flow<List<UpdateCompleteEntity>>
}