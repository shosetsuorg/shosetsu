package com.github.doomsdayrs.apps.shosetsu.domain.repository.model

import android.util.Log
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.datasource.cache.base.ICacheExtLibDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.file.base.IFileExtLibDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.local.base.ILocalExtLibDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.remote.base.IRemoteExtLibDataSource
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ExtLibEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.RepositoryEntity
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IExtLibRepository
import org.json.JSONException
import org.json.JSONObject

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
class ExtLibRepository(
		private val fileSource: IFileExtLibDataSource,
		private val databaseSource: ILocalExtLibDataSource,
		private val remoteSource: IRemoteExtLibDataSource,
		private val cacheSource: ICacheExtLibDataSource
) : IExtLibRepository {
	override suspend fun loadExtLibByRepo(
			repositoryEntity: RepositoryEntity
	): HResult<List<ExtLibEntity>> =
			databaseSource.loadExtLibByRepo(repositoryEntity)

	override suspend fun installExtLibrary(
			repositoryEntity: RepositoryEntity,
			extLibEntity: ExtLibEntity
	) {
		val result = remoteSource.downloadLibrary(repositoryEntity, extLibEntity)
		if (result is HResult.Success) {
			val data = result.data

			val json = JSONObject(data.substring(0, data.indexOf("\n")).replace("--", "").trim())
			try {
				extLibEntity.version = json.getString("version")
				databaseSource.updateOrInsert(extLibEntity)
				cacheSource.setLibrary(extLibEntity.scriptName, data)
				fileSource.writeExtLib(extLibEntity.scriptName, data)
			} catch (e: JSONException) {
				Log.e(logID(), "Unhandled", e)
			}
		}
	}

	override fun blockingLoadExtLibrary(name: String): HResult<String> =
			cacheSource.blockingLoadLibrary(name).takeIf { it is HResult.Success }
					?: fileSource.blockingLoadLib(name).also {
						if (it is HResult.Success)
							cacheSource.blockingSetLibrary(name, it.data)
					}
}