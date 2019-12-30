package com.github.doomsdayrs.apps.shosetsu.ui.novel.viewHolders

import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelChapter
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.DownloadManager.addToDownload
import com.github.doomsdayrs.apps.shosetsu.backend.DownloadManager.delete
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters.ChaptersAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentChapters
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status
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
class ChaptersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    var novelChapter: NovelChapter? = null

    var chapterID = -1

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
    var novelFragmentChapters: NovelFragmentChapters? = null

    fun addToSelect() {
        if (!novelFragmentChapters!!.contains(novelChapter!!)) novelFragmentChapters!!.selectedChapters.add(novelChapter!!) else removeFromSelect()
        if ((novelFragmentChapters!!.selectedChapters.size == 1 || novelFragmentChapters!!.selectedChapters.size <= 0) && novelFragmentChapters!!.inflater != null) novelFragmentChapters!!.onCreateOptionsMenu(novelFragmentChapters!!.menu!!, novelFragmentChapters!!.inflater!!)
        novelFragmentChapters!!.updateAdapter()
    }

    private fun removeFromSelect() {
        if (novelFragmentChapters!!.contains(novelChapter!!)) for (x in novelFragmentChapters!!.selectedChapters.indices) if (novelFragmentChapters!!.selectedChapters[x].link.equals(novelChapter!!.link, ignoreCase = true)) {
            novelFragmentChapters!!.selectedChapters.removeAt(x)
            return
        }
    }

    override fun onClick(v: View) {
        if (novelFragmentChapters != null)
            if (novelFragmentChapters!!.activity != null && novelFragmentChapters!!.novelFragment != null && novelFragmentChapters!!.novelFragment!!.formatter != null)
                Utilities.openChapter(novelFragmentChapters!!.activity!!, novelChapter!!, novelFragmentChapters!!.novelFragment!!.novelID, novelFragmentChapters!!.novelFragment!!.formatter!!.formatterID)
    }

    init {
        if (popupMenu == null) {
            popupMenu = PopupMenu(moreOptions.context, moreOptions)
            popupMenu!!.inflate(R.menu.popup_chapter_menu)
        }
        popupMenu!!.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.popup_chapter_menu_bookmark -> {
                    if (Utilities.toggleBookmarkChapter(chapterID)) title.setTextColor(itemView.resources.getColor(R.color.bookmarked)) else {
                        Log.i("SetDefault", ChaptersAdapter.DefaultTextColor.toString())
                        title.setTextColor(ChaptersAdapter.DefaultTextColor)
                    }
                    novelFragmentChapters!!.updateAdapter()
                    return@setOnMenuItemClickListener true
                }
                R.id.popup_chapter_menu_download -> {
                    if (!Database.DatabaseChapter.isSaved(chapterID)) {
                        val downloadItem = DownloadItem(novelFragmentChapters!!.novelFragment!!.formatter, novelFragmentChapters!!.novelFragment!!.novelPage.title, novelChapter!!.title, chapterID)
                        addToDownload(novelFragmentChapters!!.activity, downloadItem)
                    } else {
                        if (delete(itemView.context, DownloadItem(novelFragmentChapters!!.novelFragment!!.formatter, novelFragmentChapters!!.novelFragment!!.novelPage.title, novelChapter!!.title, chapterID))) {
                            downloadTag.visibility = View.INVISIBLE
                        }
                    }
                    novelFragmentChapters!!.updateAdapter()
                    return@setOnMenuItemClickListener true
                }
                R.id.popup_chapter_menu_mark_read -> {
                    Database.DatabaseChapter.setChapterStatus(chapterID, Status.READ)
                    novelFragmentChapters!!.updateAdapter()
                    return@setOnMenuItemClickListener true
                }
                R.id.popup_chapter_menu_mark_unread -> {
                    Database.DatabaseChapter.setChapterStatus(chapterID, Status.UNREAD)
                    novelFragmentChapters!!.updateAdapter()
                    return@setOnMenuItemClickListener true
                }
                R.id.popup_chapter_menu_mark_reading -> {
                    Database.DatabaseChapter.setChapterStatus(chapterID, Status.READING)
                    novelFragmentChapters!!.updateAdapter()
                    return@setOnMenuItemClickListener true
                }
                R.id.browser -> {
                    if (novelFragmentChapters!!.activity != null) Utilities.openInBrowser(novelFragmentChapters!!.activity!!, novelChapter!!.link)
                    return@setOnMenuItemClickListener true
                }
                R.id.webview -> {
                    if (novelFragmentChapters!!.activity != null) Utilities.openInWebview(novelFragmentChapters!!.activity!!, novelChapter!!.link)
                    return@setOnMenuItemClickListener true
                }
                else -> return@setOnMenuItemClickListener false
            }
        }
        itemView.setOnLongClickListener {
            addToSelect()
            true
        }
        moreOptions.setOnClickListener { popupMenu!!.show() }
        checkBox.setOnClickListener { addToSelect() }
    }
}