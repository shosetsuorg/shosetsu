package app.shosetsu.android.viewmodel.model.novel

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.DownloadChapterPassageUseCase
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.ShareUseCase
import app.shosetsu.android.domain.usecases.delete.DeleteChapterPassageUseCase
import app.shosetsu.android.domain.usecases.get.GetChapterUIsUseCase
import app.shosetsu.android.domain.usecases.get.GetNovelUIUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUseCase
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
		private val loadNovelUseCase: LoadNovelUseCase,
		private var isOnlineUseCase: IsOnlineUseCase,
		private val updateChapterUseCase: UpdateChapterUseCase,
		private val downloadChapterPassageUseCase: DownloadChapterPassageUseCase,
		private val deleteChapterPassageUseCase: DeleteChapterPassageUseCase,
		private val isChaptersResumeFirstUnread: LoadChaptersResumeFirstUnreadUseCase,
) : INovelViewModel() {
	@get:Synchronized
	private val chapters: ArrayList<ChapterUI>
		get() = chaptersLive.value?.handledReturnAny { ArrayList((it)) } ?: arrayListOf()

	private val chaptersManagement = ChaptersManagement()

	private fun Flow<HResult<List<ChapterUI>>>.combineBookmarked(): Flow<HResult<List<ChapterUI>>> =
			combine(chaptersManagement.onlyBookmarkedLive) { result, onlyBookmarked ->
				if (onlyBookmarked)
					result.handleReturn { chapters ->
						successResult(chapters.filter { ui -> ui.bookmarked })
					}
				else result
			}

	private fun Flow<HResult<List<ChapterUI>>>.combineDownloaded(): Flow<HResult<List<ChapterUI>>> =
			combine(chaptersManagement.onlyDownloadedLive) { result, onlyDownloaded ->
				if (onlyDownloaded)
					result.handleReturn { chapters ->
						successResult(chapters.filter { it.isSaved })
					}
				else result
			}

	private fun Flow<HResult<List<ChapterUI>>>.combineStatus(): Flow<HResult<List<ChapterUI>>> =
			combine(chaptersManagement.showOnlyReadingStatusOfLive) { result, readingStatusOf ->
				readingStatusOf?.let { status ->
					result.handleReturn { chapters ->
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
			combine(chaptersManagement.sortTypeLive) { result, sortType ->
				result.handleReturn { chapters ->
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
			combine(chaptersManagement.reversedSortLive) { result, reverse ->
				if (reverse)
					result.handleReturn { chapters -> successResult(chapters.reversed()) }
				else result
			}

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

	override val novelLive: LiveData<HResult<NovelUI>> by lazy {
		novelIDLive.transformLatest {
			emitAll(loadNovelUIUseCase(it))
		}.asIOLiveData()
	}
	private val novelIDLive: MutableStateFlow<Int> by lazy { MutableStateFlow(novelIDValue) }
	private var novelIDValue = -1

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

	override fun destroy() {
		chapters.clear()
		chaptersManagement.apply {
			showOnlyReadingStatusOf = null
			onlyDownloaded = false
			onlyBookmarked = false
			sortType = ChapterSortType.SOURCE
			reversedSort = false
		}
		novelIDValue = -1
		launchIO {
			novelIDLive.emit(-1)
		}
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
						))
			}
		}
	}

	override fun openBrowser(chapterUI: ChapterUI) {
		launchIO {
			openInBrowserUseCase(chapterUI)
		}
	}

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

	override fun openWebView() {
		launchIO { novelLive.value?.handle { openInWebviewUseCase(it) } }
	}

	override fun refresh(): LiveData<HResult<Any>> =
			liveDataIO {
				emit(loading())
				emit(loadNovelUseCase(novelIDValue, true))
			}

	override fun reverseChapters() = chaptersManagement.toggleReverse()
	override fun toggleOnlyDownloaded() = chaptersManagement.toggleOnlyDownloaded()
	override fun toggleOnlyBookmarked() = chaptersManagement.toggleOnlyBookMarked()
	override fun setSortType(sortType: ChapterSortType) {
		chaptersManagement.sortType = sortType
	}

	override fun showOnlyStatus(status: ReadingStatus?) {
		chaptersManagement.showOnlyReadingStatusOf = status
	}

	override fun setReverse(b: Boolean) {
		chaptersManagement.reversedSort = b
		logV("Set reverse")
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
		launchIO {
			novelIDLive.emit(novelID)
			novelIDValue = novelID
		}
	}

	override fun share() {
		launchIO { novelLive.value?.handle { shareUseCase(it) } }
	}

	override fun toggleNovelBookmark() {
		launchIO {
			novelLive.value?.handle { updateNovelUseCase(it.copy(bookmarked = !it.bookmarked)) }
		}
	}

	override fun isBookmarked(): Boolean = novelLive.value?.handledReturnAny { it.bookmarked }
			?: false

	override fun markChapterAsRead(chapterUI: ChapterUI) {
		launchIO {
			updateChapterUseCase(chapterUI.copy(
					readingStatus = ReadingStatus.READ
			))
		}
	}

	override fun markChapterAsReading(chapterUI: ChapterUI) {
		launchIO {
			updateChapterUseCase(chapterUI.copy(
					readingStatus = ReadingStatus.READING
			))
		}
	}

	override fun markChapterAsUnread(chapterUI: ChapterUI) {
		launchIO {
			updateChapterUseCase(chapterUI.copy(
					readingStatus = ReadingStatus.UNREAD
			))
		}
	}

	override fun toggleChapterBookmark(chapterUI: ChapterUI) {
		launchIO {
			updateChapterUseCase(chapterUI.copy(
					bookmarked = !chapterUI.bookmarked
			))
		}
	}

	override fun downloadNextChapter() {
		launchIO {
			val array = chapters.sortedBy { it.order }
			val r = array.indexOfFirst { it.readingStatus != ReadingStatus.READ }
			if (r != -1) downloadChapter(array[r])
		}
	}

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

	override fun downloadNext5Chapters() = downloadNextCustomChapters(5)
	override fun downloadNext10Chapters() = downloadNextCustomChapters(10)
	override fun downloadAllUnreadChapters() {
		launchIO {
			downloadChapter(*chapters.filter { it.readingStatus == ReadingStatus.UNREAD }.toTypedArray())
		}
	}

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

	override fun getSortReadingStatusOf(): ReadingStatus? = chaptersManagement.showOnlyReadingStatusOf

	override fun showOnlyDownloadedChapters(): Boolean = chaptersManagement.onlyDownloaded

	override fun showOnlyBookmarkedChapters(): Boolean = chaptersManagement.onlyBookmarked

	override fun getSortType(): ChapterSortType = chaptersManagement.sortType

	override fun isReversedSortOrder(): Boolean = chaptersManagement.reversedSort

	/**
	 * This class manages how chapters are filtered, sorted, and displayed
	 */
	private inner class ChaptersManagement {
		var showOnlyReadingStatusOf: ReadingStatus? = null
			set(value) {
				field = value
				launchIO { showOnlyReadingStatusOfLive.emit(value) }
			}
		var onlyDownloaded: Boolean = false
			set(value) {
				field = value
				launchIO { onlyDownloadedLive.emit(value) }
			}
		var onlyBookmarked: Boolean = false
			set(value) {
				field = value
				launchIO { onlyBookmarkedLive.emit(value) }
			}
		var sortType: ChapterSortType = ChapterSortType.SOURCE
			set(value) {
				field = value
				launchIO { sortTypeLive.emit(value) }
			}
		var reversedSort: Boolean = false
			set(value) {
				field = value
				launchIO { reversedSortLive.emit(value) }
			}

		val showOnlyReadingStatusOfLive: MutableStateFlow<ReadingStatus?> = MutableStateFlow(showOnlyReadingStatusOf)
		val onlyDownloadedLive: MutableStateFlow<Boolean> = MutableStateFlow(onlyDownloaded)
		val onlyBookmarkedLive: MutableStateFlow<Boolean> = MutableStateFlow(onlyBookmarked)

		val sortTypeLive: MutableStateFlow<ChapterSortType> = MutableStateFlow(sortType)
		val reversedSortLive: MutableStateFlow<Boolean> = MutableStateFlow(reversedSort)

		fun toggleOnlyDownloaded() = this::onlyDownloaded.toggle()
		fun toggleOnlyBookMarked() = this::onlyBookmarked.toggle()
		fun toggleReverse() = this::reversedSort.toggle()
	}
}