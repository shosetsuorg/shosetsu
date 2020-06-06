package com.github.doomsdayrs.apps.shosetsu.domain.repository.base
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
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.BookmarkedNovelEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.IDTitleImage
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.IDTitleImageBook
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity


/**
 * shosetsu
 * 25 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
interface INovelsRepository {
	suspend fun suspendedGetLiveBookmarked(): LiveData<HResult<List<BookmarkedNovelEntity>>>
	suspend fun suspendedGetBookmarkedNovels(): HResult<List<NovelEntity>>
	fun blockingGetBookmarkedNovels(): HResult<List<NovelEntity>>
	suspend fun updateNovel(novelEntity: NovelEntity)
	suspend fun unBookmarkNovels(selectedNovels: List<Int>)
	suspend fun searchBookmarked(string: String): LiveData<HResult<List<IDTitleImage>>>
	suspend fun loadNovel(novelID: Int): HResult<NovelEntity>
	suspend fun loadNovelLive(novelID: Int): LiveData<HResult<NovelEntity>>

	suspend fun insertNovelReturnCard(novelEntity: NovelEntity): IDTitleImageBook
	suspend fun insertNovel(novelEntity: NovelEntity)

	/**
	 * Updates a novel entity with new data
	 */
	suspend fun updateNovelData(novelEntity: NovelEntity, novelInfo: Novel.Info)

	/**
	 * Retrieves NovelInfo
	 */
	suspend fun retrieveNovelInfo(
			formatter: Formatter,
			novelEntity: NovelEntity,
			loadChapters: Boolean
	): HResult<Novel.Info>

	/**
	 * Bookmark a novel by it's ID
	 */
	suspend fun setNovelBookmark(novelID: Int, bookmark: Int)
}