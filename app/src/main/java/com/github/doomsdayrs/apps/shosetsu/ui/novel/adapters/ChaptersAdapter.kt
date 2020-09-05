package com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters

import android.view.MenuItem
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.ChapterUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.abstracted.INovelChaptersViewModel
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
 * 9 / June / 2019
 */
class ChaptersAdapter(
		private val viewModel: INovelChaptersViewModel,
) : FastAdapter<ChapterUI>() {

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		super.onBindViewHolder(holder, position)
		getItem(position)?.let { chapterUI ->
			(holder as ChapterUI.ViewHolder).apply {
				popupMenu?.setOnMenuItemClickListener { menuItem: MenuItem ->
					when (menuItem.itemId) {
						R.id.popup_chapter_menu_bookmark -> viewModel.updateChapter(
								chapterUI,
								bookmarked = !chapterUI.bookmarked
						)
						R.id.popup_chapter_menu_download -> if (!chapterUI.isSaved)
							viewModel.download(chapterUI)
						else viewModel.delete(chapterUI)
						R.id.set_read -> viewModel.updateChapter(
								chapterUI,
								readingStatus = ReadingStatus.READ
						)
						R.id.set_unread -> viewModel.updateChapter(
								chapterUI,
								readingStatus = ReadingStatus.UNREAD
						)
						R.id.set_reading -> viewModel.updateChapter(
								chapterUI,
								readingStatus = ReadingStatus.READING
						)
						R.id.browser -> viewModel.openBrowser(chapterUI)
						R.id.webview -> viewModel.openWebView(chapterUI)
						else -> return@setOnMenuItemClickListener false
					}
					true
				}
			}
		}
	}

}
