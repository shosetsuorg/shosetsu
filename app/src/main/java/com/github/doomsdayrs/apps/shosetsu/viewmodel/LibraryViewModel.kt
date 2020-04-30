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
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.ChaptersDao
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.NovelsDao
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ILibraryViewModel

/**
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class LibraryViewModel(
		val novelsDao: NovelsDao,
		val chaptersDao: ChaptersDao
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

	override val liveData: LiveData<List<NovelEntity>> by lazy { novelsDao.loadBookmarkedNovels() }
	override var selectedNovels = ArrayList<Int>()

	/**
	 * Internal cache of what [liveData] returns, for [selectedNovels]
	 */
	private val cachedIDs: ArrayList<Int> = arrayListOf()
		@Synchronized
		get

	@Synchronized
	private fun clearAndAdd(list: List<NovelEntity>) {
		cachedIDs.clear()
		cachedIDs.addAll(list.map { it.id })
	}

	override suspend fun selectAll(callback: () -> Unit) {
		cachedIDs.filter { !selectedNovels.contains(it) }
				.forEach { selectedNovels.add(it) }
		callback()
	}

	override suspend fun deselectAll(callback: () -> Unit) {
		selectedNovels.clear()
		callback()
	}

	override suspend fun removeAllFromLibrary(recyclerView: RecyclerView) {
		novelsDao.unBookmarkNovels(selectedNovels, liveData.value ?: arrayListOf())
		selectedNovels.clear()
	}

	override fun loadNovelIDs(): List<Int> {
		loadData()
		return cachedIDs
	}

	override fun loadChaptersUnread(novelID: Int): Int = chaptersDao.loadChapterUnreadCount(novelID)

	override fun subscribeObserver(
			owner: LifecycleOwner,
			observer: Observer<List<NovelEntity>>
	) {
		liveData.observe(owner, Observer {
			clearAndAdd(it)
			observer.onChanged(it)
		})
	}

	override fun loadData(): List<NovelEntity> {
		val data = liveData.value ?: arrayListOf()
		clearAndAdd(data)
		return data
	}
}