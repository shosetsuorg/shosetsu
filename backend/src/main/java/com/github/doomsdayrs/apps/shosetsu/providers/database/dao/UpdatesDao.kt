package com.github.doomsdayrs.apps.shosetsu.providers.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.base.BaseDao
import com.github.doomsdayrs.apps.shosetsu.providers.database.entities.UpdateEntity
import com.github.doomsdayrs.apps.shosetsu.variables.IncorrectDateException
import com.github.doomsdayrs.apps.shosetsu.variables.ext.trimDate
import org.joda.time.DateTime
import org.joda.time.Days

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
 * 22 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
@Dao
interface UpdatesDao : BaseDao<UpdateEntity> {
	@Query("SELECT time FROM updates ORDER BY ROWID ASC LIMIT 1")
	fun loadStartingDayTime(): Long


	@Query("SELECT time FROM updates ORDER BY ROWID DESC LIMIT 1")
	fun loadLatestDayTime(): Long

	fun getStartingDayTime(): Long =
			DateTime(loadStartingDayTime()).trimDate().millis

	fun getLatestDayTime(): Long =
			DateTime(loadLatestDayTime()).trimDate().millis

	@Query("SELECT COUNT(*) FROM updates WHERE time < :date2 AND time >= :date1")
	fun loadDayCountBetweenDates(date1: Long, date2: Long): Int

	/**
	 * Raw query without checking dates, suggested to use [getTimeBetweenDates]
	 */
	@Query("SELECT * FROM updates WHERE time < :date2 AND time >= :date1")
	fun loadUpdatesBetweenDates(date1: Long, date2: Long): Array<UpdateEntity>

	@Transaction
	fun getTotalDays(): Int {
		val firstDay = DateTime(getStartingDayTime())
		val latest = DateTime(getLatestDayTime())
		return Days.daysBetween(firstDay, latest).days
	}

	/**
	 * [loadUpdatesBetweenDates] but with error checking
	 */
	@Transaction
	fun getTimeBetweenDates(date1: Long, date2: Long): Array<UpdateEntity> {
		if (date2 <= date1) throw IncorrectDateException("Dates implemented wrongly")
		return loadUpdatesBetweenDates(date1, date2)
	}


	@Query("DELETE FROM updates WHERE novelID = :novelID")
	fun deleteUpdateByNovelID(novelID: Int)
}