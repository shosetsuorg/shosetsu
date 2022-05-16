package app.shosetsu.android.datasource.local.database.base

import android.database.sqlite.SQLiteException
import app.shosetsu.android.domain.model.local.GenericExtensionEntity
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
 * 04 / 05 / 2020
 */
interface IDBRepositoryExtensionsDataSource {
	/** Loads LiveData of extensions */
	fun loadExtensionsFlow(): Flow<List<GenericExtensionEntity>>

	@Throws(SQLiteException::class)
	suspend fun loadExtensions(): List<GenericExtensionEntity>

	/** Updates [extensionEntity] */
	@Throws(SQLiteException::class)
	suspend fun updateExtension(extensionEntity: GenericExtensionEntity)

	/** Delete [extensionEntity] */
	@Throws(SQLiteException::class)
	suspend fun deleteExtension(extensionEntity: GenericExtensionEntity)

	/** Load an [GenericExtensionEntity] via its [GenericExtensionEntity.id]*/
	@Throws(SQLiteException::class)
	suspend fun loadExtension(repoId: Int, extId: Int): GenericExtensionEntity?

	@Throws(SQLiteException::class)
	suspend fun getExtensions(repoID: Int): List<GenericExtensionEntity>

	@Throws(SQLiteException::class)
	suspend fun insert(extensionEntity: GenericExtensionEntity): Long
}