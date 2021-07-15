package app.shosetsu.android.viewmodel.impl

import androidx.lifecycle.LiveData
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.usecases.DownloadChapterPassageUseCase
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.ShareUseCase
import app.shosetsu.android.domain.usecases.StartDownloadWorkerAfterUpdateUseCase
import app.shosetsu.android.domain.usecases.delete.DeleteChapterPassageUseCase
import app.shosetsu.android.domain.usecases.get.GetChapterUIsUseCase
import app.shosetsu.android.domain.usecases.get.GetNovelSettingFlowUseCase
import app.shosetsu.android.domain.usecases.get.GetNovelUIUseCase
import app.shosetsu.android.domain.usecases.get.GetRemoteNovelUseCase
import app.shosetsu.android.domain.usecases.load.LoadDeletePreviousChapterUseCase
import app.shosetsu.android.domain.usecases.open.OpenInBrowserUseCase
import app.shosetsu.android.domain.usecases.open.OpenInWebviewUseCase
import app.shosetsu.android.domain.usecases.settings.LoadChaptersResumeFirstUnreadUseCase
import app.shosetsu.android.domain.usecases.start.StartDownloadWorkerUseCase
import app.shosetsu.android.domain.usecases.update.UpdateChapterUseCase
import app.shosetsu.android.domain.usecases.update.UpdateNovelSettingUseCase
import app.shosetsu.android.domain.usecases.update.UpdateNovelUseCase
import app.shosetsu.android.view.uimodels.model.ChapterUI
import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.android.viewmodel.abstracted.ANovelViewModel
import app.shosetsu.common.dto.*
import app.shosetsu.common.enums.ChapterSortType
import app.shosetsu.common.enums.ChapterSortType.SOURCE
import app.shosetsu.common.enums.ReadingStatus
import app.shosetsu.common.view.uimodel.NovelSettingUI
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
	private val loadRemoteNovel: GetRemoteNovelUseCase,
	private var isOnlineUseCase: IsOnlineUseCase,
	private val updateChapterUseCase: UpdateChapterUseCase,
	private val downloadChapterPassageUseCase: DownloadChapterPassageUseCase,
	private val deleteChapterPassageUseCase: DeleteChapterPassageUseCase,
	private val isChaptersResumeFirstUnread: LoadChaptersResumeFirstUnreadUseCase,
	private val getNovelSettingFlowUseCase: GetNovelSettingFlowUseCase,
	private val updateNovelSettingUseCase: UpdateNovelSettingUseCase,
	private val loadDeletePreviousChapterUseCase: LoadDeletePreviousChapterUseCase,
	private val startDownloadWorkerUseCase: StartDownloadWorkerUseCase,
	private val startDownloadWorkerAfterUpdateUseCase: StartDownloadWorkerAfterUpdateUseCase
) : ANovelViewModel() {
	@ExperimentalCoroutinesApi
	@get:Synchronized
	private val chapters: ArrayList<ChapterUI>
		get() = chaptersLive.value?.transmogrify { ArrayList((it)) } ?: arrayListOf()

	@ExperimentalCoroutinesApi
	override val chaptersLive: LiveData<HResult<List<ChapterUI>>> by lazy {
		novelIDLive.transformLatest { id: Int ->
			emitAll(
				getChapterUIsUseCase(id)
					.transform {
						emit(loading)
						emit(it)
					}
					.combineBookmarked()
					.combineDownloaded()
					.combineStatus()
					.combineSort()
					.combineReverse()
			)
		}.asIOLiveData()
	}

	@ExperimentalCoroutinesApi
	override val novelSettingFlow: LiveData<HResult<NovelSettingUI>> by lazy {
		novelSettingsFlow.asIOLiveData()
	}

	@ExperimentalCoroutinesApi
	override val novelLive: LiveData<HResult<NovelUI>> by lazy {
		novelIDLive.transformLatest {
			emitAll(loadNovelUIUseCase(it))
		}.asIOLiveData()
	}

	@ExperimentalCoroutinesApi
	private val novelSettingsFlow: Flow<HResult<NovelSettingUI>> by lazy {
		novelIDLive.transformLatest { emitAll(getNovelSettingFlowUseCase(it)) }
	}

	private val novelIDLive: MutableStateFlow<Int> by lazy { MutableStateFlow(-1) }


	@ExperimentalCoroutinesApi
	private val _showOnlyStatusOfFlow: Flow<ReadingStatus?> =
		novelSettingsFlow.mapLatest { it.transmogrify { ui -> ui.showOnlyReadingStatusOf } }

	@ExperimentalCoroutinesApi
	private val _onlyDownloadedFlow: Flow<Boolean> =
		novelSettingsFlow.mapLatest { it.transmogrify { ui -> ui.showOnlyDownloaded } ?: false }

	@ExperimentalCoroutinesApi
	private val _onlyBookmarkedFlow: Flow<Boolean> =
		novelSettingsFlow.mapLatest { it.transmogrify { ui -> ui.showOnlyBookmarked } ?: false }

	@ExperimentalCoroutinesApi
	private val _sortTypeFlow: Flow<ChapterSortType> =
		novelSettingsFlow.mapLatest { it.transmogrify { ui -> ui.sortType } ?: SOURCE }

	@ExperimentalCoroutinesApi
	private val _reversedSortFlow: Flow<Boolean> =
		novelSettingsFlow.mapLatest { it.transmogrify { ui -> ui.reverseOrder } ?: false }

	@ExperimentalCoroutinesApi
	private fun Flow<HResult<List<ChapterUI>>>.combineBookmarked(): Flow<HResult<List<ChapterUI>>> =
		combine(_onlyBookmarkedFlow) { result, onlyBookmarked ->
			if (onlyBookmarked)
				result.transform { chapters ->
					successResult(chapters.filter { ui -> ui.bookmarked })
				}
			else result
		}

	@ExperimentalCoroutinesApi
	private fun Flow<HResult<List<ChapterUI>>>.combineDownloaded(): Flow<HResult<List<ChapterUI>>> =
		combine(_onlyDownloadedFlow) { result, onlyDownloaded ->
			if (onlyDownloaded)
				result.transform { chapters ->
					successResult(chapters.filter { it.isSaved })
				}
			else result
		}

	@ExperimentalCoroutinesApi
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

	@ExperimentalCoroutinesApi
	private fun Flow<HResult<List<ChapterUI>>>.combineSort(): Flow<HResult<List<ChapterUI>>> =
		combine(_sortTypeFlow) { result, sortType ->
			result.transform { chapters ->
				successResult(when (sortType) {
					SOURCE -> {
						chapters.sortedBy { it.order }
					}
					ChapterSortType.UPLOAD -> {
						chapters.sortedBy { it.releaseDate }
					}
				})
			}
		}

	@ExperimentalCoroutinesApi
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

	@ExperimentalCoroutinesApi
	override fun deletePrevious() {
		launchIO {
			loadDeletePreviousChapterUseCase().handle { chaptersBackToDelete ->
				if (chaptersBackToDelete != -1) {
					/**
					 * [chapters] filters so that it is unread and downloaded
					 * Iterate through these to delete the previous
					 */
					val savedAndUnread =
						chapters.filter { it.readingStatus == ReadingStatus.READ && it.isSaved }

					/**
					 * Don't delete if the size of savedAndUnread is smaller then the [chaptersBackToDelete]
					 */
					if (savedAndUnread.size <= chaptersBackToDelete)
						return@handle

					/**
					 * Iterate through the chapters and delete previous chapters
					 */
					for (index in 0 until savedAndUnread.size - chaptersBackToDelete)
						deleteChapterPassageUseCase(savedAndUnread[index])
				}
			}

		}
	}

	@ExperimentalCoroutinesApi
	override fun destroy() {
		novelIDLive.tryEmit(-1) // Reset view to nothing
	}

	override fun downloadChapter(vararg chapterUI: ChapterUI, startManager: Boolean) {
		launchIO {
			downloadChapterPassageUseCase(*chapterUI)

			if (startManager)
				startDownloadWorkerUseCase()

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
		flow {
			emit(loading())
			val sortedArray = array.sortedBy { it.order }
			val result = isChaptersResumeFirstUnread()
			val index: Int = if (result is HResult.Success && !result.data)
				sortedArray.indexOfFirst { it.readingStatus != ReadingStatus.READ }
			else sortedArray.indexOfFirst { it.readingStatus == ReadingStatus.UNREAD }

			emit(
				if (index == -1) emptyResult() else {
					// Find the original index
					val chapter = sortedArray[index]
					successResult(array.indexOf(chapter))
				}
			)
		}.asIOLiveData()

	override fun openWebView(chapterUI: ChapterUI) {
		launchIO {
			openInWebviewUseCase(chapterUI)
		}
	}

	@ExperimentalCoroutinesApi
	override fun openWebView() {
		launchIO { novelLive.value?.handle { openInWebviewUseCase(it) } }
	}

	override fun refresh(): LiveData<HResult<*>> =
		flow {
			emit(loading())
			emit(
				loadRemoteNovel(novelIDLive.value, true).transform {
					startDownloadWorkerAfterUpdateUseCase(it.updatedChapters)
				}
			)
		}.asIOLiveData()

	override fun setNovelID(novelID: Int) {
		when {
			novelIDLive.value == -1 -> logI("Setting NovelID")
			novelIDLive.value != novelID -> logI("NovelID not equal, resetting")
			novelIDLive.value == novelID -> {
				logI("NovelID equal, ignoring")
				return
			}
		}
		novelIDLive.tryEmit(novelID)
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
			startDownloadWorkerUseCase()
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
			startDownloadWorkerUseCase()
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
			startDownloadWorkerUseCase()
		}
	}

	@ExperimentalCoroutinesApi
	override fun downloadAllChapters() {
		launchIO {
			downloadChapter(*chapters.toTypedArray())
			startDownloadWorkerUseCase()
		}
	}

	override fun updateNovelSetting(novelSettingUI: NovelSettingUI) {
		logD("Launching update")
		launchIO {
			updateNovelSettingUseCase(novelSettingUI)
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
}