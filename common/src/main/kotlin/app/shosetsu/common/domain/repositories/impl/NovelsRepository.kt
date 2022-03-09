package app.shosetsu.common.domain.repositories.impl

import app.shosetsu.common.GenericSQLiteException
import app.shosetsu.common.datasource.database.base.IDBNovelsDataSource
import app.shosetsu.common.datasource.remote.base.IRemoteCatalogueDataSource
import app.shosetsu.common.datasource.remote.base.IRemoteNovelDataSource
import app.shosetsu.common.domain.model.local.LibraryNovelEntity
import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.common.domain.model.local.StrippedBookmarkedNovelEntity
import app.shosetsu.common.domain.model.local.StrippedNovelEntity
import app.shosetsu.common.domain.repositories.base.INovelsRepository
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Novel
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
		database.loadBookmarkedNovelsFlow()

	@Throws(GenericSQLiteException::class)
	override suspend fun loadBookmarkedNovelEntities(): List<NovelEntity> =
		database.loadBookmarkedNovels()

	@Throws(GenericSQLiteException::class)
	override suspend fun loadNovels(): List<NovelEntity> =
		database.loadNovels()

	@Throws(GenericSQLiteException::class)
	override suspend fun update(novelEntity: NovelEntity): Unit =
		database.update(novelEntity)

	@Throws(GenericSQLiteException::class)
	override suspend fun insertReturnStripped(novelEntity: NovelEntity): StrippedNovelEntity? =
		database.insertReturnStripped(novelEntity)

	@Throws(GenericSQLiteException::class)
	override suspend fun insert(novelEntity: NovelEntity): Long =
		database.insert(novelEntity)


	/**
	 * TODO this operation is resource intensive, create a low level DB object
	 */
	@Throws(GenericSQLiteException::class)
	override suspend fun searchBookmarked(string: String): List<StrippedBookmarkedNovelEntity> =
		loadBookmarkedNovelEntities().let { list ->
			if (list.isEmpty())
				emptyList()
			else
				list.filter { it.title.contains(string, false) }
					.map {
						StrippedBookmarkedNovelEntity(it.id!!, it.title, it.imageURL)
					}
		}


	@Throws(GenericSQLiteException::class)
	override suspend fun getNovel(novelID: Int): NovelEntity? =
		database.getNovel(novelID)

	override suspend fun getNovelFlow(novelID: Int): Flow<NovelEntity?> =
		database.getNovelFlow(novelID)

	@Throws(GenericSQLiteException::class)
	override suspend fun updateNovelData(
		novelEntity: NovelEntity,
		novelInfo: Novel.Info
	): Unit =
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

	@Throws(GenericSQLiteException::class)
	override suspend fun updateLibraryNovelEntity(list: List<LibraryNovelEntity>): Unit =
		database.update(list)

	override suspend fun retrieveNovelInfo(
		extension: IExtension,
		novelEntity: NovelEntity,
		loadChapters: Boolean,
	): Novel.Info =
		remoteSource.loadNovel(extension, novelEntity.url, loadChapters)
			.let { info ->
				info.copy(
					chapters = info.chapters.distinctBy { it.link }
				)
			}

	@Throws(GenericSQLiteException::class)
	override suspend fun clearUnBookmarkedNovels(): Unit =
		database.clearUnBookmarkedNovels()

	override suspend fun getCatalogueSearch(
		ext: IExtension,
		query: String,
		data: Map<Int, Any>
	): List<Novel.Listing> = remoteCatalogueDataSource.search(ext, query, data)

	override suspend fun getCatalogueData(
		ext: IExtension,
		listing: Int,
		data: Map<Int, Any>,
	): List<Novel.Listing> = remoteCatalogueDataSource.loadListing(ext, listing, data)

}