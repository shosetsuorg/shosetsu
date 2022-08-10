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

package app.shosetsu.android.viewmodel.abstracted

import app.shosetsu.android.view.uimodels.model.CategoryUI
import app.shosetsu.android.viewmodel.base.ShosetsuViewModel
import app.shosetsu.android.viewmodel.base.SubscribeViewModel
import kotlinx.coroutines.flow.Flow

abstract class ACategoriesViewModel : SubscribeViewModel<List<CategoryUI>>,
    ShosetsuViewModel() {
    /**
     * Adds a category via a string the user provides
     *
     * @param name The name of the category
     */
    abstract fun addCategory(name: String): Flow<Unit>

    /**
     * Remove the category from the app
     */
    abstract fun remove(categoryUI: CategoryUI): Flow<Unit>

    /**
     * Move the category up one
     */
    abstract fun moveUp(categoryUI: CategoryUI): Flow<Unit>

    /**
     * Move the category down one
     */
    abstract fun moveDown(categoryUI: CategoryUI): Flow<Unit>
}