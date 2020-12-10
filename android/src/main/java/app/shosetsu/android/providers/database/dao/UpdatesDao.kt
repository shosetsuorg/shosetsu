package app.shosetsu.android.providers.database.dao

import android.database.sqlite.SQLiteException
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import app.shosetsu.android.common.ext.trimDate
import app.shosetsu.android.domain.model.database.DBUpdate
import app.shosetsu.android.domain.model.local.UpdateCompleteEntity
import app.shosetsu.android.providers.database.dao.base.BaseDao
import kotlinx.coroutines.flow.Flow
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
interface UpdatesDao : BaseDao<DBUpdate> {
	@Throws(SQLiteException::class)
	@Query("SELECT time FROM updates ORDER BY ROWID ASC LIMIT 1")
	fun loadStartingDayTime(): Long

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM updates")
	fun loadUpdates(): Flow<List<DBUpdate>>

	@Throws(SQLiteException::class)
	@Query("SELECT time FROM updates ORDER BY ROWID DESC LIMIT 1")
	fun loadLatestDayTime(): Long

	@Throws(SQLiteException::class)
	suspend fun getStartingDayTime(): Long =
			DateTime(loadStartingDayTime()).trimDate().millis

	@Throws(SQLiteException::class)
	fun getLatestDayTime(): Long =
			DateTime(loadLatestDayTime()).trimDate().millis

	@Throws(SQLiteException::class)
	@Query("SELECT COUNT(*) FROM updates WHERE time < :date2 AND time >= :date1")
	fun loadDayCountBetweenDates(date1: Long, date2: Long): Int

	/**
	 * Raw query without checking dates
	 */
	@Throws(SQLiteException::class)
	@Query("SELECT * FROM updates WHERE time < :date2 AND time >= :date1")
	fun loadUpdatesBetweenDates(date1: Long, date2: Long): Flow<Array<DBUpdate>>

	@Transaction
	@Throws(SQLiteException::class)
	suspend fun getTotalDays(): Int {
		val firstDay = DateTime(getStartingDayTime())
		val latest = DateTime(getLatestDayTime())
		return Days.daysBetween(firstDay, latest).days
	}



	@Query("DELETE FROM updates WHERE novelID = :novelID")
	@Throws(SQLiteException::class)
	fun deleteUpdateByNovelID(novelID: Int)

	@Transaction
	@Throws(SQLiteException::class)
	fun removeDaysWithoutUpdates(list: ArrayList<Long>): ArrayList<Long> {
		for (x in list.size - 1 downTo 1) {
			val updateDate = list[x]
			val c = loadDayCountBetweenDates(
					updateDate,
					updateDate + 86399999
			)
			if (c <= 0) list.removeAt(x)
		}
		return list
	}

	@Throws(SQLiteException::class)
	@Query("""SELECT 
						updates.chapterID, 
						updates.novelID, 
						updates.time,
						( 
							SELECT
								title
							FROM chapters WHERE id = updates.chapterID
						) AS chapterName, 
						( 
							SELECT 
								title
							FROM novels WHERE id = updates.novelID
						) AS novelName,
						(
							SELECT
								imageURL
							FROM novels WHERE id = updates.novelID
						) AS novelImageURL
					FROM updates 
					WHERE 
						(
							SELECT 
								bookmarked 
							FROM novels WHERE id = updates.novelID
						) = 1
				""")
	fun loadCompleteUpdates(): Flow<List<UpdateCompleteEntity>>
}