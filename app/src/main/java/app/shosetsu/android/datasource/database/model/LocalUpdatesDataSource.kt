package app.shosetsu.android.datasource.database.model

import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.errorResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.datasource.database.base.ILocalUpdatesDataSource
import app.shosetsu.android.domain.model.local.UpdateCompleteEntity
import app.shosetsu.android.domain.model.local.UpdateEntity
import app.shosetsu.android.providers.database.dao.UpdatesDao

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
class LocalUpdatesDataSource(
		private val updatesDao: UpdatesDao,
) : ILocalUpdatesDataSource {
	override suspend fun getUpdates(): LiveData<HResult<List<UpdateEntity>>> = liveData {
		try {
			emitSource(updatesDao.loadUpdates().map { successResult(it) })
		} catch (e: SQLiteException) {
			emit(errorResult(e))
		} catch (e: NullPointerException) {
			emit(errorResult(e))
		}
	}

	override suspend fun insertUpdates(list: List<UpdateEntity>): HResult<Array<Long>> = try {
		successResult(updatesDao.insertAllIgnore(list))
	} catch (e: SQLiteException) {
		errorResult(e)
	} catch (e: NullPointerException) {
		errorResult(e)
	}

	override suspend fun getCompleteUpdates(
	): LiveData<HResult<List<UpdateCompleteEntity>>> = liveData {
		try {
			emitSource(updatesDao.loadCompleteUpdates().map { successResult(it) })
		} catch (e: SQLiteException) {
			emit(errorResult(e))
		} catch (e: NullPointerException) {
			emit(errorResult(e))
		}
	}
}