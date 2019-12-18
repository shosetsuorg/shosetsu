package com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects

import android.view.View
import android.widget.TextView
import com.github.doomsdayrs.apps.shosetsu.R

// TODO: Change to new license/disclaimer.
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
 * 13 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */   class SettingsItem(private val itemView: View) {
    private val itemTitle: TextView? = itemView.findViewById(R.id.settings_item_title)
    private val itemDesc: TextView? = itemView.findViewById(R.id.settings_item_desc)
    fun invalidate() {
        itemView.invalidate()
    }

    fun setTitle(titleResid: Int) {
        itemTitle!!.setText(titleResid)
    }

    fun setDesc(descResid: Int) {
        itemDesc!!.setText(descResid)
    }

    fun setTitle(title: String) {
        itemTitle!!.text = title
    }

    fun setDesc(desc: String) {
        itemDesc!!.text = desc
    }

    fun setOnClickListener(onClickListener: (View) -> Unit) {
        itemView.setOnClickListener(onClickListener)
    }

    init {
        if (itemTitle == null) { // TODO: Log error & quit gracefully
        }
        if (itemDesc == null) { // TODO: Log error & quit gracefully
        }
    }
}