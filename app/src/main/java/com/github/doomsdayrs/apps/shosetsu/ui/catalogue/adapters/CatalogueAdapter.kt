package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.lib.Formatter
import com.bluelinelabs.conductor.Router
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchIO
import com.github.doomsdayrs.apps.shosetsu.common.ext.withFadeTransaction
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.viewHolder.CListingViewHolder
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController
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
 */

/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class CatalogueAdapter(
		private val recycleListingCards: List<NovelListingCard>,
		private val router: Router,
		private val formatter: Formatter,
		@LayoutRes val layout: Int
) : RecyclerView.Adapter<CListingViewHolder>() {
	override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CListingViewHolder {
		return CListingViewHolder(LayoutInflater.from(viewGroup.context).inflate(
				layout,
				viewGroup,
				false
		))
    }

	override fun onBindViewHolder(cCardsViewHolder: CListingViewHolder, i: Int) {
        val recycleCard = recycleListingCards[i]
		cCardsViewHolder.title.text = recycleCard.title
        if (recycleCard.imageURL.isNotEmpty()) {
	        Picasso.get().load(recycleCard.imageURL).into(cCardsViewHolder.imageView)
        } else cCardsViewHolder.imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE

		with(recycleCard) {
			with(cCardsViewHolder.itemView) {
				setOnClickListener {
					router.pushController(NovelController(
							bundleOf(
									BundleKeys.BUNDLE_NOVEL_URL to novelURL,
									BundleKeys.BUNDLE_FORMATTER to formatter.formatterID,
									BundleKeys.BUNDLE_NOVEL_ID to novelID
							)
					).withFadeTransaction())
				}
				setOnLongClickListener {
					launchIO {
						TODO("Add Novel in background")
					}
					true
				}
			}
		}


    }

    override fun getItemCount(): Int {
        return recycleListingCards.size
    }

}