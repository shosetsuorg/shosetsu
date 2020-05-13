package com.github.doomsdayrs.apps.shosetsu.datasource.local.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.datasource.local.base.ILocalDownloadsDataSource
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.DownloadEntity
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.DownloadsDao

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
 * 12 / 05 / 2020
 */
class LocalDownloadsDataSource(
		val downloadsDao: DownloadsDao
) : ILocalDownloadsDataSource {
	override suspend fun loadDownloads(): LiveData<HResult<List<DownloadEntity>>> =
			downloadsDao.loadDownloadItems().map { successResult(it) }

	override suspend fun loadDownloadCount(): HResult<Int> =
			successResult(downloadsDao.loadDownloadCount())

	override suspend fun loadFirstDownload(): HResult<DownloadEntity> =
			successResult(downloadsDao.loadFirstDownload())

	override suspend fun insertDownload(downloadEntity: DownloadEntity): Long =
			downloadsDao.insertIgnore(downloadEntity)

	override suspend fun updateDownload(downloadEntity: DownloadEntity) =
			downloadsDao.suspendedUpdate(downloadEntity)

	override suspend fun deleteDownload(downloadEntity: DownloadEntity) =
			downloadsDao.suspendedDelete(downloadEntity)

	override suspend fun clearDownloads() = downloadsDao.clearData()
}