package com.github.doomsdayrs.apps.shosetsu.ui.downloads

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.DownloadManager.initDownloadManager
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.downloads.adapters.DownloadAdapter
import com.github.doomsdayrs.apps.shosetsu.variables.Broadcasts
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem
import com.github.doomsdayrs.apps.shosetsu.variables.Settings
import kotlinx.android.synthetic.main.fragment_downloads.*

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
 * ====================================================================
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
//TODO selection mechanic with options to delete,  pause,  and more
class DownloadsFragment : Fragment(R.layout.fragment_downloads) {
    var downloadItems: ArrayList<DownloadItem> = ArrayList()
    var adapter: DownloadAdapter = DownloadAdapter(this)
    private lateinit var receiver: BroadcastReceiver

    init {
        setHasOptionsMenu(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(receiver)
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utilities.setActivityTitle(activity, getString(R.string.downloads))
        downloadItems = Database.DatabaseDownloads.getDownloadList()
        fragment_downloads_recycler.setHasFixedSize(false)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        adapter = DownloadAdapter(this)
        adapter.setHasStableIds(true)
        fragment_downloads_recycler.layoutManager = layoutManager
        fragment_downloads_recycler.adapter = adapter
        val filter = IntentFilter()

        filter.addAction(Broadcasts.BROADCAST_NOTIFY_DATA_CHANGE)
        filter.addAction(Broadcasts.DOWNLOADS_MARK_ERROR)
        filter.addAction(Broadcasts.DOWNLOADS_TOGGLE)
        filter.addAction(Broadcasts.DOWNLOADS_REMOVE)

        receiver = object : BroadcastReceiver() {


            private fun removeDownloads(chapterURL: String) {
                for (x in adapter.downloadsFragment.downloadItems.indices) if (adapter.downloadsFragment.downloadItems[x].chapterURL == chapterURL) {
                    adapter.downloadsFragment.downloadItems.removeAt(x)
                    return
                }
                adapter.notifyDataSetChanged()
            }

            private fun markError(chapterURL: String) {
                for (downloadItem in adapter.downloadsFragment.downloadItems)
                    if (downloadItem.chapterURL == chapterURL)
                        downloadItem.status = "Error"

                fragment_downloads_recycler?.adapter?.notifyDataSetChanged()

            }

            private fun toggleProcess(chapterURL: String) {
                for (downloadItem in adapter.downloadsFragment.downloadItems)
                    if (downloadItem.chapterURL == chapterURL)
                        if (downloadItem.status == "Pending" || downloadItem.status == "Error")
                            downloadItem.status = "Downloading"
                        else downloadItem.status = "Pending"
                fragment_downloads_recycler?.adapter?.notifyDataSetChanged()
            }


            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let { i ->
                    when (i.action) {
                        Broadcasts.BROADCAST_NOTIFY_DATA_CHANGE -> (fragment_downloads_recycler?.adapter as DownloadAdapter).notifyDataSetChanged()
                        Broadcasts.DOWNLOADS_REMOVE -> i.getStringExtra(Broadcasts.DOWNLOADS_RECIEVED_URL)?.let { removeDownloads(it) }
                        Broadcasts.DOWNLOADS_TOGGLE -> i.getStringExtra(Broadcasts.DOWNLOADS_RECIEVED_URL)?.let { toggleProcess(it) }
                        Broadcasts.DOWNLOADS_MARK_ERROR -> i.getStringExtra(Broadcasts.DOWNLOADS_RECIEVED_URL)?.let { markError(it) }
                        else -> Log.e("DownloadsFragment", "No action provided!")
                    }
                }
            }

        }
        activity?.registerReceiver(receiver, filter)

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
        if (Settings.downloadPaused)
            menuItem.setIcon(R.drawable.ic_play_circle_filled_black_24dp)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.toolbar_downloads_pause) {
            if (Utilities.togglePause())
                item.setIcon(R.drawable.ic_play_circle_filled_black_24dp)
            else {
                item.setIcon(R.drawable.ic_pause_circle_outline_black_24dp)
                initDownloadManager(activity!!)
            }
            return true
        }
        return false
    }
}