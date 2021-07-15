package app.shosetsu.common.domain.repositories.base
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
import app.shosetsu.common.domain.model.local.LibraryNovelEntity
import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.common.domain.model.local.StrippedBookmarkedNovelEntity
import app.shosetsu.common.domain.model.local.StrippedNovelEntity
import app.shosetsu.common.dto.HResult
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Novel
import kotlinx.coroutines.flow.Flow


/**
 * shosetsu
 * 25 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
interface INovelsRepository {
	/**
	 * Loads all [NovelEntity]s that are bookmarked in a flow
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] Initial Value
	 */
	fun loadLibraryNovelEntities(): Flow<HResult<List<LibraryNovelEntity>>>

	/**
	 * Loads all [NovelEntity]s that are bookmarked
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] never
	 */
	suspend fun loadBookmarkedNovelEntities(): HResult<List<NovelEntity>>

	/**
	 * Loads all [NovelEntity]s that are in the repository
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] never
	 */
	suspend fun loadNovels(): HResult<List<NovelEntity>>

	/**
	 * Searches the bookmarked novels and returns a live data of them
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] never
	 */
	suspend fun searchBookmarked(string: String): HResult<List<StrippedBookmarkedNovelEntity>>

	/**
	 * Loads the [NovelEntity] by its [novelID]
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] never
	 */
	suspend fun getNovel(novelID: Int): HResult<NovelEntity>

	/**
	 * Loads live data of the [NovelEntity] by its [novelID]
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] Initial value
	 */
	suspend fun getNovelFlow(novelID: Int): Flow<HResult<NovelEntity>>

	/**
	 * Inserts the [novelEntity] and returns a UI version of it
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] never
	 */
	suspend fun insertReturnStripped(novelEntity: NovelEntity): HResult<StrippedNovelEntity>

	/**
	 * Inserts the [novelEntity]
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] never
	 *
	 * [HResult.Loading] never
	 */
	suspend fun insert(novelEntity: NovelEntity): HResult<*>

	/**
	 * Updates the [novelEntity]
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] never
	 *
	 * [HResult.Loading] never
	 */
	suspend fun update(novelEntity: NovelEntity): HResult<*>

	/**
	 * Updates a novel entity with new data
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] never
	 *
	 * [HResult.Loading] never
	 */
	suspend fun updateNovelData(novelEntity: NovelEntity, novelInfo: Novel.Info): HResult<*>

	/**
	 * Updates a list of bookmarked novels
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] never
	 *
	 * [HResult.Loading] never
	 */
	suspend fun updateLibraryNovelEntity(list: List<LibraryNovelEntity>): HResult<*>

	/**
	 * Retrieves NovelInfo from it's source
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] never
	 */
	suspend fun retrieveNovelInfo(
		extension: IExtension,
		novelEntity: NovelEntity,
		loadChapters: Boolean,
	): HResult<Novel.Info>


	/**
	 *  Removes all novels that are not bookmarked
	 *  This should cascade and delete all their chapters as well
	 */
	suspend fun clearUnBookmarkedNovels(): HResult<*>


	/**
	 * Queries the [IExtension] for a search result
	 *
	 * @return
	 * [HResult.Success] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Error] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Empty] TODO RETURN DESCRIPTION
	 *
	 * [HResult.Loading] never
	 */
	suspend fun getCatalogueSearch(
		ext: IExtension,
		query: String,
		data: Map<Int, Any>
	): HResult<List<Novel.Listing>>

	/**
	 * Loads catalogue data of an [IExtension]
	 *
	 * @return
	 * [HResult.Success] Data successfully loaded
	 *
	 * [HResult.Error] Error loading data
	 *
	 * [HResult.Empty] ?
	 *
	 * [HResult.Loading] never
	 */
	suspend fun getCatalogueData(
		ext: IExtension,
		listing: Int,
		data: Map<Int, Any>,
	): HResult<List<Novel.Listing>>
}