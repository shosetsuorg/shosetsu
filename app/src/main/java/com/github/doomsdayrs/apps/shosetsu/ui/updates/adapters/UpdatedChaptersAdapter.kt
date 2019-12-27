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
 * ====================================================================
 */

import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.api.shosetsu.services.core.objects.NovelPage
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.DownloadManager
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.isSaved
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseChapter.setChapterStatus
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification.*
import com.github.doomsdayrs.apps.shosetsu.ui.updates.viewHolder.UpdatedChapterHolder
import com.github.doomsdayrs.apps.shosetsu.ui.updates.viewHolder.UpdatedNovelHolder
import com.github.doomsdayrs.apps.shosetsu.variables.DefaultScrapers.Companion.getByID
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status

/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class UpdatedChaptersAdapter(private val updatedNovelHolder: UpdatedNovelHolder) : RecyclerView.Adapter<UpdatedChapterHolder>() {
    var size: Int = if (updatedNovelHolder.updates.size > 20) 5 else updatedNovelHolder.updates.size

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): UpdatedChapterHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.update_card, viewGroup, false)
        val updatedChapterHolder = UpdatedChapterHolder(view)
        if (!set) {
            DefaultTextColor = updatedChapterHolder.title.currentTextColor
            Log.i("TextDefaultColor", DefaultTextColor.toString())
            set = !set
        }
        return updatedChapterHolder
    }

    override fun onBindViewHolder(updatedChapterHolder: UpdatedChapterHolder, i: Int) {
        Log.d("Binding", updatedNovelHolder.updates[i].chapterID.toString())
        val novelChapter = DatabaseChapter.getChapter(updatedNovelHolder.updates[i].chapterID)
        if (novelChapter != null) {
            updatedChapterHolder.novelChapter = (novelChapter)
            updatedChapterHolder.popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
                var novelPage = NovelPage()
                val nURL = getNovelURLFromChapterURL(updatedChapterHolder.novelChapter?.link)
                if (nURL != null) novelPage = Database.DatabaseNovels.getNovelPage(getNovelIDFromNovelURL(nURL))
                val formatter = getByID(getFormatterIDFromNovelURL(nURL))
                val chapterID = getChapterIDFromChapterURL(novelChapter.link)
                when (menuItem.itemId) {
                    R.id.popup_chapter_menu_bookmark -> {
                        if (Utilities.toggleBookmarkChapter(chapterID)) updatedChapterHolder.title.setTextColor(updatedChapterHolder.itemView.resources.getColor(R.color.bookmarked)) else {
                            Log.i("SetDefault", DefaultTextColor.toString())
                            updatedChapterHolder.title.setTextColor(DefaultTextColor)
                        }
                        notifyDataSetChanged()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.popup_chapter_menu_download -> {
                        run {
                            if (!isSaved(chapterID)) {
                                val downloadItem = DownloadItem(formatter, novelPage.title, updatedChapterHolder.novelChapter?.title, chapterID)
                                DownloadManager.addToDownload(updatedNovelHolder.activity, downloadItem)
                            } else {
                                if (DownloadManager.delete(updatedChapterHolder.itemView.context, DownloadItem(formatter, novelPage.title, updatedChapterHolder.novelChapter?.title, chapterID))) {
                                    updatedChapterHolder.downloadTag.visibility = View.INVISIBLE
                                }
                            }
                        }
                        notifyDataSetChanged()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.popup_chapter_menu_mark_read -> {
                        setChapterStatus(chapterID, Status.READ)
                        notifyDataSetChanged()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.popup_chapter_menu_mark_unread -> {
                        setChapterStatus(chapterID, Status.UNREAD)
                        notifyDataSetChanged()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.popup_chapter_menu_mark_reading -> {
                        setChapterStatus(chapterID, Status.READING)
                        notifyDataSetChanged()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.browser -> {
                        if (updatedChapterHolder.novelChapter?.link != null)
                            Utilities.openInBrowser(updatedNovelHolder.activity, updatedChapterHolder.novelChapter!!.link)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.webview -> {
                        if (updatedChapterHolder.novelChapter?.link != null)
                            Utilities.openInWebview(updatedNovelHolder.activity, updatedChapterHolder.novelChapter!!.link)
                        return@setOnMenuItemClickListener true
                    }
                    else -> return@setOnMenuItemClickListener false
                }
            }
            updatedChapterHolder.moreOptions.setOnClickListener { updatedChapterHolder.popupMenu.show() }
        }
    }

    override fun getItemCount(): Int {
        return size
    }

    companion object {
        var DefaultTextColor = 0
        private var set = false
    }

}