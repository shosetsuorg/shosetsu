package com.github.doomsdayrs.apps.shosetsu.ui.reader.viewHolders

import android.view.View
import android.widget.TextView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.ui.reader.fragments.ChapterView
import com.github.doomsdayrs.apps.shosetsu.ui.reader.listeners.ToolbarHideOnClickListener

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
class NewTextReader(itemView: View, chapterReader: ChapterView) : NewReader(itemView, chapterReader) {
    private val textView: TextView = itemView.findViewById(R.id.textview)

    override fun setText(text: String?) {
        textView.text = text
    }

    override fun bind() {
        chapterView.chapterReader?.let { textView.setOnClickListener(ToolbarHideOnClickListener(it.getToolbar())) }
    }
}