package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.adapters

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.api.shosetsu.services.core.dep.Formatter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogueFragment
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.viewHolder.NovelCardViewHolder
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueNovelCard
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
class CatalogueAdapter(private val recycleCards: List<CatalogueNovelCard?>?, private val catalogueFragment: CatalogueFragment, private val formatter: Formatter) : RecyclerView.Adapter<NovelCardViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): NovelCardViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.recycler_novel_card, viewGroup, false)
        val novelCardsViewHolder = NovelCardViewHolder(view)
        novelCardsViewHolder.catalogueFragment = catalogueFragment
        novelCardsViewHolder.formatter = formatter
        return novelCardsViewHolder
    }

    override fun onBindViewHolder(novelCardsViewHolder: NovelCardViewHolder, i: Int) {
        val recycleCard = recycleCards!![i]
        if (recycleCard != null) {
            novelCardsViewHolder.novelID = recycleCard.novelID
            novelCardsViewHolder.url = recycleCard.novelURL
            novelCardsViewHolder.title.text = recycleCard.title
            if (recycleCard.imageURL.isNotEmpty()) {
                Picasso.get().load(recycleCard.imageURL).into(novelCardsViewHolder.imageView)
            } else novelCardsViewHolder.imageView.visibility = View.GONE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Database.DatabaseNovels.isNotBookmarked(recycleCard.novelID)) {
                    if (catalogueFragment.context != null) novelCardsViewHolder.constraintLayout.foreground = ColorDrawable(ContextCompat.getColor(catalogueFragment.context!!, R.color.shade))
                } else novelCardsViewHolder.constraintLayout.foreground = ColorDrawable()
            } else { //TODO Tint for cards before 22
            }
            Utilities.setBackgroundByTheme(novelCardsViewHolder.title)
        }
    }

    override fun getItemCount(): Int {
        return recycleCards!!.size
    }

}