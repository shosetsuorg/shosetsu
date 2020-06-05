package com.github.doomsdayrs.apps.shosetsu.datasource.local.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.datasource.local.base.ILocalChaptersDataSource
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ChapterEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.ChaptersDao

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
		val chaptersDao: ChaptersDao
) : ILocalChaptersDataSource {

	override fun loadChaptersByID(novelID: Int): LiveData<HResult<List<ChapterEntity>>> =
			chaptersDao.loadLiveChapters(novelID).map { successResult(it) }

	override fun loadUnreadChapterCount(novelID: Int): HResult<Int> =
			successResult(chaptersDao.loadChapterUnreadCount(novelID))

	override suspend fun handleChapters(novelEntity: NovelEntity, list: List<Novel.Chapter>) =
			chaptersDao.handleChapters(novelEntity, list)
}