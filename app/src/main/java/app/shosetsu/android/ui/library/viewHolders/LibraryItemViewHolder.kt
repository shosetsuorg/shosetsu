package app.shosetsu.android.ui.library.viewHolders

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import app.shosetsu.android.common.consts.selectedStrokeWidth
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.common.ext.picasso
import app.shosetsu.android.common.ext.toast
import app.shosetsu.android.view.uimodels.model.library.ABookmarkedNovelUI
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.mikepenz.fastadapter.FastAdapter

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
 * 13 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
class LibraryItemViewHolder(itemView: View) : FastAdapter.ViewHolder<ABookmarkedNovelUI>(itemView) {
	private val materialCardView: MaterialCardView = itemView.findViewById(R.id.novel_item_card)
	val imageView: ImageView = itemView.findViewById(R.id.imageView)
	private val titleView: TextView = itemView.findViewById(R.id.title)
	val chip: Chip = itemView.findViewById(R.id.novel_item_left_to_read)

	override fun bindView(item: ABookmarkedNovelUI, payloads: List<Any>) {
		//Sets values
		run {
			if (item.imageURL.isNotEmpty())
				picasso(item.imageURL, imageView)
			titleView.text = item.title
		}

		setUnreadCount(this, item.unread)

		// Loads Chapters Unread for a specific novel

		run {
			materialCardView.strokeWidth = if (item.isSelected)
				selectedStrokeWidth else 0
		}

		chip.setOnClickListener {
			it.context.toast(it.context.getString(R.string.chapters_unread_label) + chip.text)
		}
	}

	override fun unbindView(item: ABookmarkedNovelUI) {
		titleView.text = null
		chip.text = null
		chip.setOnClickListener(null)
	}

	private fun setUnreadCount(viewHolder: LibraryItemViewHolder, unreadCount: Int) {
		Log.d(logID(), "Setting unread count of $unreadCount")
		if (unreadCount != 0) {
			viewHolder.chip.visibility = View.VISIBLE
			viewHolder.chip.text = unreadCount.toString()
		} else viewHolder.chip.visibility = View.INVISIBLE
	}
}