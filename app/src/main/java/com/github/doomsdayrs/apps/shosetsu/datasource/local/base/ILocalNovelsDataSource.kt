package com.github.doomsdayrs.apps.shosetsu.datasource.local.base

import androidx.lifecycle.LiveData
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.IDTitleImage
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.IDTitleImageBook
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
 * 04 / 05 / 2020
 */
interface ILocalNovelsDataSource {
	/** load list of novels that are to be bookmarked */
	suspend fun loadBookmarkedNovels(): LiveData<HResult<List<NovelEntity>>>
	suspend fun loadBookmarkedNovelsCard(): LiveData<HResult<List<IDTitleImage>>>
	suspend fun loadNovel(novelID: Int): HResult<NovelEntity>
	suspend fun loadNovelLive(novelID: Int): LiveData<HResult<NovelEntity>>
	suspend fun updateNovel(novelEntity: NovelEntity)
	suspend fun bookmarkNovel(novelID: Int)
	suspend fun insertNovelReturnCard(novelEntity: NovelEntity): IDTitleImageBook
	suspend fun insertNovel(novelEntity: NovelEntity)
}