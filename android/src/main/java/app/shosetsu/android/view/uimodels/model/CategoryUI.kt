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

package app.shosetsu.android.view.uimodels.model

import androidx.compose.runtime.Immutable
import app.shosetsu.android.domain.model.local.CategoryEntity
import app.shosetsu.android.dto.Convertible

@Immutable
data class CategoryUI(
    val id: Int,
    val name: String,
    val order: Int
) : Convertible<CategoryEntity> {
    override fun convertTo() = CategoryEntity(
        id = id,
        name = name,
        order = order
    )

    companion object {
        val default: () -> CategoryUI
            get() = { CategoryUI(0, "Default", 0) }
    }
}