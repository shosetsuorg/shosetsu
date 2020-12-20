package app.shosetsu.common.domain.repositories.impl

import app.shosetsu.common.consts.ErrorKeys
import app.shosetsu.common.consts.ErrorKeys.ERROR_GENERAL
import app.shosetsu.common.consts.ErrorKeys.ERROR_LUA_BROKEN
import app.shosetsu.common.datasource.database.base.ILocalExtRepoDataSource
import app.shosetsu.common.datasource.database.base.ILocalExtensionsDataSource
import app.shosetsu.common.datasource.file.base.IFileExtensionDataSource
import app.shosetsu.common.datasource.memory.base.IMemExtensionsDataSource
import app.shosetsu.common.datasource.remote.base.IRemoteCatalogueDataSource
import app.shosetsu.common.datasource.remote.base.IRemoteExtensionDataSource
import app.shosetsu.common.domain.model.local.ExtensionEntity
import app.shosetsu.common.domain.model.local.StrippedExtensionEntity
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository
import app.shosetsu.common.dto.*
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Novel
import app.shosetsu.lib.lua.LuaExtension
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
	private val memorySource: IMemExtensionsDataSource,
	private val databaseSource: ILocalExtensionsDataSource,
	private val fileSource: IFileExtensionDataSource,
	private val remoteSource: IRemoteExtensionDataSource,
	private val repositorySource: ILocalExtRepoDataSource,
	private val remoteCatalogueDataSource: IRemoteCatalogueDataSource,
) : IExtensionsRepository {
	override fun loadExtensionEntitiesFLow(): Flow<HResult<List<ExtensionEntity>>> =
		databaseSource.loadExtensions()


	override fun getExtensionEntityFlow(id: Int): Flow<HResult<ExtensionEntity>> =
		databaseSource.loadExtensionLive(id)

	override suspend fun getExtensionEntity(id: Int): HResult<ExtensionEntity> =
		databaseSource.loadExtension(id)

	override suspend fun getExtensionEntities(repoID: Int): HResult<List<ExtensionEntity>> =
		databaseSource.getExtensions(repoID)

	override suspend fun installExtension(extensionEntity: ExtensionEntity): HResult<*> {
		val repo = repositorySource.loadRepository(extensionEntity.repoID)
		if (repo is HResult.Success)
			when (val result = remoteSource.downloadExtension(
				repo.data,
				extensionEntity
			)) {
				is HResult.Success -> {
					try {
						val formatter = LuaExtension(result.data)

						// Write to storage/cache
						memorySource.putExtensionInMemory(formatter)
						fileSource.writeExtension(extensionEntity.fileName, result.data)

						// Update database info
						formatter.exMetaData.let { meta ->
							extensionEntity.installedVersion = meta.version
							extensionEntity.repositoryVersion = meta.version
						}
						extensionEntity.name = formatter.name
						extensionEntity.imageURL = formatter.imageURL
						extensionEntity.installed = true
						extensionEntity.enabled = true
						databaseSource.updateExtension(extensionEntity)
						return successResult("")
					} catch (e: IllegalArgumentException) {
						return errorResult(ERROR_LUA_BROKEN, e)
					} catch (e: Exception) {
						return errorResult(ERROR_GENERAL, e)
					}
				}
				is HResult.Error -> {
					return result
				}
			}
		return emptyResult()
	}

	override suspend fun uninstallExtension(extensionEntity: ExtensionEntity): HResult<*> =
		memorySource.removeExtensionFromMemory(extensionEntity.id) and
				fileSource.deleteExtension(extensionEntity.fileName) and
				databaseSource.updateExtension(
					extensionEntity.copy(
						enabled = false,
						installed = false,
						installedVersion = null
					)
				)

	override suspend fun insertOrUpdate(extensionEntity: ExtensionEntity): HResult<*> =
		databaseSource.insertOrUpdate(extensionEntity)

	override suspend fun updateExtensionEntity(extensionEntity: ExtensionEntity): HResult<*> =
		databaseSource.updateExtension(extensionEntity)

	override suspend fun getIExtension(extensionEntity: ExtensionEntity): HResult<IExtension> {
		memorySource.loadExtensionFromMemory(extensionEntity.id)
			.takeIf { it is HResult.Success }?.let { return it }

		val fileResult = fileSource.loadExtension(extensionEntity.fileName)
		if (fileResult !is HResult.Success)
			return errorResult(ErrorKeys.ERROR_NOT_FOUND, "Extension file not found")

		if (!fileResult.data.exMetaData.libVersion.isCompatible())
			return errorResult(ErrorKeys.ERROR_INCOMPATIBLE)

		memorySource.putExtensionInMemory(fileResult.data)
		return fileResult
	}

	override suspend fun getIExtension(extensionID: Int): HResult<IExtension> =
		databaseSource.loadExtension(extensionID).transform { getIExtension(it) }

	override fun loadStrippedExtensionEntityFlow(): Flow<HResult<List<StrippedExtensionEntity>>> =
		databaseSource.loadPoweredExtensionsCards()


	override suspend fun getCatalogueSearch(
		ext: IExtension,
		query: String,
		data: Map<Int, Any>
	): HResult<List<Novel.Listing>> =
		remoteCatalogueDataSource.search(
			ext, query, data
		)

	override suspend fun getCatalogueData(
		ext: IExtension,
		listing: Int,
		data: Map<Int, Any>,
	): HResult<List<Novel.Listing>> =
		remoteCatalogueDataSource.loadListing(ext, listing, data)

	override suspend fun removeExtension(extensionEntity: ExtensionEntity): HResult<*> =
		databaseSource.deleteExtension(extensionEntity) and
				fileSource.deleteExtension(extensionEntity.fileName) and
				memorySource.removeExtensionFromMemory(extensionEntity.id)
}