package com.github.doomsdayrs.apps.shosetsu.ui.search.adapters

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.catlog.ACatalogNovelUI
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.search.SearchRowUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.abstracted.ISearchViewModel
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil

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
class SearchRowAdapter(private val lifecycleOwner: LifecycleOwner, val viewModel: ISearchViewModel) : FastAdapter<SearchRowUI>() {
	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		super.onBindViewHolder(holder, position)
		@Suppress("NAME_SHADOWING")
		val holder = holder as SearchRowUI.ViewHolder

		val itemAdapter = ItemAdapter<ACatalogNovelUI>()
		val fastAdapter = with(itemAdapter)
		holder.recyclerView.adapter = fastAdapter

		val handleUpdate = { result: HResult<List<ACatalogNovelUI>> ->
			when (result) {
				is HResult.Loading -> holder.progressBar.visibility = View.VISIBLE
				is HResult.Empty -> holder.itemView.visibility = View.GONE
				is HResult.Error -> {
				}
				is HResult.Success -> {
					FastAdapterDiffUtil[itemAdapter] = FastAdapterDiffUtil.calculateDiff(
							itemAdapter,
							result.data
					)
				}
			}
		}

		getItem(position)?.let { (formatterID) ->
			if (formatterID != -1) {
				viewModel.searchFormatter(formatterID).observe(lifecycleOwner) {
					handleUpdate(it)
				}
			} else {
				viewModel.searchLibrary().observe(lifecycleOwner) {
					handleUpdate(it)
				}
			}
		}
	}
}