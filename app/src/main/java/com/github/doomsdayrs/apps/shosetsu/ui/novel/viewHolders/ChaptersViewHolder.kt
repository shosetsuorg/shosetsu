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
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.api.shosetsu.services.core.Novel
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
    var novelChapter: Novel.Chapter? = null

    var chapterID = -1

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
    var novelChaptersController: NovelChaptersController? = null

    @Throws(MissingResourceException::class)
    fun addToSelect() {
        if (!novelChaptersController!!.contains(novelChapter!!)) novelChaptersController!!.selectedChapters.add(getChapterIDFromChapterURL(novelChapter!!.link)) else removeFromSelect()
        if ((novelChaptersController!!.selectedChapters.size == 1 || novelChaptersController!!.selectedChapters.size <= 0) && novelChaptersController!!.inflater != null) novelChaptersController!!.activity?.invalidateOptionsMenu()
        novelChaptersController!!.updateAdapter()
    }

    private fun removeFromSelect() {
        if (novelChaptersController!!.contains(novelChapter!!)) for (x in novelChaptersController!!.selectedChapters.indices) if (novelChaptersController!!.selectedChapters[x] == getChapterIDFromChapterURL(novelChapter!!.link)) {
            novelChaptersController!!.selectedChapters.removeAt(x)
            return
        }
    }

    override fun onClick(v: View) {
        try {
            if (novelChaptersController != null)
                if (novelChaptersController!!.activity != null && novelChaptersController!!.novelFragment != null)
                    openChapter(novelChaptersController!!.activity!!, novelChapter!!, novelChaptersController!!.novelFragment!!.novelID, novelChaptersController!!.novelFragment!!.formatter.formatterID)
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
                        if (Utilities.toggleBookmarkChapter(chapterID)) title.setTextColor(itemView.resources.getColor(R.color.bookmarked)) else {
                            Log.i("SetDefault", ChaptersAdapter.DefaultTextColor.toString())
                            title.setTextColor(ChaptersAdapter.DefaultTextColor)
                        }

                        novelChaptersController!!.updateAdapter()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.popup_chapter_menu_download -> {
                        if (!Database.DatabaseChapter.isSaved(chapterID)) {
                            val downloadItem = DownloadItem(novelChaptersController!!.novelFragment!!.formatter, novelChaptersController!!.novelFragment!!.novelPage.title, novelChapter!!.title, chapterID)
                            addToDownload(novelChaptersController!!.activity, downloadItem)
                        } else {
                            if (delete(itemView.context, DownloadItem(novelChaptersController!!.novelFragment!!.formatter, novelChaptersController!!.novelFragment!!.novelPage.title, novelChapter!!.title, chapterID))) {
                                downloadTag.visibility = View.INVISIBLE
                            }
                        }
                        novelChaptersController!!.updateAdapter()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.popup_chapter_menu_mark_read -> {
                        Database.DatabaseChapter.setChapterStatus(chapterID, Status.READ)
                        novelChaptersController!!.updateAdapter()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.popup_chapter_menu_mark_unread -> {
                        Database.DatabaseChapter.setChapterStatus(chapterID, Status.UNREAD)
                        novelChaptersController!!.updateAdapter()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.popup_chapter_menu_mark_reading -> {
                        Database.DatabaseChapter.setChapterStatus(chapterID, Status.READING)
                        novelChaptersController!!.updateAdapter()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.browser -> {
                        if (novelChaptersController!!.activity != null) Utilities.openInBrowser(novelChaptersController!!.activity!!, novelChapter!!.link)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.webview -> {
                        if (novelChaptersController!!.activity != null) openInWebview(novelChaptersController!!.activity!!, novelChapter!!.link)
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
        moreOptions.setOnClickListener { popupMenu!!.show() }
        checkBox.setOnClickListener { addToSelect() }

    }
}