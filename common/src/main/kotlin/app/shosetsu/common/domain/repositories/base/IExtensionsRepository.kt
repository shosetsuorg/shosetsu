package app.shosetsu.common.domain.repositories.base

import app.shosetsu.common.domain.model.local.ExtensionEntity
import app.shosetsu.common.domain.model.local.StrippedExtensionEntity
import app.shosetsu.common.dto.HResult
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
 * 25 / 04 / 2020
 *
 * Repository for [IExtension]
 */
interface IExtensionsRepository {
	/**
	 * [Flow] of all [ExtensionEntity] that the app knows of
	 *
	 * @return
	 * [HResult.Success] When properly emits
	 *
	 * [HResult.Error] If anything went wrong in the stream
	 *
	 * [HResult.Loading] Initial value emitted
	 *
	 * [HResult.Empty] Should never occur?
	 */
	fun loadExtensionEntitiesFLow(): Flow<HResult<List<ExtensionEntity>>>

	/**
	 * [Flow] of the [ExtensionEntity] with an [ExtensionEntity.id] matching [id]
	 *
	 * @return
	 * [HResult.Success] When properly emits
	 *
	 * [HResult.Error] If anything went wrong in the stream
	 *
	 * [HResult.Loading] Initial value emitted
	 *
	 * [HResult.Empty] If no [ExtensionEntity] matches [id]
	 */
	fun getExtensionEntityFlow(id: Int): Flow<HResult<ExtensionEntity>>

	/**
	 * Gets the [ExtensionEntity] that has an [ExtensionEntity.id] matching [id]
	 *
	 * @return
	 * [HResult.Success] If the extension is found
	 *
	 * [HResult.Error] If something went wrong retrieving the result
	 *
	 * [HResult.Empty] If nothing was found
	 *
	 * [HResult.Loading] never
	 */
	suspend fun getExtensionEntity(id: Int): HResult<ExtensionEntity>

	/**
	 * Loads all [ExtensionEntity] with an [ExtensionEntity.repoID] matching [repoID]
	 *
	 * @return
	 * [HResult.Success]
	 *
	 * [HResult.Error]
	 *
	 * [HResult.Empty]
	 *
	 * [HResult.Loading]
	 */
	suspend fun getExtensionEntities(repoID: Int): HResult<List<ExtensionEntity>>

	/**
	 * Installs an [extensionEntity]
	 *
	 * Adds the [IExtension] to the filesystem & memory
	 *
	 * @see InstallExtensionFlags
	 * @return
	 * [HResult.Success] If the extension is found
	 *
	 * [HResult.Error] If something went wrong retrieving the result
	 *
	 * [HResult.Empty] If nothing was found
	 *
	 * [HResult.Loading] never
	 */
	suspend fun installExtension(extensionEntity: ExtensionEntity): HResult<InstallExtensionFlags>

	/**
	 * Flags returned after installing an extension
	 */
	data class InstallExtensionFlags(
		val deleteChapters: Boolean
	)

	/**
	 * Uninstalls an [extensionEntity]
	 *
	 * This removes the [extensionEntity] from memory & filesystem
	 *
	 * Updates the source that the [extensionEntity] is not installed
	 *
	 * @return
	 * [HResult.Success] Updated
	 *
	 * [HResult.Error] Error
	 *
	 * [HResult.Empty] never
	 *
	 * [HResult.Loading] never
	 */
	suspend fun uninstallExtension(extensionEntity: ExtensionEntity): HResult<*>

	/**
	 * Inserts or Updates an [extensionEntity]
	 *
	 * Safe call to not cause duplicate entities
	 *
	 * @return
	 * [HResult.Success] Inserted or Updated
	 *
	 * [HResult.Error] Something went wrong
	 *
	 * [HResult.Empty] never
	 *
	 * [HResult.Loading] never
	 */
	suspend fun insertOrUpdate(extensionEntity: ExtensionEntity): HResult<Int>

	/**
	 * Updates an [extensionEntity]
	 *
	 * @return
	 * [HResult.Success] if the operation completed properly
	 *
	 * [HResult.Error] if anything went wrong
	 *
	 * [HResult.Empty] never
	 *
	 * [HResult.Loading] never
	 */
	suspend fun updateExtensionEntity(extensionEntity: ExtensionEntity): HResult<*>

	/**
	 * Gets an [IExtension] via it's [extensionEntity]
	 *
	 * @return
	 * [HResult.Empty]  if no extension was found
	 *
	 * [HResult.Error]  if an issue occluded when creating the extension
	 * or any other portion of the loading code
	 *
	 * [HResult.Success] if the extension was properly loaded
	 *
	 * [HResult.Loading] will never be thrown
	 */
	suspend fun getIExtension(extensionEntity: ExtensionEntity): HResult<IExtension>

	/**
	 * Gets an [IExtension] via its [extensionID]
	 *
	 * @see [getIExtension]
	 * @return see [getIExtension]
	 */
	suspend fun getIExtension(extensionID: Int): HResult<IExtension>

	/**
	 * Gets enabled [ExtensionEntity] but as [StrippedExtensionEntity]
	 *
	 * This method is more IO efficient, as it should not be loading extra data it does not need
	 * as compared to calling for all the [ExtensionEntity]s and just mapping them
	 *
	 * @return
	 * [HResult.Success] Successfully loaded
	 *
	 * [HResult.Error] Error occurred loading
	 *
	 * [HResult.Empty] No extensions
	 *
	 * [HResult.Loading] Initial value
	 */
	fun loadStrippedExtensionEntityFlow(): Flow<HResult<List<StrippedExtensionEntity>>>


	/**
	 * Removes an [ExtensionEntity] entirely, completely removing it from shosetsu code
	 *
	 * This will also remove it from memory, and the file system. Releasing the resources
	 *
	 * @return
	 * [HResult.Success] [extensionEntity] removed
	 *
	 * [HResult.Error] Error removing [extensionEntity]
	 *
	 * [HResult.Empty] never
	 *
	 * [HResult.Loading] never
	 */
	suspend fun removeExtension(extensionEntity: ExtensionEntity): HResult<*>
}