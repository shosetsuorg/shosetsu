package com.github.doomsdayrs.apps.shosetsu.domain.repository.impl

import androidx.lifecycle.LiveData
import com.github.doomsdayrs.apps.shosetsu.domain.repository.model.NovelsRepository
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.NovelsDao
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity

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
 * ====================================================================
 */

/**
 * shosetsu
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class NovelsRepositoryImpl(val novelsDao: NovelsDao) : NovelsRepository {
	override suspend fun getBookmarkedNovels(): LiveData<List<NovelEntity>> =
			novelsDao.loadBookmarkedNovels()

	override suspend fun updateNovel(novelEntity: NovelEntity) =
			novelsDao.update(novelEntity)
}