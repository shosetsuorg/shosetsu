package com.github.doomsdayrs.apps.shosetsu.ui.updates.viewHolder

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.novelsDao
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.entities.ChapterEntity
import com.github.doomsdayrs.apps.shosetsu.backend.database.room.entities.NovelEntity
import com.github.doomsdayrs.apps.shosetsu.variables.ext.openChapter
import com.squareup.picasso.Picasso

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
 * ====================================================================
 */ /**
 * shosetsu
 * 17 / 08 / 2019
 *
 * @author github.com/doomsdayrs
 */
class UpdatedChapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
	val moreOptions: ImageView = itemView.findViewById(R.id.more_options)
	val downloadTag: TextView = itemView.findViewById(R.id.recycler_novel_chapter_download)
	val title: TextView = itemView.findViewById(R.id.title)
	private val image: ImageView = itemView.findViewById(R.id.image)

	lateinit var novelEntity: NovelEntity

	var novelChapter: ChapterEntity? = null
		set(value) {
			field = value
			if (value != null) {
				title.text = novelChapter!!.title
				//TODO fix this disgust
				novelEntity = novelsDao.loadNovel(value.novelID)
				if (novelEntity.imageURL.isNotEmpty())
					Picasso.get().load(novelEntity.imageURL).into(image)
				itemView.setOnClickListener(this)
			}
		}

	val popupMenu: PopupMenu = PopupMenu(moreOptions.context, moreOptions)


	override fun onClick(view: View) {
		openChapter(
				(itemView.context as Activity),
				novelChapter!!,
				novelEntity.id,
				novelEntity.formatter
		)
	}

	init {
		popupMenu.inflate(R.menu.popup_chapter_menu)
	}
}