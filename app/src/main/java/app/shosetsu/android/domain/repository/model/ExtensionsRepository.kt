package app.shosetsu.android.domain.repository.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import app.shosetsu.android.common.consts.ErrorKeys
import app.shosetsu.android.common.consts.ErrorKeys.ERROR_GENERAL
import app.shosetsu.android.common.consts.ErrorKeys.ERROR_LUA_BROKEN
import app.shosetsu.android.common.dto.*
import app.shosetsu.android.common.ext.logError
import app.shosetsu.android.datasource.cache.base.ICacheExtensionsDataSource
import app.shosetsu.android.datasource.file.base.IFileExtensionDataSource
import app.shosetsu.android.datasource.local.base.ILocalExtRepoDataSource
import app.shosetsu.android.datasource.local.base.ILocalExtensionsDataSource
import app.shosetsu.android.datasource.remote.base.IRemoteCatalogueDataSource
import app.shosetsu.android.datasource.remote.base.IRemoteExtensionDataSource
import app.shosetsu.android.domain.model.local.ExtensionEntity
import app.shosetsu.android.domain.model.local.IDTitleImage
import app.shosetsu.android.domain.repository.base.IExtensionsRepository
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.LuaFormatter
import app.shosetsu.lib.Novel

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
		private val memorySource: ICacheExtensionsDataSource,
		private val databaseSource: ILocalExtensionsDataSource,
		private val fileSource: IFileExtensionDataSource,
		private val remoteSource: IRemoteExtensionDataSource,
		private val repositorySource: ILocalExtRepoDataSource,
		private val remoteCatalogueDataSource: IRemoteCatalogueDataSource,
) : IExtensionsRepository {
	override fun getExtensions(): LiveData<HResult<List<ExtensionEntity>>> =
			databaseSource.loadExtensions()

	override fun getExtensionLive(id: Int): LiveData<HResult<ExtensionEntity>> =
			databaseSource.loadExtensionLive(id)

	override suspend fun installExtension(extensionEntity: ExtensionEntity): HResult<*> {
		val repo = repositorySource.loadRepository(extensionEntity.repoID)
		if (repo is HResult.Success)
			when (val result = remoteSource.downloadExtension(
					repo.data,
					extensionEntity
			)) {
				is HResult.Success -> {
					try {
						val formatter = LuaFormatter(result.data)

						// Write to storage/cache
						memorySource.putFormatterInMemory(formatter)
						fileSource.writeFormatter(extensionEntity.fileName, result.data)

						// Update database info
						formatter.getMetaData()!!.let { meta ->
							val version = meta.getString("version")
							extensionEntity.installedVersion = version
							extensionEntity.repositoryVersion = version
						}
						extensionEntity.name = formatter.name
						extensionEntity.imageURL = formatter.imageURL
						extensionEntity.installed = true
						extensionEntity.enabled = true
						databaseSource.updateExtension(extensionEntity)
						return successResult("")
					} catch (e: IllegalArgumentException) {
						return errorResult(ERROR_LUA_BROKEN, e).also { logError { it } }
					} catch (e: Exception) {
						return errorResult(ERROR_GENERAL, e).also { logError { it } }
					}
				}
				is HResult.Error -> {
					logError { result }
					return result
				}
			}
		return emptyResult()
	}

	override suspend fun uninstallExtension(extensionEntity: ExtensionEntity): HResult<*> =
			memorySource.removeFormatterFromMemory(extensionEntity.id) and
					fileSource.deleteFormatter(extensionEntity.fileName) and
					databaseSource.updateExtension(
							extensionEntity.copy(enabled = false, installed = false, installedVersion = null)
					)

	override suspend fun insertOrUpdate(extensionEntity: ExtensionEntity): HResult<*> =
			databaseSource.insertOrUpdate(extensionEntity)

	override suspend fun updateExtension(extensionEntity: ExtensionEntity): HResult<*> =
			databaseSource.updateExtension(extensionEntity)

	override suspend fun loadFormatter(extensionEntity: ExtensionEntity): HResult<Formatter> = try {
		memorySource.loadFormatterFromMemory(extensionEntity.id).takeIf { it is HResult.Success }
				?: fileSource.loadFormatter(extensionEntity.fileName).takeIf { it is HResult.Success }
						?.also { if (it is HResult.Success) memorySource.putFormatterInMemory(it.data) }
				?: errorResult(ErrorKeys.ERROR_NOT_FOUND, "Formatter not found")
	} catch (e: NullPointerException) {
		errorResult(ErrorKeys.ERROR_IMPOSSIBLE, "Impossible NPE")
	}

	override suspend fun loadFormatter(formatterID: Int): HResult<Formatter> =
			databaseSource.loadExtension(formatterID).withSuccess { loadFormatter(it) }

	override fun getCards(): LiveData<HResult<List<IDTitleImage>>> = liveData {
		emitSource(databaseSource.loadPoweredExtensionsCards())
	}

	override suspend fun loadCatalogueSearch(
			formatter: Formatter,
			query: String,
			page: Int,
			data: Map<Int, Any>
	): HResult<List<Novel.Listing>> =
			remoteCatalogueDataSource.search(
					formatter, query, page, data
			)

	override suspend fun loadCatalogueData(
			formatter: Formatter,
			listing: Int,
			page: Int,
			data: Map<Int, Any>,
	): HResult<List<Novel.Listing>> =
			remoteCatalogueDataSource.loadListing(formatter, listing, page, data)
}