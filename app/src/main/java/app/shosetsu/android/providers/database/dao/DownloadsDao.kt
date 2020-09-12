package app.shosetsu.android.providers.database.dao

import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import app.shosetsu.android.domain.model.local.DownloadEntity
import app.shosetsu.android.providers.database.dao.base.BaseDao

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
interface DownloadsDao : BaseDao<DownloadEntity> {
	/**
	 * Loads the first download
	 */
	@Throws(SQLiteException::class)
	@Query("SELECT * FROM downloads WHERE status = 0 LIMIT 1")
	suspend fun loadFirstDownload(): DownloadEntity?

	/**
	 * Loads the first download, and also sets it as downloading
	 */
	@Throws(SQLiteException::class)
	@Transaction
	suspend fun loadAndStartFirstDownload(): DownloadEntity {
		val d = loadFirstDownload()!!
		d.status = 1
		blockingUpdate(d)
		return d
	}

	@Throws(SQLiteException::class)
	@Query("SELECT COUNT(*) FROM downloads WHERE status == 0")
	suspend fun loadDownloadCount(): Int

	@Throws(SQLiteException::class)
	@Query("SELECT COUNT(*) FROM downloads WHERE chapterID = :chapterID")
	suspend fun loadDownloadCount(chapterID: Int): Int

	@Throws(SQLiteException::class)
	suspend fun isInDownloads(chapterID: Int): Boolean = loadDownloadCount(chapterID) > 0

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM downloads")
	fun loadDownloadItems(): LiveData<List<DownloadEntity>>

	@Throws(SQLiteException::class)
	@Query("SELECT * FROM downloads WHERE chapterID = :chapterID LIMIT 1")
	suspend fun loadDownload(chapterID: Int): DownloadEntity

	@Query("DELETE FROM downloads")
	@Throws(SQLiteException::class)
	suspend fun clearData()
}