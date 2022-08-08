package app.shosetsu.android.datasource.local.database.impl

import app.shosetsu.android.datasource.local.database.base.IDBNovelCategoriesDataSource
import app.shosetsu.android.domain.model.local.NovelCategoryEntity
import app.shosetsu.android.dto.convertList
import app.shosetsu.android.providers.database.dao.NovelCategoriesDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
 * Shosetsu
 * 10 / May / 2020
 *
 * @author github.com/doomsdayrs
 */
class DBNovelCategoriesDataSource(
	private val novelCategoriesDao: NovelCategoriesDao,
) : IDBNovelCategoriesDataSource {

	override fun getNovelCategoriesFromNovelFlow(novelID: Int): Flow<List<NovelCategoryEntity>> =
		novelCategoriesDao.getNovelCategoriesFromNovelFlow(novelID).map { it.convertList() }

	override fun getNovelCategoriesFromCategoryFlow(categoryID: Int): Flow<List<NovelCategoryEntity>> =
		novelCategoriesDao.getNovelCategoriesFromCategoryFlow(categoryID).map { it.convertList() }

}