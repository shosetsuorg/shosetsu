package com.github.doomsdayrs.apps.shosetsu.viewmodel.base

import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.NovelListingCard
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
interface ICatalogViewModel :
		SubscribeHandleViewModel<List<NovelListingCard>> {
	/**
	 * The current max page loaded, if 2, then the current page that has been appended is 2
	 */
	var currentMaxPage: Int

	/**
	 * Instructs the view model to load more UwU
	 */
	fun loadMore()

	/**
	 * Instruction to clear loaded chapters, append more UwU
	 */
	fun clearAndLoad()
}