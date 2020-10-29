package app.shosetsu.android.datasource.database.base

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.domain.model.local.BookmarkedNovelEntity
import app.shosetsu.android.domain.model.local.IDTitleImageBook
import app.shosetsu.android.domain.model.local.NovelEntity
import kotlinx.coroutines.flow.Flow

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
	suspend fun loadLiveBookmarkedNovels(): Flow<HResult<List<NovelEntity>>>

	/** Load list of bookmarked [NovelEntity] */
	suspend fun loadBookmarkedNovels(): HResult<List<NovelEntity>>

	/** Loads a [List] of [BookmarkedNovelEntity] that are in the library */
	suspend fun loadLiveBookmarkedNovelsAndCount(): Flow<HResult<List<BookmarkedNovelEntity>>>

	/** Loads a [NovelEntity] by its [novelID] */
	suspend fun loadNovel(novelID: Int): HResult<NovelEntity>

	/** Loads a [LiveData] of a [NovelEntity] by its [novelID] */
	suspend fun loadNovelLive(novelID: Int): Flow<HResult<NovelEntity>>

	/** Updates a [NovelEntity] */
	suspend fun updateNovel(novelEntity: NovelEntity): HResult<*>

	/** Updates a list of [BookmarkedNovelEntity] */
	suspend fun updateBookmarkedNovels(list: List<BookmarkedNovelEntity>): HResult<*>

	/** Inserts a [NovelEntity] then returns its [IDTitleImageBook] */
	suspend fun insertNovelReturnCard(novelEntity: NovelEntity): HResult<IDTitleImageBook>

	/** Inserts a [NovelEntity] */
	suspend fun insertNovel(novelEntity: NovelEntity): HResult<*>
}