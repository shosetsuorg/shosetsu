package com.github.doomsdayrs.apps.shosetsu.providers.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
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
	@Query("SELECT * FROM downloads LIMIT 1")
	fun loadFirstDownload(): DownloadEntity

	@Query("SELECT COUNT(*) FROM downloads")
	fun loadDownloadCount(): Int

	@Query("SELECT COUNT(*) FROM downloads WHERE chapterID = :chapterID")
	fun loadDownloadCount(chapterID: Int): Int

	fun isInDownloads(chapterID: Int): Boolean = loadDownloadCount(chapterID) > 0

	@Query("SELECT * FROM downloads")
	fun loadDownloadItems(): LiveData<List<DownloadEntity>>

	@Query("SELECT * FROM downloads WHERE chapterID = :chapterID LIMIT 1")
	fun loadDownload(chapterID: Int): LiveData<DownloadEntity>
}