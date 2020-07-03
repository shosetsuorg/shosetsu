package com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters

import android.app.Activity
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.common.ext.openInBrowser
import com.github.doomsdayrs.apps.shosetsu.common.ext.openInWebView
import com.github.doomsdayrs.apps.shosetsu.ui.novel.viewHolders.ChapterUIViewHolder
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ChapterUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.INovelChaptersViewModel
import com.mikepenz.fastadapter.FastAdapter
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
 */

/**
 * Shosetsu
 * 9 / June / 2019
 */
class ChaptersAdapter(
		private val viewModel: INovelChaptersViewModel
) : FastAdapter<ChapterUI>(), FastScrollRecyclerView.SectionedAdapter {

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		super.onBindViewHolder(holder, position)
		Log.d(logID(), "Binding $position")
		val chapterUI = getItem(position)!!
		(holder as ChapterUIViewHolder).apply {
			popupMenu!!.setOnMenuItemClickListener { menuItem: MenuItem ->
				when (menuItem.itemId) {
					R.id.popup_chapter_menu_bookmark -> {
						viewModel.updateChapter(chapterUI, bookmarked = !chapterUI.bookmarked)
						return@setOnMenuItemClickListener true
					}
					R.id.popup_chapter_menu_download -> {
						if (!chapterUI.isSaved) {
							viewModel.download(chapterUI)
						} else {
							viewModel.delete(chapterUI)
						}
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
						(itemView.context as Activity).openInBrowser(chapterUI.link)
						return@setOnMenuItemClickListener true
					}
					R.id.webview -> {
						(itemView.context as Activity)?.openInWebView(chapterUI.link)
						return@setOnMenuItemClickListener true
					}
					else -> return@setOnMenuItemClickListener false
				}
			}
		}
	}

	override fun getSectionName(position: Int) =
			"C. ${getItem(position)?.order}"
}
