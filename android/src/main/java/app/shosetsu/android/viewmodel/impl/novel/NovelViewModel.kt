package app.shosetsu.android.viewmodel.impl.novel

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.liveDataIO
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.toggle
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.DownloadChapterPassageUseCase
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.ShareUseCase
import app.shosetsu.android.domain.usecases.delete.DeleteChapterPassageUseCase
import app.shosetsu.android.domain.usecases.get.GetChapterUIsUseCase
import app.shosetsu.android.domain.usecases.get.GetNovelUIUseCase
import app.shosetsu.android.domain.usecases.get.GetNovelUseCase
import app.shosetsu.android.domain.usecases.open.OpenInBrowserUseCase
import app.shosetsu.android.domain.usecases.open.OpenInWebviewUseCase
import app.shosetsu.android.domain.usecases.settings.LoadChaptersResumeFirstUnreadUseCase
import app.shosetsu.android.domain.usecases.update.UpdateChapterUseCase
import app.shosetsu.android.domain.usecases.update.UpdateNovelUseCase
import app.shosetsu.android.view.uimodels.model.ChapterUI
import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.android.viewmodel.abstracted.INovelViewModel
import app.shosetsu.common.dto.*
import app.shosetsu.common.enums.ChapterSortType
import app.shosetsu.common.enums.ReadingStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

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
	private val getChapterUIsUseCase: GetChapterUIsUseCase,
	private val loadNovelUIUseCase: GetNovelUIUseCase,
	private val reportExceptionUseCase: ReportExceptionUseCase,
	private val updateNovelUseCase: UpdateNovelUseCase,
	private val openInBrowserUseCase: OpenInBrowserUseCase,
	private val openInWebviewUseCase: OpenInWebviewUseCase,
	private val shareUseCase: ShareUseCase,
	private val loadNovelUseCase: GetNovelUseCase,
	private var isOnlineUseCase: IsOnlineUseCase,
	private val updateChapterUseCase: UpdateChapterUseCase,
	private val downloadChapterPassageUseCase: DownloadChapterPassageUseCase,
	private val deleteChapterPassageUseCase: DeleteChapterPassageUseCase,
	private val isChaptersResumeFirstUnread: LoadChaptersResumeFirstUnreadUseCase,
) : INovelViewModel() {
	@ExperimentalCoroutinesApi
	@get:Synchronized
	private val chapters: ArrayList<ChapterUI>
		get() = chaptersLive.value?.transmogrify { ArrayList((it)) } ?: arrayListOf()

	@ExperimentalCoroutinesApi
	override val chaptersLive: LiveData<HResult<List<ChapterUI>>> by lazy {
		novelIDLive.transformLatest { id: Int ->
			emitAll(
				getChapterUIsUseCase(id)
					.combineBookmarked()
					.combineDownloaded()
					.combineStatus()
					.combineSort()
					.combineReverse()
			)
		}.asIOLiveData()
	}

	@ExperimentalCoroutinesApi
	override val novelLive: LiveData<HResult<NovelUI>> by lazy {
		novelIDLive.transformLatest {
			emitAll(loadNovelUIUseCase(it))
		}.asIOLiveData()
	}

	private val novelIDLive: MutableStateFlow<Int> by lazy { MutableStateFlow(novelIDValue) }
	private var novelIDValue = -1

	private var _showOnlyStatusOf: ReadingStatus? = null
		set(value) {
			field = value
			_showOnlyStatusOfFlow.tryEmit(value)
		}

	private var _showOnlyDownloaded: Boolean = false
		set(value) {
			field = value
			_onlyDownloadedFlow.tryEmit(value)
		}

	private var _showOnlyBookmarked: Boolean = false
		set(value) {
			field = value
			_onlyBookmarkedFlow.tryEmit(value)
		}

	private var _chapterSortType: ChapterSortType = ChapterSortType.SOURCE
		set(value) {
			field = value
			_sortTypeFlow.tryEmit(value)
		}

	private var _isSortReversed: Boolean = false
		set(value) {
			field = value
			_reversedSortFlow.tryEmit(value)
		}

	private val _showOnlyStatusOfFlow: MutableStateFlow<ReadingStatus?> =
		MutableStateFlow(_showOnlyStatusOf)

	private val _onlyDownloadedFlow: MutableStateFlow<Boolean> =
		MutableStateFlow(_showOnlyDownloaded)

	private val _onlyBookmarkedFlow: MutableStateFlow<Boolean> =
		MutableStateFlow(_showOnlyBookmarked)

	private val _sortTypeFlow: MutableStateFlow<ChapterSortType> =
		MutableStateFlow(_chapterSortType)

	private val _reversedSortFlow: MutableStateFlow<Boolean> =
		MutableStateFlow(_isSortReversed)

	private fun Flow<HResult<List<ChapterUI>>>.combineBookmarked(): Flow<HResult<List<ChapterUI>>> =
		combine(_onlyBookmarkedFlow) { result, onlyBookmarked ->
			if (onlyBookmarked)
				result.transform { chapters ->
					successResult(chapters.filter { ui -> ui.bookmarked })
				}
			else result
		}

	private fun Flow<HResult<List<ChapterUI>>>.combineDownloaded(): Flow<HResult<List<ChapterUI>>> =
		combine(_onlyDownloadedFlow) { result, onlyDownloaded ->
			if (onlyDownloaded)
				result.transform { chapters ->
					successResult(chapters.filter { it.isSaved })
				}
			else result
		}

	private fun Flow<HResult<List<ChapterUI>>>.combineStatus(): Flow<HResult<List<ChapterUI>>> =
		combine(_showOnlyStatusOfFlow) { result, readingStatusOf ->
			readingStatusOf?.let { status ->
				result.transform { chapters ->
					successResult(
						if (status != ReadingStatus.UNREAD)
							chapters.filter { it.readingStatus == status }
						else chapters.filter {
							it.readingStatus == status || it.readingStatus == ReadingStatus.READING
						}
					)
				}

			} ?: result
		}

	private fun Flow<HResult<List<ChapterUI>>>.combineSort(): Flow<HResult<List<ChapterUI>>> =
		combine(_sortTypeFlow) { result, sortType ->
			result.transform { chapters ->
				successResult(when (sortType) {
					ChapterSortType.SOURCE -> {
						chapters.sortedBy { it.order }
					}
					ChapterSortType.UPLOAD -> {
						chapters.sortedBy { it.releaseDate }
					}
				})
			}
		}

	private fun Flow<HResult<List<ChapterUI>>>.combineReverse(): Flow<HResult<List<ChapterUI>>> =
		combine(_reversedSortFlow) { result, reverse ->
			if (reverse)
				result.transform { chapters -> successResult(chapters.reversed()) }
			else result
		}

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}

	override fun delete(vararg chapterUI: ChapterUI) {
		launchIO {
			chapterUI.forEach {
				deleteChapterPassageUseCase(it)
			}
		}
	}

	override fun deletePrevious() {
		TODO("Not yet implemented")
	}

	@ExperimentalCoroutinesApi
	override fun destroy() {
		chapters.clear()

		// resets filters
		_showOnlyStatusOf = null
		_showOnlyDownloaded = false
		_showOnlyBookmarked = false
		_chapterSortType = ChapterSortType.SOURCE
		_isSortReversed = false

		novelIDValue = -1
		novelIDLive.tryEmit(-1)
	}

	override fun downloadChapter(vararg chapterUI: ChapterUI) {
		launchIO {
			chapterUI.forEach {
				downloadChapterPassageUseCase(it)
			}
		}
	}

	override fun isOnline(): Boolean = isOnlineUseCase()
	override fun markAllChaptersAs(vararg chapterUI: ChapterUI, readingStatus: ReadingStatus) {
		launchIO {
			chapterUI.forEach {
				updateChapterUseCase(
					it.copy(
						readingStatus = readingStatus
					)
				)
			}
		}
	}

	override fun openBrowser(chapterUI: ChapterUI) {
		launchIO {
			openInBrowserUseCase(chapterUI)
		}
	}

	@ExperimentalCoroutinesApi
	override fun openBrowser() {
		launchIO { novelLive.value?.handle { openInBrowserUseCase(it) } }
	}

	override fun openLastRead(array: List<ChapterUI>): LiveData<HResult<Int>> =
		liveDataIO {
			emit(loading())
			val array = array.sortedBy { it.order }
			val result = isChaptersResumeFirstUnread()
			val r = if (result is HResult.Success && !result.data)
				array.indexOfFirst { it.readingStatus != ReadingStatus.READ }
			else array.indexOfFirst { it.readingStatus == ReadingStatus.UNREAD }
			emit(if (r == -1) emptyResult() else successResult(r))
		}

	override fun openWebView(chapterUI: ChapterUI) {
		launchIO {
			openInWebviewUseCase(chapterUI)
		}
	}

	@ExperimentalCoroutinesApi
	override fun openWebView() {
		launchIO { novelLive.value?.handle { openInWebviewUseCase(it) } }
	}

	override fun refresh(): LiveData<HResult<Any>> =
		liveDataIO {
			emit(loading())
			emit(loadNovelUseCase(novelIDValue, true))
		}

	override fun reverseChapters() = ::_isSortReversed.toggle()

	override fun toggleOnlyDownloaded() = ::_showOnlyDownloaded.toggle()

	override fun toggleOnlyBookmarked() = ::_showOnlyBookmarked.toggle()

	override fun setChapterSortType(sortType: ChapterSortType) {
		_chapterSortType = sortType
	}

	override fun showOnlyStatus(status: ReadingStatus?) {
		_showOnlyStatusOf = status
	}

	override fun setReverse(b: Boolean) {
		_isSortReversed = b
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
		novelIDLive.tryEmit(novelID)
		novelIDValue = novelID
	}

	@ExperimentalCoroutinesApi
	override fun share() {
		launchIO { novelLive.value?.handle { shareUseCase(it) } }
	}

	@ExperimentalCoroutinesApi
	override fun toggleNovelBookmark() {
		launchIO {
			novelLive.value?.handle { updateNovelUseCase(it.copy(bookmarked = !it.bookmarked)) }
		}
	}

	@ExperimentalCoroutinesApi
	override fun isBookmarked(): Boolean = novelLive.value?.transmogrify { it.bookmarked }
		?: false

	override fun markChapterAsRead(chapterUI: ChapterUI) {
		launchIO {
			updateChapterUseCase(
				chapterUI.copy(
					readingStatus = ReadingStatus.READ
				)
			)
		}
	}

	override fun markChapterAsReading(chapterUI: ChapterUI) {
		launchIO {
			updateChapterUseCase(
				chapterUI.copy(
					readingStatus = ReadingStatus.READING
				)
			)
		}
	}

	override fun markChapterAsUnread(chapterUI: ChapterUI) {
		launchIO {
			updateChapterUseCase(
				chapterUI.copy(
					readingStatus = ReadingStatus.UNREAD
				)
			)
		}
	}

	override fun toggleChapterBookmark(chapterUI: ChapterUI) {
		launchIO {
			updateChapterUseCase(
				chapterUI.copy(
					bookmarked = !chapterUI.bookmarked
				)
			)
		}
	}

	@ExperimentalCoroutinesApi
	override fun downloadNextChapter() {
		launchIO {
			val array = chapters.sortedBy { it.order }
			val r = array.indexOfFirst { it.readingStatus != ReadingStatus.READ }
			if (r != -1) downloadChapter(array[r])
		}
	}

	@ExperimentalCoroutinesApi
	override fun downloadNextCustomChapters(max: Int) {
		launchIO {
			val array = chapters.sortedBy { it.order }
			val r = array.indexOfFirst { it.readingStatus != ReadingStatus.READ }
			if (r != -1) {
				val list = arrayListOf<ChapterUI>()
				list.add(array[r])
				var count = 1
				while ((r + count) < array.size && count <= max) {
					list.add(array[r + count])
					count++
				}
				downloadChapter(*list.toTypedArray())
			}
		}
	}

	@ExperimentalCoroutinesApi
	override fun downloadNext5Chapters() = downloadNextCustomChapters(5)

	@ExperimentalCoroutinesApi
	override fun downloadNext10Chapters() = downloadNextCustomChapters(10)

	@ExperimentalCoroutinesApi
	override fun downloadAllUnreadChapters() {
		launchIO {
			downloadChapter(*chapters.filter { it.readingStatus == ReadingStatus.UNREAD }
				.toTypedArray())
		}
	}

	@ExperimentalCoroutinesApi
	override fun downloadAllChapters() {
		launchIO {
			downloadChapter(*chapters.toTypedArray())
		}
	}

	override fun bookmarkChapters(vararg chapterUI: ChapterUI) {
		launchIO {
			chapterUI.forEach {
				if (!it.bookmarked) updateChapterUseCase(it.copy(bookmarked = true))
			}
		}
	}

	override fun removeChapterBookmarks(vararg chapterUI: ChapterUI) {
		launchIO {
			chapterUI.forEach {
				if (it.bookmarked) updateChapterUseCase(it.copy(bookmarked = false))
			}
		}
	}

	override fun getSortReadingStatusOf(): ReadingStatus? = _showOnlyStatusOf

	override fun showOnlyDownloadedChapters(): Boolean = _showOnlyDownloaded

	override fun showOnlyBookmarkedChapters(): Boolean = _showOnlyBookmarked

	override fun getSortType(): ChapterSortType = _chapterSortType

	override fun isReversedSortOrder(): Boolean = _isSortReversed
}