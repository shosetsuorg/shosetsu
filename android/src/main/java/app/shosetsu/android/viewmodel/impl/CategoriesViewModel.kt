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

package app.shosetsu.android.viewmodel.impl

import app.shosetsu.android.domain.usecases.AddCategoryUseCase
import app.shosetsu.android.domain.usecases.DeleteCategoryUseCase
import app.shosetsu.android.domain.usecases.MoveCategoryUseCase
import app.shosetsu.android.domain.usecases.get.GetCategoriesUseCase
import app.shosetsu.android.view.uimodels.model.CategoryUI
import app.shosetsu.android.viewmodel.abstracted.ACategoriesViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CategoriesViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val moveCategoryUseCase: MoveCategoryUseCase
) : ACategoriesViewModel() {

    override val liveData: Flow<List<CategoryUI>> by lazy {
        getCategoriesUseCase()
    }

    override fun addCategory(name: String): Flow<Unit> = flow {
        addCategoryUseCase(name)
        emit(Unit)
    }

    override fun remove(categoryUI: CategoryUI): Flow<Unit> = flow {
        deleteCategoryUseCase(categoryUI)
        emit(Unit)
    }

    override fun moveUp(categoryUI: CategoryUI) = flow {
        moveCategoryUseCase(categoryUI, categoryUI.order + 1)
        emit(Unit)
    }

    override fun moveDown(categoryUI: CategoryUI) = flow {
        moveCategoryUseCase(categoryUI, categoryUI.order - 1)
        emit(Unit)
    }
}