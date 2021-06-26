package app.shosetsu.android.viewmodel.abstracted

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.utils.ColumnCalculator
import app.shosetsu.android.view.uimodels.model.catlog.ACatalogNovelUI
import app.shosetsu.android.viewmodel.base.ErrorReportingViewModel
import app.shosetsu.android.viewmodel.base.ShosetsuViewModel
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.enums.NovelCardType
import app.shosetsu.lib.Filter
import app.shosetsu.lib.IExtension

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
abstract class ICatalogViewModel :
	ShosetsuViewModel(),
	ErrorReportingViewModel,
	ColumnCalculator {

	/**
	 * What is currently being displayed to the user
	 */
	abstract val itemsLive: LiveData<HResult<List<ACatalogNovelUI>>>

	abstract val filterItemsLive: LiveData<HResult<List<Filter<*>>>>

	/**
	 * enable or disable searching
	 */
	abstract val hasSearchLive: LiveData<Boolean>

	/**
	 * Name of the extension that is used for its catalogue
	 */
	abstract val extensionName: LiveData<HResult<String>>


	abstract val novelCardTypeLive: LiveData<NovelCardType>


	/**
	 * Sets the [IExtension]
	 */
	abstract fun setExtensionID(extensionID: Int)

	/**
	 * Initializes [itemsLive]
	 */

	abstract fun applyQuery(newQuery: String)

	/**
	 * Ask for more to be loaded
	 */
	abstract fun loadMore()

	/**
	 * Reset [itemsLive], and runs [loadData] once again
	 */
	abstract fun resetView()

	/**
	 * Bookmarks and loads the specific novel in the background
	 * @param novelID ID of novel to load
	 */
	abstract fun backgroundNovelAdd(novelID: Int)

	/**
	 * Updates the filter data, does not apply the result
	 */
	abstract fun applyFilter(map: Map<Int, Any>)

	/**
	 * Reset the filter data to nothing
	 */
	abstract fun resetFilter()


	/** Destroy data */
	abstract fun destroy()
}