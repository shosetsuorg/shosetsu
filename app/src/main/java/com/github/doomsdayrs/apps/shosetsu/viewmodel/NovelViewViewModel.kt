package com.github.doomsdayrs.apps.shosetsu.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.ChapterEntity
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.NovelEntity
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.INovelViewViewModel

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
class NovelViewViewModel() : ViewModel(), INovelViewViewModel {
	override val liveData: LiveData<NovelEntity>
		get() = TODO("Not yet implemented")

	override fun subscribeObserver(owner: LifecycleOwner, observer: Observer<NovelEntity>) {
		TODO("Not yet implemented")
	}

	override suspend fun loadData(): NovelEntity {
		TODO("Not yet implemented")
	}

	override val liveData2: LiveData<List<ChapterEntity>>
		get() = TODO("Not yet implemented")

	override fun subscribeObserver2(owner: LifecycleOwner, observer: Observer<List<ChapterEntity>>) {
		TODO("Not yet implemented")
	}

	override fun loadData2(): List<ChapterEntity> {
		TODO("Not yet implemented")
	}

}