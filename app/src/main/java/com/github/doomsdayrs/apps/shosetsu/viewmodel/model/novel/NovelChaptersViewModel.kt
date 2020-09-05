package com.github.doomsdayrs.apps.shosetsu.viewmodel.model.novel

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.emptyResult
import com.github.doomsdayrs.apps.shosetsu.common.dto.loading
import com.github.doomsdayrs.apps.shosetsu.common.dto.successResult
import com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.common.ext.launchIO
import com.github.doomsdayrs.apps.shosetsu.domain.usecases.*
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.model.ChapterUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.abstracted.INovelChaptersViewModel
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
		private val getChapterUIsUseCase: GetChapterUIsUseCase,
		private val updateChapterUseCase: UpdateChapterUseCase,
		private val downloadChapterPassageUseCase: DownloadChapterPassageUseCase,
		private val deleteChapterPassageUseCase: DeleteChapterPassageUseCase,
		private val openChapterUseCase: OpenInWebviewUseCase,
		private val openInBrowserUseCase: OpenInBrowserUseCase,
		private val settings: ShosetsuSettings,
) : INovelChaptersViewModel() {
	private var nID: Int = -1

	override fun setNovelID(novelID: Int) {
		if (nID == -1)
			nID = novelID
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

	override val liveData: LiveData<HResult<List<ChapterUI>>> by lazy {
		getChapterUIsUseCase(nID)
	}
}