package com.github.doomsdayrs.apps.shosetsu.ui.updates.viewHolder
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
import android.app.Activity
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.UpdateUI
import com.google.android.material.chip.Chip




/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class UpdatedNovelHolder(itemView: View, val activity: Activity) : RecyclerView.ViewHolder(itemView) {
	val imageView: ImageView = itemView.findViewById(R.id.imageView)
	val title: TextView = itemView.findViewById(R.id.title)
	val chip: Chip = itemView.findViewById(R.id.count)
	val button: ImageButton = itemView.findViewById(R.id.button)
	val recyclerView: RecyclerView = itemView.findViewById(R.id.recyclerView)
	val expand: ImageButton = itemView.findViewById(R.id.loadMore)

	var updates: ArrayList<UpdateUI> = ArrayList()

	var novelName: String = ""
		set(value) {
			field = value
			title.post { title.text = value }
		}

	var novelID: Int = -1
}