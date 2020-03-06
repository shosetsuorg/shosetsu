package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async

import android.os.AsyncTask
import android.util.Log
import com.github.doomsdayrs.api.shosetsu.services.core.Novel.Listing
import com.github.doomsdayrs.apps.shosetsu.backend.async.CatalogueLoader
import com.github.doomsdayrs.apps.shosetsu.backend.database.Database
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogueController
import com.github.doomsdayrs.apps.shosetsu.variables.ext.context
import com.github.doomsdayrs.apps.shosetsu.variables.ext.smallMessage
import com.github.doomsdayrs.apps.shosetsu.variables.ext.toast
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelListingCard
import org.luaj.vm2.LuaError

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
class CataloguePageLoader(private val catalogueFragment: CatalogueController) : AsyncTask<Int, Void, Boolean>() {

    /**
     * Loads up the category
     *
     * @param integers if length = 0, loads first page otherwise loads the page # correlated to the integer
     * @return if this was completed or not
     */
    override fun doInBackground(vararg integers: Int?): Boolean? {
        Log.d("Loading", "Catalogue")
        return catalogueFragment.let {
            if (it.formatter.hasCloudFlare && it.activity != null) it.activity!!.runOnUiThread {
                it.context?.toast("CLOUDFLARE")
            }
            try {
                val novels: Array<Listing> = if (integers.isNotEmpty()) CatalogueLoader(it.formatter).execute(integers[0]) else CatalogueLoader(it.formatter).execute()
                for ((title, link, imageURL) in novels) it.catalogueNovelCards.add(NovelListingCard(imageURL, title, Database.DatabaseIdentification.getNovelIDFromNovelURL(link), link))
                Log.d("FragmentRefresh", "Complete")
                true
            } catch (e: LuaError) {
                catalogueFragment.activity?.toast(e.smallMessage())
                false
            } catch (e: Exception) {
                catalogueFragment.activity?.toast(e.message ?: "UNKOWN ERROR")
                false
            }
        }

    }

    /**
     * Ends progress bar
     */
    override fun onCancelled() {
        catalogueFragment.swipeRefreshLayout?.isRefreshing = false
    }

    /**
     * Starts the loading action
     */
    override fun onPreExecute() {
        catalogueFragment.swipeRefreshLayout?.isRefreshing = true
    }

    /**
     * Once done remove progress bar
     *
     * @param aBoolean result of doInBackground
     */
    override fun onPostExecute(aBoolean: Boolean?) {
        aBoolean?.let {
            if (it) {
                catalogueFragment.catalogueAdapter.notifyDataSetChanged()
            }
        }
        catalogueFragment.swipeRefreshLayout?.isRefreshing = false
    }


}