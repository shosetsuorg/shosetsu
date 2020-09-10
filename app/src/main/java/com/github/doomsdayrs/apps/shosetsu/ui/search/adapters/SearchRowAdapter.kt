package com.github.doomsdayrs.apps.shosetsu.ui.search.adapters

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bluelinelabs.conductor.Router
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys.BUNDLE_NOVEL_ID
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.common.ext.setOnClickListener
import com.github.doomsdayrs.apps.shosetsu.common.ext.withFadeTransaction
import com.github.doomsdayrs.apps.shosetsu.ui.novel.NovelController
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
class SearchRowAdapter(private val lifecycleOwner: LifecycleOwner, private val router: Router, private val viewModel: ISearchViewModel) : FastAdapter<SearchRowUI>() {
	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		super.onBindViewHolder(holder, position)
		@Suppress("NAME_SHADOWING")
		val holder = holder as SearchRowUI.ViewHolder

		val itemAdapter = ItemAdapter<ACatalogNovelUI>()
		val fastAdapter = object : FastAdapter<ACatalogNovelUI>() {
			override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
				super.onBindViewHolder(holder, position)
				holder.itemView.layoutParams = ViewGroup.MarginLayoutParams(400, 600).apply {
					this.setMargins(10, 10, 10, 10)
				}
			}
		}
		fastAdapter.addAdapter(0, itemAdapter)
		fastAdapter.setOnClickListener { _, _, item, _ ->
			Log.d(logID(),"Pushing")
			router.pushController(NovelController(bundleOf(BUNDLE_NOVEL_ID to item.id)).withFadeTransaction())
			true
		}

		holder.recyclerView.adapter = fastAdapter

		val handleUpdate = { result: HResult<List<ACatalogNovelUI>> ->
			when (result) {
				is HResult.Loading -> holder.progressBar.visibility = View.VISIBLE
				is HResult.Empty -> holder.itemView.visibility = View.GONE
				is HResult.Error -> {
				}
				is HResult.Success -> {
					holder.progressBar.visibility = View.GONE
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