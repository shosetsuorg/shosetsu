package app.shosetsu.android.view.controller

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import com.github.doomsdayrs.apps.shosetsu.databinding.ControllerRefreshableRecyclerBinding
import com.mikepenz.fastadapter.GenericItem

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
 * 09 / 02 / 2021
 */
abstract class FastAdapterRefreshableRecyclerController<ITEM : GenericItem> :
	FastAdapterRecyclerController<ControllerRefreshableRecyclerBinding, ITEM>() {

	override fun bindView(inflater: LayoutInflater): ControllerRefreshableRecyclerBinding =
		ControllerRefreshableRecyclerBinding.inflate(inflater)
			.also { recyclerView = it.recyclerView }

	override fun onDestroyView(view: View) {
		binding.swipeRefreshLayout.setOnRefreshListener(null)
	}

	@CallSuper
	override fun hideEmpty() {
		if (!binding.recyclerView.isVisible) binding.recyclerView.isVisible = true
		binding.emptyDataView.hide()
	}

	@CallSuper
	override fun showEmpty() {
		if (itemAdapter.adapterItemCount > 0) return
		binding.recyclerView.isVisible = false
	}

	@CallSuper
	override fun onViewCreated(view: View) {
		binding.swipeRefreshLayout.setOnRefreshListener {
			onRefresh()
			binding.swipeRefreshLayout.isRefreshing = false
		}
	}

	/**
	 * Called when the user swipes down enough to trigger a refresh
	 * This will then be proceeded with the refreshing action ending
	 */
	abstract fun onRefresh()
}