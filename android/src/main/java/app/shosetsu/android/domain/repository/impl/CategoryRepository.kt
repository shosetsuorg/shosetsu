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

package app.shosetsu.android.domain.repository.impl

import android.database.sqlite.SQLiteException
import app.shosetsu.android.datasource.local.database.base.IDBCategoriesDataSource
import app.shosetsu.android.domain.model.local.CategoryEntity
import app.shosetsu.android.domain.repository.base.ICategoryRepository
import kotlinx.coroutines.flow.Flow

class CategoryRepository(
    private val database: IDBCategoriesDataSource,
) : ICategoryRepository {

    override fun getCategoriesAsFlow(): Flow<List<CategoryEntity>> =
        database.getCategoriesFlow()

    @Throws(SQLiteException::class)
    override suspend fun getCategories(): List<CategoryEntity> =
        database.getCategories()

    @Throws(SQLiteException::class)
    override suspend fun addCategory(categoryEntity: CategoryEntity) =
        database.addCategory(categoryEntity)

    @Throws(SQLiteException::class)
    override suspend fun categoryExists(name: String): Boolean =
        database.categoryExists(name)

    @Throws(SQLiteException::class)
    override suspend fun getNextCategoryOrder() =
        database.getNextCategoryOrder()

    @Throws(SQLiteException::class)
    override suspend fun deleteCategory(categoryEntity: CategoryEntity) =
        database.deleteCategory(categoryEntity)

    @Throws(SQLiteException::class)
    override suspend fun updateCategories(categories: List<CategoryEntity>) =
        database.updateCategories(categories)
}