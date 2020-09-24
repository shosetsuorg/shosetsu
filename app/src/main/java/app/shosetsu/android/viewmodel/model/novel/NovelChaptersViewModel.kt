package app.shosetsu.android.viewmodel.model.novel

import androidx.lifecycle.*
import app.shosetsu.android.common.ShosetsuSettings
import app.shosetsu.android.common.dto.*
import app.shosetsu.android.common.enums.ReadingStatus
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.domain.usecases.DownloadChapterPassageUseCase
import app.shosetsu.android.domain.usecases.delete.DeleteChapterPassageUseCase
import app.shosetsu.android.domain.usecases.load.LoadChapterUIsUseCase
import app.shosetsu.android.domain.usecases.open.OpenInBrowserUseCase
import app.shosetsu.android.domain.usecases.open.OpenInWebviewUseCase
import app.shosetsu.android.domain.usecases.update.UpdateChapterUseCase
import app.shosetsu.android.view.uimodels.model.ChapterUI
import app.shosetsu.android.viewmodel.abstracted.INovelChaptersViewModel
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
 * 17 / 05 / 2020
 */
class NovelChaptersViewModel(
		private val getChapterUIsUseCase: LoadChapterUIsUseCase,
		private val updateChapterUseCase: UpdateChapterUseCase,
		private val downloadChapterPassageUseCase: DownloadChapterPassageUseCase,
		private val deleteChapterPassageUseCase: DeleteChapterPassageUseCase,
		private val openChapterUseCase: OpenInWebviewUseCase,
		private val openInBrowserUseCase: OpenInBrowserUseCase,
		private val settings: ShosetsuSettings,
) : INovelChaptersViewModel() {

	private var nID: Int = -1
	private val novelIDLive: MutableLiveData<Int> by lazy {
		MutableLiveData(nID)
	}

	private var areChaptersReversed: Boolean = false
	private val reverseChapters: MutableLiveData<Boolean> by lazy {
		MutableLiveData(areChaptersReversed)
	}

	override val liveData: LiveData<HResult<List<ChapterUI>>> by lazy {
		novelIDLive.switchMap { id ->
			getChapterUIsUseCase(id).switchMap { list ->
				reverseChapters.switchMap { isReversed ->
					reverseChapterList(isReversed, list)
				}
			}
		}
	}

	private fun reverseChapterList(
			boolean: Boolean,
			list: HResult<List<ChapterUI>>
	): LiveData<HResult<List<ChapterUI>>> =
			liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
				emit(list.handleReturn {
					successResult(ArrayList(it).apply { if (boolean) reverse() }.toList())
				})
			}

	override fun setNovelID(novelID: Int) {
		when {
			nID == -1 -> logI("Setting novelID as $novelID")
			nID != novelID -> logI("Novel ids are not the same, resetting view")
			nID == novelID -> {
				logI("Novel ids are the same, ignoring")
				return
			}
		}
		destroy()
		nID = novelID
		novelIDLive.postValue(nID)
	}

	override fun download(vararg chapterUI: ChapterUI) {
		launchIO {
			chapterUI.forEach {
				downloadChapterPassageUseCase(it)
			}
		}
	}

	override fun deletePrevious() {
		TODO("Not yet implemented")
	}

	override fun openLastRead(array: List<ChapterUI>): LiveData<HResult<Int>> =
			liveData<HResult<Int>>(viewModelScope.coroutineContext + Dispatchers.IO) {
				emit(loading())
				val array = array.sortedBy { it.order }
				val r = if (!settings.resumeOpenFirstUnread)
					array.indexOfFirst { it.readingStatus != ReadingStatus.READ }
				else array.indexOfFirst { it.readingStatus == ReadingStatus.UNREAD }
				emit(if (r == -1) emptyResult() else successResult(r))
			}

	override fun updateChapter(
			chapterUI: ChapterUI,
			readingPosition: Int,
			readingStatus: ReadingStatus,
			bookmarked: Boolean,
	) {
		launchIO {
			updateChapterUseCase(
					chapterUI.copy(
							readingPosition = readingPosition,
							readingStatus = readingStatus,
							bookmarked = bookmarked
					))
		}
	}

	override fun markAllAs(vararg chapterUI: ChapterUI, readingStatus: ReadingStatus) {
		launchIO {
			chapterUI.forEach {
				updateChapterUseCase(
						it.copy(
								readingStatus = readingStatus
						))
			}
		}
	}

	override fun openWebView(chapterUI: ChapterUI) {
		launchIO {
			openChapterUseCase(chapterUI)
		}
	}

	override fun openBrowser(chapterUI: ChapterUI) {
		launchIO {
			openInBrowserUseCase(chapterUI)
		}
	}

	override fun delete(vararg chapterUI: ChapterUI) {
		launchIO {
			chapterUI.forEach {
				deleteChapterPassageUseCase(it)
			}
		}
	}

	override fun reverseChapters() {
		launchIO {
			areChaptersReversed = !areChaptersReversed
			reverseChapters.postValue(areChaptersReversed)
		}
	}

	override fun destroy() {
		areChaptersReversed = false
		reverseChapters.postValue(areChaptersReversed)
	}
}