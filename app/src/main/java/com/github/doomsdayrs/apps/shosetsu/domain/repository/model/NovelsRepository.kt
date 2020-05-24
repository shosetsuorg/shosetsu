package com.github.doomsdayrs.apps.shosetsu.domain.repository.model

import androidx.lifecycle.LiveData
import app.shosetsu.lib.Formatter
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.datasource.local.base.ILocalNovelsDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.remote.base.IRemoteNovelDataSource
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.IDTitleImage
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.IDTitleImageBook
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.INovelsRepository

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
		val database: ILocalNovelsDataSource,
		val remoteSource: IRemoteNovelDataSource
) : INovelsRepository {
	override suspend fun suspendedGetLiveBookmarked(): LiveData<HResult<List<IDTitleImage>>> =
			database.loadBookmarkedNovelsCard()

	override suspend fun suspendedGetBookmarkedNovels(): HResult<List<NovelEntity>> {
		TODO("Not yet implemented")
	}

	override fun blockingGetBookmarkedNovels(): HResult<List<NovelEntity>> {
		TODO("Not yet implemented")
	}

	override suspend fun updateNovel(novelEntity: NovelEntity) {
		TODO("Not yet implemented")
	}

	override suspend fun insertNovelReturnCard(novelEntity: NovelEntity): IDTitleImageBook =
			database.insertNovelReturnCard(novelEntity)

	override suspend fun insertNovel(novelEntity: NovelEntity) =
			database.insertNovel(novelEntity)

	override suspend fun unBookmarkNovels(selectedNovels: List<Int>) {
		TODO("Not yet implemented")
	}

	override suspend fun searchBookmarked(string: String): LiveData<HResult<List<IDTitleImage>>> {
		TODO("Not yet implemented")
	}

	override suspend fun loadNovel(novelID: Int): HResult<NovelEntity> =
			database.loadNovel(novelID)

	override suspend fun loadNovelLive(novelID: Int): LiveData<HResult<NovelEntity>> =
			database.loadNovelLive(novelID)

	override suspend fun updateNovelData(novelEntity: NovelEntity, novelInfo: Novel.Info) =
			database.updateNovel(
					novelEntity.copy(
							title = novelInfo.title,
							imageURL = novelInfo.imageURL,
							language = novelInfo.language,
							loaded = true,
							status = novelInfo.status,
							description = novelInfo.description,
							genres = novelInfo.genres,
							tags = novelInfo.tags,
							authors = novelInfo.authors,
							artists = novelInfo.artists
					)
			)

	override suspend fun retrieveNovelInfo(
			formatter: Formatter,
			novelEntity: NovelEntity,
			loadChapters: Boolean
	): HResult<Novel.Info> =
			remoteSource.loadNovel(formatter, novelEntity.url, loadChapters)

	override suspend fun bookmarkNovel(novelID: Int) =
			database.bookmarkNovel(novelID)
}