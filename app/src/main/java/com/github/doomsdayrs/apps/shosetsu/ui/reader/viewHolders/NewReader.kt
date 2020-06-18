package com.github.doomsdayrs.apps.shosetsu.ui.reader.viewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView

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
abstract class NewReader internal constructor(
		itemView: View
) : RecyclerView.ViewHolder(itemView) {
	/**
	 * Sets the unformatted text
	 */
	abstract fun setText(text: String?)

	/**
	 * Holder binds the text to the view, with formatting
	 */
	abstract fun bind()
}