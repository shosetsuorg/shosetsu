package app.shosetsu.android.providers.database.dao

import android.database.sqlite.SQLiteException
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import app.shosetsu.android.domain.model.database.DBCategoryEntity
import app.shosetsu.android.domain.model.local.CategoryEntity
import app.shosetsu.android.providers.database.dao.base.BaseDao
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
 * 08 / 08 / 2022
 *
 * @author github.com/doomsdayrs
 */
@Dao
interface CategoriesDao : BaseDao<DBCategoryEntity> {

	//# Queries

	/**
	 * Gets a flow of the categories
	 */
	@Query("SELECT * FROM categories")
	fun getCategoriesFlow(): Flow<List<DBCategoryEntity>>

	/**
	 * Gets a list of the categories
	 */
	@Query("SELECT * FROM categories")
	suspend fun getCategories(): List<DBCategoryEntity>

	/**
	 * If the category already exists
	 */
	@Query("SELECT count(*) FROM categories WHERE name LIKE :name")
	suspend fun categoryExists(name: String): Int

	/**
	 * Get the next [CategoryEntity.order] variable
	 */
	@Query("SELECT count(*) + 1 FROM categories")
	suspend fun getNextCategoryOrder(): Int

	@Transaction
	@Throws(SQLiteException::class)
	suspend fun update(list: List<DBCategoryEntity>) {
		list.forEach { entity ->
			update(entity)
		}
	}
}