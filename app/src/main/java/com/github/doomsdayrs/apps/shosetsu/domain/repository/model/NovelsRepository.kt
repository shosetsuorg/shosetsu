package com.github.doomsdayrs.apps.shosetsu.domain.repository.model

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity
import com.github.doomsdayrs.apps.shosetsu.domain.repository.base.INovelsRepository
import com.github.doomsdayrs.apps.shosetsu.providers.database.dao.NovelsDao
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.NovelUI

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
class NovelsRepository(val novelsDao: NovelsDao) : INovelsRepository {
	override val daoLiveData: LiveData<List<NovelEntity>> by lazy { novelsDao.loadNovels() }

	// updates

	override suspend fun updateNovel(novelEntity: NovelEntity) =
			novelsDao.update(novelEntity)

	override suspend fun unBookmarkNovels(selectedNovels: List<Int>) =
			novelsDao.unBookmarkNovels(selectedNovels, loadDataSnap())


	// Subscribe

	override fun subscribeRepository(
			owner: LifecycleOwner,
			observer: Observer<List<NovelUI>>
	) =
			subscribeDao(owner, Observer { observer.onChanged(it.map { l -> l.convertTo() }) })

	override fun subscribeDao(
			owner: LifecycleOwner,
			observer: Observer<List<NovelEntity>>
	) =
			daoLiveData.observe(owner, observer)

	// get data

	override suspend fun getBookmarkedNovels(): List<NovelUI> = loadData().filter { it.bookmarked }

	override fun loadData(): List<NovelUI> = loadDataSnap().map { it.convertTo() }

	override fun loadDataSnap(): List<NovelEntity> = daoLiveData.value ?: arrayListOf()

}