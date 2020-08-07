package com.github.doomsdayrs.apps.shosetsu.providers.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.DownloadEntity
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.base.BaseDao

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
	@Query("SELECT * FROM downloads WHERE status = 0 LIMIT 1")
	suspend fun loadFirstDownload(): DownloadEntity?

	/**
	 * Loads the first download, and also sets it as downloading
	 */
	@Transaction
	suspend fun loadAndStartFirstDownload(): DownloadEntity {
		val d = loadFirstDownload()!!
		d.status = 1
		blockingUpdate(d)
		return d
	}

	@Query("SELECT COUNT(*) FROM downloads WHERE status == 0")
	suspend fun loadDownloadCount(): Int

	@Query("SELECT COUNT(*) FROM downloads WHERE chapterID = :chapterID")
	suspend fun loadDownloadCount(chapterID: Int): Int

	suspend fun isInDownloads(chapterID: Int): Boolean = loadDownloadCount(chapterID) > 0

	@Query("SELECT * FROM downloads")
	fun loadDownloadItems(): LiveData<List<DownloadEntity>>

	@Query("SELECT * FROM downloads WHERE chapterID = :chapterID LIMIT 1")
	suspend fun loadDownload(chapterID: Int): DownloadEntity

	@Query("DELETE FROM downloads")
	suspend fun clearData()
}