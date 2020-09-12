package app.shosetsu.android.domain.repository.model

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.datasource.local.base.ILocalUpdatesDataSource
import app.shosetsu.android.domain.model.local.UpdateCompleteEntity
import app.shosetsu.android.domain.model.local.UpdateEntity
import app.shosetsu.android.domain.repository.base.IUpdatesRepository

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

	override suspend fun getUpdates(): LiveData<HResult<List<UpdateEntity>>> =
			iLocalUpdatesDataSource.getUpdates()

	override suspend fun getCompleteUpdates(): LiveData<HResult<List<UpdateCompleteEntity>>> =
			iLocalUpdatesDataSource.getCompleteUpdates()
}