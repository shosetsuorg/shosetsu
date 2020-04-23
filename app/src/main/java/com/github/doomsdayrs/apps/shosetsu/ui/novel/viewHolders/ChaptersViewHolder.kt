package com.github.doomsdayrs.apps.shosetsu.ui.novel.viewHolders

import android.content.res.Resources
import android.database.SQLException
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.DownloadManager.addToDownload
import com.github.doomsdayrs.apps.shosetsu.backend.DownloadManager.delete
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.getChapterIDFromChapterURL
import com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters.ChaptersAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelChaptersController
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status
import com.github.doomsdayrs.apps.shosetsu.variables.ext.openChapter
import com.github.doomsdayrs.apps.shosetsu.variables.ext.openInWebview
import com.google.android.material.card.MaterialCardView
import java.util.*

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
class ChaptersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
	var cardView: MaterialCardView = itemView.findViewById(R.id.recycler_novel_chapter_card)
	var constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraint)
	var checkBox: CheckBox = itemView.findViewById(R.id.recycler_novel_chapter_selectCheck)
	var title: TextView = itemView.findViewById(R.id.recycler_novel_chapter_title)
	var status: TextView = itemView.findViewById(R.id.recycler_novel_chapter_status)
	var read: TextView = itemView.findViewById(R.id.recycler_novel_chapter_read)
	var readTag: TextView = itemView.findViewById(R.id.recycler_novel_chapter_read_tag)
	var downloadTag: TextView = itemView.findViewById(R.id.recycler_novel_chapter_download)
	private var moreOptions: ImageView = itemView.findViewById(R.id.more_options)

	var popupMenu: PopupMenu? = null

	lateinit var chaptersController: NovelChaptersController
	lateinit var novelChapter: Novel.Chapter
	var chapterID = -1

	@Throws(MissingResourceException::class)
	fun addToSelect() {
		if (!chaptersController.contains(novelChapter))
			chaptersController.selectedChapters.add(getChapterIDFromChapterURL(novelChapter.link))
		else removeFromSelect()
		if ((chaptersController.selectedChapters.isNotEmpty() || chaptersController.selectedChapters.size <= 0) && chaptersController.inflater != null)
			chaptersController.activity?.invalidateOptionsMenu()
		chaptersController.updateAdapter()
	}

	private fun removeFromSelect() {
		if (chaptersController.contains(novelChapter))
			for (x in chaptersController.selectedChapters.indices)
				if (chaptersController.selectedChapters[x] == getChapterIDFromChapterURL(novelChapter.link)) {
					chaptersController.selectedChapters.removeAt(x)
					return
				}
	}

	override fun onClick(v: View) {
		try {
			if (chaptersController.activity != null && chaptersController.novelController != null)
				openChapter(
						chaptersController.activity!!,
						novelChapter,
						chaptersController.novelController!!.novelID,
						chaptersController.novelController!!.formatter.formatterID
				)
		} catch (e: MissingResourceException) {
			TODO("Add error handling here")
		}
	}

	init {
		if (popupMenu == null) {
			popupMenu = PopupMenu(moreOptions.context, moreOptions)
			popupMenu!!.inflate(R.menu.popup_chapter_menu)
		}
		popupMenu!!.setOnMenuItemClickListener { menuItem: MenuItem ->
			try {
				when (menuItem.itemId) {
					R.id.popup_chapter_menu_bookmark -> {
						if (Utilities.toggleBookmarkChapter(chapterID))
							title.setTextColor(ContextCompat.getColor(
									itemView.context,
									R.color.bookmarked
							))
						else {
							Log.i("SetDefault", ChaptersAdapter.DefaultTextColor.toString())
							title.setTextColor(ChaptersAdapter.DefaultTextColor)
						}

						chaptersController.updateAdapter()
						return@setOnMenuItemClickListener true
					}
					R.id.popup_chapter_menu_download -> {
						if (!Database.DatabaseChapter.isSaved(chapterID)) {
							val downloadItem = DownloadItem(
									chaptersController.novelController!!.formatter,
									chaptersController.novelController!!.novelPage.title,
									novelChapter.title,
									chapterID
							)
							addToDownload(chaptersController.activity, downloadItem)
						} else if (delete(itemView.context, DownloadItem(
										chaptersController.novelController!!.formatter,
										chaptersController.novelController!!.novelPage.title,
										novelChapter.title,
										chapterID
								))) {
							downloadTag.visibility = View.INVISIBLE
						}
						chaptersController.updateAdapter()
						return@setOnMenuItemClickListener true
					}
					R.id.popup_chapter_menu_mark_read -> {
						Database.DatabaseChapter.setChapterStatus(chapterID, Status.READ)
						chaptersController.updateAdapter()
						return@setOnMenuItemClickListener true
					}
					R.id.popup_chapter_menu_mark_unread -> {
						Database.DatabaseChapter.setChapterStatus(chapterID, Status.UNREAD)
						chaptersController.updateAdapter()
						return@setOnMenuItemClickListener true
					}
					R.id.popup_chapter_menu_mark_reading -> {
						Database.DatabaseChapter.setChapterStatus(chapterID, Status.READING)
						chaptersController.updateAdapter()
						return@setOnMenuItemClickListener true
					}
					R.id.browser -> {
						if (chaptersController.activity != null)
							Utilities.openInBrowser(
									chaptersController.activity!!,
									novelChapter.link
							)
						return@setOnMenuItemClickListener true
					}
					R.id.webview -> {
						if (chaptersController.activity != null)
							openInWebview(
									chaptersController.activity!!,
									novelChapter.link
							)
						return@setOnMenuItemClickListener true
					}
					else -> return@setOnMenuItemClickListener false
				}
			} catch (e: Resources.NotFoundException) {
				TODO("Add error handling here")
			} catch (e: SQLException) {
				TODO("Add error handling here")
			} catch (e: MissingResourceException) {
				TODO("Add error handling here")
			}
		}
		itemView.setOnLongClickListener {
			addToSelect()
			true
		}
		moreOptions.setOnClickListener { popupMenu?.show() }
		checkBox.setOnClickListener { addToSelect() }

	}
}