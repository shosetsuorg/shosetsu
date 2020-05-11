package com.github.doomsdayrs.apps.shosetsu.ui.novel.viewHolders

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.card.MaterialCardView

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
 * 16 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
class ChaptersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
	var cardView: MaterialCardView = itemView.findViewById(R.id.recycler_novel_chapter_card)
	var constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraint)
	var checkBox: CheckBox = itemView.findViewById(R.id.recycler_novel_chapter_selectCheck)
	var title: TextView = itemView.findViewById(R.id.recycler_novel_chapter_title)
	var status: TextView = itemView.findViewById(R.id.recycler_novel_chapter_status)
	var read: TextView = itemView.findViewById(R.id.recycler_novel_chapter_read)
	var readTag: TextView = itemView.findViewById(R.id.recycler_novel_chapter_read_tag)
	var downloadTag: TextView = itemView.findViewById(R.id.recycler_novel_chapter_download)
	var moreOptions: ImageView = itemView.findViewById(R.id.more_options)
	var popupMenu: PopupMenu? = null

	init {
		if (popupMenu == null) {
			popupMenu = PopupMenu(moreOptions.context, moreOptions)
			popupMenu!!.inflate(R.menu.popup_chapter_menu)
		}
	}
}