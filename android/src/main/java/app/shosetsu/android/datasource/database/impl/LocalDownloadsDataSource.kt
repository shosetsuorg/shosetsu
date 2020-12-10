package app.shosetsu.android.datasource.database.impl

import app.shosetsu.android.common.ext.toDB
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.providers.database.dao.DownloadsDao
import app.shosetsu.common.datasource.database.base.ILocalDownloadsDataSource
import app.shosetsu.common.domain.model.local.DownloadEntity
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.mapLatestListTo
import app.shosetsu.common.dto.mapLatestToSuccess
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
class LocalDownloadsDataSource(
		private val downloadsDao: DownloadsDao,
) : ILocalDownloadsDataSource {
	override fun loadLiveDownloads(): Flow<HResult<List<DownloadEntity>>> = flow {
		try {
			emitAll(downloadsDao.loadDownloadItems().mapLatestListTo().mapLatestToSuccess())
		} catch (e: Exception) {
			emit(e.toHError())
		}
	}

	override suspend fun loadDownloadCount(): HResult<Int> = try {
		app.shosetsu.common.dto.successResult(downloadsDao.loadDownloadCount())
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun loadFirstDownload(): HResult<DownloadEntity> = try {
		downloadsDao.loadFirstDownload()?.convertTo()?.let { app.shosetsu.common.dto.successResult(it) }
				?: app.shosetsu.common.dto.emptyResult()
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun insertDownload(downloadEntity: DownloadEntity): HResult<Long> = try {
		app.shosetsu.common.dto.successResult(downloadsDao.insertIgnore(downloadEntity.toDB()))
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun updateDownload(downloadEntity: DownloadEntity): HResult<*> = try {
		app.shosetsu.common.dto.successResult(downloadsDao.suspendedUpdate(downloadEntity.toDB()))
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun deleteDownload(downloadEntity: DownloadEntity): HResult<*> = try {
		app.shosetsu.common.dto.successResult(downloadsDao.suspendedDelete(downloadEntity.toDB()))
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun clearDownloads(): HResult<*> = try {
		app.shosetsu.common.dto.successResult(downloadsDao.clearData())
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun loadDownload(chapterID: Int): HResult<DownloadEntity> = try {
		app.shosetsu.common.dto.successResult(downloadsDao.loadDownload(chapterID).convertTo())
	} catch (e: Exception) {
		e.toHError()
	}
}