package app.shosetsu.android.datasource.local.database.impl

import app.shosetsu.android.common.ext.toDB
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.providers.database.dao.DownloadsDao
import app.shosetsu.common.datasource.database.base.IDBDownloadsDataSource
import app.shosetsu.common.domain.model.local.DownloadEntity
import app.shosetsu.common.dto.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

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
	@ExperimentalCoroutinesApi
	override fun loadLiveDownloads(): Flow<HResult<List<DownloadEntity>>> = flow {
		emit(loading())
		try {
			emitAll(downloadsDao.loadDownloadItems().mapLatestListTo().mapLatestToSuccess())
		} catch (e: Exception) {
			emit(e.toHError())
		}
	}

	override suspend fun loadDownloadCount(): HResult<Int> = try {
		successResult(downloadsDao.loadDownloadCount())
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun loadFirstDownload(): HResult<DownloadEntity> = try {
		downloadsDao.loadFirstDownload()?.convertTo()
			?.let { successResult(it) }
			?: emptyResult()
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun insertDownload(downloadEntity: DownloadEntity): HResult<Long> = try {
		successResult(downloadsDao.insertIgnore(downloadEntity.toDB()))
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun updateDownload(downloadEntity: DownloadEntity): HResult<*> = try {
		successResult(downloadsDao.update(downloadEntity.toDB()))
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun deleteDownload(downloadEntity: DownloadEntity): HResult<*> = try {
		successResult(downloadsDao.delete(downloadEntity.toDB()))
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun loadDownload(chapterID: Int): HResult<DownloadEntity> = try {
		successResult(downloadsDao.loadDownload(chapterID).convertTo())
	} catch (e: Exception) {
		e.toHError()
	}
}