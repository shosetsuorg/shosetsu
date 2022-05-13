package app.shosetsu.android.domain.repository.impl

import app.shosetsu.android.common.GenericSQLiteException
import app.shosetsu.android.datasource.local.database.base.IDBUpdatesDataSource
import app.shosetsu.android.domain.model.local.UpdateCompleteEntity
import app.shosetsu.android.domain.model.local.UpdateEntity
import app.shosetsu.android.domain.repository.base.IUpdatesRepository
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
 * ====================================================================
 */

/**
 * shosetsu
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class UpdatesRepository(
	private val IDBUpdatesDataSource: IDBUpdatesDataSource,
) : IUpdatesRepository {

	@Throws(GenericSQLiteException::class)
	override suspend fun addUpdates(list: List<UpdateEntity>): Array<Long> =
		IDBUpdatesDataSource.insertUpdates(list)

	override suspend fun getUpdatesFlow(): Flow<List<UpdateEntity>> =
		IDBUpdatesDataSource.getUpdates()

	override suspend fun getCompleteUpdatesFlow(): Flow<List<UpdateCompleteEntity>> =
		IDBUpdatesDataSource.getCompleteUpdates()
}