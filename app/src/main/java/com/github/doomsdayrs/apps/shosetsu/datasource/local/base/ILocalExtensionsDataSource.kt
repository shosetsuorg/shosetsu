package com.github.doomsdayrs.apps.shosetsu.datasource.local.base

import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ExtensionEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.IDTitleImage

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
 * 04 / 05 / 2020
 */
interface ILocalExtensionsDataSource {
	/** Loads LiveData of extensions */
	suspend fun loadExtensions(): LiveData<HResult<List<ExtensionEntity>>>

	/** Loads LiveData of extension cards that are enabled */
	suspend fun loadPoweredExtensionsCards(): LiveData<HResult<List<IDTitleImage>>>

	/** Updates [extensionEntity] */
	@Throws(SQLiteException::class)
	suspend fun updateExtension(extensionEntity: ExtensionEntity)

	/** Delete [extensionEntity] */
	@Throws(SQLiteException::class)
	suspend fun deleteExtension(extensionEntity: ExtensionEntity)

	/** Load an [ExtensionEntity] via its [formatterID]*/
	suspend fun loadExtension(formatterID: Int): HResult<ExtensionEntity>

	/** Inserts an [extensionEntity] otherwise updates it */
	@Throws(SQLiteException::class)
	suspend fun insertOrUpdate(extensionEntity: ExtensionEntity)
}