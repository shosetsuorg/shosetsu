package app.shosetsu.common.datasource.database.base

import app.shosetsu.common.domain.model.local.LibraryNovelEntity
import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.common.domain.model.local.StrippedNovelEntity
import app.shosetsu.common.dto.HResult
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
interface IDBNovelsDataSource {
	/** Loads a [List] of [NovelEntity]s present */
	fun loadNovels(): HResult<List<NovelEntity>>

	/** Load a [List] of [NovelEntity]s that are bookmarked */
	suspend fun loadBookmarkedNovels(): HResult<List<NovelEntity>>

	/** Loads a [List] okf [LibraryNovelEntity] */
	fun loadBookmarkedNovelsFlow(): Flow<HResult<List<LibraryNovelEntity>>>

	/** Loads a [NovelEntity] by its [novelID] */
	suspend fun getNovel(novelID: Int): HResult<NovelEntity>

	/** Loads a [Flow] of a [NovelEntity] by its [novelID] */
	suspend fun getNovelFlow(novelID: Int): Flow<HResult<NovelEntity>>

	/** Updates a [NovelEntity] */
	suspend fun update(novelEntity: NovelEntity): HResult<*>

	/** Updates a list of [LibraryNovelEntity] */
	suspend fun update(list: List<LibraryNovelEntity>): HResult<*>

	/** Inserts a [NovelEntity] then returns its [StrippedNovelEntity] */
	suspend fun insertReturnStripped(novelEntity: NovelEntity): HResult<StrippedNovelEntity>

	/** Inserts a [NovelEntity] */
	suspend fun insert(novelEntity: NovelEntity): HResult<*>

	/**
	 * Clears out novels that have not been bookmarked
	 */
	suspend fun clearUnBookmarkedNovels(): HResult<*>
}