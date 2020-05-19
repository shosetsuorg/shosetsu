package com.github.doomsdayrs.apps.shosetsu.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.loading
import com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.domain.model.local.URLImageTitle
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.GetUpdateDaysUseCase
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.UpdateChapterUI
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.UpdateUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IUpdatesViewModel
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
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class UpdatesViewModel(
		private val getUpdateDaysUseCase: GetUpdateDaysUseCase
) : IUpdatesViewModel() {
	override val liveData: LiveData<HResult<List<Long>>> by lazy {
		liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
			emit(loading())
			emitSource(getUpdateDaysUseCase())
		}
	}

	override fun getURLImageTitle(novelID: Int): URLImageTitle {
		TODO("Not yet implemented")
	}

	override fun updateChapter(updateUI: UpdateUI, readingStatus: ReadingStatus) {
		TODO("Not yet implemented")
	}

	override fun getChapter(chapterID: Int): UpdateChapterUI {
		TODO("Not yet implemented")
	}

	override fun getTimeBetweenDates(date: Long, date2: Long): LiveData<HResult<List<UpdateUI>>> {
		TODO("Not yet implemented")
	}
}