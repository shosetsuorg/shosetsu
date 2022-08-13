package app.shosetsu.android.providers.database.dao

import androidx.room.Dao
import androidx.room.Query
import app.shosetsu.android.domain.model.database.DBNovelCategoryEntity
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
interface NovelCategoriesDao : BaseDao<DBNovelCategoryEntity> {

	//# Queries

	/**
	 * Gets a flow of the novel categories corresponding to the novel
	 */
	@Query("SELECT * FROM novel_categories WHERE novelID = :novelID")
	fun getNovelCategoriesFromNovelFlow(novelID: Int): Flow<List<DBNovelCategoryEntity>>

	/**
	 * Gets a flow of the novel categories corresponding to the novel
	 */
	@Query("SELECT * FROM novel_categories WHERE novelID = :novelID")
	suspend fun getNovelCategoriesFromNovel(novelID: Int): List<DBNovelCategoryEntity>

	/**
	 * Gets a flow of the novel categories corresponding to the category
	 */
	@Query("SELECT * FROM novel_categories WHERE categoryID = :categoryID")
	fun getNovelCategoriesFromCategoryFlow(categoryID: Int): Flow<List<DBNovelCategoryEntity>>

	/**
	 * Delete the categories for a novel
	 */
	@Query("DELETE FROM novel_categories WHERE novelID = :novelID")
	suspend fun deleteNovelCategories(novelID: Int)

	/**
	 * Delete the categories for a novel
	 */
	@Query("DELETE FROM novel_categories WHERE novelID IN (:novelIDs)")
	suspend fun deleteNovelsCategories(novelIDs: List<Int>)
}