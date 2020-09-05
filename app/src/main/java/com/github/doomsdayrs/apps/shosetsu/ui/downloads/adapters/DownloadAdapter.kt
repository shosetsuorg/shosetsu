package com.github.doomsdayrs.apps.shosetsu.ui.downloads.adapters

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

import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.DownloadUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.abstracted.IDownloadsViewModel
import com.mikepenz.fastadapter.FastAdapter

/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class DownloadAdapter(
		private var viewModel: IDownloadsViewModel,
) : FastAdapter<DownloadUI>() {
	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		super.onBindViewHolder(holder, position)
		getItem(position)?.let { downloadUI ->
			(holder as DownloadUI.ViewHolder).apply {
				popupMenu?.setOnMenuItemClickListener {
					when (it.itemId) {
						R.id.delete -> {
							viewModel.delete(downloadUI)
							true
						}
						R.id.pause -> {
							viewModel.pause(downloadUI)
							true
						}
						R.id.start -> {
							viewModel.start(downloadUI)
							true
						}
						else -> false
					}
				}
			}
		}
	}
}