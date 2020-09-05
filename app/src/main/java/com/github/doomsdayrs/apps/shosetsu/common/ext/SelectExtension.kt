package com.github.doomsdayrs.apps.shosetsu.common.ext

import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.ISelectionListener
import com.mikepenz.fastadapter.select.SelectExtension

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
 * 03 / 07 / 2020
 */

inline fun <reified T : GenericItem> SelectExtension<T>.setSelectionListener(
		crossinline onSelect: (
				@ParameterName("item") T,
				@ParameterName("selected") Boolean,
		) -> Unit,
) {
	selectionListener = object : ISelectionListener<T> {
		override fun onSelectionChanged(item: T, selected: Boolean) {
			onSelect(item, selected)
		}
	}
}