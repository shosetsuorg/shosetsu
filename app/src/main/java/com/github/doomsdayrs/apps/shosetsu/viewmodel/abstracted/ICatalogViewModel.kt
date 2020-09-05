package com.github.doomsdayrs.apps.shosetsu.viewmodel.abstracted

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.catlog.ACatalogNovelUI
import kotlinx.coroutines.Job

/*
 * This file is part of shosetsu.
 *
 * shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */




/**
 * shosetsu
 * 01 / 05 / 2020
 * Used for showing the specific listing of a novel
 */
abstract class ICatalogViewModel : ViewModel() {

	/**
	 * The current max page loaded, if 2, then the current page that has been appended is 2
	 */
	var currentMaxPage: Int = 1
	private var inQuery: Boolean = false
	private var inSearch: Boolean = false

	/**
	 * Novels listed by the catalogue listing
	 */
	abstract val listingItemsLive: LiveData<HResult<List<ACatalogNovelUI>>>

	/**
	 * Name of the extension that is used for its catalogue
	 */
	abstract val extensionName: LiveData<HResult<String>>

	/**
	 * Sets the [Formatter]
	 */
	abstract fun setFormatterID(formatterID: Int)

	/**
	 * Initializes [listingItemsLive]
	 */
	abstract fun loadData(): Job

	/**
	 * Queries the source and puts the results in [listingItemsLive]
	 */
	abstract fun loadQuery(query: String)

	/**
	 * Load up sequential data
	 * Action depends on conditions
	 * If [inQuery] tries to load more for the query
	 * If [inSearch] it rejects the action
	 * Else loads more default data
	 */

	abstract fun loadMore()

	/**
	 * Takes currently viewed data, and returns it to the user
	 * @param query to compare [listingItemsLive] against
	 */
	abstract fun searchPage(query: String)

	/**
	 * Reset [listingItemsLive], and runs [loadData] once again
	 */
	abstract fun resetView()

	/**
	 * Bookmarks and loads the specific novel in the background
	 * @param novelID ID of novel to load
	 */
	abstract fun backgroundNovelAdd(novelID: Int)
}