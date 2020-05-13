package com.github.doomsdayrs.apps.shosetsu.domain.repository.model

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

import androidx.lifecycle.LiveData
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchAsync
import com.github.doomsdayrs.apps.shosetsu.datasource.local.base.ILocalDownloadsDataSource
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.DownloadEntity
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IDownloadsRepository

/**
 * shosetsu
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class DownloadsRepository(
		private val iLocalDownloadsDataSource: ILocalDownloadsDataSource
) : IDownloadsRepository {
	override suspend fun loadDownloads(): LiveData<HResult<List<DownloadEntity>>> =
			iLocalDownloadsDataSource.loadDownloads()

	override suspend fun loadFirstDownload(): HResult<DownloadEntity> =
			iLocalDownloadsDataSource.loadFirstDownload()

	override suspend fun loadDownloadCount(): HResult<Int> =
			iLocalDownloadsDataSource.loadDownloadCount()

	override suspend fun addDownload(download: DownloadEntity): HResult<Long> =
			successResult(iLocalDownloadsDataSource.insertDownload(download))

	override suspend fun suspendedUpdate(download: DownloadEntity) =
			iLocalDownloadsDataSource.updateDownload(download)

	override fun blockingUpdate(download: DownloadEntity) {
		launchAsync { iLocalDownloadsDataSource.updateDownload(download) }
	}

	override suspend fun suspendedDelete(download: DownloadEntity) =
			iLocalDownloadsDataSource.deleteDownload(download)

	override suspend fun resetList() =
			iLocalDownloadsDataSource.clearDownloads()

}