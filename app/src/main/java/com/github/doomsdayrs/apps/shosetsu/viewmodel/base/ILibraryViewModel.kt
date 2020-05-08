package com.github.doomsdayrs.apps.shosetsu.viewmodel.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
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
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
interface ILibraryViewModel : SubscribeHandleViewModel<List<IDTitleImageUI>> {
	/** List of selected novels */
	var selectedNovels: MutableLiveData<List<Int>>

	/** Novels that are currently visible, Good for search */
	var visible: MutableLiveData<List<Int>>

	fun handleSelect(id: Int)
	fun select(id: Int)
	fun deselect(id: Int)
	fun selectAll()
	fun deselectAll()
	fun removeAllFromLibrary()
	fun loadChaptersUnread(novelID: Int): LiveData<HResult<Int>>

	/**
	 * @return new list
	 */
	fun search(search: String): List<IDTitleImageUI>
}