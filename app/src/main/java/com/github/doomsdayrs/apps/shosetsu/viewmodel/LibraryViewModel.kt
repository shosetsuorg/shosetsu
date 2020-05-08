package com.github.doomsdayrs.apps.shosetsu.viewmodel

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

import androidx.lifecycle.LiveData
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IChaptersRepository
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.INovelsRepository
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.NovelUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ILibraryViewModel

/**
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class LibraryViewModel(
		val novelsRepository: INovelsRepository,
		val chaptersRepository: IChaptersRepository
) : ILibraryViewModel {
	override var selectedNovels = ArrayList<Int>()
	override fun selectAll() {
		TODO("Not yet implemented")
	}

	override fun deselectAll() = selectedNovels.clear()
	override fun removeAllFromLibrary() {
		TODO("Not yet implemented")
	}

	override fun loadNovelIDs(): List<Int> {
		TODO("Not yet implemented")
	}

	override fun loadChaptersUnread(novelID: Int): Int {
		TODO("Not yet implemented")
	}

	override fun loadNovel(id: Int): NovelUI? {
		TODO("Not yet implemented")
	}

	override fun getCachedData(): List<NovelUI> {
		TODO("Not yet implemented")
	}

	override fun search(search: String): List<NovelUI> {
		TODO("Not yet implemented")
	}

	override val liveData: LiveData<HResult<List<NovelUI>>>
		get() = TODO("Not yet implemented")

}