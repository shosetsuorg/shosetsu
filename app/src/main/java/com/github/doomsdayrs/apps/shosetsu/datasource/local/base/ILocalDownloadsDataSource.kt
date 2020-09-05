package com.github.doomsdayrs.apps.shosetsu.datasource.local.base

import androidx.lifecycle.LiveData
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.DownloadEntity

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
interface ILocalDownloadsDataSource {
	/** Loads LiveData of [DownloadEntity]s */
	fun loadLiveDownloads(): LiveData<HResult<List<DownloadEntity>>>

	/** Loads the download count */
	suspend fun loadDownloadCount(): HResult<Int>

	/** Loads the first download that isn't paused or broken */
	suspend fun loadFirstDownload(): HResult<DownloadEntity>

	/** Inserts a new [DownloadEntity] */
	suspend fun insertDownload(downloadEntity: DownloadEntity): HResult<Long>

	/** Updates a [DownloadEntity] */
	suspend fun updateDownload(downloadEntity: DownloadEntity): HResult<*>

	/** Deletes a [DownloadEntity] */
	suspend fun deleteDownload(downloadEntity: DownloadEntity): HResult<*>

	/** Clear all downloads */
	suspend fun clearDownloads(): HResult<*>

	/** Loads a [DownloadEntity] via its [chapterID] */
	suspend fun loadDownload(chapterID: Int): HResult<DownloadEntity>
}