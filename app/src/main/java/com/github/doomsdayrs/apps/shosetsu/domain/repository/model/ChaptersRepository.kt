package com.github.doomsdayrs.apps.shosetsu.domain.repository.model

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
 */
class ChaptersRepository(
		val memorySource: ICacheChaptersDataSource,
		val localSource: ILocalChaptersDataSource,
		val remoteSource: IRemoteChaptersDataSource
) : IChaptersRepository {

	override suspend fun loadChapterPassage(chapterEntity: ChapterEntity): HResult<String> =
			memorySource.loadChapterFromCache(chapterEntity.id).takeIf { it is HResult.Success }
					?: localSource.loadChapterPassageFromStorage(chapterEntity)
							.takeIf { it is HResult.Success }
					?: remoteSource.loadChapterPassageFromOnline()

	override suspend fun saveChapterPassageToMemory(chapterID: Int, savePath: String) =
			memorySource.saveChapterInCache(chapterID, savePath)

	override fun loadChapterUnreadCount(novelID: Int): HResult<Int> {
		TODO("Not yet implemented")
	}

	override fun addSavePath(chapterID: Int, savePath: String) {
		TODO("Not yet implemented")
	}

}