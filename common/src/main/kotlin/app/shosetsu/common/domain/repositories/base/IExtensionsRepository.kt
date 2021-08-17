package app.shosetsu.common.domain.repositories.base

import app.shosetsu.common.domain.model.local.ExtensionEntity
import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.common.domain.model.local.StrippedExtensionEntity
import app.shosetsu.common.dto.HResult
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Novel
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
	fun loadExtensionsFLow(): Flow<HResult<List<ExtensionEntity>>>

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
	fun getExtensionFlow(id: Int): Flow<HResult<ExtensionEntity>>

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
	suspend fun getExtension(id: Int): HResult<ExtensionEntity>

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
	suspend fun getExtensions(repoID: Int): HResult<List<ExtensionEntity>>

	/**
	 * Loads all [ExtensionEntity] present
	 */
	suspend fun loadExtensions(): HResult<List<ExtensionEntity>>

	/**
	 * Flags returned after installing an extension
	 *
	 * @param deleteChapters True if old chapters should be deleted
	 * @param oldType not null if [deleteChapters] is true,
	 *  otherwise provides the expected type to delete
	 */
	data class InstallExtensionFlags(
		val deleteChapters: Boolean,
		val oldType: Novel.ChapterType? = null
	)

	/**
	 * Updates the db that the [extensionEntity] is not installed
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
	suspend fun uninstall(extensionEntity: ExtensionEntity): HResult<*>

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
	suspend fun update(extensionEntity: ExtensionEntity): HResult<*>

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
	fun loadStrippedExtensionFlow(): Flow<HResult<List<StrippedExtensionEntity>>>


	/**
	 * This removes the extension from db
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
	suspend fun delete(extensionEntity: ExtensionEntity): HResult<*>

	suspend fun insert(extensionEntity: ExtensionEntity): HResult<*>

	suspend fun downloadExtension(
		repositoryEntity: RepositoryEntity,
		extension: ExtensionEntity
	): HResult<ByteArray>
}