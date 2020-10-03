package app.shosetsu.android.ui.downloads

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
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.ui.downloads.adapters.DownloadAdapter
import app.shosetsu.android.view.base.FABController
import app.shosetsu.android.view.base.FastAdapterRecyclerController.BasicFastAdapterRecyclerController
import app.shosetsu.android.view.base.PushCapableController
import app.shosetsu.android.view.uimodels.model.DownloadUI
import app.shosetsu.android.viewmodel.abstracted.IDownloadsViewModel
import com.bluelinelabs.conductor.Controller
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mikepenz.fastadapter.FastAdapter

/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
//TODO selection mechanic with options to delete,  pause,  and more
class DownloadsController : BasicFastAdapterRecyclerController<DownloadUI>(),
		PushCapableController, FABController {
	override val viewTitleRes: Int = R.string.downloads

	private val viewModel: IDownloadsViewModel by viewModel()

	private var fab: FloatingActionButton? = null

	override val fastAdapter: FastAdapter<DownloadUI> by lazy {
		val adapter = DownloadAdapter(viewModel)
		adapter.addAdapter(0, itemAdapter)
		adapter
	}

	private fun togglePause() {
		if (viewModel.isOnline()) viewModel.togglePause() else toast(R.string.you_not_online)
	}

	override fun onViewCreated(view: View) {
		viewModel.liveData.observe(this) { handleRecyclerUpdate(it) }
		viewModel.isDownloadPaused.observe(this) {
			fab?.setImageResource(
					if (it)
						R.drawable.play_arrow
					else R.drawable.ic_pause_circle_outline_24dp
			)
		}
	}


	override fun setupRecyclerView() {
		recyclerView.setHasFixedSize(false)
		super.setupRecyclerView()
	}

	override fun manipulateFAB(fab: FloatingActionButton) {
		this.fab = fab
		fab.setOnClickListener { togglePause() }
		fab.setImageResource(R.drawable.ic_pause_circle_outline_24dp)
	}

	override fun acceptPushing(pushController: (Controller) -> Unit) {
	}

}