package app.shosetsu.android.ui.catalogue.listeners

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import app.shosetsu.android.viewmodel.abstracted.ACatalogViewModel

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
 * 18 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
class CatalogueHitBottom(
	private val viewModel: ACatalogViewModel,
) : RecyclerView.OnScrollListener() {
	private var lastBottomHit = 0L

	override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
		super.onScrollStateChanged(recyclerView, newState)
		if (!recyclerView.canScrollVertically(1)) {
			if (newState == SCROLL_STATE_IDLE)
				if (newState != SCROLL_STATE_DRAGGING)
					if (newState != SCROLL_STATE_SETTLING) {
						val currentTime = System.currentTimeMillis()
						if (lastBottomHit < currentTime - 500) {
							lastBottomHit = currentTime
							viewModel.loadMore()
						}
					}

		}
	}
}