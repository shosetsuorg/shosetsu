package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.api.shosetsu.services.core.Formatter
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogueController
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.viewHolder.NovelListingCViewHolder
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelListingCard
import com.squareup.picasso.Picasso

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
 * ====================================================================
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class CatalogueCAdapter(private val recycleListingCards: List<NovelListingCard>, private val catalogueFragment: CatalogueController, private val formatter: Formatter, @LayoutRes val layout: Int) : RecyclerView.Adapter<NovelListingCViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): NovelListingCViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(layout, viewGroup, false)
        val novelCardsViewHolder = NovelListingCViewHolder(view)
        novelCardsViewHolder.catalogueFragment = catalogueFragment
        novelCardsViewHolder.formatter = formatter
        return novelCardsViewHolder
    }

    override fun onBindViewHolder(novelCardsViewHolder: NovelListingCViewHolder, i: Int) {
        val recycleCard = recycleListingCards[i]
        novelCardsViewHolder.novelID = recycleCard.novelID
        novelCardsViewHolder.url = recycleCard.novelURL
        novelCardsViewHolder.title.text = recycleCard.title
        if (recycleCard.imageURL.isNotEmpty()) {
            Picasso.get().load(recycleCard.imageURL).into(novelCardsViewHolder.imageView)
        } else novelCardsViewHolder.imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
    }

    override fun getItemCount(): Int {
        return recycleListingCards.size
    }

}