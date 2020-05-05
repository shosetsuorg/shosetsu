package com.github.doomsdayrs.apps.shosetsu.domain.repository.base
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
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.base.SubscribeLiveData


/**
 * shosetsu
 * 25 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
interface INovelsRepository :
		SubscribeLiveData<List<NovelEntity>> {
	suspend fun suspendedGetBookmarkedNovels(): HResult<List<NovelEntity>>
	fun blockingGetBookmarkedNovels(): HResult<List<NovelEntity>>
	suspend fun updateNovel(novelEntity: NovelEntity)
	suspend fun unBookmarkNovels(selectedNovels: List<Int>)
}