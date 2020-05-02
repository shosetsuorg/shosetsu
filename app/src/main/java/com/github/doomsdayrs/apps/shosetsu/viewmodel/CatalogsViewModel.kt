package com.github.doomsdayrs.apps.shosetsu.viewmodel

import androidx.lifecycle.*
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.emptyResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.loading
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.FormatterAsCardsUseCase
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.FormatterCard
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ICatalogsViewModel
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
 * 30 / 04 / 2020
 */
class CatalogsViewModel(
		private val formatterAsCardsUseCase: FormatterAsCardsUseCase
) : ViewModel(), ICatalogsViewModel {

	override val liveData: LiveData<HResult<List<FormatterCard>>> by lazy {
		liveData(context = viewModelScope.coroutineContext + Dispatchers.Main) {
			emit(loading())
			emitSource(formatterAsCardsUseCase.invoke())
		}
	}

	override fun subscribeObserver(
			owner: LifecycleOwner,
			observer: Observer<HResult<List<FormatterCard>>>
	): Unit = liveData.observe(owner, observer)

	override suspend fun getLiveData(): HResult<List<FormatterCard>> =
			liveData.value ?: emptyResult()
}

