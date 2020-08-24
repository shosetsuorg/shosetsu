package com.github.doomsdayrs.apps.shosetsu.viewmodel.base

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
	var inQuery: Boolean = false
	var inSearch: Boolean = false

	abstract var displayItems: LiveData<HResult<List<ACatalogNovelUI>>>
	abstract val formatterData: LiveData<HResult<Formatter>>

	/**
	 * Sets the [formatterData]
	 */
	abstract fun setFormatterID(formatterID: Int)

	/**
	 * Initializes [displayItems]
	 */
	abstract fun loadData(formatter: Formatter): Job

	/**
	 * Queries the source and puts the results in [displayItems]
	 */
	abstract fun loadQuery(query: String)

	/**
	 * Load up sequential data
	 * Action depends on conditions
	 * If [inQuery] tries to load more for the query
	 * If [inSearch] it rejects the action
	 * Else loads more default data
	 */
	abstract fun loadMore(formatter: Formatter)

	/**
	 * Takes currently viewed data, and returns it to the user
	 * @param query to compare [displayItems] against
	 */
	abstract fun searchPage(query: String)

	/**
	 * Reset [displayItems], and runs [loadData] once again
	 */
	abstract fun resetView(formatter: Formatter)

	/**
	 * Bookmarks and loads the specific novel in the background
	 * @param novelID ID of novel to load
	 */
	abstract fun backgroundNovelAdd(novelID: Int)
}