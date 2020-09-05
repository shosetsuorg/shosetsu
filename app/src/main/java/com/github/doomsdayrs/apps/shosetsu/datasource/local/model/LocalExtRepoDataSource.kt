package com.github.doomsdayrs.apps.shosetsu.datasource.local.model

import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.errorResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.datasource.local.base.ILocalExtRepoDataSource
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.RepositoryEntity
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.RepositoryDao

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
class LocalExtRepoDataSource(
		private val repositoryDao: RepositoryDao,
) : ILocalExtRepoDataSource {
	override fun loadRepositoriesLive(): LiveData<HResult<List<RepositoryEntity>>> = liveData {
		try {
			emitSource(repositoryDao.loadRepositoriesLive().map { successResult(it) })
		} catch (e: SQLiteException) {
			emit(errorResult(e))
		}
	}

	override fun loadRepositories(): HResult<List<RepositoryEntity>> = try {
		successResult(repositoryDao.loadRepositories())
	} catch (e: SQLiteException) {
		errorResult(e)
	}

	override fun loadRepository(repoID: Int): HResult<RepositoryEntity> = try {
		successResult(repositoryDao.loadRepositoryFromID(repoID))
	} catch (e: SQLiteException) {
		errorResult(e)
	}
}