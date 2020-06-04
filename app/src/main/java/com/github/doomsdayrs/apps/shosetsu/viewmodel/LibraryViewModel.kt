package com.github.doomsdayrs.apps.shosetsu.viewmodel

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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.loading
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchAsync
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.LibraryAsCardsUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.UnreadChaptersUseCase
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.IDTitleImageUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.ILibraryViewModel
import kotlinx.coroutines.Dispatchers

/**
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class LibraryViewModel(
		private val unreadChaptersUseCase: UnreadChaptersUseCase,
		private val libraryAsCardsUseCase: LibraryAsCardsUseCase
) : ILibraryViewModel() {
	override var selectedNovels = MutableLiveData<List<Int>>()
	override var visible: MutableLiveData<List<Int>>
		get() = TODO("Not yet implemented")
		set(value) {}

	override fun handleSelect(id: Int) {
		TODO("Not yet implemented")
	}

	override fun select(id: Int) {
		TODO("Not yet implemented")
	}

	override fun deselect(id: Int) {
		TODO("Not yet implemented")
	}

	override fun selectAll() {
		launchAsync {
			val v = liveData.value
			val r = (selectedNovels.value ?: listOf()) as ArrayList
			if (v is HResult.Success)
				v.data.forEach { r.add(it.id) }
			selectedNovels.postValue(r)
		}
	}

	override fun deselectAll() = selectedNovels.postValue(listOf())

	override fun removeAllFromLibrary() {
		TODO("Not yet implemented")
	}

	override fun loadChaptersUnread(novelID: Int): LiveData<HResult<Int>> {
		return liveData(context = viewModelScope.coroutineContext + Dispatchers.Main) {
			emit(loading())
			emitSource(unreadChaptersUseCase(novelID))
		}
	}


	override fun search(search: String): List<IDTitleImageUI> {
		TODO("Not yet implemented")
	}

	override val liveData: LiveData<HResult<List<IDTitleImageUI>>> by lazy {
		liveData(context = viewModelScope.coroutineContext + Dispatchers.Default) {
			emit(loading())
			emitSource(libraryAsCardsUseCase())
		}
	}
}