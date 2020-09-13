package app.shosetsu.android.viewmodel.model.novel

import androidx.lifecycle.*
import app.shosetsu.android.common.ShosetsuSettings
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.emptyResult
import app.shosetsu.android.common.dto.loading
import app.shosetsu.android.common.dto.successResult
import app.shosetsu.android.common.enums.ReadingStatus
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.domain.usecases.DownloadChapterPassageUseCase
import app.shosetsu.android.domain.usecases.OpenInBrowserUseCase
import app.shosetsu.android.domain.usecases.OpenInWebviewUseCase
import app.shosetsu.android.domain.usecases.delete.DeleteChapterPassageUseCase
import app.shosetsu.android.domain.usecases.load.LoadChapterUIsUseCase
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

	override val liveData: LiveData<HResult<List<ChapterUI>>> by lazy {
		novelIDLive.switchMap { getChapterUIsUseCase(it) }
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
}