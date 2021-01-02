package app.shosetsu.android.datasource.local.database.impl

import app.shosetsu.android.common.ext.toDB
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.common.datasource.database.base.IDBNovelsDataSource
import app.shosetsu.android.providers.database.dao.NovelsDao
import app.shosetsu.common.domain.model.local.BookmarkedNovelEntity
import app.shosetsu.common.domain.model.local.NovelEntity
import app.shosetsu.common.domain.model.local.StrippedNovelEntity
import app.shosetsu.common.dto.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class DBNovelsDataSource(
	private val novelsDao: NovelsDao,
) : IDBNovelsDataSource {
	@ExperimentalCoroutinesApi
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

	@ExperimentalCoroutinesApi
	override fun loadLiveBookmarkedNovelsAndCount(
	): Flow<HResult<List<BookmarkedNovelEntity>>> = flow {
		emit(loading())
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

	@ExperimentalCoroutinesApi
	override suspend fun loadNovelLive(novelID: Int): Flow<HResult<NovelEntity>> = flow {
		try {
			emitAll(novelsDao.loadNovelLive(novelID).mapLatestTo().mapLatestToSuccess())
		} catch (e: Exception) {
			emit(e.toHError())
		}
	}

	override suspend fun updateNovel(novelEntity: NovelEntity): HResult<*> = try {
		successResult(novelsDao.update(novelEntity.toDB()))
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
	): HResult<StrippedNovelEntity> = try {
		successResult(novelsDao.insertNovelReturnCard(novelEntity.toDB()).convertTo())
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun insertNovel(novelEntity: NovelEntity): HResult<*> = try {
		successResult(novelsDao.insertIgnore(novelEntity.toDB()))
	} catch (e: Exception) {
		e.toHError()
	}
}