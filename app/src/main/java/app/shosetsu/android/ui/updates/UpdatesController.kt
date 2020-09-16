package app.shosetsu.android.ui.updates

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

import android.view.View
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.view.base.CollapsedToolBarController
import app.shosetsu.android.view.base.FastAdapterRecyclerController.BasicFastAdapterRecyclerController
import app.shosetsu.android.view.uimodels.model.UpdateUI
import app.shosetsu.android.viewmodel.abstracted.IUpdatesViewModel
import com.github.doomsdayrs.apps.shosetsu.R

/**
 * shosetsu
 * 15 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */
class UpdatesController : BasicFastAdapterRecyclerController<UpdateUI>(), CollapsedToolBarController {
	val viewModel: IUpdatesViewModel by viewModel()
	override val viewTitleRes: Int = R.string.updates
	override fun onViewCreated(view: View) {}


	override fun setupRecyclerView() {
		super.setupRecyclerView()
		recyclerView.setPadding(0, 0, 0, 8)
	}

	override fun setupFastAdapter() {
		fastAdapter.setOnClickListener { _, _, (chapterID, novelID), _ ->
			activity?.openChapter(chapterID, novelID)
			true
		}
		startObservation()
	}

	private fun startObservation() {
		viewModel.liveData.observe(this) {
			handleRecyclerUpdate(it)
		}
	}

	override fun updateUI(newList: List<UpdateUI>) {
		// Launches the sorting task async, then it passes the result to the UI
		launchIO { newList.sortedBy { it.time }.let { launchUI { super.updateUI(it) } } }
	}

	override fun showEmpty() {
		super.showEmpty()
		binding.emptyDataView.show("No updates yet! Maybe check again?")
	}
}