package com.github.doomsdayrs.apps.shosetsu.ui.updates.viewHolder
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
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R

/**
 * shosetsu
 * 17 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
class UpdatedChapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
	val moreOptions: ImageView = itemView.findViewById(R.id.more_options)
	val downloadTag: TextView = itemView.findViewById(R.id.recycler_novel_chapter_download)
	val title: TextView = itemView.findViewById(R.id.title)
	val image: ImageView = itemView.findViewById(R.id.image)
	val popupMenu = PopupMenu(moreOptions.context, moreOptions)

	init { popupMenu.inflate(R.menu.popup_chapter_menu) }
}