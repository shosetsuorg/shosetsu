package com.github.doomsdayrs.apps.shosetsu.common.ext

import androidx.recyclerview.widget.DiffUtil

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
 */

/**
 * shosetsu
 * 08 / 05 / 2020
 */

abstract class AutoUtil<O : List<*>>(var old: O, var new: O) : DiffUtil.Callback() {
	override fun getOldListSize(): Int = old.size
	override fun getNewListSize(): Int = new.size
	override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
			old[oldItemPosition] == new[newItemPosition]

}