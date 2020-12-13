package app.shosetsu.android.datasource.database.impl

import app.shosetsu.android.common.ext.toDB
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.datasource.database.base.ILocalNovelsDataSource
import app.shosetsu.android.domain.model.local.IDTitleImageBook
import app.shosetsu.android.providers.database.dao.NovelsDao
import app.shosetsu.common.domain.model.local.BookmarkedNovelEntity
import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.common.dto.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

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
 * 12 / 05 / 2020
 */
class LocalNovelsDataSource(
		private val novelsDao: NovelsDao,
) : ILocalNovelsDataSource {
	override suspend fun loadLiveBookmarkedNovels(): Flow<HResult<List<NovelEntity>>> = flow {
		try {
			emitAll(novelsDao.loadListBookmarkedNovels().mapLatestListTo().mapLatestToSuccess())
		} catch (e: Exception) {
			emit(e.toHError())
		}
	}

	override suspend fun loadBookmarkedNovels(): HResult<List<NovelEntity>> = try {
		successResult(novelsDao.loadBookmarkedNovels().convertList())
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun loadLiveBookmarkedNovelsAndCount(
	): Flow<HResult<List<BookmarkedNovelEntity>>> = flow {
		try {
			emitAll(novelsDao.loadBookmarkedNovelsCount().mapLatestToSuccess())
		} catch (e: Exception) {
			emit(e.toHError())
		}
	}

	override suspend fun loadNovel(novelID: Int): HResult<NovelEntity> = try {
		successResult(novelsDao.loadNovel(novelID).convertTo())
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun loadNovelLive(novelID: Int): Flow<HResult<NovelEntity>> = flow {
		try {
			emitAll(novelsDao.loadNovelLive(novelID).mapLatestTo().mapLatestToSuccess())
		} catch (e: Exception) {
			emit(e.toHError())
		}
	}

	override suspend fun updateNovel(novelEntity: NovelEntity): HResult<*> = try {
		successResult(novelsDao.suspendedUpdate(novelEntity.toDB()))
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun updateBookmarkedNovels(
			list: List<BookmarkedNovelEntity>
	): HResult<*> = try {
		successResult(novelsDao.updateBookmarked(list))
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun insertNovelReturnCard(
			novelEntity: NovelEntity,
	): HResult<IDTitleImageBook> = try {
		successResult(novelsDao.insertNovelReturnCard(novelEntity.toDB()))
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun insertNovel(novelEntity: NovelEntity): HResult<*> = try {
		successResult(novelsDao.insertIgnore(novelEntity.toDB()))
	} catch (e: Exception) {
		e.toHError()
	}
}