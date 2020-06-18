package com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.consts.selectedStrokeWidth
import com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.common.ext.context
import com.github.doomsdayrs.apps.shosetsu.common.ext.openChapter
import com.github.doomsdayrs.apps.shosetsu.common.ext.openInBrowser
import com.github.doomsdayrs.apps.shosetsu.common.ext.openInWebView
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelChaptersController
import com.github.doomsdayrs.apps.shosetsu.ui.novel.viewHolders.ChaptersViewHolder
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.INovelChaptersViewModel
import com.google.android.material.card.MaterialCardView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView

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
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class ChaptersAdapter(
		private val chaptersController: NovelChaptersController,
		private val viewModel: INovelChaptersViewModel
) : RecyclerView.Adapter<ChaptersViewHolder>(), FastScrollRecyclerView.SectionedAdapter {
	init {
		setHasStableIds(true)
	}

	override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ChaptersViewHolder {
		val view = LayoutInflater.from(viewGroup.context).inflate(
				R.layout.recycler_novel_chapter,
				viewGroup,
				false
		)
		return ChaptersViewHolder(view)
	}

	override fun onBindViewHolder(chaptersViewHolder: ChaptersViewHolder, i: Int) {
		val chapterUI = chaptersController.recyclerArray[i]
		chaptersViewHolder.title.text = chapterUI.title

		if (chapterUI.bookmarked) {
			chaptersViewHolder.title.setTextColor(ContextCompat.getColor(
					chaptersViewHolder.itemView.context,
					R.color.bookmarked
			))
		}

		val isSelected = viewModel.isChapterSelected(chapterUI)
		chaptersViewHolder.cardView.strokeWidth = if (isSelected) selectedStrokeWidth else 0
		chaptersViewHolder.checkBox.isChecked = isSelected

		chaptersViewHolder.checkBox.visibility =
				if (chaptersController.recyclerArray.any { viewModel.isChapterSelected(it) })
					View.VISIBLE
				else View.GONE

		if (chapterUI.isSaved) {
			chaptersViewHolder.downloadTag.visibility = View.VISIBLE
			chaptersViewHolder.popupMenu!!.menu.findItem(R.id.popup_chapter_menu_download)
					.title = "Delete"
		} else {
			chaptersViewHolder.popupMenu!!.menu.findItem(R.id.popup_chapter_menu_download)
					.title = "Download"
			chaptersViewHolder.downloadTag.visibility = View.INVISIBLE
		}

		when (chapterUI.readingStatus) {
			ReadingStatus.READING -> {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					chaptersViewHolder.constraintLayout.foreground = ColorDrawable()
				} else {
					(chaptersViewHolder.itemView as MaterialCardView).strokeColor =
							ColorDrawable(ContextCompat.getColor(
									chaptersViewHolder.itemView.context,
									R.color.colorAccent
							)).color
				}
				chaptersViewHolder.status.text = ReadingStatus.READING.status
				chaptersViewHolder.readTag.visibility = View.VISIBLE
				chaptersViewHolder.read.visibility = View.VISIBLE
				chaptersViewHolder.read.text = chapterUI.readingPosition.toString()
			}
			ReadingStatus.UNREAD -> {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					chaptersViewHolder.constraintLayout.foreground = ColorDrawable()
				} else {
					(chaptersViewHolder.itemView as MaterialCardView).strokeColor =
							ColorDrawable(ContextCompat.getColor(
									chaptersViewHolder.itemView.context,
									R.color.colorAccent
							)).color
				}
				chaptersViewHolder.status.text = ReadingStatus.UNREAD.status
			}
			ReadingStatus.READ -> {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
					if (chaptersController.context != null)
						chaptersViewHolder.constraintLayout.foreground =
								ColorDrawable(ContextCompat.getColor(
										chaptersController.context!!,
										R.color.shade
								))
				} else {
					(chaptersViewHolder.itemView as MaterialCardView).strokeColor =
							ColorDrawable(ContextCompat.getColor(
									chaptersViewHolder.itemView.context,
									R.color.colorAccent
							)).color
				}
				chaptersViewHolder.status.text = ReadingStatus.READ.status
				chaptersViewHolder.readTag.visibility = View.GONE
				chaptersViewHolder.read.visibility = View.GONE
			}
			else -> {

			}
		}

		if (chaptersController.recyclerArray.none { viewModel.isChapterSelected(it) })
			chaptersViewHolder.itemView.setOnClickListener {
				chaptersController.activity?.openChapter(chapterUI)
			}
		else chaptersViewHolder.itemView.setOnClickListener { viewModel.addToSelect(chapterUI) }

		chaptersViewHolder.popupMenu!!.setOnMenuItemClickListener { menuItem: MenuItem ->
			when (menuItem.itemId) {
				R.id.popup_chapter_menu_bookmark -> {
					viewModel.updateChapter(chapterUI, bookmarked = !chapterUI.bookmarked)
					return@setOnMenuItemClickListener true
				}
				R.id.popup_chapter_menu_download -> {
					/*
					if (!Database.DatabaseChapter.isSaved(chapterID)) {
						val downloadItem = DownloadEntity(
								chapterID,
								chaptersController.novelController!!.novelInfoController!!.novelPage.title,
								novelChapter.title,
								status = "Pending"
						)
						addToDownload(chaptersController.activity, downloadItem)
					} else if (delete(itemView.context, DownloadEntity(
									chapterID,
									chaptersController.novelController!!.novelInfoController!!.novelPage.title,
									novelChapter.title,
									status = "Pending"
							))) {
						downloadTag.visibility = View.INVISIBLE
					}
					chaptersController.updateAdapter()
					*/
					TODO("Fix popup menu download for chapter")
					return@setOnMenuItemClickListener true
				}
				R.id.set_read -> {
					viewModel.updateChapter(chapterUI, readingStatus = ReadingStatus.READ)
					return@setOnMenuItemClickListener true
				}
				R.id.set_unread -> {
					viewModel.updateChapter(chapterUI, readingStatus = ReadingStatus.UNREAD)
					return@setOnMenuItemClickListener true
				}
				R.id.set_reading -> {
					viewModel.updateChapter(chapterUI, readingStatus = ReadingStatus.READING)
					return@setOnMenuItemClickListener true
				}
				R.id.browser -> {
					chaptersController.activity?.openInBrowser(chapterUI.link)
					return@setOnMenuItemClickListener true
				}
				R.id.webview -> {
					chaptersController.activity?.openInWebView(chapterUI.link)
					return@setOnMenuItemClickListener true
				}
				else -> return@setOnMenuItemClickListener false
			}
		}
		chaptersViewHolder.itemView.setOnLongClickListener {
			viewModel.addToSelect(chapterUI)
			true
		}
		chaptersViewHolder.moreOptions.setOnClickListener { chaptersViewHolder.popupMenu?.show() }
		chaptersViewHolder.checkBox.setOnClickListener { viewModel.addToSelect(chapterUI) }

	}

	override fun getItemCount(): Int = chaptersController.recyclerArray.size

	override fun getSectionName(position: Int) =
			"C. ${chaptersController.recyclerArray[position].order}"

	override fun getItemId(position: Int): Long = position.toLong()

	override fun getItemViewType(position: Int): Int = position
}