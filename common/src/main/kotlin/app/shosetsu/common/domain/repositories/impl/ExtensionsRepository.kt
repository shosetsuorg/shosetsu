package app.shosetsu.common.domain.repositories.impl

import app.shosetsu.common.consts.ErrorKeys.ERROR_EXT_INCOMPATIBLE
import app.shosetsu.common.consts.ErrorKeys.ERROR_GENERAL
import app.shosetsu.common.consts.ErrorKeys.ERROR_LUA_BROKEN
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.datasource.database.base.IDBExtRepoDataSource
import app.shosetsu.common.datasource.database.base.IDBExtensionsDataSource
import app.shosetsu.common.datasource.file.base.IFileExtensionDataSource
import app.shosetsu.common.datasource.file.base.IFileSettingsDataSource
import app.shosetsu.common.datasource.memory.base.IMemExtensionsDataSource
import app.shosetsu.common.datasource.remote.base.IRemoteExtensionDataSource
import app.shosetsu.common.domain.model.local.ExtensionEntity
import app.shosetsu.common.domain.model.local.StrippedExtensionEntity
import app.shosetsu.common.domain.repositories.base.IExtensionsRepository
import app.shosetsu.common.dto.*
import app.shosetsu.common.utils.asIEntity
import app.shosetsu.lib.Filter
import app.shosetsu.lib.IExtension
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
	private val dbSource: IDBExtensionsDataSource,
	private val fileSource: IFileExtensionDataSource,
	private val remoteSource: IRemoteExtensionDataSource,
	private val dbRepoSource: IDBExtRepoDataSource,
	private val settingsSource: IFileSettingsDataSource
) : IExtensionsRepository {

	suspend fun getInt(extensionID: Int, settingID: Int, default: Int): HResult<Int> =
		settingsSource.getInt("$extensionID", SettingKey.CustomInt("$settingID", default))

	suspend fun getString(
		extensionID: Int,
		settingID: Int,
		default: String
	): HResult<String> =
		settingsSource.getString("$extensionID", SettingKey.CustomString("$settingID", default))

	suspend fun getBoolean(
		extensionID: Int,
		settingID: Int,
		default: Boolean
	): HResult<Boolean> =
		settingsSource.getBoolean(
			"$extensionID",
			SettingKey.CustomBoolean("$settingID", default)
		)

	suspend fun getFloat(
		extensionID: Int,
		settingID: Int,
		default: Float
	): HResult<Float> =
		settingsSource.getFloat("$extensionID", SettingKey.CustomFloat("$settingID", default))


	private suspend fun setSettings(extension: IExtension, filters: Array<out Filter<out Any?>>) {
		filters.forEach { filter ->
			when (filter) {
				is Filter.Text -> {
					extension.updateSetting(
						filter.id,
						getString(extension.formatterID, filter.id, filter.state)
					)
				}
				is Filter.Switch -> {
					extension.updateSetting(
						filter.id,
						getBoolean(extension.formatterID, filter.id, filter.state)
					)
				}
				is Filter.Checkbox -> {
					extension.updateSetting(
						filter.id,
						getBoolean(extension.formatterID, filter.id, filter.state)
					)
				}
				is Filter.TriState -> {
					extension.updateSetting(
						filter.id,
						getInt(extension.formatterID, filter.id, filter.state)
					)
				}
				is Filter.Dropdown -> {
					extension.updateSetting(
						filter.id,
						getInt(extension.formatterID, filter.id, filter.state)
					)
				}
				is Filter.RadioGroup -> {
					extension.updateSetting(
						filter.id,
						getInt(extension.formatterID, filter.id, filter.state)
					)
				}
				is Filter.List -> {
					setSettings(extension, filter.filters)
				}
				is Filter.Group<*> -> {
					setSettings(extension, filter.filters)
				}
				else -> {
				}
			}
		}
	}


	override fun loadExtensionEntitiesFLow(): Flow<HResult<List<ExtensionEntity>>> =
		dbSource.loadExtensions()


	override fun getExtensionEntityFlow(id: Int): Flow<HResult<ExtensionEntity>> =
		dbSource.loadExtensionLive(id)

	override suspend fun getExtensionEntity(id: Int): HResult<ExtensionEntity> =
		dbSource.loadExtension(id)

	override suspend fun getExtensionEntities(repoID: Int): HResult<List<ExtensionEntity>> =
		dbSource.getExtensions(repoID)

	override suspend fun installExtension(extensionEntity: ExtensionEntity): HResult<IExtensionsRepository.InstallExtensionFlags> =
		dbRepoSource.loadRepository(extensionEntity.repoID).transform { repo ->
			remoteSource.downloadExtension(
				repo,
				extensionEntity
			).transform { extensionContent ->
				try {
					val iExt = extensionEntity.asIEntity(extensionContent)

					// Write to storage/cache
					memorySource.putExtensionInMemory(iExt)
					fileSource.writeExtension(extensionEntity, extensionContent)

					// Update database info
					iExt.exMetaData.let { meta ->
						extensionEntity.installedVersion = meta.version
						extensionEntity.repositoryVersion = meta.version
					}

					extensionEntity.name = iExt.name
					extensionEntity.imageURL = iExt.imageURL
					extensionEntity.installed = true
					extensionEntity.enabled = true

					val deleteChapters = extensionEntity.chapterType != iExt.chapterType
					extensionEntity.chapterType = iExt.chapterType

					dbSource.updateExtension(extensionEntity)

					successResult(IExtensionsRepository.InstallExtensionFlags(deleteChapters))

				} catch (e: IllegalArgumentException) {
					errorResult(ERROR_LUA_BROKEN, e)
				} catch (e: Exception) {
					errorResult(ERROR_GENERAL, e)
				}
			}
		}


	override suspend fun uninstallExtension(extensionEntity: ExtensionEntity): HResult<*> =
		memorySource.removeExtensionFromMemory(extensionEntity.id) ifSo {
			fileSource.deleteExtension(extensionEntity) ifSo {
				dbSource.updateExtension(
					extensionEntity.copy(
						enabled = false,
						installed = false,
						installedVersion = null
					)
				)
			}
		}

	override suspend fun insertOrUpdate(extensionEntity: ExtensionEntity): HResult<Int> =
		dbSource.insertOrUpdate(extensionEntity)

	override suspend fun updateExtensionEntity(extensionEntity: ExtensionEntity): HResult<*> =
		dbSource.updateExtension(extensionEntity)

	override suspend fun getIExtension(extensionEntity: ExtensionEntity): HResult<IExtension> =
		memorySource.loadExtensionFromMemory(extensionEntity.id).catch {
			fileSource.loadExtension(extensionEntity).transform {
				if (!it.exMetaData.libVersion.isCompatible())
					return errorResult(ERROR_EXT_INCOMPATIBLE)
				setSettings(it, it.settingsModel)
				memorySource.putExtensionInMemory(it)
				successResult(it)
			}
		}

	override suspend fun getIExtension(extensionID: Int): HResult<IExtension> =
		dbSource.loadExtension(extensionID).transform { getIExtension(it) }

	override fun loadStrippedExtensionEntityFlow(): Flow<HResult<List<StrippedExtensionEntity>>> =
		dbSource.loadPoweredExtensionsCards()


	override suspend fun removeExtension(extensionEntity: ExtensionEntity): HResult<*> =
		memorySource.removeExtensionFromMemory(extensionEntity.id) ifSo {
			fileSource.deleteExtension(extensionEntity) ifSo {
				dbSource.deleteExtension(extensionEntity)
			}
		}
}