package com.github.doomsdayrs.apps.shosetsu.viewmodel.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.IDTitleImageUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.base.SubscribeHandleViewModel

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
		SubscribeHandleViewModel<List<IDTitleImageUI>>, ViewModel() {
	/**
	 * The current max page loaded, if 2, then the current page that has been appended is 2
	 */
	var currentMaxPage: Int = 1
	abstract val formatter: MutableLiveData<Formatter>
	abstract val formatterID: MutableLiveData<Int>

	abstract fun setFormatterID(formatterID: Int)
	abstract fun getFormatterID(): Int

	/**
	 * Instructs the view model to load more UwU
	 */
	abstract fun loadMore()

	/**
	 * Queries a string
	 */
	abstract fun loadQuery(query: String)

	/** Queries the current data displayed */
	abstract fun searchPage(query: String)

	/**
	 * Instruction to clear loaded chapters, append more UwU
	 */
	abstract fun clearAndLoad()

	abstract fun backgroundNovelAdd(novelID: Int)
}