package app.shosetsu.android.domain.repository.base
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
import android.database.sqlite.SQLiteException
import androidx.paging.PagingSource
import app.shosetsu.android.domain.model.local.LibraryNovelEntity
import app.shosetsu.android.domain.model.local.NovelEntity
import app.shosetsu.android.domain.model.local.StrippedBookmarkedNovelEntity
import app.shosetsu.android.domain.model.local.StrippedNovelEntity
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Novel
import kotlinx.coroutines.flow.Flow
import org.luaj.vm2.LuaError
import javax.net.ssl.SSLException


/**
 * shosetsu
 * 25 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
interface INovelsRepository {
	/**
	 * Loads all [NovelEntity]s that are bookmarked in a flow
	 */
	@Throws(SQLiteException::class)
	fun loadLibraryNovelEntities(): Flow<List<LibraryNovelEntity>>

	/**
	 * Loads all [NovelEntity]s that are bookmarked
	 */
	@Throws(SQLiteException::class)
	suspend fun loadBookmarkedNovelEntities(): List<NovelEntity>

	/**
	 * Loads all [NovelEntity]s that are in the repository
	 */
	suspend fun loadNovels(): List<NovelEntity>

	/**
	 * Searches the bookmarked novels and returns a live data of them
	 */
	@Throws(SQLiteException::class)
	fun searchBookmarked(string: String): PagingSource<Int, StrippedBookmarkedNovelEntity>

	/**
	 * Loads the [NovelEntity] by its [novelID]
	 */
	@Throws(SQLiteException::class)
	suspend fun getNovel(novelID: Int): NovelEntity?

	/**
	 * Loads live data of the [NovelEntity] by its [novelID]
	 */
	suspend fun getNovelFlow(novelID: Int): Flow<NovelEntity?>

	/**
	 * Inserts the [novelEntity] and returns a UI version of it
	 */
	@Throws(SQLiteException::class)
	suspend fun insertReturnStripped(novelEntity: NovelEntity): StrippedNovelEntity?

	/**
	 * Inserts the [novelEntity]
	 */
	@Throws(SQLiteException::class)
	suspend fun insert(novelEntity: NovelEntity): Long

	/**
	 * Updates the [novelEntity]
	 */
	@Throws(SQLiteException::class)
	suspend fun update(novelEntity: NovelEntity)

	/**
	 * Updates a novel entity with new data
	 */
	@Throws(SQLiteException::class)
	suspend fun updateNovelData(novelEntity: NovelEntity, novelInfo: Novel.Info)

	/**
	 * Updates a list of bookmarked novels
	 */
	@Throws(SQLiteException::class)
	suspend fun updateLibraryNovelEntity(list: List<LibraryNovelEntity>)

	/**
	 * Retrieves NovelInfo from it's source
	 */
	@Throws(LuaError::class)
	suspend fun retrieveNovelInfo(
		extension: IExtension,
		novelEntity: NovelEntity,
		loadChapters: Boolean,
	): Novel.Info


	/**
	 *  Removes all novels that are not bookmarked
	 *  This should cascade and delete all their chapters as well
	 */
	@Throws(SQLiteException::class)
	suspend fun clearUnBookmarkedNovels()


	/**
	 * Queries the [IExtension] for a search result
	 */
	@Throws(LuaError::class)
	suspend fun getCatalogueSearch(
		ext: IExtension,
		query: String,
		data: Map<Int, Any>
	): List<Novel.Listing>

	/**
	 * Loads catalogue data of an [IExtension]
	 */
	@Throws(SSLException::class, LuaError::class)
	suspend fun getCatalogueData(
		ext: IExtension,
		listing: Int,
		data: Map<Int, Any>,
	): List<Novel.Listing>
}