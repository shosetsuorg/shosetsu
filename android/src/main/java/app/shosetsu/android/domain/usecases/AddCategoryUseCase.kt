package app.shosetsu.android.domain.usecases

import android.database.sqlite.SQLiteException
import app.shosetsu.android.domain.model.local.CategoryEntity
import app.shosetsu.android.domain.repository.base.ICategoryRepository

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
 * 13 / 01 / 2021
 */
class AddCategoryUseCase(
	private val repo: ICategoryRepository
) {
	@Throws(SQLiteException::class)
	suspend operator fun invoke(name: String): Int {
		if (repo.categoryExists(name)) {
			throw Exception("Name already exists")
		}

		val nextOrder = repo.getNextCategoryOrder()

		return repo.addCategory(CategoryEntity(name = name, order = nextOrder)).toInt()
	}
}