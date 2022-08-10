package app.shosetsu.android.datasource.local.database.base

import android.database.sqlite.SQLiteException
import app.shosetsu.android.domain.model.local.CategoryEntity
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
 */
interface IDBCategoriesDataSource {

    fun getCategoriesFlow(): Flow<List<CategoryEntity>>

    @Throws(SQLiteException::class)
    suspend fun getCategories(): List<CategoryEntity>

    @Throws(SQLiteException::class)
    suspend fun addCategory(categoryEntity: CategoryEntity): Long

    /**
     * If the category already exists
     */
    @Throws(SQLiteException::class)
    suspend fun categoryExists(name: String): Boolean

    /**
     * Get the next [CategoryEntity.order] variable
     */
    @Throws(SQLiteException::class)
    suspend fun getNextCategoryOrder(): Int

    /**
     * Delete a [CategoryEntity] from the database
     */
    @Throws(SQLiteException::class)
    suspend fun deleteCategory(categoryEntity: CategoryEntity)

    /**
     * Update a list of [CategoryEntity]s
     */
    @Throws(SQLiteException::class)
    suspend fun updateCategories(categories: List<CategoryEntity>)
}