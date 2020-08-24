package com.github.doomsdayrs.apps.shosetsu.ui.novel.viewHolders

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.consts.selectedStrokeWidth
import com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.ChapterUI
import com.google.android.material.card.MaterialCardView
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
 * 16 / 06 / 2019
 */
class ChapterUIViewHolder(itemView: View) : FastAdapter.ViewHolder<ChapterUI>(itemView) {
	var cardView: MaterialCardView = itemView.findViewById(R.id.recycler_novel_chapter_card)
	var constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraint)
	var title: TextView = itemView.findViewById(R.id.title)
	var read: TextView = itemView.findViewById(R.id.read_progress_value)
	var readTag: TextView = itemView.findViewById(R.id.read_progress_label)
	var downloadTag: TextView = itemView.findViewById(R.id.download_status)
	var moreOptions: ImageView = itemView.findViewById(R.id.more_options)

	var popupMenu: PopupMenu? = null

	init {
		if (popupMenu == null) {
			popupMenu = PopupMenu(moreOptions.context, moreOptions)
			popupMenu!!.inflate(R.menu.popup_chapter_menu)
		}
	}

	override fun bindView(chapterUI: ChapterUI, payloads: List<Any>) {
		Log.d(logID(), "Binding ${chapterUI.id}")

		cardView.strokeWidth = if (chapterUI.isSelected) selectedStrokeWidth else 0
		if (chapterUI.isSelected) {
			cardView.isSelected
		}


		title.text = chapterUI.title

		if (chapterUI.bookmarked) {
			title.setTextColor(ContextCompat.getColor(
					itemView.context,
					R.color.bookmarked
			))
		}

		if (chapterUI.isSaved) {
			Log.d(logID(), "Chapter is downloaded")
			popupMenu!!.menu.findItem(R.id.popup_chapter_menu_download)
					.title = "Delete"

		} else {
			Log.d(logID(), "Chapter is!downloaded")
			popupMenu!!.menu.findItem(R.id.popup_chapter_menu_download)
					.title = "Download"
		}

		downloadTag.visibility = if (chapterUI.isSaved) View.VISIBLE else View.INVISIBLE

		when (chapterUI.readingStatus) {
			ReadingStatus.READING -> {
				setAlpha(1f)
				readTag.visibility = View.VISIBLE
				read.visibility = View.VISIBLE
				read.text = chapterUI.readingPosition.toString()
			}
			ReadingStatus.UNREAD -> setAlpha(1f)
			ReadingStatus.READ -> {
				setAlpha(0.5F)
				readTag.visibility = View.GONE
				read.visibility = View.GONE
			}
			else -> {
			}
		}

		moreOptions.setOnClickListener { popupMenu?.show() }
	}

	private fun setAlpha(float: Float) {
		title.alpha = float
		read.alpha = float
		readTag.alpha = float
		downloadTag.alpha = float
		moreOptions.imageAlpha = (255 * float).toInt()
	}

	override fun unbindView(item: ChapterUI) {
		title.text = null
		read.text = null
		readTag.text = null
		downloadTag.text = null
		moreOptions.setOnClickListener(null)
	}
}