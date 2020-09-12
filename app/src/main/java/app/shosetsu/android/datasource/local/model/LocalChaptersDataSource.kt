package app.shosetsu.android.datasource.local.model

import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.errorResult
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.datasource.local.base.ILocalChaptersDataSource
import app.shosetsu.android.domain.model.local.ChapterEntity
import app.shosetsu.android.domain.model.local.NovelEntity
import app.shosetsu.android.domain.model.local.ReaderChapterEntity
import app.shosetsu.android.providers.database.dao.ChaptersDao
import app.shosetsu.lib.Novel

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 */


/**
 * Shosetsu
 * 10 / May / 2020
 *
 * @author github.com/doomsdayrs
 */
class LocalChaptersDataSource(
		private val chaptersDao: ChaptersDao,
) : ILocalChaptersDataSource {

	override suspend fun loadChapters(
			novelID: Int,
	): LiveData<HResult<List<ChapterEntity>>> = liveData {
		try {
			emitSource(chaptersDao.loadLiveChapters(novelID).map { successResult(it) })
		} catch (e: SQLiteException) {
			emit(errorResult(e))
		}
	}

	override suspend fun loadChapter(chapterID: Int): HResult<ChapterEntity> = try {
		successResult(chaptersDao.loadChapter(chapterID))
	} catch (e: SQLiteException) {
		errorResult(e)
	}

	override suspend fun loadReaderChapters(
			novelID: Int,
	): LiveData<HResult<List<ReaderChapterEntity>>> = liveData {
		try {
			emitSource(chaptersDao.loadLiveReaderChapters(novelID).map { successResult(it) })
		} catch (e: SQLiteException) {
			emit(errorResult(e))
		}
	}

	override suspend fun handleChapters(
			novelEntity: NovelEntity,
			list: List<Novel.Chapter>,
	): HResult<*> =
			try {
				successResult(chaptersDao.handleChapters(novelEntity, list))
			} catch (e: SQLiteException) {
				errorResult(e)
			}


	override suspend fun handleChapterReturn(
			novelEntity: NovelEntity,
			list: List<Novel.Chapter>,
	): HResult<List<ChapterEntity>> = try {
		chaptersDao.handleChaptersReturnNew(novelEntity, list)
	} catch (e: SQLiteException) {
		errorResult(e)
	}

	override suspend fun updateChapter(chapterEntity: ChapterEntity): HResult<*> = try {
		successResult(chaptersDao.suspendedUpdate(chapterEntity))
	} catch (e: SQLiteException) {
		errorResult(e)
	}


	override suspend fun updateReaderChapter(readerChapterEntity: ReaderChapterEntity): HResult<*> =
			try {
				successResult(chaptersDao.updateReaderChapter(readerChapterEntity))
			} catch (e: SQLiteException) {
				errorResult(e)
			}

}