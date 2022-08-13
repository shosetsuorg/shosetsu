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
import app.shosetsu.android.domain.model.local.NovelCategoryEntity
import kotlinx.coroutines.flow.Flow

interface INovelCategoryRepository {

    /**
     * Loads all [NovelCategoryEntity]s from a novel id in a flow
     */
    fun getNovelCategoriesFromNovelFlow(novelID: Int): Flow<List<NovelCategoryEntity>>

    /**
     * Loads all [NovelCategoryEntity]s from a novel id
     */
    @Throws(SQLiteException::class)
    suspend fun getNovelCategoriesFromNovel(novelID: Int): List<NovelCategoryEntity>

    /**
     * Loads all [NovelCategoryEntity]s from a category id in a flow
     */
    fun getNovelCategoriesFromCategoryFlow(categoryID: Int): Flow<List<NovelCategoryEntity>>

    /**
     * Set the categories for a novel
     */
    @Throws(SQLiteException::class)
    suspend fun setNovelCategories(entities: List<NovelCategoryEntity>): Array<Long>

    /**
     * Delete the categories for a novel
     */
    @Throws(SQLiteException::class)
    suspend fun deleteNovelCategories(novelID: Int)

    /**
     * Delete the categories for multiple novels
     */
    @Throws(SQLiteException::class)
    suspend fun deleteNovelsCategories(novelIDs: List<Int>)
}