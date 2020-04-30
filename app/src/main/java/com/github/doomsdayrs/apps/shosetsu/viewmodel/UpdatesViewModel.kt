package com.github.doomsdayrs.apps.shosetsu.viewmodel

import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.URLImageTitle
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.NovelsDao
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.UpdatesDao
import com.github.doomsdayrs.apps.shosetsu.ui.updates.UpdateController
import com.github.doomsdayrs.apps.shosetsu.variables.ext.trimDate
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IUpdatesViewModel
import org.joda.time.DateTime
import java.util.*

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
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class UpdatesViewModel(
		val updatesDao: UpdatesDao,
		val novelsDao: NovelsDao
) : ViewModel(), IUpdatesViewModel {

	override suspend fun createControllers(): ArrayList<UpdateController> {
		val updatePages = ArrayList<UpdateController>()

		val days = updatesDao.getTotalDays()
		Log.d("TotalDays", days.toString())

		var startTime = updatesDao.getStartingDayTime()
		Log.d("StartingDay", DateTime(startTime).toString())

		for (x in 0 until days) {
			val updateFragment = UpdateController(bundleOf("date" to startTime))
			startTime += 86400000
			updatePages.add(updateFragment)
		}
		for (x in updatePages.size - 1 downTo 1) {
			val updateFragment = updatePages[x]
			val c = updatesDao.loadDayCountBetweenDates(
					updateFragment.date,
					updateFragment.date + 86399999
			)
			if (c <= 0) updatePages.removeAt(x)
		}

		// Today
		val currentDayController = UpdateController(bundleOf(
				"date" to DateTime(System.currentTimeMillis()).trimDate().millis
		))

		updatePages.add(currentDayController)
		updatePages.reverse()

		return updatePages
	}

	override suspend fun getTimeBetweenDates(date: Long, date2: Long) =
			updatesDao.loadUpdatesBetweenDates(date, date2)

	override suspend fun getURLImageTitle(novelID: Int): URLImageTitle {
		TODO("Not yet implemented")
	}
}