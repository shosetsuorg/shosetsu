package app.shosetsu.android.datasource.local.database.base

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
 * 04 / 05 / 2020
 */
interface IDBDownloadsDataSource {
	/** Loads LiveData of [DownloadEntity]s */
	fun loadLiveDownloads(): Flow<List<DownloadEntity>>

	/** Loads the download count */
	@Throws(SQLiteException::class)
	suspend fun loadDownloadCount(): Int

	/** Loads the first download that isn't paused or broken */
	@Throws(SQLiteException::class)
	suspend fun loadFirstDownload(): DownloadEntity?

	/** Inserts a new [DownloadEntity] */
	@Throws(SQLiteException::class)
	suspend fun insertDownload(downloadEntity: DownloadEntity): Long

	/** Updates a [DownloadEntity] */
	@Throws(SQLiteException::class)
	suspend fun updateDownload(downloadEntity: DownloadEntity)

	/** Deletes a [DownloadEntity] */
	@Throws(SQLiteException::class)
	suspend fun deleteDownload(downloadEntity: DownloadEntity)

	/** Deletes [DownloadEntity]s */
	@Throws(SQLiteException::class)
	suspend fun deleteDownload(downloads: List<DownloadEntity>)

	/** Loads a [DownloadEntity] via its [chapterID] */
	@Throws(SQLiteException::class)
	suspend fun loadDownload(chapterID: Int): DownloadEntity?

	/**
	 * Insert all [downloads] into the database
	 */
	@Throws(SQLiteException::class)
	suspend fun insertDownloads(downloads: List<DownloadEntity>)

	/**
	 * Update the status of downloads via chapter ids
	 */
	@Throws(SQLiteException::class)
	suspend fun updateStatus(chapterIds: List<Int>, status: DownloadStatus)

	/**
	 * Set all downloads as pending
	 */
	@Throws(SQLiteException::class)
	suspend fun setAllPending()
}