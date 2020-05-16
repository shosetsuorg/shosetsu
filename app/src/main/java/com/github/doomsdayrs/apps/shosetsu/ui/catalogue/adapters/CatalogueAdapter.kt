package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys.BUNDLE_FORMATTER
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys.BUNDLE_NOVEL_ID
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.common.ext.withFadeTransaction
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogController
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.IDTitleImageBookUI
import com.github.doomsdayrs.apps.shosetsu.view.viewholders.TitleImageViewHolder
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
		private val recycleListingCards: List<IDTitleImageBookUI>,
		private val controller: CatalogController,
		@LayoutRes val layout: Int
) : RecyclerView.Adapter<TitleImageViewHolder>() {
	override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): TitleImageViewHolder {
		return TitleImageViewHolder(LayoutInflater.from(viewGroup.context).inflate(
				layout,
				viewGroup,
				false
		))
	}

	override fun onBindViewHolder(cCardsViewHolder: TitleImageViewHolder, i: Int) {
		recycleListingCards[i].let { (id, title, image) ->
			cCardsViewHolder.title.text = title
			if (image.isNotEmpty()) {
				Picasso.get().load(image).into(cCardsViewHolder.imageView)
			} else cCardsViewHolder.imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE

			with(cCardsViewHolder.itemView) {
				setOnClickListener {
					controller.router.pushController(NovelController(
							bundleOf(
									BUNDLE_NOVEL_ID to id,
									BUNDLE_FORMATTER to controller.viewModel.formatterData.value!!.
							)
					).withFadeTransaction())
				}
				setOnLongClickListener {
					controller.viewModel.backgroundNovelAdd(id).observe(controller, Observer { })
					true
				}
			}
		}
	}

	override fun getItemCount(): Int = recycleListingCards.size

	private fun handleBackgroundAdd(cCardsViewHolder: TitleImageViewHolder, result: HResult<*>) {
		when (result) {
			is HResult.Success -> {
				// TODO bring back novel tinting
			}
			is HResult.Error -> Log.e(logID(), "[${result.code}]\t${result.message}")
			is HResult.Loading -> {
			}
		}
	}
}