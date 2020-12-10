package app.shosetsu.android.viewmodel.model

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
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.StartUpdateWorkerUseCase
import app.shosetsu.android.domain.usecases.load.LoadLibraryUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUIColumnsHUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUIColumnsPUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUITypeUseCase
import app.shosetsu.android.domain.usecases.update.UpdateBookmarkedNovelUseCase
import app.shosetsu.android.view.uimodels.model.library.ABookmarkedNovelUI
import app.shosetsu.android.viewmodel.abstracted.ILibraryViewModel
import app.shosetsu.common.com.consts.settings.SettingKey
import app.shosetsu.common.com.consts.settings.SettingKey.ChapterColumnsInLandscape
import app.shosetsu.common.com.consts.settings.SettingKey.ChapterColumnsInPortait
import app.shosetsu.common.com.dto.HResult
import app.shosetsu.common.com.dto.loading
import app.shosetsu.common.com.enums.NovelUIType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest

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
		private val reportExceptionUseCase: ReportExceptionUseCase,
		private val loadNovelUITypeUseCase: LoadNovelUITypeUseCase,
		private val loadNovelUIColumnsHUseCase: LoadNovelUIColumnsHUseCase,
		private val loadNovelUIColumnsPUseCase: LoadNovelUIColumnsPUseCase,
) : ILibraryViewModel() {
	private var novelUIType: NovelUIType = NovelUIType.fromInt(SettingKey.NovelCardType.default)
	private var columnP: Int = ChapterColumnsInPortait.default
	private var columnH: Int = ChapterColumnsInLandscape.default

	override val liveData: LiveData<HResult<List<ABookmarkedNovelUI>>> by lazy {
		liveData(context = viewModelScope.coroutineContext + Dispatchers.Default) {
			emit(loading())
			emitSource(libraryAsCardsUseCase().asIOLiveData())
		}
	}

	init {
		launchIO {
			loadNovelUIColumnsHUseCase().collectLatest {
				columnH = it
			}
			loadNovelUIColumnsPUseCase().collectLatest {
				columnP = it
			}
			loadNovelUITypeUseCase().collectLatest {
				novelUIType = it
			}
		}
	}

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}

	override fun getColumnsInP(): Int = columnP
	override fun getColumnsInH(): Int = columnH
	override fun getNovelUIType(): NovelUIType = novelUIType
	override fun isOnline(): Boolean = isOnlineUseCase()
	override fun startUpdateManager() {
		startUpdateWorkerUseCase(true)
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