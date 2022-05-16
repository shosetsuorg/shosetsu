package app.shosetsu.android.domain.repository.base
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
import android.database.sqlite.SQLiteException
import app.shosetsu.android.domain.model.local.UpdateCompleteEntity
import app.shosetsu.android.domain.model.local.UpdateEntity
import kotlinx.coroutines.flow.Flow


/**
 * shosetsu
 * 25 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
interface IUpdatesRepository {
	/**
	 * Adds updates
	 */
	@Throws(SQLiteException::class)
	suspend fun addUpdates(list: List<UpdateEntity>): Array<Long>

	/**
	 * [Flow] of [List] of [UpdateEntity] of all entities present
	 */
	suspend fun getUpdatesFlow(): Flow<List<UpdateEntity>>

	/**
	 * [Flow] of [List] of [UpdateCompleteEntity] of all entities present
	 */
	suspend fun getCompleteUpdatesFlow(): Flow<List<UpdateCompleteEntity>>
}