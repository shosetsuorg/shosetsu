package app.shosetsu.android.datasource.local.database.base

import android.database.sqlite.SQLiteException
import app.shosetsu.android.domain.model.local.ExtLibEntity

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
interface IDBExtLibDataSource {
	/** Updates an [extLibEntity] */
	@Throws(SQLiteException::class)
	suspend fun updateExtension(extLibEntity: ExtLibEntity)

	/** Update or insert an [extLibEntity] */
	@Throws(SQLiteException::class)
	suspend fun updateOrInsert(extLibEntity: ExtLibEntity)

	/** Loads a [List] of [ExtLibEntity] by its [repoID] */
	@Throws(SQLiteException::class)
	suspend fun loadExtLibByRepo(repoID: Int): List<ExtLibEntity>
}