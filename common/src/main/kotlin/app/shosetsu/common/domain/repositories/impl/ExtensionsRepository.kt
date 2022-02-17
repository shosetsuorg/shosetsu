package app.shosetsu.common.domain.repositories.impl

import app.shosetsu.common.datasource.database.base.IDBInstalledExtensionsDataSource
import app.shosetsu.common.datasource.database.base.IDBRepositoryExtensionsDataSource
import app.shosetsu.common.datasource.remote.base.IRemoteExtensionDataSource
import app.shosetsu.common.domain.model.local.BrowseExtensionEntity
import app.shosetsu.common.domain.model.local.GenericExtensionEntity
import app.shosetsu.common.domain.model.local.InstalledExtensionEntity
import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository
import app.shosetsu.lib.exceptions.HTTPException
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
	private val installedDBSource: IDBInstalledExtensionsDataSource,
	private val repoDBSource: IDBRepositoryExtensionsDataSource,
	private val remoteSource: IRemoteExtensionDataSource,
) : IExtensionsRepository {
	override fun loadBrowseExtensions(): Flow<List<BrowseExtensionEntity>> {
		TODO("Not yet implemented")
	}

	override fun loadExtensionsFLow(): Flow<List<InstalledExtensionEntity>> =
		installedDBSource.loadExtensionsFlow()

	override fun getExtensionFlow(id: Int): Flow<InstalledExtensionEntity> =
		installedDBSource.loadExtensionLive(id)

	override suspend fun getExtension(id: Int): GenericExtensionEntity =
		repoDBSource.loadExtension(id)

	override suspend fun getInstalledExtension(id: Int): InstalledExtensionEntity? {
		TODO("Not yet implemented")
	}

	override suspend fun getRepositoryExtensions(repoID: Int): List<GenericExtensionEntity> {
		TODO("Not yet implemented")
	}

	override suspend fun loadRepositoryExtensions(): List<GenericExtensionEntity> {
		TODO("Not yet implemented")
	}

	override suspend fun uninstall(extensionEntity: InstalledExtensionEntity) {
		installedDBSource.deleteExtension(extensionEntity)
	}

	override suspend fun updateRepositoryExtension(extensionEntity: GenericExtensionEntity): Unit =
		repoDBSource.updateExtension(extensionEntity)

	override suspend fun updateInstalledExtension(extensionEntity: InstalledExtensionEntity) {
		installedDBSource.updateExtension(extensionEntity)
	}

	override suspend fun delete(extensionEntity: GenericExtensionEntity) {
		installedDBSource.loadExtension(extensionEntity.id)?.let {
			installedDBSource.deleteExtension(it)
		}

		repoDBSource.deleteExtension(extensionEntity)
	}

	override suspend fun insert(extensionEntity: GenericExtensionEntity): Long =
		repoDBSource.insert(extensionEntity)


	@Throws(HTTPException::class)
	override suspend fun downloadExtension(
		repositoryEntity: RepositoryEntity,
		extension: GenericExtensionEntity
	): ByteArray =
		remoteSource.downloadExtension(repositoryEntity, extension)

	override fun isExtensionInstalled(extensionEntity: GenericExtensionEntity): Boolean {
		TODO("Not yet implemented")
	}
}