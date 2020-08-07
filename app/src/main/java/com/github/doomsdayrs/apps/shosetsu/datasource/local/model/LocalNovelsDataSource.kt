package com.github.doomsdayrs.apps.shosetsu.datasource.local.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.datasource.local.base.ILocalNovelsDataSource
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.BookmarkedNovelEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.IDTitleImageBook
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.NovelsDao

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
 * 12 / 05 / 2020
 */
class LocalNovelsDataSource(
		private val novelsDao: NovelsDao
) : ILocalNovelsDataSource {
	override suspend fun loadLiveBookmarkedNovels(): LiveData<HResult<List<NovelEntity>>> =
			novelsDao.loadListBookmarkedNovels().map { successResult(it) }

	override suspend fun loadBookmarkedNovels(): HResult<List<NovelEntity>> =
			successResult(novelsDao.loadBookmarkedNovels())

	override suspend fun loadLiveBookmarkedNovelsAndCount()
			: LiveData<HResult<List<BookmarkedNovelEntity>>> =
			novelsDao.loadBookmarkedNovelsCount().map { successResult(it) }

	override suspend fun loadNovel(novelID: Int): HResult<NovelEntity> =
			successResult(novelsDao.loadNovel(novelID))

	override suspend fun loadNovelLive(novelID: Int): LiveData<HResult<NovelEntity>> =
			novelsDao.loadNovelLive(novelID).map { successResult(it) }

	override suspend fun updateNovel(novelEntity: NovelEntity) =
			novelsDao.suspendedUpdate(novelEntity)

	override suspend fun updateBookmarkedNovels(list: List<BookmarkedNovelEntity>) {
		novelsDao.updateBookmarked(list)
	}

	override suspend fun setNovelBookmark(novelID: Int, bookmark: Int) =
			novelsDao.setNovelBookmark(novelID, bookmark)

	override suspend fun insertNovelReturnCard(novelEntity: NovelEntity): IDTitleImageBook {
		Log.d(logID(), "insertNovelReturnCard $novelEntity")
		return novelsDao.insertNovelReturnCard(novelEntity)
	}

	override suspend fun insertNovel(novelEntity: NovelEntity) {
		novelsDao.insertIgnore(novelEntity)
	}
}