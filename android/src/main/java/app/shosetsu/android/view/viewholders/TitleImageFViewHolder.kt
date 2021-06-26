package app.shosetsu.android.view.viewholders

import android.view.View
import android.widget.ImageView
import androidx.annotation.CallSuper
import androidx.appcompat.widget.AppCompatTextView
import app.shosetsu.android.common.ext.picasso
import app.shosetsu.android.view.uimodels.base.GetImageURL
import app.shosetsu.android.view.uimodels.base.GetTitle
import com.github.doomsdayrs.apps.shosetsu.R
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem

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
 * 12 / 05 / 2020
 */
open class TitleImageFViewHolder<ITEM>(itemView: View) : FastAdapter.ViewHolder<ITEM>(itemView)
		where ITEM : GenericItem,
		      ITEM : GetImageURL,
		      ITEM : GetTitle {
	val title: AppCompatTextView = itemView.findViewById(R.id.title)
	val imageView: ImageView = itemView.findViewById(R.id.imageView)
	var oldType: ImageView.ScaleType? = null

	@CallSuper
	override fun bindView(item: ITEM, payloads: List<Any>) {
		title.text = item.getDataTitle()
		val imageURL = item.getDataImageURL()
		if (imageURL.isNotEmpty()) picasso(imageURL, imageView)
		else {
			imageView.setImageResource(R.drawable.broken_image)
			if (oldType == null) oldType = imageView.scaleType
			imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
		}
	}

	@CallSuper
	override fun unbindView(item: ITEM) {
		oldType?.let {
			imageView.scaleType = it
		}
		title.text = null
	}
}