package com.github.doomsdayrs.apps.shosetsu.viewmodel.model.novel

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.loading
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.IsOnlineUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.LoadNovelUseCase
import com.github.doomsdayrs.apps.shosetsu.viewmodel.abstracted.INovelViewModel
import kotlinx.coroutines.Dispatchers

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
class NovelViewModel(
		private val loadNovelUseCase: LoadNovelUseCase,
		private var isOnlineUseCase: IsOnlineUseCase,
) : INovelViewModel() {
	private var novelIDValue: Int = -1

	override fun setNovelID(novelID: Int) {
		if (novelIDValue == -1) novelIDValue = novelID
	}

	override fun refresh(): LiveData<HResult<Any>> =
			liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
				emit(loading())
				emit(loadNovelUseCase(novelIDValue, true))
			}

	override fun isOnline(): Boolean = isOnlineUseCase()
}