package com.github.doomsdayrs.apps.shosetsu.datasource.local.model

import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.errorResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
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
		private val extensionsDao: ExtensionsDao,
) : ILocalExtensionsDataSource {
	override fun loadExtensions(): LiveData<HResult<List<ExtensionEntity>>> = liveData {
		try {
			emitSource(extensionsDao.loadExtensions().map { successResult(it) })
		} catch (e: SQLiteException) {
			emit(errorResult(e))
		}
	}


	override fun loadPoweredExtensionsCards(
	): LiveData<HResult<List<IDTitleImage>>> = liveData {
		try {
			emitSource(extensionsDao.loadPoweredExtensionsBasic().map { list ->
				successResult(list.map { IDTitleImage(it.id, it.name, it.imageURL) })
			})
		} catch (e: SQLiteException) {
			emit(errorResult(e))
		}
	}

	override suspend fun updateExtension(extensionEntity: ExtensionEntity): HResult<*> = try {
		successResult(extensionsDao.suspendedUpdate(extensionEntity))
	} catch (e: SQLiteException) {
		errorResult(e)

	}

	override suspend fun deleteExtension(extensionEntity: ExtensionEntity): HResult<*> = try {
		successResult(extensionsDao.suspendedDelete(extensionEntity))
	} catch (e: SQLiteException) {
		errorResult(e)

	}

	override suspend fun loadExtension(formatterID: Int): HResult<ExtensionEntity> = try {
		successResult(extensionsDao.loadExtension(formatterID))
	} catch (e: SQLiteException) {
		errorResult(e)
	}

	override suspend fun insertOrUpdate(extensionEntity: ExtensionEntity): HResult<*> = try {
		successResult(extensionsDao.insertOrUpdate(extensionEntity))
	} catch (e: SQLiteException) {
		errorResult(e)
	}
}