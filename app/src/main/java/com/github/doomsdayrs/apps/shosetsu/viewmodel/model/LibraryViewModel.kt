package com.github.doomsdayrs.apps.shosetsu.viewmodel.model

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
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.loading
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchIO
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.IsOnlineUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.load.LoadLibraryUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.StartUpdateWorkerUseCase
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.update.UpdateBookmarkedNovelUseCase
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.library.ABookmarkedNovelUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.abstracted.ILibraryViewModel
import kotlinx.coroutines.Dispatchers

/**
 * shosetsu
 * 29 / 04 / 2020
 *
 * @author github.com/doomsdayrs
 */
class LibraryViewModel(
		private val libraryAsCardsUseCase: LoadLibraryUseCase,
		private val updateBookmarkedNovelUseCase: UpdateBookmarkedNovelUseCase,
		private val isOnlineUseCase: IsOnlineUseCase,
		private var startUpdateWorkerUseCase: StartUpdateWorkerUseCase,
) : ILibraryViewModel() {
	override val liveData: LiveData<HResult<List<ABookmarkedNovelUI>>> by lazy {
		liveData(context = viewModelScope.coroutineContext + Dispatchers.Default) {
			emit(loading())
			emitSource(libraryAsCardsUseCase())
		}
	}

	override fun isOnline(): Boolean = isOnlineUseCase()

	override fun startUpdateManager() {
		startUpdateWorkerUseCase()
	}

	override fun removeFromLibrary(list: List<ABookmarkedNovelUI>) {
		launchIO {
			updateBookmarkedNovelUseCase(list.apply {
				forEach {
					it.bookmarked = false
				}
			})
		}
	}
}