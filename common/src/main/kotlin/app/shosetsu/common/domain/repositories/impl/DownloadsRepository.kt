package app.shosetsu.common.domain.repositories.impl

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

import app.shosetsu.common.domain.repositories.base.IDownloadsRepository
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.datasource.database.base.ILocalDownloadsDataSource
import app.shosetsu.common.domain.model.local.DownloadEntity
import kotlinx.coroutines.flow.Flow

/**
 * shosetsu
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class DownloadsRepository(
		private val iLocalDownloadsDataSource: ILocalDownloadsDataSource,
) : IDownloadsRepository {
	override fun loadDownloadsFlow(): Flow<HResult<List<DownloadEntity>>> =
		iLocalDownloadsDataSource.loadLiveDownloads()

	override suspend fun loadFirstDownload(): HResult<DownloadEntity> =
		iLocalDownloadsDataSource.loadFirstDownload()

	override suspend fun loadDownloadCount(): HResult<Int> =
		iLocalDownloadsDataSource.loadDownloadCount()

	override suspend fun getDownload(chapterID: Int): HResult<DownloadEntity> =
		iLocalDownloadsDataSource.loadDownload(chapterID)

	override suspend fun addDownload(download: DownloadEntity): HResult<Long> =
		iLocalDownloadsDataSource.insertDownload(download)

	override suspend fun update(download: DownloadEntity): HResult<*> =
		iLocalDownloadsDataSource.updateDownload(download)

	override suspend fun deleteEntity(download: DownloadEntity): HResult<*> =
		iLocalDownloadsDataSource.deleteDownload(download)


}