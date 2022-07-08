package app.shosetsu.android.datasource.local.database.impl

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.enums.DownloadStatus
import app.shosetsu.android.common.ext.toDB
import app.shosetsu.android.datasource.local.database.base.IDBDownloadsDataSource
import app.shosetsu.android.domain.model.local.DownloadEntity
import app.shosetsu.android.dto.convertList
import app.shosetsu.android.providers.database.dao.DownloadsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
class DBDownloadsDataSource(
	private val downloadsDao: DownloadsDao,
) : IDBDownloadsDataSource {
	override fun loadLiveDownloads(): Flow<List<DownloadEntity>> =
		downloadsDao.loadDownloadItems().map { it.convertList() }

	@Throws(SQLiteException::class)
	override suspend fun loadDownloadCount(): Int =
		(downloadsDao.loadDownloadCount())

	@Throws(SQLiteException::class)
	override suspend fun loadFirstDownload(): DownloadEntity? =
		downloadsDao.loadFirstDownload()?.convertTo()

	@Throws(SQLiteException::class)
	override suspend fun insertDownload(downloadEntity: DownloadEntity): Long =
		(downloadsDao.insertIgnore(downloadEntity.toDB()))

	@Throws(SQLiteException::class)
	override suspend fun updateDownload(downloadEntity: DownloadEntity): Unit =
		(downloadsDao.update(downloadEntity.toDB()))

	@Throws(SQLiteException::class)
	override suspend fun deleteDownload(downloadEntity: DownloadEntity): Unit =
		(downloadsDao.delete(downloadEntity.toDB()))

	@Throws(SQLiteException::class)
	override suspend fun loadDownload(chapterID: Int): DownloadEntity? =
		downloadsDao.loadDownload(chapterID)?.convertTo()

	@Throws(SQLiteException::class)
	override suspend fun insertDownloads(downloads: List<DownloadEntity>) {
		downloadsDao.insertAllIgnore(downloads.map { it.toDB() })
	}

	@Throws(SQLiteException::class)
	override suspend fun deleteDownload(downloads: List<DownloadEntity>) {
		downloadsDao.delete(downloads.map { it.toDB() })
	}

	@Throws(SQLiteException::class)
	override suspend fun updateStatus(chapterIds: List<Int>, status: DownloadStatus) {
		downloadsDao.updateStatusBulk(chapterIds, status)
	}

	@Throws(SQLiteException::class)
	override suspend fun setAllPending() {
		downloadsDao.setAllPending()
	}
}