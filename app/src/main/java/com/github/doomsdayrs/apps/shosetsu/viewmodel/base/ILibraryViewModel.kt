package com.github.doomsdayrs.apps.shosetsu.viewmodel.base

import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity

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
interface ILibraryViewModel : SubscribeViewModel<List<NovelEntity>> {
	var selectedNovels: ArrayList<Int>

	suspend fun selectAll(callback: () -> Unit = {})
	suspend fun deselectAll(callback: () -> Unit = {})
	suspend fun removeAllFromLibrary(recyclerView: RecyclerView)
	fun loadNovelIDs(): List<Int>
	fun loadChaptersUnread(novelID: Int): Int
}