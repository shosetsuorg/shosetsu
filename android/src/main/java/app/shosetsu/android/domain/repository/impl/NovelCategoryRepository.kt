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

import app.shosetsu.android.datasource.local.database.base.IDBNovelCategoriesDataSource
import app.shosetsu.android.domain.model.local.NovelCategoryEntity
import app.shosetsu.android.domain.repository.base.INovelCategoryRepository
import kotlinx.coroutines.flow.Flow

class NovelCategoryRepository(
    private val database: IDBNovelCategoriesDataSource,
) : INovelCategoryRepository {

    override fun getNovelCategoriesFromNovelFlow(novelID: Int): Flow<List<NovelCategoryEntity>> =
        database.getNovelCategoriesFromNovelFlow(novelID)

    override suspend fun getNovelCategoriesFromNovel(novelID: Int): List<NovelCategoryEntity> =
        database.getNovelCategoriesFromNovel(novelID)

    override fun getNovelCategoriesFromCategoryFlow(categoryID: Int): Flow<List<NovelCategoryEntity>> =
        database.getNovelCategoriesFromCategoryFlow(categoryID)

    override suspend fun setNovelCategories(entities: List<NovelCategoryEntity>) =
        database.setNovelCategories(entities)

    override suspend fun deleteNovelCategories(novelID: Int) =
        database.deleteNovelCategories(novelID)

    override suspend fun deleteNovelsCategories(novelIDs: List<Int>) =
        database.deleteNovelsCategories(novelIDs)
}