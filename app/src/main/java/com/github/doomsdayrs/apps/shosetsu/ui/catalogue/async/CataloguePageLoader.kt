package com.github.doomsdayrs.apps.shosetsu.ui.catalogue.async

import android.os.AsyncTask
import android.util.Log
import com.github.doomsdayrs.apps.shosetsu.ui.catalogue.CatalogController

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
class CataloguePageLoader(private val catalogController: CatalogController)
	: AsyncTask<Int, Void, Boolean>() {

	/**
	 * Loads up the category
	 *
	 * @param v if length = 0, loads first page, otherwise loads the v[0]th page
	 * @return if this was completed or not
	 */
	override fun doInBackground(vararg v: Int?): Boolean? {
		Log.d("Loading", "Catalogue")
		return false
	}

	override fun onCancelled() {
		catalogController.swipeRefreshLayout?.isRefreshing = false
	}

	override fun onPreExecute() {
		catalogController.swipeRefreshLayout?.isRefreshing = true
	}

	/**
	 * Once done remove progress bar
	 *
	 * @param aBoolean result of doInBackground
	 */
	override fun onPostExecute(aBoolean: Boolean?) {
		catalogController.swipeRefreshLayout?.isRefreshing = false
	}
}