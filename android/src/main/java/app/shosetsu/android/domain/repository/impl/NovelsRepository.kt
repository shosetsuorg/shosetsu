package app.shosetsu.android.domain.repository.impl

import android.database.sqlite.SQLiteException
import androidx.paging.PagingSource
import app.shosetsu.android.common.ext.onIO
import app.shosetsu.android.datasource.local.database.base.IDBNovelsDataSource
import app.shosetsu.android.datasource.remote.base.IRemoteCatalogueDataSource
import app.shosetsu.android.datasource.remote.base.IRemoteNovelDataSource
import app.shosetsu.android.domain.model.local.LibraryNovelEntity
import app.shosetsu.android.domain.model.local.NovelEntity
import app.shosetsu.android.domain.model.local.StrippedBookmarkedNovelEntity
import app.shosetsu.android.domain.model.local.StrippedNovelEntity
import app.shosetsu.android.domain.repository.base.INovelsRepository
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Novel
import app.shosetsu.lib.exceptions.HTTPException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.luaj.vm2.LuaError
import java.io.IOException
import javax.net.ssl.SSLException

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
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class NovelsRepository(
	private val database: IDBNovelsDataSource,
	private val remoteSource: IRemoteNovelDataSource,
	private val remoteCatalogueDataSource: IRemoteCatalogueDataSource,
) : INovelsRepository {
	override fun loadLibraryNovelEntities(): Flow<List<LibraryNovelEntity>> =
		database.loadBookmarkedNovelsFlow().distinctUntilChanged().onIO()

	@Throws(SQLiteException::class)
	override suspend fun loadBookmarkedNovelEntities(): List<NovelEntity> =
		onIO { database.loadBookmarkedNovels() }

	@Throws(SQLiteException::class)
	override suspend fun loadNovels(): List<NovelEntity> =
		onIO { database.loadNovels() }

	@Throws(SQLiteException::class)
	override suspend fun update(novelEntity: NovelEntity): Unit =
		onIO { database.update(novelEntity) }

	@Throws(SQLiteException::class)
	override suspend fun insertReturnStripped(novelEntity: NovelEntity): StrippedNovelEntity? =
		onIO { database.insertReturnStripped(novelEntity) }

	@Throws(SQLiteException::class)
	override suspend fun insert(novelEntity: NovelEntity): Long =
		onIO { database.insert(novelEntity) }


	/**
	 * TODO this operation is resource intensive, create a low level DB object
	 */
	@Throws(SQLiteException::class)
	override fun searchBookmarked(string: String): PagingSource<Int, StrippedBookmarkedNovelEntity> =
		database.searchBookmarked(string)


	@Throws(SQLiteException::class)
	override suspend fun getNovel(novelID: Int): NovelEntity? =
		onIO { database.getNovel(novelID) }

	override suspend fun getNovelFlow(novelID: Int): Flow<NovelEntity?> =
		database.getNovelFlow(novelID).onIO()

	@Throws(SQLiteException::class)
	override suspend fun updateNovelData(
		novelEntity: NovelEntity,
		novelInfo: Novel.Info
	) {
		onIO {
			database.update(
				novelEntity.copy(
					title = novelInfo.title,
					imageURL = novelInfo.imageURL,
					language = novelInfo.language,
					loaded = true,
					status = novelInfo.status,
					description = novelInfo.description,
					genres = novelInfo.genres.toList(),
					tags = novelInfo.tags.toList(),
					authors = novelInfo.authors.toList(),
					artists = novelInfo.artists.toList()
				)
			)
		}
	}

	@Throws(SQLiteException::class)
	override suspend fun updateLibraryNovelEntity(list: List<LibraryNovelEntity>) {
		onIO { database.update(list) }
	}

	@Throws(HTTPException::class, IOException::class, LuaError::class)
	override suspend fun retrieveNovelInfo(
		extension: IExtension,
		novelEntity: NovelEntity,
		loadChapters: Boolean,
	): Novel.Info =
		onIO {
			remoteSource.loadNovel(extension, novelEntity.url, loadChapters)
				.let { info ->
					info.copy(
						chapters = info.chapters.distinctBy { it.link }.toTypedArray()
					)
				}
		}

	@Throws(SQLiteException::class)
	override suspend fun clearUnBookmarkedNovels() {
		onIO { database.clearUnBookmarkedNovels() }
	}

	@Throws(LuaError::class)
	override suspend fun getCatalogueSearch(
		ext: IExtension,
		query: String,
		data: Map<Int, Any>
	): List<Novel.Listing> = onIO { remoteCatalogueDataSource.search(ext, query, data) }

	@Throws(SSLException::class, LuaError::class)
	override suspend fun getCatalogueData(
		ext: IExtension,
		listing: Int,
		data: Map<Int, Any>,
	): List<Novel.Listing> = onIO { remoteCatalogueDataSource.loadListing(ext, listing, data) }

}