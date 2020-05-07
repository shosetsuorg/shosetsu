package com.github.doomsdayrs.apps.shosetsu.ui.updates.adapters

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

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.utils.DownloadManager
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ChapterEntity
import com.github.doomsdayrs.apps.shosetsu.ui.updates.viewHolder.UpdatedChapterHolder
import com.github.doomsdayrs.apps.shosetsu.ui.updates.viewHolder.UpdatedNovelHolder
import com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.common.ext.openChapter
import com.github.doomsdayrs.apps.shosetsu.common.ext.openInWebview
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IUpdatesViewModel

/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class UpdatedChaptersAdapter(
		private val updatedNovelHolder: UpdatedNovelHolder,
		val updatesViewModel: IUpdatesViewModel
) : RecyclerView.Adapter<UpdatedChapterHolder>() {
	var size = if (updatedNovelHolder.updates.size > 20) 5 else updatedNovelHolder.updates.size

	override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): UpdatedChapterHolder {
		val view = LayoutInflater.from(viewGroup.context).inflate(
				R.layout.update_card,
				viewGroup,
				false
		)
		val updatedChapterHolder = UpdatedChapterHolder(view)
		if (!set) {
			DefaultTextColor = updatedChapterHolder.title.currentTextColor
			Log.i("TextDefaultColor", DefaultTextColor.toString())
			set = !set
		}
		return updatedChapterHolder
	}

	override fun onBindViewHolder(updatedChapterHolder: UpdatedChapterHolder, i: Int) {
		val chapterID = updatedNovelHolder.updates[i].chapterID
		updatedNovelHolder
		Log.d("Binding", chapterID.toString())
		val chapterEntity: ChapterEntity = updatesViewModel.loadChapter(chapterID)
		// chapterName chapterURL chapterID novelID

		with(updatedChapterHolder) {
			title.text = chapterEntity.title
			//TODO fix this disgust


			itemView.setOnClickListener {
				openChapter(
						(itemView.context as Activity),
						chapterEntity!!,
						updatedNovelHolder.novelID,
						chapterEntity.formatter.formatterID
				)
			}

			popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
				val novelName: String = updatedNovelHolder.novelName

				when (menuItem.itemId) {
					R.id.popup_chapter_menu_bookmark -> {


						if (Utilities.toggleBookmarkChapter(chapterID))
							title.setTextColor(itemView.resources.getColor(R.color.bookmarked))
						else {
							Log.i("SetDefault", DefaultTextColor.toString())
							title.setTextColor(DefaultTextColor)
						}
						notifyDataSetChanged()
						return@setOnMenuItemClickListener true
					}
					R.id.popup_chapter_menu_download -> {
						run {
							if (!chapterEntity.isSaved) {
								val downloadItem = chapterEntity.toDownload(novelName)
								DownloadManager.addToDownload(updatedNovelHolder.activity, downloadItem)
							} else {
								if (DownloadManager.delete(
												itemView.context,
												chapterEntity.toDownload(novelName)
										)) {
									downloadTag.visibility = View.INVISIBLE
								}
							}
						}
						notifyDataSetChanged()
						return@setOnMenuItemClickListener true
					}
					R.id.popup_chapter_menu_mark_read -> {
						setChapterStatus(chapterID, ReadingStatus.READ)
						notifyDataSetChanged()
						return@setOnMenuItemClickListener true
					}
					R.id.popup_chapter_menu_mark_unread -> {
						setChapterStatus(chapterID, ReadingStatus.UNREAD)
						notifyDataSetChanged()
						return@setOnMenuItemClickListener true
					}
					R.id.popup_chapter_menu_mark_reading -> {
						setChapterStatus(chapterID, ReadingStatus.READING)
						notifyDataSetChanged()
						return@setOnMenuItemClickListener true
					}
					R.id.browser -> {
						Utilities.openInBrowser(updatedNovelHolder.activity, chapterEntity.url)
						return@setOnMenuItemClickListener true
					}
					R.id.webview -> {
						openInWebview(updatedNovelHolder.activity, chapterEntity.url)
						return@setOnMenuItemClickListener true
					}
					else -> return@setOnMenuItemClickListener false
				}
			}
			moreOptions.setOnClickListener { updatedChapterHolder.popupMenu.show() }
		}
	}

	override fun getItemCount(): Int = size

	companion object {
		var DefaultTextColor = 0
		private var set = false
	}

}