package app.shosetsu.android.domain.usecases.get

import android.database.sqlite.SQLiteException
import app.shosetsu.android.domain.repository.base.ICategoryRepository
import app.shosetsu.android.view.uimodels.model.CategoryUI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

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
 * Shosetsu
 *
 * @since 06 / 03 / 2022
 * @author Doomsdayrs
 */
class GetCategoriesUseCase(
	private val repo: ICategoryRepository
) {
	@OptIn(ExperimentalCoroutinesApi::class)
	@Throws(SQLiteException::class)
	operator fun invoke() = repo.getCategoriesAsFlow()
		.mapLatest { entities -> entities.map { CategoryUI(it.id!!, it.name, it.order) }.sortedBy { it.order } }
}