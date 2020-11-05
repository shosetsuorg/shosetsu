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
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.view.base.ExtendedFABController
import app.shosetsu.android.view.base.FastAdapterRecyclerController.BasicFastAdapterRecyclerController
import app.shosetsu.android.view.base.PushCapableController
import app.shosetsu.android.view.uimodels.model.DownloadUI
import app.shosetsu.android.viewmodel.abstracted.IDownloadsViewModel
import com.bluelinelabs.conductor.Controller
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
//TODO selection mechanic with options to delete,  pause,  and more
class DownloadsController : BasicFastAdapterRecyclerController<DownloadUI>(),
		PushCapableController, ExtendedFABController {
	override val viewTitleRes: Int = R.string.downloads

	private val viewModel: IDownloadsViewModel by viewModel()

	private var fab: ExtendedFloatingActionButton? = null


	private fun togglePause() {
		if (viewModel.isOnline()) viewModel.togglePause() else toast(R.string.you_not_online)
	}

	override fun onViewCreated(view: View) {
		viewModel.liveData.observe(this) { handleRecyclerUpdate(it) }
		viewModel.isDownloadPaused.observe(this) {
			fab?.setText(
					if (it)
						R.string.resume
					else R.string.pause
			)
			fab?.setIconResource(
					if (it)
						R.drawable.play_arrow
					else R.drawable.ic_pause_circle_outline_24dp
			)
		}
	}

	override fun handleErrorResult(e: HResult.Error) {
		super.handleErrorResult(e)
		viewModel.reportError(e)
	}

	override fun setupRecyclerView() {
		recyclerView.setHasFixedSize(false)
		super.setupRecyclerView()
	}

	override fun acceptPushing(pushController: (Controller) -> Unit) {
	}

	override fun manipulateFAB(fab: ExtendedFloatingActionButton) {
		this.fab = fab
		fab.setOnClickListener { togglePause() }
		fab.setText(R.string.paused)
		fab.setIconResource(R.drawable.ic_pause_circle_outline_24dp)
	}

}