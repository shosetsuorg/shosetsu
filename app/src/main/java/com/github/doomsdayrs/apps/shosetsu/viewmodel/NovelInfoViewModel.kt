package com.github.doomsdayrs.apps.shosetsu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.BookMarkNovelIDUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.GetFormatterNameUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.GetNovelUIUseCase
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.NovelUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.INovelInfoViewModel

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
class NovelInfoViewModel(
		private val getFormatterNameUseCase: GetFormatterNameUseCase,
		private val bookMarkNovelIDUseCase: BookMarkNovelIDUseCase,
		private val loadNovelUIUseCase: GetNovelUIUseCase
) : INovelInfoViewModel() {
	override val liveData: LiveData<HResult<NovelUI>> = liveData {
		novelID.switchMap { loadNovelUIUseCase(it) }
	}

	override var novelID: MutableLiveData<Int> = MutableLiveData()

	override val formatterName: LiveData<HResult<String>> = liveData {
		novelID.switchMap { getFormatterNameUseCase(it) }
	}

	override fun setNovelID(novelID: Int) {
		if (liveData.value !is HResult.Success) {
			this.novelID.postValue(novelID)
		}
	}

	override fun toggleBookmark() {
		novelID.value?.let { bookMarkNovelIDUseCase(it) }
	}

	override fun refresh() {
		TODO("Not yet implemented")
	}
}