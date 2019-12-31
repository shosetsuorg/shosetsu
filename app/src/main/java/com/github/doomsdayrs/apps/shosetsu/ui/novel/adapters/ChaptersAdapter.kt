package com.github.doomsdayrs.apps.shosetsu.ui.novel.adapters

import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database.DatabaseIdentification
import com.github.doomsdayrs.apps.shosetsu.ui.novel.pages.NovelFragmentChapters
import com.github.doomsdayrs.apps.shosetsu.ui.novel.viewHolders.ChaptersViewHolder
import com.github.doomsdayrs.apps.shosetsu.variables.enums.Status

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
class ChaptersAdapter(private val novelFragmentChapters: NovelFragmentChapters) : RecyclerView.Adapter<ChaptersViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ChaptersViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.recycler_novel_chapter, viewGroup, false)
        val chaptersViewHolder = ChaptersViewHolder(view)
        if (!set) {
            DefaultTextColor = chaptersViewHolder.title.currentTextColor
            Log.i("TextDefaultColor", DefaultTextColor.toString())
            set = !set
        }
        return chaptersViewHolder
    }

    override fun onBindViewHolder(chaptersViewHolder: ChaptersViewHolder, i: Int) {
        val novelChapter = novelFragmentChapters.novelFragment!!.novelChapters[i]
        chaptersViewHolder.novelChapter = novelChapter
        chaptersViewHolder.title.text = novelChapter.title
        chaptersViewHolder.novelFragmentChapters = novelFragmentChapters
        val chapterID = DatabaseIdentification.getChapterIDFromChapterURL(novelChapter.link)
        chaptersViewHolder.chapterID = chapterID
        //TODO The getNovelID in this method likely will cause slowdowns due to IO
        if (Database.DatabaseChapter.isNotInChapters(novelChapter.link)) Database.DatabaseChapter.addToChapters(DatabaseIdentification.getNovelIDFromNovelURL(novelFragmentChapters.novelFragment!!.novelURL), novelChapter)
        if (Database.DatabaseChapter.isNotBookMarked(chapterID)) {
            chaptersViewHolder.title.setTextColor(chaptersViewHolder.itemView.resources.getColor(R.color.bookmarked))
            chaptersViewHolder.popupMenu!!.menu.findItem(R.id.popup_chapter_menu_bookmark).title = "UnBookmark"
        } else {
            chaptersViewHolder.popupMenu!!.menu.findItem(R.id.popup_chapter_menu_bookmark).title = "Bookmark"
        }
        if (novelFragmentChapters.contains(novelChapter)) {
            chaptersViewHolder.cardView.strokeWidth = Utilities.SELECTED_STROKE_WIDTH
            chaptersViewHolder.checkBox.isChecked = true
        } else {
            chaptersViewHolder.cardView.strokeWidth = 0
            chaptersViewHolder.checkBox.isChecked = false
        }
        if (novelFragmentChapters.selectedChapters.size > 0) {
            chaptersViewHolder.checkBox.visibility = View.VISIBLE
        } else chaptersViewHolder.checkBox.visibility = View.GONE
        if (Database.DatabaseChapter.isNotSaved(chapterID)) {
            chaptersViewHolder.downloadTag.visibility = View.VISIBLE
            chaptersViewHolder.popupMenu!!.menu.findItem(R.id.popup_chapter_menu_download).title = "Delete"
        } else {
            chaptersViewHolder.popupMenu!!.menu.findItem(R.id.popup_chapter_menu_download).title = "Download"
            chaptersViewHolder.downloadTag.visibility = View.INVISIBLE
        }
        when (Database.DatabaseChapter.getStatus(chapterID)) {
            Status.READING -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    chaptersViewHolder.constraintLayout.foreground = ColorDrawable()
                } else { //TODO Tint for cards before 22
                }
                chaptersViewHolder.status.text = Status.READING.status
                chaptersViewHolder.readTag.visibility = View.VISIBLE
                chaptersViewHolder.read.visibility = View.VISIBLE
                chaptersViewHolder.read.text = Database.DatabaseChapter.getY(chapterID).toString()
            }
            Status.UNREAD -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    chaptersViewHolder.constraintLayout.foreground = ColorDrawable()
                } else { //TODO Tint for cards before 22
                }
                chaptersViewHolder.status.text = Status.UNREAD.status
            }
            Status.READ -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (novelFragmentChapters.context != null) chaptersViewHolder.constraintLayout.foreground = ColorDrawable(ContextCompat.getColor(novelFragmentChapters.context!!, R.color.shade))
                } else { //TODO Tint for cards before 22
                }
                chaptersViewHolder.status.text = Status.READ.status
                chaptersViewHolder.readTag.visibility = View.GONE
                chaptersViewHolder.read.visibility = View.GONE
            }
            else -> {

            }
        }
        if (novelFragmentChapters.selectedChapters.size <= 0)
            chaptersViewHolder.itemView.setOnClickListener(chaptersViewHolder)
        else chaptersViewHolder.itemView.setOnClickListener { chaptersViewHolder.addToSelect() }
    }

    override fun getItemCount(): Int {
        return if (novelFragmentChapters.novelFragment != null) novelFragmentChapters.novelFragment!!.novelChapters.size else 0
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    companion object {
        @JvmField
        var DefaultTextColor = 0
        private var set = false
    }

}