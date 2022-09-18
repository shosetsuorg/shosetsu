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
 *
 */

package app.shosetsu.android.domain.repository.base

import android.database.sqlite.SQLiteException
import app.shosetsu.android.domain.model.local.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface ICategoryRepository {

    /**
     * Loads all [CategoryEntity]s in a flow
     */
    fun getCategoriesAsFlow(): Flow<List<CategoryEntity>>

    /**
     * Loads all [CategoryEntity]s in a flow
     */
    @Throws(SQLiteException::class)
    suspend fun getCategories(): List<CategoryEntity>

    /**
     * Add category to the database
     */
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