package app.shosetsu.android.viewmodel.model.novel

import androidx.lifecycle.*
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.handle
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.wait
import app.shosetsu.android.domain.usecases.ShareUseCase
import app.shosetsu.android.domain.usecases.load.LoadFormatterNameUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUIUseCase
import app.shosetsu.android.domain.usecases.open.OpenInBrowserUseCase
import app.shosetsu.android.domain.usecases.open.OpenInWebviewUseCase
import app.shosetsu.android.domain.usecases.update.UpdateNovelUseCase
import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.android.viewmodel.abstracted.INovelInfoViewModel
import app.shosetsu.lib.Novel
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
class NovelInfoViewModel(
		private val getFormatterNameUseCase: LoadFormatterNameUseCase,
		private val updateNovelUseCase: UpdateNovelUseCase,
		private val loadNovelUIUseCase: LoadNovelUIUseCase,
		private val openInBrowserUseCase: OpenInBrowserUseCase,
		private val openInWebviewUseCase: OpenInWebviewUseCase,
		private val shareUseCase: ShareUseCase,
) : INovelInfoViewModel() {
	override val liveData: LiveData<HResult<NovelUI>> by lazy {
		novelIDLive.switchMap {
			liveData(viewModelScope.coroutineContext + Dispatchers.IO) {
				emit(successResult(NovelUI(
						-1,
						"",
						-1,
						false,
						-1,
						"",
						"",
						"",
						true,
						"",
						arrayOf(),
						arrayOf(),
						arrayOf(),
						arrayOf(),
						Novel.Status.UNKNOWN
				)))
				wait(100)
				emitSource(loadNovelUIUseCase(it))
			}
		}
	}

	private val novelIDLive by lazy { MutableLiveData<Int>() }

	private var novelIDValue: Int = -1

	override val formatterName: LiveData<HResult<String>> by lazy {
		novelIDLive.switchMap {
			liveData<HResult<String>>(viewModelScope.coroutineContext + Dispatchers.IO) {
				emit(successResult(""))
				getFormatterNameUseCase(it)
			}
		}
	}

	override fun setNovelID(novelID: Int) {
		when {
			novelIDValue == -1 -> logI("Setting NovelID")
			novelIDValue != novelID -> logI("NovelID not equal, resetting")
			novelIDValue == novelID -> {
				logI("NovelID equal, ignoring")
				return
			}
		}
		this.novelIDLive.postValue(novelID)
		novelIDValue = novelID
	}

	override fun toggleBookmark() {
		launchIO {
			liveData.value?.handle { updateNovelUseCase(it.copy(bookmarked = !it.bookmarked)) }
		}
	}

	override fun openBrowser() {
		launchIO { liveData.value?.handle { openInBrowserUseCase(it) } }
	}

	override fun openWebView() {
		launchIO { liveData.value?.handle { openInWebviewUseCase(it) } }
	}

	override fun share() {
		launchIO { liveData.value?.handle { shareUseCase(it) } }
	}
}