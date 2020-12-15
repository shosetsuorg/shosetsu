package app.shosetsu.android.ui.repository

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.view.controller.base.FABController
import app.shosetsu.android.view.controller.FastAdapterRecyclerController.BasicFastAdapterRecyclerController
import app.shosetsu.android.view.uimodels.model.RepositoryUI
import app.shosetsu.android.viewmodel.abstracted.ARepositoryViewModel
import app.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook

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
 * shosetsu
 * 16 / 09 / 2020
 */
class RepositoryController : BasicFastAdapterRecyclerController<RepositoryUI>(), FABController {
	private val viewModel: ARepositoryViewModel by viewModel()

	override val viewTitleRes: Int = R.string.repositories

	override fun onViewCreated(view: View) {
	}

	override fun showEmpty() {
		super.showEmpty()
		binding.emptyDataView.show("HOW DO YOU NOT HAVE ANY REPOSITORIES")
	}

	override fun setupRecyclerView() {
		super.setupRecyclerView()
		viewModel.liveData.observe(this) { handleRecyclerUpdate(it) }
	}

	override fun handleErrorResult(e: HResult.Error) {
		super.handleErrorResult(e)
		viewModel.reportError(e)
	}

	override fun setupFastAdapter() {
		fastAdapter.addEventHook(object : ClickEventHook<RepositoryUI>() {
			override fun onBind(viewHolder: RecyclerView.ViewHolder): View? =
				if (viewHolder is RepositoryUI.ViewHolder)
					viewHolder.binding.removeButton
				else null

			override fun onClick(
				v: View,
				position: Int,
				fastAdapter: FastAdapter<RepositoryUI>,
				item: RepositoryUI
			) {
				// Open remove thing
			}
		})
	}


	override fun manipulateFAB(fab: FloatingActionButton) {
		// On add repo
		fab.setImageResource(R.drawable.add_circle_outline)
	}
}