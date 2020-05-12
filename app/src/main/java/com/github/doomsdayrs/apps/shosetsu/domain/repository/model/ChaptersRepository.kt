package com.github.doomsdayrs.apps.shosetsu.domain.repository.model

import androidx.lifecycle.LiveData
import app.shosetsu.lib.Formatter
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.datasource.cache.base.ICacheChaptersDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.local.base.ILocalChaptersDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.remote.base.IRemoteChaptersDataSource
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ChapterEntity
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.IChaptersRepository

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
 * 02 / 05 / 2020
 * @param memorySource Source from memory
 * @param localSource Source from storage
 * @param remoteSource Source from online
 */
class ChaptersRepository(
		private val memorySource: ICacheChaptersDataSource,
		private val localSource: ILocalChaptersDataSource,
		private val remoteSource: IRemoteChaptersDataSource
) : IChaptersRepository {
	override suspend fun loadChapterPassage(
			formatter: Formatter,
			chapterEntity: ChapterEntity
	): HResult<String> =
			memorySource.loadChapterFromCache(chapterEntity.id).takeIf { it is HResult.Success }
					?: localSource.loadChapterPassageFromStorage(chapterEntity)
							.takeIf { it is HResult.Success }
					?: remoteSource.loadChapterPassageFromOnline(
							formatter,
							chapterEntity.url
					)

	override suspend fun saveChapterPassageToMemory(
			chapterEntity: ChapterEntity,
			passage: String
	): Unit = memorySource.saveChapterInCache(chapterEntity.id, passage)

	override suspend fun saveChapterPassageToStorage(
			chapterEntity: ChapterEntity,
			passage: String
	): Unit = saveChapterPassageToMemory(chapterEntity, passage).also {
		localSource.saveChapterPassageToStorage(chapterEntity, passage)
	}

	override suspend fun loadChapterUnreadCount(novelID: Int): LiveData<HResult<Int>> =
			localSource.loadUnreadChapterCount(novelID)
}