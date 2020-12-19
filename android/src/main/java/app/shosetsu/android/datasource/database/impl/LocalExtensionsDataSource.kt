package app.shosetsu.android.datasource.database.impl

import app.shosetsu.android.common.ext.toDB
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.common.datasource.database.base.ILocalExtensionsDataSource
import app.shosetsu.android.providers.database.dao.ExtensionsDao
import app.shosetsu.common.domain.model.local.StrippedExtensionEntity
import app.shosetsu.common.domain.model.local.ExtensionEntity
import app.shosetsu.common.dto.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

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
	@ExperimentalCoroutinesApi
	override fun loadExtensions(): Flow<HResult<List<ExtensionEntity>>> = flow {
		emit(loading())
		try {
			emitAll(extensionsDao.loadExtensions().mapLatestListTo().mapLatestToSuccess())
		} catch (e: Exception) {
			emit(e.toHError())
		}
	}

	@ExperimentalCoroutinesApi
	override fun loadExtensionLive(formatterID: Int): Flow<HResult<ExtensionEntity>> = flow {
		emit(loading())
		try {
			emitAll(extensionsDao.getExtensionLive(formatterID).mapLatestTo().mapLatestToSuccess())
		} catch (e: Exception) {
			emit(e.toHError())
		}
	}

	@ExperimentalCoroutinesApi
	override fun loadPoweredExtensionsCards(): Flow<HResult<List<StrippedExtensionEntity>>> = flow {
		emit(loading())
		try {
			emitAll(extensionsDao.loadPoweredExtensionsBasic().mapLatestToSuccess())
		} catch (e: Exception) {
			emit(e.toHError())
		}
	}

	override suspend fun updateExtension(extensionEntity: ExtensionEntity): HResult<*> = try {
		successResult(extensionsDao.suspendedUpdate(extensionEntity.toDB()))
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun deleteExtension(extensionEntity: ExtensionEntity): HResult<*> = try {
		successResult(extensionsDao.suspendedDelete(extensionEntity.toDB()))
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun loadExtension(formatterID: Int): HResult<ExtensionEntity> = try {
		successResult(extensionsDao.getExtension(formatterID).convertTo())
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun insertOrUpdate(extensionEntity: ExtensionEntity): HResult<*> = try {
		successResult(extensionsDao.insertOrUpdate(extensionEntity.toDB()))
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun getExtensions(repoID: Int): HResult<List<ExtensionEntity>> = try {
		successResult(extensionsDao.getExtensions(repoID).convertList())
	} catch (e: Exception) {
		e.toHError()
	}

}