package com.github.doomsdayrs.apps.shosetsu.ui.library.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ext.toast
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip

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
 */

/**
 * Shosetsu
 * 13 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
class LibraryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
	val materialCardView: MaterialCardView = itemView.findViewById(R.id.novel_item_card)
	val imageView: ImageView = itemView.findViewById(R.id.image)
	val title: TextView = itemView.findViewById(R.id.title)
	val chip: Chip = itemView.findViewById(R.id.novel_item_left_to_read)

	init {
		chip.setOnClickListener {
			it.context.toast(it.context.getString(R.string.chapters_unread_label) + chip.text)
		}
	}
}