package com.github.doomsdayrs.apps.shosetsu.datasource.local.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.datasource.local.base.ILocalExtensionsDataSource
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ExtensionEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.IDTitleImage
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.ExtensionsDao

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */


/**
 * shosetsu
 * 12 / May / 2020
 */
class LocalExtensionsDataSource(
		private val extensionsDao: ExtensionsDao
) : ILocalExtensionsDataSource {
	override suspend fun loadExtensions(): LiveData<HResult<List<ExtensionEntity>>> =
			extensionsDao.loadExtensions().map {
				successResult(it)
			}

	override suspend fun loadPoweredExtensionsCards(): LiveData<HResult<List<IDTitleImage>>> =
			extensionsDao.loadPoweredExtensionsBasic().map { list ->
				successResult(list.map { IDTitleImage(it.id, it.name, it.imageURL) })
			}

	override suspend fun updateExtension(extensionEntity: ExtensionEntity) {
		Log.d(logID(), "Updating extension data ${extensionEntity.id}")
		extensionsDao.suspendedUpdate(extensionEntity)
	}

	override suspend fun deleteExtension(extensionEntity: ExtensionEntity) =
			extensionsDao.suspendedDelete(extensionEntity)

	override suspend fun loadExtension(formatterID: Int): ExtensionEntity =
			extensionsDao.loadExtension(formatterID)

	override suspend fun insertOrUpdate(extensionEntity: ExtensionEntity) =
			extensionsDao.insertOrUpdate(extensionEntity)

	override fun loadPoweredExtensionsFileNames(): HResult<List<String>> =
			successResult(extensionsDao.loadPoweredExtensionsFileNames().toList())

	override fun loadExtensionMD5(extensionID: Int): HResult<String> =
			successResult(extensionsDao.loadExtensionMD5(extensionID))
}