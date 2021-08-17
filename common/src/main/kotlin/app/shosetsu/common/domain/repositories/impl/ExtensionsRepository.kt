package app.shosetsu.common.domain.repositories.impl

import app.shosetsu.common.datasource.database.base.IDBExtensionsDataSource
import app.shosetsu.common.datasource.remote.base.IRemoteExtensionDataSource
import app.shosetsu.common.domain.model.local.ExtensionEntity
import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.common.domain.model.local.StrippedExtensionEntity
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository
import app.shosetsu.common.dto.HResult
import kotlinx.coroutines.flow.Flow

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
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionsRepository(
	private val dbSource: IDBExtensionsDataSource,
	private val remoteSource: IRemoteExtensionDataSource,
) : IExtensionsRepository {

	override fun loadExtensionsFLow(): Flow<HResult<List<ExtensionEntity>>> =
		dbSource.loadExtensionsFlow()

	override fun getExtensionFlow(id: Int): Flow<HResult<ExtensionEntity>> =
		dbSource.loadExtensionLive(id)

	override suspend fun getExtension(id: Int): HResult<ExtensionEntity> =
		dbSource.loadExtension(id)

	override suspend fun getExtensions(repoID: Int): HResult<List<ExtensionEntity>> =
		dbSource.getExtensions(repoID)

	override suspend fun loadExtensions(): HResult<List<ExtensionEntity>> =
		dbSource.loadExtensions()

	override suspend fun uninstall(extensionEntity: ExtensionEntity): HResult<*> =
		dbSource.updateExtension(
			extensionEntity.copy(
				enabled = false,
				installed = false,
				installedVersion = null
			)
		)

	override suspend fun update(extensionEntity: ExtensionEntity): HResult<*> =
		dbSource.updateExtension(extensionEntity)

	override fun loadStrippedExtensionFlow(): Flow<HResult<List<StrippedExtensionEntity>>> =
		dbSource.loadPoweredExtensionsCards()

	override suspend fun delete(extensionEntity: ExtensionEntity): HResult<*> =
		dbSource.deleteExtension(extensionEntity)

	override suspend fun insert(extensionEntity: ExtensionEntity): HResult<*> =
		dbSource.insert(extensionEntity)

	override suspend fun downloadExtension(
		repositoryEntity: RepositoryEntity,
		extension: ExtensionEntity
	): HResult<ByteArray> =
		remoteSource.downloadExtension(repositoryEntity, extension)
}