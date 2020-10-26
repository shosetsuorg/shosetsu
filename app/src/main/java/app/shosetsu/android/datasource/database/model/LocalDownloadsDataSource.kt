package app.shosetsu.android.datasource.database.model

import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.emptyResult
import app.shosetsu.android.common.dto.errorResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.datasource.database.base.ILocalDownloadsDataSource
import app.shosetsu.android.domain.model.local.DownloadEntity
import app.shosetsu.android.providers.database.dao.DownloadsDao

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
		private val downloadsDao: DownloadsDao,
) : ILocalDownloadsDataSource {
	override fun loadLiveDownloads(): LiveData<HResult<List<DownloadEntity>>> = liveData {
		try {
			emitSource(downloadsDao.loadDownloadItems().map { successResult(it) })
		} catch (e: SQLiteException) {
			emit(errorResult(e))
		} catch (e: NullPointerException) {
			emit(errorResult(e))
		}
	}

	override suspend fun loadDownloadCount(): HResult<Int> = try {
		successResult(downloadsDao.loadDownloadCount())
	} catch (e: SQLiteException) {
		errorResult(e)
	} catch (e: NullPointerException) {
		errorResult(e)
	}

	override suspend fun loadFirstDownload(): HResult<DownloadEntity> = try {
		downloadsDao.loadFirstDownload()?.let { successResult(it) } ?: emptyResult()
	} catch (e: SQLiteException) {
		errorResult(e)
	} catch (e: NullPointerException) {
		errorResult(e)
	}

	override suspend fun insertDownload(downloadEntity: DownloadEntity): HResult<Long> = try {
		successResult(downloadsDao.insertIgnore(downloadEntity))
	} catch (e: SQLiteException) {
		errorResult(e)
	} catch (e: NullPointerException) {
		errorResult(e)
	}

	override suspend fun updateDownload(downloadEntity: DownloadEntity): HResult<*> = try {
		successResult(downloadsDao.suspendedUpdate(downloadEntity))
	} catch (e: SQLiteException) {
		errorResult(e)
	} catch (e: NullPointerException) {
		errorResult(e)
	}

	override suspend fun deleteDownload(downloadEntity: DownloadEntity): HResult<*> = try {
		successResult(downloadsDao.suspendedDelete(downloadEntity))
	} catch (e: SQLiteException) {
		errorResult(e)
	} catch (e: NullPointerException) {
		errorResult(e)
	}

	override suspend fun clearDownloads(): HResult<*> = try {
		successResult(downloadsDao.clearData())
	} catch (e: SQLiteException) {
		errorResult(e)
	} catch (e: NullPointerException) {
		errorResult(e)
	}

	override suspend fun loadDownload(chapterID: Int): HResult<DownloadEntity> = try {
		successResult(downloadsDao.loadDownload(chapterID))
	} catch (e: SQLiteException) {
		errorResult(e)
	} catch (e: NullPointerException) {
		errorResult(e)
	}
}