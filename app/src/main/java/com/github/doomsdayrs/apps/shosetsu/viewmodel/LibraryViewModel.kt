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

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
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
	class LibraryDiffCallBack(
			private val oldList: List<Int>,
			private val newList: List<Int>
	) : DiffUtil.Callback() {
		override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
				(oldList[oldItemPosition] == newList[newItemPosition])

		override fun getOldListSize() = oldList.size

		override fun getNewListSize() = newList.size

		override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
				areItemsTheSame(oldItemPosition, newItemPosition)
	}

	override var selectedNovels = ArrayList<Int>()

	/**
	 * Internal cache of what [liveData] returns, for [selectedNovels]
	 */
	private val cachedNovel: ArrayList<NovelUI> = novelsRepository.loadData() as ArrayList<NovelUI>

	@Synchronized
	private fun clearAndAdd(list: List<NovelUI>) {
		cachedNovel.clear()
		cachedNovel.addAll(list)
	}

	override suspend fun selectAll() = cachedNovel
			.filter { !selectedNovels.contains(it.id) }
			.forEach { selectedNovels.add(it.id) }

	override suspend fun deselectAll() = selectedNovels.clear()

	override suspend fun removeAllFromLibrary() {
		novelsRepository.unBookmarkNovels(selectedNovels)
		selectedNovels.clear()
	}

	override suspend fun loadNovelIDs(): List<Int> = loadData().map { it.id }

	override suspend fun loadChaptersUnread(novelID: Int): Int =
			chaptersRepository.loadChapterUnreadCount(novelID)

	override fun loadNovel(id: Int): NovelUI? = cachedNovel.find { it.id == id }
	override fun getCachedData(): List<NovelUI> = cachedNovel

	override fun search(search: String): List<NovelUI> =
			cachedNovel.filter { it.title.contains(search, ignoreCase = true) }

	override fun subscribeObserver(
			owner: LifecycleOwner,
			observer: Observer<List<NovelUI>>
	) = novelsRepository.subscribeRepository(owner, observer)

	override suspend fun loadData(): List<NovelUI> {
		val data = novelsRepository.loadData()
		clearAndAdd(data)
		return data
	}
}