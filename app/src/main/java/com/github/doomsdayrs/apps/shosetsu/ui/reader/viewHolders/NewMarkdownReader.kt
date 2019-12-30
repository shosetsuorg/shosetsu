package com.github.doomsdayrs.apps.shosetsu.ui.reader.viewHolders

import android.view.View
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.reader.fragments.ChapterView
import com.github.doomsdayrs.apps.shosetsu.ui.reader.listeners.ToolbarHideOnClickListener
import us.feras.mdv.MarkdownView

/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 */ /**
 * shosetsu
 * 13 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
class NewMarkdownReader(itemView: View, chapterView: ChapterView) : NewReader(itemView, chapterView) {
    private val markdownView: MarkdownView = itemView.findViewById(R.id.markdown_view)

    override fun setText(text: String?) {
        markdownView.loadMarkdown(text)
    }

    override fun bind() {
        chapterView.chapterReader?.let { markdownView.setOnClickListener(ToolbarHideOnClickListener(it.getToolbar())) }
    }
}