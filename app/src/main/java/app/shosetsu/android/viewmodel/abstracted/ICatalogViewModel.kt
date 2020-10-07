package app.shosetsu.android.viewmodel.abstracted

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.lib.Filter
import app.shosetsu.lib.IExtension
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

	abstract val filterItemsLive: LiveData<HResult<List<Filter<*>>>>

	abstract val hasSearchLive: LiveData<HResult<Boolean>>

	/**
	 * Name of the extension that is used for its catalogue
	 */
	abstract val extensionName: LiveData<HResult<String>>

	/**
	 * Sets the [IExtension]
	 */
	abstract fun setFormatterID(formatterID: Int)

	/**
	 * Initializes [listingItemsLive]
	 */
	abstract fun loadData(): Job

	abstract fun setQuery(string: String)

	/**
	 * Called when done with [setQuery]
	 * Removes state of normal listing, and swaps [loadData] to load a query
	 */
	abstract fun loadQuery(): Job

	/**
	 * Load up sequential data
	 * Action depends on conditions
	 * If [inQuery] tries to load more for the query
	 * If [inSearch] it rejects the action
	 * Else loads more default data
	 */

	abstract fun loadMore()

	/**
	 * Reset [listingItemsLive], and runs [loadData] once again
	 */
	abstract fun resetView()

	/**
	 * Bookmarks and loads the specific novel in the background
	 * @param novelID ID of novel to load
	 */
	abstract fun backgroundNovelAdd(novelID: Int)

	/**
	 * Sets filters
	 */
	abstract fun setFilters(map: Map<Int, Any>)


	/** Destroy data */
	abstract fun destroy()
}