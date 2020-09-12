package app.shosetsu.android.ui.settings.viewHolder

import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.xw.repo.BubbleSeekBar

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

/**
 * Shosetsu
 * 13 / 07 / 2019
 */
@Suppress("unused")
class SettingsItem(val view: View) : RecyclerView.ViewHolder(view) {
	val itemTitle: TextView = itemView.findViewById(R.id.settings_item_title)
	val itemDescription: TextView = itemView.findViewById(R.id.settings_item_desc)
	val button: Button = itemView.findViewById(R.id.button)
	val spinner: Spinner = itemView.findViewById(R.id.spinner)
	val textView: TextView = itemView.findViewById(R.id.text)
	val switchView: Switch = itemView.findViewById(R.id.switchView)
	val numberPicker: NumberPicker = itemView.findViewById(R.id.numberPicker)
	val colorBox: View = itemView.findViewById(R.id.colorBox)
	val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
	val seekbar: BubbleSeekBar = itemView.findViewById(R.id.bubbleSeekBar)
}