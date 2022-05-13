package app.shosetsu.android.datasource.local.database.impl

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.GenericSQLiteException
import app.shosetsu.android.common.ext.toDB
import app.shosetsu.android.datasource.local.database.base.IDBDownloadsDataSource
import app.shosetsu.android.domain.model.local.DownloadEntity
import app.shosetsu.android.dto.convertList
import app.shosetsu.android.providers.database.dao.DownloadsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
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
	override fun loadLiveDownloads(): Flow<List<DownloadEntity>> = flow {
		try {
			emitAll(downloadsDao.loadDownloadItems().map { it.convertList() })
		} catch (e: SQLiteException) {
			throw GenericSQLiteException(e)
		}
	}

	@Throws(GenericSQLiteException::class)
	override suspend fun loadDownloadCount(): Int = try {
		(downloadsDao.loadDownloadCount())
	} catch (e: SQLiteException) {
		throw GenericSQLiteException(e)
	}

	@Throws(GenericSQLiteException::class)
	override suspend fun loadFirstDownload(): DownloadEntity? = try {
		downloadsDao.loadFirstDownload()?.convertTo()
	} catch (e: SQLiteException) {
		throw GenericSQLiteException(e)
	}

	@Throws(GenericSQLiteException::class)
	override suspend fun insertDownload(downloadEntity: DownloadEntity): Long = try {
		(downloadsDao.insertIgnore(downloadEntity.toDB()))
	} catch (e: SQLiteException) {
		throw GenericSQLiteException(e)
	}

	@Throws(GenericSQLiteException::class)
	override suspend fun updateDownload(downloadEntity: DownloadEntity): Unit = try {
		(downloadsDao.update(downloadEntity.toDB()))
	} catch (e: SQLiteException) {
		throw GenericSQLiteException(e)
	}

	@Throws(GenericSQLiteException::class)
	override suspend fun deleteDownload(downloadEntity: DownloadEntity): Unit = try {
		(downloadsDao.delete(downloadEntity.toDB()))
	} catch (e: SQLiteException) {
		throw GenericSQLiteException(e)
	}

	@Throws(GenericSQLiteException::class)
	override suspend fun loadDownload(chapterID: Int): DownloadEntity? = try {
		downloadsDao.loadDownload(chapterID)?.convertTo()
	} catch (e: SQLiteException) {
		throw GenericSQLiteException(e)
	}
}