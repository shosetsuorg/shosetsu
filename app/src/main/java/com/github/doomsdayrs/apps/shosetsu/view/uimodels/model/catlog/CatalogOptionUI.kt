package com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.catlog

import android.view.View
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.base.BaseRecyclerItem
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.base.GetImageURL
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.base.GetTitle
import com.github.doomsdayrs.apps.shosetsu.view.viewholders.TitleImageFViewHolder

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
 * 04 / 07 / 2020
 *
 * This class defines the item for
 * [com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogsController]
 */
data class CatalogOptionUI(
		override var identifier: Long,
		val title: String,
		val imageURL: String,
) : BaseRecyclerItem<TitleImageFViewHolder<CatalogOptionUI>>(), GetTitle, GetImageURL {
	override fun getDataImageURL(): String = imageURL

	override fun getDataTitle(): String = title

	override val layoutRes: Int
		get() = R.layout.catalogue_item_card
	override val type: Int
		get() = -1

	override fun getViewHolder(v: View): TitleImageFViewHolder<CatalogOptionUI> = TitleImageFViewHolder(v)
}