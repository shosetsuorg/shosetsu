package com.github.doomsdayrs.apps.shosetsu.domain.usecases

import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.IDTitleImageBook
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.catlog.ACatalogNovelUI
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.catlog.CompactCatalogNovelUI
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.catlog.FullCatalogNovelUI

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
 * 08 / 09 / 2020
 */
class ConvertNCToCNUIUseCase(
		private val shosetsuSettings: ShosetsuSettings
) {
	operator fun invoke(idTitleImageBook: IDTitleImageBook): ACatalogNovelUI =
			idTitleImageBook.let { (id, title, imageURL, bookmarked) ->
				if (shosetsuSettings.novelCardType == 0)
					FullCatalogNovelUI(id, title, imageURL, bookmarked)
				else CompactCatalogNovelUI(id, title, imageURL, bookmarked)
			}
}