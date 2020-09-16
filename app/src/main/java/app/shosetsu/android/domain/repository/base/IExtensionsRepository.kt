package app.shosetsu.android.domain.repository.base

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.domain.model.local.ExtensionEntity
import app.shosetsu.android.domain.model.local.IDTitleImage
import app.shosetsu.lib.Formatter
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
 * 25 / 04 / 2020
 */
interface IExtensionsRepository {
	/** LiveData of all extensions */
	fun loadExtensions(): LiveData<HResult<List<ExtensionEntity>>>

	/** LiveData of a specific extension */
	fun getExtensionLive(id: Int): LiveData<HResult<ExtensionEntity>>

	/** Get extensions by their repository ID */
	suspend fun getExtensions(repoID: Int): HResult<List<ExtensionEntity>>

	/** Installs an [extensionEntity] */
	suspend fun installExtension(extensionEntity: ExtensionEntity): HResult<*>

	/** Uninstalls an [extensionEntity] */
	suspend fun uninstallExtension(extensionEntity: ExtensionEntity): HResult<*>

	/** Inserts or Updates an [extensionEntity] */
	suspend fun insertOrUpdate(extensionEntity: ExtensionEntity): HResult<*>

	/** Updates an [extensionEntity] */
	suspend fun updateExtension(extensionEntity: ExtensionEntity): HResult<*>

	/** Loads the formatter via its extension */
	suspend fun loadFormatter(extensionEntity: ExtensionEntity): HResult<Formatter>

	/** Loads the formatter via its ID */
	suspend fun loadFormatter(formatterID: Int): HResult<Formatter>

	/** Gets the extensions as cards containing their ID, Title, and Image */
	fun getCards(): LiveData<HResult<List<IDTitleImage>>>

	/** Queries the extension for a search result*/
	suspend fun loadCatalogueSearch(
			formatter: Formatter,
			query: String,
			page: Int,
			data: Map<Int, Any>
	): HResult<List<Novel.Listing>>

	/** Loads catalogue data of an extension */
	suspend fun loadCatalogueData(
			formatter: Formatter,
			listing: Int,
			page: Int,
			data: Map<Int, Any>,
	): HResult<List<Novel.Listing>>

	suspend fun removeExtension(it: ExtensionEntity): HResult<*>
}