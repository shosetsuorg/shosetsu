package com.github.doomsdayrs.apps.shosetsu.ui.downloads

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
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings
import com.github.doomsdayrs.apps.shosetsu.common.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.ui.downloads.adapters.DownloadAdapter
import com.github.doomsdayrs.apps.shosetsu.view.base.FastAdapterRecyclerController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.DownloadUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.abstracted.IDownloadsViewModel
import com.mikepenz.fastadapter.FastAdapter
import org.kodein.di.generic.instance

/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
//TODO selection mechanic with options to delete,  pause,  and more
class DownloadsController : FastAdapterRecyclerController<DownloadUI>() {
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
		viewModel.liveData.observe(this, Observer(::handleRecyclerUpdate))
	}

	override fun setupRecyclerView() {
		recyclerView?.setHasFixedSize(false)
		super.setupRecyclerView()
	}
}