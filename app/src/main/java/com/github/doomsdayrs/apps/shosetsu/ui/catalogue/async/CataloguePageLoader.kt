package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async

import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.github.doomsdayrs.api.shosetsu.services.core.Novel.Listing
import com.github.doomsdayrs.apps.shosetsu.backend.async.CatalogueLoader
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogueFragment
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.CatalogueNovelCard
import kotlinx.android.synthetic.main.fragment_catalogue.*

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
 */
/**
 * Shosetsu
 * 17 / 06 / 2019
 *
 * @author github.com/doomsdayrs
 */
class CataloguePageLoader
/**
 * Constructor
 *
 * @param catalogueFragment the fragment this is assigned to (reference to parent)
 */(catalogueFragment: CatalogueFragment) : AsyncTask<Int, Void, Boolean>() {
    // References to objects
    private val catalogueFragment: CatalogueFragment? = catalogueFragment


    /**
     * Loads up the category
     *
     * @param integers if length = 0, loads first page otherwise loads the page # correlated to the integer
     * @return if this was completed or not
     */
    override fun doInBackground(vararg integers: Int?): Boolean {
        Log.d("Loading", "Catalogue")
        catalogueFragment?.let {
            if (it.formatter.hasCloudFlare) {
                if (it.activity != null) it.activity!!.runOnUiThread { Toast.makeText(it.context, "CLOUDFLARE", Toast.LENGTH_SHORT).show() }
            }
            val novels: Array<Listing> = if (integers.isNotEmpty()) CatalogueLoader(it.formatter).execute(integers[0]) else CatalogueLoader(it.formatter).execute()
            for (novel in novels) it.catalogueNovelCards.add(CatalogueNovelCard(novel.imageURL, novel.title, Database.DatabaseIdentification.getNovelIDFromNovelURL(novel.link), novel.link))
            it.recyclerView?.post { it.catalogueAdapter.notifyDataSetChanged() }

            Log.d("FragmentRefresh", "Complete")
            if (it.activity != null) it.activity!!.runOnUiThread {
                it.catalogueAdapter.notifyDataSetChanged()
                it.swipeRefreshLayout?.isRefreshing = false
            }
        }

        return true
    }

    /**
     * Ends progress bar
     */
    override fun onCancelled() {
        catalogueFragment?.swipeRefreshLayout?.isRefreshing = false
    }

    /**
     * Starts the loading action
     */
    override fun onPreExecute() {
        catalogueFragment?.swipeRefreshLayout?.isRefreshing = true
    }

    /**
     * Once done remove progress bar
     *
     * @param aBoolean result of doInBackground
     */
    override fun onPostExecute(aBoolean: Boolean) {
        catalogueFragment?.swipeRefreshLayout?.isRefreshing = false
    }


}