package com.github.doomsdayrs.apps.shosetsu.domain.repository.model

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.datasource.local.base.ILocalNovelsDataSource
import com.github.doomsdayrs.apps.shosetsu.datasource.remote.base.IRemoteNovelDataSource
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.IDNameURL
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.INovelsRepository

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
 * 24 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class NovelsRepository(
		val iLocalNovelsDataSource: ILocalNovelsDataSource,
		val iRemoteNovelDataSource: IRemoteNovelDataSource
) : INovelsRepository {
	override suspend fun suspendedGetLiveBookmarked(): LiveData<HResult<List<IDNameURL>>> {
		TODO("Not yet implemented")
	}

	override suspend fun suspendedGetBookmarkedNovels(): HResult<List<NovelEntity>> {
		TODO("Not yet implemented")
	}

	override fun blockingGetBookmarkedNovels(): HResult<List<NovelEntity>> {
		TODO("Not yet implemented")
	}

	override suspend fun updateNovel(novelEntity: NovelEntity) {
		TODO("Not yet implemented")
	}

	override suspend fun unBookmarkNovels(selectedNovels: List<Int>) {
		TODO("Not yet implemented")
	}

	override val daoLiveData: LiveData<List<NovelEntity>>
		get() = TODO("Not yet implemented")

	override fun subscribeDao(owner: LifecycleOwner, observer: Observer<List<NovelEntity>>) {
		TODO("Not yet implemented")
	}

	override fun loadDataSnap(): List<NovelEntity> {
		TODO("Not yet implemented")
	}

}