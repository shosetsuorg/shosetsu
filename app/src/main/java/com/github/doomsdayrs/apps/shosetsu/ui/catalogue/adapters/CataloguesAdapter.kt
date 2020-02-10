package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.viewHolder.CatalogueHolder
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.FormatterCard
import com.squareup.picasso.Picasso
import java.util.*

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
class CataloguesAdapter(private val formatters: ArrayList<FormatterCard>, private val fragmentManager: FragmentManager) : RecyclerView.Adapter<CatalogueHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CatalogueHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.catalogue_item_card, viewGroup, false)
        return CatalogueHolder(view, fragmentManager)
    }

    override fun onBindViewHolder(catalogueHolder: CatalogueHolder, i: Int) {
        val catalogueCard = formatters[i]
        catalogueHolder.formatter = (catalogueCard.formatter)
        catalogueCard.formatter.imageURL
        if (catalogueCard.formatter.imageURL.isNotEmpty()) Picasso.get()
                .load(catalogueCard.formatter.imageURL)
                .into(catalogueHolder.imageView) else catalogueHolder.imageView.setImageResource(catalogueCard.libraryImageResource)
        catalogueHolder.title.text = catalogueCard.title
    }

    override fun getItemCount(): Int {
        return formatters.size
    }

}