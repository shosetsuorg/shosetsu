package app.shosetsu.android.domain.repository.base

import app.shosetsu.android.common.GenericSQLiteException
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
	@Throws(GenericSQLiteException::class)
	suspend fun loadFirstDownload(): DownloadEntity?

	/**
	 * Queries for the download count
	 */
	@Throws(GenericSQLiteException::class)
	suspend fun loadDownloadCount(): Int

	/**
	 * Gets a download entity by its ID
	 */
	@Throws(GenericSQLiteException::class)
	suspend fun getDownload(chapterID: Int): DownloadEntity?

	/**
	 * Adds a new download to the repository
	 */
	@Throws(GenericSQLiteException::class)
	suspend fun addDownload(download: DownloadEntity): Long

	/**
	 * Updates a download in repository
	 */
	@Throws(GenericSQLiteException::class)
	suspend fun update(download: DownloadEntity)

	/**
	 * Removes the [download] from the repository
	 */
	@Throws(GenericSQLiteException::class)
	suspend fun deleteEntity(download: DownloadEntity)
}