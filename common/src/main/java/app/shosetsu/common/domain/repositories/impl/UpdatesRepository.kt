package app.shosetsu.common.domain.repositories.impl

import app.shosetsu.common.datasource.database.base.ILocalUpdatesDataSource
import app.shosetsu.common.domain.model.local.UpdateCompleteEntity
import app.shosetsu.common.domain.repositories.base.IUpdatesRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.domain.model.local.UpdateEntity
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
		private val iLocalUpdatesDataSource: ILocalUpdatesDataSource,
) : IUpdatesRepository {

	override suspend fun addUpdates(list: List<UpdateEntity>): HResult<Array<Long>> =
			iLocalUpdatesDataSource.insertUpdates(list)

	override suspend fun getUpdates(): Flow<HResult<List<UpdateEntity>>> =
			iLocalUpdatesDataSource.getUpdates()

	override suspend fun getCompleteUpdates(): Flow<HResult<List<UpdateCompleteEntity>>> =
			iLocalUpdatesDataSource.getCompleteUpdates()
}