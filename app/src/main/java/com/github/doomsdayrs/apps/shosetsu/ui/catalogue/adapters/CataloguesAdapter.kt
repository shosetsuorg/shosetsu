package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.adapters

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bluelinelabs.conductor.Router
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.isOnline
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys.BUNDLE_FORMATTER
import com.github.doomsdayrs.apps.shosetsu.common.ext.withFadeTransaction
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogController
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.viewHolder.CatalogueHolder
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.IDTitleImageUI
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
class CataloguesAdapter(private val formatters: ArrayList<IDTitleImageUI>, private val router: Router) : RecyclerView.Adapter<CatalogueHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CatalogueHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.catalogue_item_card, viewGroup, false)
        return CatalogueHolder(view, router)
    }

    override fun onBindViewHolder(catalogueHolder: CatalogueHolder, i: Int) {
        val catalogueCard = formatters[i]
	    if (catalogueCard.imageURL.isNotEmpty()) Picasso.get()
			    .load(catalogueCard.imageURL)
			    .into(catalogueHolder.imageView)
	    else catalogueHolder.imageView.setImageResource(R.drawable.ic_broken_image_24dp)
        catalogueHolder.title.text = catalogueCard.title

	    catalogueHolder.itemView.setOnClickListener {
		    Log.d("FormatterSelection", catalogueCard.title)
		    if (isOnline) {
			    val bundle = Bundle()
			    bundle.putInt(BUNDLE_FORMATTER, catalogueCard.id)
			    val catalogueFragment = CatalogController(bundle)
			    router.pushController(catalogueFragment.withFadeTransaction())
			    //TODO Router push to catalogue
		    } else v.context.toast(R.string.you_not_online)
	    }
    }

    override fun getItemCount(): Int {
        return formatters.size
    }

}