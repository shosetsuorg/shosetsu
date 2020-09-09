package com.github.doomsdayrs.apps.shosetsu.ui.search.adapters

import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.ui.search.SearchController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.catlog.ACatalogNovelUI
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.search.SearchRowUI
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter

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
 * 09 / 09 / 2020
 */
class SearchRowAdapter(private val searchController: SearchController) : FastAdapter<SearchRowUI>() {
	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		super.onBindViewHolder(holder, position)
		@Suppress("NAME_SHADOWING")
		val holder = holder as SearchRowUI.ViewHolder

		val itemAdapter = ItemAdapter<ACatalogNovelUI>()
		val fastAdapter = with(itemAdapter)
		holder.recyclerView.adapter = fastAdapter
		getItem(position)?.let {}
	}
}