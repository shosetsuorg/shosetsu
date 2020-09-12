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

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import app.shosetsu.android.common.ShosetsuSettings
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.ui.downloads.adapters.DownloadAdapter
import app.shosetsu.android.view.base.FastAdapterRecyclerController.BasicFastAdapterRecyclerController
import app.shosetsu.android.view.uimodels.model.DownloadUI
import app.shosetsu.android.viewmodel.abstracted.IDownloadsViewModel
import com.github.doomsdayrs.apps.shosetsu.R
import com.mikepenz.fastadapter.FastAdapter
import org.kodein.di.generic.instance

/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
//TODO selection mechanic with options to delete,  pause,  and more
class DownloadsController : BasicFastAdapterRecyclerController<DownloadUI>() {
	override val viewTitleRes: Int = R.string.downloads

	private val viewModel: IDownloadsViewModel by viewModel()
	private val settings by instance<ShosetsuSettings>()

	override val fastAdapter: FastAdapter<DownloadUI> by lazy {
		val adapter = DownloadAdapter(viewModel)
		adapter.addAdapter(0, itemAdapter)
		adapter
	}

	init {
		setHasOptionsMenu(true)
	}

	/***/
	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_downloads, menu)
		settings.isDownloadPausedLive.observe(this) {
			menu.findItem(R.id.toolbar_downloads_pause)?.setIcon(
					if (it)
						R.drawable.ic_play_circle_filled_24dp
					else R.drawable.ic_pause_circle_outline_24dp
			)
		}
	}

	/***/
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == R.id.toolbar_downloads_pause) {
			if (viewModel.isOnline()) {
				viewModel.togglePause()
				return true
			} else toast(R.string.you_not_online)
		}
		return false
	}


	override fun onViewCreated(view: View) {
		viewModel.liveData.observe(this) { handleRecyclerUpdate(it) }
	}

	override fun setupRecyclerView() {
		recyclerView.setHasFixedSize(false)
		super.setupRecyclerView()
	}
}