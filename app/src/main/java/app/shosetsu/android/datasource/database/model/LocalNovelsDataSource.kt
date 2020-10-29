package app.shosetsu.android.datasource.database.model

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.errorResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.datasource.database.base.ILocalNovelsDataSource
import app.shosetsu.android.domain.model.local.BookmarkedNovelEntity
import app.shosetsu.android.domain.model.local.IDTitleImageBook
import app.shosetsu.android.domain.model.local.NovelEntity
import app.shosetsu.android.providers.database.dao.NovelsDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest

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
			emitAll(novelsDao.loadListBookmarkedNovels().mapLatest { successResult(it) })
		} catch (e: SQLiteException) {
			emit(errorResult(e))
		} catch (e: NullPointerException) {
			emit(errorResult(e))
		}
	}

	override suspend fun loadBookmarkedNovels(): HResult<List<NovelEntity>> = try {
		successResult(novelsDao.loadBookmarkedNovels())
	} catch (e: SQLiteException) {
		errorResult(e)
	} catch (e: NullPointerException) {
		errorResult(e)
	}

	override suspend fun loadLiveBookmarkedNovelsAndCount(
	): Flow<HResult<List<BookmarkedNovelEntity>>> = flow {
		try {
			emitAll(novelsDao.loadBookmarkedNovelsCount().mapLatest { successResult(it) })
		} catch (e: SQLiteException) {
			emit(errorResult(e))
		} catch (e: NullPointerException) {
			emit(errorResult(e))
		}
	}

	override suspend fun loadNovel(novelID: Int): HResult<NovelEntity> = try {
		successResult(novelsDao.loadNovel(novelID))
	} catch (e: SQLiteException) {
		errorResult(e)
	} catch (e: NullPointerException) {
		errorResult(e)
	}

	override suspend fun loadNovelLive(novelID: Int): Flow<HResult<NovelEntity>> = flow {
		try {
			emitAll(novelsDao.loadNovelLive(novelID).mapLatest { successResult(it) })
		} catch (e: SQLiteException) {
			emit(errorResult(e))
		} catch (e: NullPointerException) {
			emit(errorResult(e))
		}
	}

	override suspend fun updateNovel(novelEntity: NovelEntity): HResult<*> = try {
		successResult(novelsDao.suspendedUpdate(novelEntity))
	} catch (e: SQLiteException) {
		errorResult(e)
	} catch (e: NullPointerException) {
		errorResult(e)
	}

	override suspend fun updateBookmarkedNovels(
			list: List<BookmarkedNovelEntity>
	): HResult<*> = try {
		successResult(novelsDao.updateBookmarked(list))
	} catch (e: SQLiteException) {
		errorResult(e)
	} catch (e: NullPointerException) {
		errorResult(e)
	}

	override suspend fun insertNovelReturnCard(
			novelEntity: NovelEntity,
	): HResult<IDTitleImageBook> = try {
		successResult(novelsDao.insertNovelReturnCard(novelEntity))
	} catch (e: SQLiteException) {
		errorResult(e)
	} catch (e: NullPointerException) {
		errorResult(e)
	}

	override suspend fun insertNovel(novelEntity: NovelEntity): HResult<*> = try {
		successResult(novelsDao.insertIgnore(novelEntity))
	} catch (e: SQLiteException) {
		errorResult(e)
	} catch (e: NullPointerException) {
		errorResult(e)
	}
}