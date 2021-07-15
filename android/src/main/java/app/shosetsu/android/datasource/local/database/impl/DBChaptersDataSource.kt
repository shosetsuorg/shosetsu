package app.shosetsu.android.datasource.local.database.impl

import app.shosetsu.android.common.ext.toDB
import app.shosetsu.android.common.ext.toHError
import app.shosetsu.android.providers.database.dao.ChaptersDao
import app.shosetsu.common.datasource.database.base.IDBChaptersDataSource
import app.shosetsu.common.domain.model.local.ChapterEntity
import app.shosetsu.common.domain.model.local.ReaderChapterEntity
import app.shosetsu.common.dto.*
import app.shosetsu.lib.Novel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

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
 */

/**
 * Shosetsu
 * 10 / May / 2020
 *
 * @author github.com/doomsdayrs
 */
class DBChaptersDataSource(
	private val chaptersDao: ChaptersDao,
) : IDBChaptersDataSource {

	@ExperimentalCoroutinesApi
	override suspend fun getChaptersFlow(
		novelID: Int,
	): HListFlow<ChapterEntity> = flow {
		emit(loading())
		try {
			emitAll(chaptersDao.getChaptersFlow(novelID).mapLatestListTo().mapLatestToSuccess())
		} catch (e: Exception) {
			emit(e.toHError())
		}
	}

	override suspend fun getChapters(novelID: Int): HList<ChapterEntity> = try {
		successResult(chaptersDao.getChapters(novelID).convertList())
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun getChapter(chapterID: Int): HResult<ChapterEntity> = try {
		successResult(chaptersDao.getChapter(chapterID).convertTo())
	} catch (e: Exception) {
		e.toHError()
	}

	@ExperimentalCoroutinesApi
	override suspend fun getReaderChapters(
		novelID: Int,
	): HListFlow<ReaderChapterEntity> = flow {
		emit(loading())
		try {
			emitAll(chaptersDao.getReaderChaptersFlow(novelID).mapLatestToSuccess())
		} catch (e: Exception) {
			emit(e.toHError())
		}
	}

	override suspend fun handleChapters(
		novelID: Int,
		extensionID: Int,
		list: List<Novel.Chapter>,
	): HResult<*> =
		try {
			successResult(chaptersDao.handleNewData(novelID, extensionID, list))
		} catch (e: Exception) {
			e.toHError()
		}


	override suspend fun handleChapterReturn(
		novelID: Int,
		extensionID: Int,
		list: List<Novel.Chapter>,
	): HList<ChapterEntity> = try {
		chaptersDao.handleNewDataReturn(novelID, extensionID, list).convertList()
			.let { successResult(it) }
	} catch (e: Exception) {
		e.toHError()
	}

	override suspend fun updateChapter(chapterEntity: ChapterEntity): HResult<*> = try {
		successResult(chaptersDao.update(chapterEntity.toDB()))
	} catch (e: Exception) {
		e.toHError()
	}


	override suspend fun updateReaderChapter(readerChapterEntity: ReaderChapterEntity): HResult<*> =
		try {
			successResult(chaptersDao.update(readerChapterEntity))
		} catch (e: Exception) {
			e.toHError()
		}

}