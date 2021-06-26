package app.shosetsu.android.view.susScript.viewHolders

import android.view.View
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatTextView
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
 * 21 / 01 / 2020
 *
 * @author github.com/doomsdayrs
 */
class SusScriptCard(itemView: View) : RecyclerView.ViewHolder(itemView) {
	val title1: AppCompatTextView = itemView.findViewById(R.id.title)
	val version1: AppCompatTextView = itemView.findViewById(R.id.version)
	val hash1: AppCompatTextView = itemView.findViewById(R.id.hash)

	val title2: AppCompatTextView = itemView.findViewById(R.id.title2)
	val version2: AppCompatTextView = itemView.findViewById(R.id.version2)
	val hash2: AppCompatTextView = itemView.findViewById(R.id.hash2)

	val spinner: Spinner = itemView.findViewById(R.id.spinner)
}