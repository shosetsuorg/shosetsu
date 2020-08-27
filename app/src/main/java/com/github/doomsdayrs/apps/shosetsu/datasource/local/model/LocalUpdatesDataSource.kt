package com.github.doomsdayrs.apps.shosetsu.datasource.local.model

import androidx.lifecycle.LiveData
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.datasource.local.base.ILocalUpdatesDataSource
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.UpdateEntity
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.UpdatesDao

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
 * 12 / 05 / 2020
 */
class LocalUpdatesDataSource(
		private val updatesDao: UpdatesDao
) : ILocalUpdatesDataSource {
	override suspend fun getUpdates(): LiveData<HResult<List<UpdateEntity>>> {
		TODO("Not yet implemented")
	}
	
	/*
	override suspend fun getUpdates(): LiveData<HResult<List<Long>>> {
		return liveData(context = Dispatchers.IO) {
			var updatePages = ArrayList<Long>()

			val days = updatesDao.getTotalDays()
			Log.d(logID(), "Total Days: $days")

			var startTime = updatesDao.getStartingDayTime()
			Log.d(logID(), "Starting Day" + DateTime(startTime).toString())

			// Adds the days up
			for (x in 0 until days) {
				val updateFragment = startTime
				startTime += 86400000
				updatePages.add(updateFragment)
			}

			// Removes days without updates
			updatePages = updatesDao.removeDaysWithoutUpdates(updatePages)

			// Today
			val currentDate = DateTime(System.currentTimeMillis()).trimDate().millis

			updatePages.add(currentDate)
			updatePages.reverse()

			successResult(updatePages)
		}
	}
	 */
	override suspend fun insertUpdates(list: List<UpdateEntity>): Array<Long> =
			updatesDao.insertAllIgnore(list)
}