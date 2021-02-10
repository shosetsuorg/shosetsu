package app.shosetsu.android.datasource.local.database.impl

import android.database.sqlite.SQLiteException
import app.shosetsu.android.common.ext.toDB
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.providers.database.dao.NovelsDao
import app.shosetsu.common.datasource.database.base.IDBNovelsDataSource
import app.shosetsu.common.domain.model.local.LibraryNovelEntity
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
	override suspend fun loadBookmarkedNovels(): HResult<List<NovelEntity>> = try {
		successResult(novelsDao.loadBookmarkedNovels().convertList())
	} catch (e: Exception) {
		e.toHError()
	}

	@ExperimentalCoroutinesApi
	override fun loadBookmarkedNovelsFlow(
	): Flow<HResult<List<LibraryNovelEntity>>> = flow {
		emit(loading())
		try {
			emitAll(novelsDao.loadBookmarkedNovelsFlow().mapLatestToSuccess())
		} catch (e: Exception) {
			emit(e.toHError())
		}
	}

	override suspend fun getNovel(novelID: Int): HResult<NovelEntity> = try {
		successResult(novelsDao.getNovel(novelID).convertTo())
	} catch (e: Exception) {
		e.toHError()
	}

	@ExperimentalCoroutinesApi
	override suspend fun getNovelFlow(novelID: Int): Flow<HResult<NovelEntity>> = flow {
		try {
			emitAll(novelsDao.getNovelFlow(novelID).mapLatestTo().mapLatestToSuccess())
		} catch (e: Exception) {
			emit(e.toHError())
		}
	}

	override suspend fun update(novelEntity: NovelEntity): HResult<*> = try {
		successResult(novelsDao.update(novelEntity.toDB()))
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun update(
		list: List<LibraryNovelEntity>
	): HResult<*> = try {
		successResult(novelsDao.update(list))
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun insertReturnStripped(
		novelEntity: NovelEntity,
	): HResult<StrippedNovelEntity> = try {
		successResult(novelsDao.insertReturnStripped(novelEntity.toDB()).convertTo())
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun insert(novelEntity: NovelEntity): HResult<*> = try {
		successResult(novelsDao.insertAbort(novelEntity.toDB()))
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun clearUnBookmarkedNovels(): HResult<*> = try {
		successResult(novelsDao.clearUnBookmarkedNovels())
	} catch (e: SQLiteException) {
		e.toHError()
	}

	override fun loadNovels(): HResult<List<NovelEntity>> = try {
		successResult(novelsDao.loadNovels().convertList())
	} catch (e: SQLiteException) {
		e.toHError()
	}
}