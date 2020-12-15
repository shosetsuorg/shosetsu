package app.shosetsu.android.domain.repository.impl

import app.shosetsu.android.datasource.database.base.ILocalNovelsDataSource
import app.shosetsu.android.domain.model.local.IDTitleImage
import app.shosetsu.android.domain.model.local.IDTitleImageBook
import app.shosetsu.android.domain.repository.base.INovelsRepository
import app.shosetsu.common.datasource.remote.base.IRemoteNovelDataSource
import app.shosetsu.common.domain.model.local.BookmarkedNovelEntity
import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.emptyResult
import app.shosetsu.common.dto.successResult
import app.shosetsu.common.dto.transform
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
	private val database: ILocalNovelsDataSource,
	private val remoteSource: IRemoteNovelDataSource,
) : INovelsRepository {
	override fun getLiveBookmarked(): Flow<HResult<List<BookmarkedNovelEntity>>> =
		database.loadLiveBookmarkedNovelsAndCount()

	override suspend fun getBookmarkedNovels(): HResult<List<NovelEntity>> =
		database.loadBookmarkedNovels()

	override suspend fun updateNovel(novelEntity: NovelEntity): HResult<*> =
		database.updateNovel(novelEntity)


	override suspend fun insertNovelReturnCard(novelEntity: NovelEntity): HResult<IDTitleImageBook> =
		database.insertNovelReturnCard(novelEntity)

	override suspend fun insertNovel(novelEntity: NovelEntity): HResult<*> =
		database.insertNovel(novelEntity)


	override suspend fun searchBookmarked(string: String): HResult<List<IDTitleImage>> =
		getBookmarkedNovels().let { result ->
			result.transform { list: List<NovelEntity> ->
				if (list.isEmpty()) emptyResult()
				successResult(list.filter { it.title.contains(string, false) }
					.map { (id, _, _, _, _, _, t, imageURL, _, _, _, _, _, _, _) ->
						IDTitleImage(id!!, t, imageURL)
					})
			}
		}


	override suspend fun loadNovel(novelID: Int): HResult<NovelEntity> =
		database.loadNovel(novelID)

	override suspend fun loadNovelLive(novelID: Int): Flow<HResult<NovelEntity>> =
		database.loadNovelLive(novelID)

	override suspend fun updateNovelData(
		novelEntity: NovelEntity,
		novelInfo: Novel.Info
	): HResult<*> =
		database.updateNovel(
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

	override suspend fun updateBookmarkedNovelData(list: List<BookmarkedNovelEntity>): HResult<*> =
		database.updateBookmarkedNovels(list)

	override suspend fun retrieveNovelInfo(
		formatter: IExtension,
		novelEntity: NovelEntity,
		loadChapters: Boolean,
	): HResult<Novel.Info> =
		remoteSource.loadNovel(formatter, novelEntity.url, loadChapters)
}