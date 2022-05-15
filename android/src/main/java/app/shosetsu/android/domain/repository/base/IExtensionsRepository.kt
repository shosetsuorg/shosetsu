package app.shosetsu.android.domain.repository.base

import app.shosetsu.android.common.GenericSQLiteException
import app.shosetsu.android.domain.model.local.BrowseExtensionEntity
import app.shosetsu.android.domain.model.local.GenericExtensionEntity
import app.shosetsu.android.domain.model.local.InstalledExtensionEntity
import app.shosetsu.android.domain.model.local.RepositoryEntity
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Novel
import app.shosetsu.lib.exceptions.HTTPException
import kotlinx.coroutines.flow.Flow
import java.net.SocketTimeoutException
import java.net.UnknownHostException

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

	@Throws(GenericSQLiteException::class)
	fun loadBrowseExtensions(): Flow<List<BrowseExtensionEntity>>

	/**
	 * [Flow] of all [GenericExtensionEntity] that the app knows of
	 */
	fun loadExtensionsFLow(): Flow<List<InstalledExtensionEntity>>

	/**
	 * Retrieves repository extensions with an id matching [id]
	 */
	fun getInstalledExtensionFlow(id: Int): Flow<InstalledExtensionEntity?>

	/**
	 * Gets the [GenericExtensionEntity] that has an [GenericExtensionEntity.id] matching the id
	 */
	@Throws(GenericSQLiteException::class)
	suspend fun getExtension(repoId: Int, extId: Int): GenericExtensionEntity?

	@Throws(GenericSQLiteException::class)
	suspend fun getInstalledExtension(id: Int): InstalledExtensionEntity?

	/**
	 * Loads all [GenericExtensionEntity] with an [GenericExtensionEntity.repoID] matching [repoID]
	 */
	@Throws(GenericSQLiteException::class)
	suspend fun getRepositoryExtensions(repoID: Int): List<GenericExtensionEntity>

	/**
	 * Loads all [GenericExtensionEntity] present
	 */
	@Throws(GenericSQLiteException::class)
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
	@Throws(GenericSQLiteException::class)
	suspend fun uninstall(extensionEntity: InstalledExtensionEntity)

	/**
	 * Updates an [GenericExtensionEntity]
	 */
	@Throws(GenericSQLiteException::class)
	suspend fun updateRepositoryExtension(extensionEntity: GenericExtensionEntity)

	/**
	 * Updates an [InstalledExtensionEntity]
	 */
	@Throws(GenericSQLiteException::class)
	suspend fun updateInstalledExtension(extensionEntity: InstalledExtensionEntity)

	/**
	 * This removes the extension completely from the application
	 */
	@Throws(GenericSQLiteException::class)
	suspend fun delete(extensionEntity: GenericExtensionEntity)

	/**
	 * Insert a new extension
	 */
	@Throws(GenericSQLiteException::class)
	suspend fun insert(extensionEntity: GenericExtensionEntity): Long

	@Throws(GenericSQLiteException::class)
	suspend fun insert(extensionEntity: InstalledExtensionEntity): Long

	@Throws(
		HTTPException::class,
		SocketTimeoutException::class,
		UnknownHostException::class,
	)
	suspend fun downloadExtension(
		repositoryEntity: RepositoryEntity,
		extension: GenericExtensionEntity
	): ByteArray

	/**
	 * Check if a given extension is installed
	 */
	@Throws(GenericSQLiteException::class)
	suspend fun isExtensionInstalled(extensionEntity: GenericExtensionEntity): Boolean
}