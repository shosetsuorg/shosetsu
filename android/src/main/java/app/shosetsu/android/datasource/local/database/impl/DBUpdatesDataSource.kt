package app.shosetsu.android.datasource.local.database.impl

import app.shosetsu.android.common.ext.toDB
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.providers.database.dao.UpdatesDao
import app.shosetsu.common.datasource.database.base.IDBUpdatesDataSource
import app.shosetsu.common.domain.model.local.UpdateCompleteEntity
import app.shosetsu.common.domain.model.local.UpdateEntity
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.mapLatestListTo
import app.shosetsu.common.dto.mapLatestToSuccess
import app.shosetsu.common.dto.successResult
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
class DBUpdatesDataSource(
	private val updatesDao: UpdatesDao,
) : IDBUpdatesDataSource {
	@ExperimentalCoroutinesApi
	override suspend fun getUpdates(): Flow<HResult<List<UpdateEntity>>> = flow {
		try {
			emitAll(updatesDao.loadUpdates().mapLatestListTo().mapLatestToSuccess())
		} catch (e: Exception) {
			emit(e.toHError())
		}
	}

	override suspend fun insertUpdates(list: List<UpdateEntity>): HResult<Array<Long>> = try {
		successResult(updatesDao.insertAllReplace(list.toDB()))
	} catch (e: Exception) {
		e.toHError()
	}

	@ExperimentalCoroutinesApi
	override suspend fun getCompleteUpdates(
	): Flow<HResult<List<UpdateCompleteEntity>>> = flow {
		try {
			emitAll(updatesDao.loadCompleteUpdates().mapLatestToSuccess())
		} catch (e: Exception) {
			emit(e.toHError())
		}
	}
}