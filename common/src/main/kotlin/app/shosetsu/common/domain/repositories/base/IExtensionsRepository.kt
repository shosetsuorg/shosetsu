package app.shosetsu.common.domain.repositories.base

import app.shosetsu.common.domain.model.local.BrowseExtensionEntity
import app.shosetsu.common.domain.model.local.GenericExtensionEntity
import app.shosetsu.common.domain.model.local.InstalledExtensionEntity
import app.shosetsu.common.domain.model.local.RepositoryEntity
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Novel
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
 * 25 / 04 / 2020
 *
 * Repository for [IExtension]
 */
interface IExtensionsRepository {

	fun loadBrowseExtensions(): Flow<List<BrowseExtensionEntity>>

	/**
	 * [Flow] of all [GenericExtensionEntity] that the app knows of
	 */
	fun loadExtensionsFLow(): Flow<List<GenericExtensionEntity>>

	/**
	 * Retrieves repository extensions with an id matching [id]
	 */
	fun getExtensionFlow(id: Int): Flow<GenericExtensionEntity>

	/**
	 * Gets the [GenericExtensionEntity] that has an [GenericExtensionEntity.id] matching [id]
	 */
	suspend fun getExtension(id: Int): GenericExtensionEntity?
	suspend fun getInstalledExtension(id: Int): InstalledExtensionEntity?

	/**
	 * Loads all [GenericExtensionEntity] with an [GenericExtensionEntity.repoID] matching [repoID]
	 */
	suspend fun getRepositoryExtensions(repoID: Int): List<GenericExtensionEntity>

	/**
	 * Loads all [GenericExtensionEntity] present
	 */
	suspend fun loadRepositoryExtensions(): List<GenericExtensionEntity>

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
	 */
	suspend fun uninstall(extensionEntity: GenericExtensionEntity)

	/**
	 * Updates an [GenericExtensionEntity]
	 */
	suspend fun updateRepositoryExtension(extensionEntity: GenericExtensionEntity)

	/**
	 * Updates an [InstalledExtensionEntity]
	 */
	suspend fun updateInstalledExtension(extensionEntity: InstalledExtensionEntity)

	/**
	 * This removes the extension completely from the application
	 */
	suspend fun delete(extensionEntity: GenericExtensionEntity)

	/**
	 * Insert a new extension
	 */
	suspend fun insert(extensionEntity: GenericExtensionEntity): Long

	@Throws(HTTPException::class)
	suspend fun downloadExtension(
		repositoryEntity: RepositoryEntity,
		extension: GenericExtensionEntity
	): ByteArray
}