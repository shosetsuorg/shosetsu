package app.shosetsu.common.domain.repositories.impl

import app.shosetsu.common.datasource.database.base.IDBExtRepoDataSource
import app.shosetsu.common.datasource.database.base.IDBInstalledExtensionsDataSource
import app.shosetsu.common.datasource.database.base.IDBRepositoryExtensionsDataSource
import app.shosetsu.common.datasource.remote.base.IRemoteExtensionDataSource
import app.shosetsu.common.domain.model.local.*
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository
import app.shosetsu.lib.exceptions.HTTPException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transformLatest

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
	private val _repoDBSource: IDBExtRepoDataSource
) : IExtensionsRepository {
	@OptIn(ExperimentalCoroutinesApi::class)
	override fun loadBrowseExtensions(): Flow<List<BrowseExtensionEntity>> {
		return repoDBSource.loadExtensionsFlow().transformLatest { list ->

			val browseExtensions = list.groupBy { it.id }.map { (extId, matchingExtensions) ->
				println("Working on extension $extId")
				val installedExt = installedDBSource.loadExtension(extId)
				val firstExt = matchingExtensions.first()

				BrowseExtensionEntity(
					id = extId,
					name = installedExt?.name ?: firstExt.name,
					imageURL = installedExt?.imageURL ?: firstExt.imageURL,
					lang = installedExt?.lang ?: firstExt.lang,
					installOptions = if (installedExt == null) {
						matchingExtensions.mapNotNull { genericExt ->
							val repo = _repoDBSource.loadRepository(genericExt.id)

							if (repo != null && repo.isEnabled)
								ExtensionInstallOptionEntity(
									genericExt.repoID,
									repo.name,
									genericExt.version
								)
							else null
						}
					} else emptyList(),
					isInstalled = installedExt != null,
					installedVersion = installedExt?.version,
					installedRepo = installedExt?.repoID ?: -1,
					isUpdateAvailable = if (installedExt != null) {
						val repoVersion =
							matchingExtensions.find { it.repoID == installedExt.repoID }?.version

						if (repoVersion != null) {
							installedExt.version > repoVersion
						} else false
					} else false,
					updateVersion = matchingExtensions.find { it.repoID == installedExt?.repoID }?.version,
					isInstalling = false, // We can ignore this, another layer will set it
				)
			}

			emit(browseExtensions)
		}

	}

	override fun loadExtensionsFLow(): Flow<List<InstalledExtensionEntity>> =
		installedDBSource.loadExtensionsFlow()

	override fun getExtensionFlow(id: Int): Flow<InstalledExtensionEntity> =
		installedDBSource.loadExtensionLive(id)

	override suspend fun getExtension(repoId: Int, extId: Int): GenericExtensionEntity? =
		repoDBSource.loadExtension(repoId, extId)

	override suspend fun getInstalledExtension(id: Int): InstalledExtensionEntity? =
		installedDBSource.loadExtension(id)

	override suspend fun getRepositoryExtensions(repoID: Int): List<GenericExtensionEntity> =
		repoDBSource.getExtensions(repoID)

	override suspend fun loadRepositoryExtensions(): List<GenericExtensionEntity> =
		repoDBSource.loadExtensions()

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

	override suspend fun isExtensionInstalled(extensionEntity: GenericExtensionEntity): Boolean =
		installedDBSource.loadExtension(extensionEntity.id) != null
}