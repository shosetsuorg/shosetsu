package app.shosetsu.android.domain.repository.base

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.enums.DownloadStatus
import app.shosetsu.android.domain.model.local.DownloadEntity
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
 */

/**
 * shosetsu
 * 25 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
interface IDownloadsRepository {

	/**
	 * Gets a flow of the downloads
	 */
	fun loadDownloadsFlow(): Flow<List<DownloadEntity>>

	/**
	 * Loads the first download
	 */
	@Throws(SQLiteException::class)
	suspend fun loadFirstDownload(): DownloadEntity?

	/**
	 * Queries for the download count
	 */
	@Throws(SQLiteException::class)
	suspend fun loadDownloadCount(): Int

	/**
	 * Gets a download entity by its ID
	 */
	@Throws(SQLiteException::class)
	suspend fun getDownload(chapterID: Int): DownloadEntity?

	/**
	 * Adds a new download to the repository
	 */
	@Throws(SQLiteException::class)
	suspend fun addDownload(download: DownloadEntity): Long

	/**
	 * Adds downloads to the repository
	 */
	@Throws(SQLiteException::class)
	suspend fun addDownload(downloads: List<DownloadEntity>)

	/**
	 * Updates a download in repository
	 */
	@Throws(SQLiteException::class)
	suspend fun update(download: DownloadEntity)

	/**
	 * Removes the [download] from the repository
	 */
	@Throws(SQLiteException::class)
	suspend fun deleteEntity(download: DownloadEntity)

	/**
	 * Removes the [downloads] from the repository
	 */
	@Throws(SQLiteException::class)
	suspend fun deleteEntity(downloads: List<DownloadEntity>)

	/**
	 * Update the download statuses
	 */
	@Throws(SQLiteException::class)
	suspend fun updateStatus(downloads: List<DownloadEntity>, status: DownloadStatus)

	/**
	 * Set all downloads pending
	 */
	@Throws(SQLiteException::class)
	suspend fun setAllPending()
}