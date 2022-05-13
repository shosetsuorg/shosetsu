package app.shosetsu.android.datasource.local.database.base

import app.shosetsu.android.common.GenericSQLiteException
import app.shosetsu.common.domain.model.local.DownloadEntity
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
	@Throws(GenericSQLiteException::class)
	suspend fun loadDownloadCount(): Int

	/** Loads the first download that isn't paused or broken */
	@Throws(GenericSQLiteException::class)
	suspend fun loadFirstDownload(): DownloadEntity?

	/** Inserts a new [DownloadEntity] */
	@Throws(GenericSQLiteException::class)
	suspend fun insertDownload(downloadEntity: DownloadEntity): Long

	/** Updates a [DownloadEntity] */
	@Throws(GenericSQLiteException::class)
	suspend fun updateDownload(downloadEntity: DownloadEntity)

	/** Deletes a [DownloadEntity] */
	@Throws(GenericSQLiteException::class)
	suspend fun deleteDownload(downloadEntity: DownloadEntity)

	/** Loads a [DownloadEntity] via its [chapterID] */
	@Throws(GenericSQLiteException::class)
	suspend fun loadDownload(chapterID: Int): DownloadEntity?
}