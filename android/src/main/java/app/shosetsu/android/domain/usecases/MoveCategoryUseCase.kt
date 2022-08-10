package app.shosetsu.android.domain.usecases

import android.database.sqlite.SQLiteException
import app.shosetsu.android.domain.repository.base.ICategoryRepository
import app.shosetsu.android.domain.usecases.get.GetCategoriesUseCase
import app.shosetsu.android.view.uimodels.model.CategoryUI
import kotlinx.coroutines.flow.first

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
class MoveCategoryUseCase(
	private val repo: ICategoryRepository,
	private val getCategoriesUseCase: GetCategoriesUseCase,
) {
	@Throws(SQLiteException::class)
	suspend operator fun invoke(categoryUI: CategoryUI, newOrder: Int) {
		val unalteredNewOrder = newOrder - 1
		val categories = getCategoriesUseCase().first()

		val currentIndex = categories.indexOfFirst { it.id == categoryUI.id }
		if (currentIndex == unalteredNewOrder) return

		val reorderedCategories = categories.toMutableList()
		val reorderedCategory = reorderedCategories.removeAt(currentIndex)
		reorderedCategories.add(unalteredNewOrder, reorderedCategory)

		val updatedCategories = reorderedCategories.mapIndexed { index, categoryEntity ->
			categoryEntity.convertTo().copy(
				order = index + 1
			)
		}

		repo.updateCategories(updatedCategories)
	}
}