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
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.services.DownloadWorker
import com.github.doomsdayrs.apps.shosetsu.common.Settings
import com.github.doomsdayrs.apps.shosetsu.common.ext.setActivityTitle
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

	val viewModel: IDownloadsViewModel by viewModel()

	init {
		setHasOptionsMenu(true)
	}

	override fun onViewCreated(view: View) {
		activity?.setActivityTitle(R.string.downloads)
		createRecycler()
		viewModel.liveData.observe(this, Observer(::handleRecyclerUpdate))
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
			if (viewModel.togglePause())
				item.setIcon(R.drawable.ic_play_circle_filled_24dp)
			else {
				item.setIcon(R.drawable.ic_pause_circle_outline_24dp)
				DownloadWorker.start(activity!!)
			}
			return true
		}
		return false
	}

	override fun updateUI(list: List<DownloadUI>) {
		if (list.size < recyclerArray.size) {
			recyclerArray.clear()
			recyclerArray.addAll(list)
			recyclerView?.adapter?.notifyDataSetChanged()
		} else super.updateUI(list)
	}

	override fun difAreItemsTheSame(oldItem: DownloadUI, newItem: DownloadUI): Boolean =
			oldItem.chapterID == newItem.chapterID
}