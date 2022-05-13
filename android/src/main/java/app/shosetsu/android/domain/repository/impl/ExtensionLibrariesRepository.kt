package app.shosetsu.android.domain.repository.impl

import app.shosetsu.android.common.GenericSQLiteException
import app.shosetsu.android.datasource.file.base.IFileExtLibDataSource
import app.shosetsu.android.datasource.local.database.base.IDBExtLibDataSource
import app.shosetsu.android.datasource.local.memory.base.IMemExtLibDataSource
import app.shosetsu.android.datasource.remote.base.IRemoteExtLibDataSource
import app.shosetsu.android.domain.model.local.ExtLibEntity
import app.shosetsu.android.domain.repository.base.IExtensionLibrariesRepository
import app.shosetsu.lib.Version
import app.shosetsu.lib.json.J_VERSION
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

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
	@Throws(GenericSQLiteException::class)
	override suspend fun loadExtLibByRepo(
		repoID: Int,
	): List<ExtLibEntity> =
		databaseSource.loadExtLibByRepo(repoID)

	@Throws(GenericSQLiteException::class)
	override suspend fun installExtLibrary(
		repoURL: String,
		extLibEntity: ExtLibEntity,
	) {
		val data = remoteSource.downloadLibrary(repoURL, extLibEntity)
		val json =
			Json.parseToJsonElement(data.substring(0, data.indexOf("\n")).replace("--", "").trim())
		extLibEntity.version = Version(json.jsonObject[J_VERSION]!!.jsonPrimitive.content)
		databaseSource.updateOrInsert(extLibEntity)
		memSource.setLibrary(extLibEntity.scriptName, data)
		fileSource.writeExtLib(extLibEntity.scriptName, data)
	}

	override fun blockingLoadExtLibrary(name: String): String =
		memSource.loadLibrary(name) ?: fileSource.blockingLoadLib(name).also {
			memSource.setLibrary(name, it)
		}
}