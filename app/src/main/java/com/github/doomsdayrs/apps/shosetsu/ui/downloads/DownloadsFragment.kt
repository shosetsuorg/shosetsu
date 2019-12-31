package com.github.doomsdayrs.apps.shosetsu.ui.downloads

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.DownloadManager.initDownloadManager
import com.github.doomsdayrs.apps.shosetsu.backend.Utilities
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.downloads.adapters.DownloadAdapter
import com.github.doomsdayrs.apps.shosetsu.variables.DownloadItem
import com.github.doomsdayrs.apps.shosetsu.variables.Settings
import kotlinx.android.synthetic.main.fragment_downloads.*
import kotlin.collections.ArrayList

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
class DownloadsFragment : Fragment() {
    var downloadItems: ArrayList<DownloadItem> = ArrayList()
    var adapter: DownloadAdapter = DownloadAdapter(this)

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    /**
     * Creates view
     *
     * @param inflater           inflater to retrieve objects
     * @param container          container of this fragment
     * @param savedInstanceState save
     * @return View
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("OnCreateView", "NovelFragmentChapters")
        Utilities.setActivityTitle(activity, "Downloads")
        return inflater.inflate(R.layout.fragment_downloads, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        downloadItems = Database.DatabaseDownloads.getDownloadList()
        setDownloads()
    }

    /**
     * Sets the novel chapters down
     */
    private fun setDownloads() {
        fragment_downloads_recycler.setHasFixedSize(false)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        adapter = DownloadAdapter(this)
        adapter.setHasStableIds(true)
        fragment_downloads_recycler.layoutManager = layoutManager
        fragment_downloads_recycler.adapter = adapter
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
        if (Settings.downloadPaused) menuItem.setIcon(R.drawable.ic_pause_circle_filled_black_24dp)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.toolbar_downloads_pause) {
            if (Utilities.togglePause()) item.setIcon(R.drawable.ic_pause_circle_filled_black_24dp) else {
                item.setIcon(R.drawable.ic_pause_circle_outline_black_24dp)
                initDownloadManager(activity!!)
            }
            return true
        }
        return false
    }

    companion object {


    }

    /**
     * Constructor
     */
    init {
        setHasOptionsMenu(true)
    }
}