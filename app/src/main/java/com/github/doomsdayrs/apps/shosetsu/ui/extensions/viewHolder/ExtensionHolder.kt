package com.github.doomsdayrs.apps.shosetsu.ui.extensions.viewHolder

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R

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
 */

/**
 * shosetsu
 * 27 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class ExtensionHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView: ImageView = itemView.findViewById(R.id.imageView)
    val language: TextView = itemView.findViewById(R.id.language)
    val title: TextView = itemView.findViewById(R.id.title)
    val hash: TextView = itemView.findViewById(R.id.hash)
    val id: TextView = itemView.findViewById(R.id.id)
    val version: TextView = itemView.findViewById(R.id.version)
    val updatedVersion: TextView = itemView.findViewById(R.id.update_version)
    val button: Button = itemView.findViewById(R.id.button)
}