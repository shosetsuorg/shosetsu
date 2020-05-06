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

import android.content.BroadcastReceiver
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.services.DownloadService
import com.github.doomsdayrs.apps.shosetsu.common.ext.getString
import com.github.doomsdayrs.apps.shosetsu.common.ext.viewModel
import com.github.doomsdayrs.apps.shosetsu.ui.downloads.adapters.DownloadAdapter
import com.github.doomsdayrs.apps.shosetsu.view.base.RecyclerController
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.DownloadUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IDownloadsViewModel

/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
//TODO selection mechanic with options to delete,  pause,  and more
class DownloadsController : RecyclerController<DownloadAdapter, DownloadUI>() {

	private lateinit var receiver: BroadcastReceiver
	private val downloadsViewModel: IDownloadsViewModel by viewModel()

	init {
		setHasOptionsMenu(true)
	}

	override fun onDestroy() {
		super.onDestroy()
		activity?.unregisterReceiver(receiver)
	}

	override fun onViewCreated(view: View) {
		Utilities.setActivityTitle(activity, getString(R.string.downloads))
		createRecycler()
		downloadsViewModel.liveData.observe(this, Observer(::handleRecyclerUpdate))
	}

	private fun createRecycler() {
		recyclerView?.setHasFixedSize(false)
		adapter = DownloadAdapter(this)
		adapter?.setHasStableIds(true)
	}

	/**
	 * Creates the option menu (on the top toolbar)
	 *
	 * @param menu     Menu reference to fill
	 * @param inflater Object to inflate the menu
	 */
	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.toolbar_downloads, menu)
		val menuItem = menu.findItem(R.id.toolbar_downloads_pause)
		if (Settings.isDownloadPaused)
			menuItem.setIcon(R.drawable.ic_play_circle_filled_24dp)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == R.id.toolbar_downloads_pause) {
			if (Utilities.togglePause())
				item.setIcon(R.drawable.ic_play_circle_filled_24dp)
			else {
				item.setIcon(R.drawable.ic_pause_circle_outline_24dp)
				DownloadService.start(activity!!)
			}
			return true
		}
		return false
	}

	override fun difAreItemsTheSame(oldItem: DownloadUI, newItem: DownloadUI): Boolean =
			oldItem.chapterID == newItem.chapterID
}