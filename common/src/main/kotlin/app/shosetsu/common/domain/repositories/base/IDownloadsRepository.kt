package app.shosetsu.common.domain.repositories.base

import app.shosetsu.common.domain.model.local.DownloadEntity
import app.shosetsu.common.dto.HResult
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
	 *
	 * @return
	 * [HResult.Success] Successfully loaded list of entities
	 *
	 * [HResult.Error] Something went wrong loading entities
	 *
	 * [HResult.Empty] If no results were found
	 *
	 * [HResult.Loading] Initial value
	 */
	fun loadDownloadsFlow(): Flow<HResult<List<DownloadEntity>>>

	/**
	 * Loads the first download
	 *
	 * @return
	 * [HResult.Success] Found the first download
	 *
	 * [HResult.Error] Something went wrong loading
	 *
	 * [HResult.Empty] Nothing was found
	 *
	 * [HResult.Loading] never
	 */
	suspend fun loadFirstDownload(): HResult<DownloadEntity>

	/**
	 * Queries for the download count
	 *
	 * @return
	 * [HResult.Success] Download count
	 *
	 * [HResult.Error] Something went wrong
	 *
	 * [HResult.Empty] never?
	 *
	 * [HResult.Loading] never
	 */
	suspend fun loadDownloadCount(): HResult<Int>

	/**
	 * Gets a download entity by its ID
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] never
	 */
	suspend fun getDownload(chapterID: Int): HResult<DownloadEntity>

	/**
	 * Adds a new download to the repository
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] never
	 */
	suspend fun addDownload(download: DownloadEntity): HResult<Long>

	/**
	 * Updates a download in repository
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] never
	 */
	suspend fun update(download: DownloadEntity): HResult<*>

	/**
	 * Removes a download from the repository
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] never
	 */
	suspend fun deleteEntity(download: DownloadEntity): HResult<*>


}