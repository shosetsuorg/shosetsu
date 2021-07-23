package app.shosetsu.android.domain.repository.impl

import app.shosetsu.android.common.dto.errorResult
import app.shosetsu.common.datasource.database.base.IDBExtLibDataSource
import app.shosetsu.common.datasource.file.base.IFileExtLibDataSource
import app.shosetsu.common.datasource.memory.base.IMemExtLibDataSource
import app.shosetsu.common.datasource.remote.base.IRemoteExtLibDataSource
import app.shosetsu.common.domain.model.local.ExtLibEntity
import app.shosetsu.common.domain.repositories.base.IExtensionLibrariesRepository
import app.shosetsu.common.dto.*
import app.shosetsu.lib.Version
import app.shosetsu.lib.json.J_VERSION
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
class ExtensionLibrariesRepository(
	private val fileSource: IFileExtLibDataSource,
	private val databaseSource: IDBExtLibDataSource,
	private val remoteSource: IRemoteExtLibDataSource,
	private val memSource: IMemExtLibDataSource,
) : IExtensionLibrariesRepository {
	override suspend fun loadExtLibByRepo(
		repoID: Int,
	): HResult<List<ExtLibEntity>> =
		databaseSource.loadExtLibByRepo(repoID)

	@Throws(JSONException::class)
	override suspend fun installExtLibrary(
		repoURL: String,
		extLibEntity: ExtLibEntity,
	): HResult<*> =
		remoteSource.downloadLibrary(repoURL, extLibEntity).transform {
			val data = it
			val json = JSONObject(data.substring(0, data.indexOf("\n")).replace("--", "").trim())
			try {
				extLibEntity.version = Version(json.getString(J_VERSION))
				databaseSource.updateOrInsert(extLibEntity).ifSo {
					memSource.setLibrary(extLibEntity.scriptName, data).ifSo {
						fileSource.writeExtLib(extLibEntity.scriptName, data)
					}
				}
			} catch (e: JSONException) {
				errorResult(e)
			}
		}

	override fun blockingLoadExtLibrary(name: String): HResult<String> =
		memSource.loadLibrary(name).catch {
			fileSource.blockingLoadLib(name).transform {
				memSource.setLibrary(name, it)
				successResult(it)
			}
		}
}