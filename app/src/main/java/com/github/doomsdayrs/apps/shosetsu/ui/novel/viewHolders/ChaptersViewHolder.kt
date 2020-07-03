package com.github.doomsdayrs.apps.shosetsu.ui.novel.viewHolders

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.consts.selectedStrokeWidth
import com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ChapterUI
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

	/**
	 * Gives item data
	 */
	fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
			object : ItemDetailsLookup.ItemDetails<Long>() {
				override fun getPosition(): Int = adapterPosition
				override fun getSelectionKey(): Long? = itemId
			}
}

class ChapterUIViewHolder(itemView: View) : FastAdapter.ViewHolder<ChapterUI>(itemView) {
	var cardView: MaterialCardView = itemView.findViewById(R.id.recycler_novel_chapter_card)
	var constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraint)
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

	override fun bindView(chapterUI: ChapterUI, payloads: List<Any>) {
		title.text = chapterUI.title

		if (chapterUI.bookmarked) {
			title.setTextColor(ContextCompat.getColor(
					itemView.context,
					R.color.bookmarked
			))
		}

		cardView.strokeWidth = if (chapterUI.isSelected) selectedStrokeWidth else 0


		if (chapterUI.isSaved) {
			downloadTag.visibility = View.VISIBLE
			popupMenu!!.menu.findItem(R.id.popup_chapter_menu_download)
					.title = "Delete"
		} else {
			popupMenu!!.menu.findItem(R.id.popup_chapter_menu_download)
					.title = "Download"
			downloadTag.visibility = View.INVISIBLE
		}

		when (chapterUI.readingStatus) {
			ReadingStatus.READING -> {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					constraintLayout.foreground = ColorDrawable()
				} else {
					(itemView as MaterialCardView).strokeColor =
							ColorDrawable(ContextCompat.getColor(
									itemView.context,
									R.color.colorAccent
							)).color
				}
				status.text = ReadingStatus.READING.status
				readTag.visibility = View.VISIBLE
				read.visibility = View.VISIBLE
				read.text = chapterUI.readingPosition.toString()
			}
			ReadingStatus.UNREAD -> {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					constraintLayout.foreground = ColorDrawable()
				} else {
					(itemView as MaterialCardView).strokeColor =
							ColorDrawable(ContextCompat.getColor(
									itemView.context,
									R.color.colorAccent
							)).color
				}
				status.text = ReadingStatus.UNREAD.status
			}
			ReadingStatus.READ -> {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					if (itemView.context != null)
						constraintLayout.foreground =
								ColorDrawable(ContextCompat.getColor(
										itemView.context,
										R.color.shade
								))
				} else {
					cardView.strokeColor =
							ColorDrawable(ContextCompat.getColor(
									itemView.context,
									R.color.colorAccent
							)).color
				}
				status.text = ReadingStatus.READ.status
				readTag.visibility = View.GONE
				read.visibility = View.GONE
			}
			else -> {
			}
		}

		moreOptions.setOnClickListener { popupMenu?.show() }
	}

	override fun unbindView(item: ChapterUI) {
		title.text = null
		status.text = null
		read.text = null
		readTag.text = null
		downloadTag.text = null
		moreOptions.setOnClickListener(null)
	}

	/**
	 * Gives item data
	 */
	fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
			object : ItemDetailsLookup.ItemDetails<Long>() {
				override fun getPosition(): Int = adapterPosition
				override fun getSelectionKey(): Long? = itemId
			}
}