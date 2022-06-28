package app.shosetsu.android.domain.repository.impl

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

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.enums.DownloadStatus
import app.shosetsu.android.common.ext.onIO
import app.shosetsu.android.datasource.local.database.base.IDBDownloadsDataSource
import app.shosetsu.android.domain.model.local.DownloadEntity
import app.shosetsu.android.domain.repository.base.IDownloadsRepository
import kotlinx.coroutines.flow.Flow

/**
 * shosetsu
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class DownloadsRepository(
	private val database: IDBDownloadsDataSource,
) : IDownloadsRepository {
	override fun loadDownloadsFlow(): Flow<List<DownloadEntity>> =
		database.loadLiveDownloads().onIO()

	@Throws(SQLiteException::class)
	override suspend fun loadFirstDownload(): DownloadEntity? =
		onIO { database.loadFirstDownload() }

	@Throws(SQLiteException::class)
	override suspend fun loadDownloadCount(): Int =
		onIO { database.loadDownloadCount() }

	@Throws(SQLiteException::class)
	override suspend fun getDownload(chapterID: Int): DownloadEntity? =
		onIO { database.loadDownload(chapterID) }

	@Throws(SQLiteException::class)
	override suspend fun addDownload(download: DownloadEntity): Long =
		onIO { database.insertDownload(download) }

	@Throws(SQLiteException::class)
	override suspend fun addDownload(downloads: List<DownloadEntity>) {
		onIO { database.insertDownloads(downloads) }
	}

	@Throws(SQLiteException::class)
	override suspend fun update(download: DownloadEntity) = onIO {
		database.updateDownload(download)
	}

	@Throws(SQLiteException::class)
	override suspend fun deleteEntity(download: DownloadEntity) = onIO {
		database.deleteDownload(download)
	}

	@Throws(SQLiteException::class)
	override suspend fun deleteEntity(downloads: List<DownloadEntity>) {
		onIO {
			database.deleteDownload(downloads)
		}
	}

	@Throws(SQLiteException::class)
	override suspend fun updateStatus(downloads: List<DownloadEntity>, status: DownloadStatus) {
		onIO {
			database.updateStatus(downloads.map { it.chapterID }, status)
		}
	}

	@Throws(SQLiteException::class)
	override suspend fun setAllPending() {
		onIO {
			database.setAllPending()
		}
	}
}