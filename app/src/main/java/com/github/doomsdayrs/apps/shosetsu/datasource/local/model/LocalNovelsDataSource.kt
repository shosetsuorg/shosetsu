package com.github.doomsdayrs.apps.shosetsu.datasource.local.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.datasource.local.base.ILocalNovelsDataSource
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.IDTitleImage
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.NovelsDao

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
		val novelsDao: NovelsDao
) : ILocalNovelsDataSource {
	override suspend fun loadBookmarkedNovels(): LiveData<HResult<List<NovelEntity>>> =
			novelsDao.loadBookmarkedNovels().map { successResult(it) }

	override suspend fun loadBookmarkedNovelsCard(): LiveData<HResult<List<IDTitleImage>>> =
			novelsDao.loadIDImageTitle().map { successResult(it) }

	override suspend fun loadNovel(novelID: Int): HResult<NovelEntity> =
			successResult(novelsDao.loadNovel(novelID))

	override suspend fun updateNovel(novelEntity: NovelEntity) =
			novelsDao.suspendedUpdate(novelEntity)

	override suspend fun bookmarkNovel(novelID: Int) =
			novelsDao.bookmarkNovel(novelID)

}