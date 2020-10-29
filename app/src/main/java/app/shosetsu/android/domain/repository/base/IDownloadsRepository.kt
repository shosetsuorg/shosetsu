package app.shosetsu.android.domain.repository.base

import app.shosetsu.android.common.dto.HResult
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

	/** Gets a live view of the downloads */
	fun loadLiveDownloads(): Flow<HResult<List<DownloadEntity>>>

	/** Loads the first download in the list, also starts it */
	suspend fun loadFirstDownload(): HResult<DownloadEntity>

	/** Queries for the download count */
	suspend fun loadDownloadCount(): HResult<Int>

	/** Gets a download entity by its ID */
	suspend fun loadDownload(chapterID: Int): HResult<DownloadEntity>

	/** Adds a new download to the repository */
	suspend fun addDownload(download: DownloadEntity): HResult<Long>

	/** Updates a download in repository */
	suspend fun update(download: DownloadEntity): HResult<*>

	/** Removes a download from the repository */
	suspend fun delete(download: DownloadEntity): HResult<*>

	/** Orders database to set all values back to pending */
	suspend fun resetList(): HResult<*>
}