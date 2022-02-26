package app.shosetsu.android.viewmodel.impl

import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logD
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.domain.usecases.DownloadChapterPassageUseCase
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.StartDownloadWorkerAfterUpdateUseCase
import app.shosetsu.android.domain.usecases.delete.DeleteChapterPassageUseCase
import app.shosetsu.android.domain.usecases.delete.TrueDeleteChapterUseCase
import app.shosetsu.android.domain.usecases.get.*
import app.shosetsu.android.domain.usecases.load.LoadDeletePreviousChapterUseCase
import app.shosetsu.android.domain.usecases.settings.LoadChaptersResumeFirstUnreadUseCase
import app.shosetsu.android.domain.usecases.start.StartDownloadWorkerUseCase
import app.shosetsu.android.domain.usecases.update.UpdateChapterUseCase
import app.shosetsu.android.domain.usecases.update.UpdateNovelSettingUseCase
import app.shosetsu.android.domain.usecases.update.UpdateNovelUseCase
import app.shosetsu.android.view.uimodels.model.ChapterUI
import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.android.viewmodel.abstracted.ANovelViewModel
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
@OptIn(ExperimentalCoroutinesApi::class)
class NovelViewModel(
	private val getChapterUIsUseCase: GetChapterUIsUseCase,
	private val loadNovelUIUseCase: GetNovelUIUseCase,
	private val updateNovelUseCase: UpdateNovelUseCase,
	private val getContentURL: GetURLUseCase,
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
	private val startDownloadWorkerAfterUpdateUseCase: StartDownloadWorkerAfterUpdateUseCase,
	private val getLastReadChapter: GetLastReadChapterUseCase,
	private val getTrueDelete: GetTrueDeleteChapterUseCase,
	private val trueDeleteChapter: TrueDeleteChapterUseCase
) : ANovelViewModel() {

	override val chaptersLive: Flow<List<ChapterUI>> by lazy {
		chaptersFlow.onIO()
	}

	private val chaptersFlow: Flow<List<ChapterUI>> by lazy {
		novelIDLive.transformLatest { id: Int ->
			emitAll(
				getChapterUIsUseCase(id)
					.transform {
						emit(it)
					}
					.combineBookmarked()
					.combineDownloaded()
					.combineStatus()
					.combineSort()
					.combineReverse()
			)
			_isRefreshing.emit(false)
		}
	}

	override val novelSettingFlow: Flow<NovelSettingUI?> by lazy {
		novelSettingsFlow.onIO()
	}

	override fun getIfAllowTrueDelete(): Flow<Boolean> =
		flow {
			emit(getTrueDelete())
		}.onIO()

	private val novelFlow: Flow<NovelUI?> by lazy {
		novelIDLive.transformLatest {
			_isRefreshing.emit(true)
			emitAll(loadNovelUIUseCase(it))
		}
	}

	override val novelLive: Flow<NovelUI?> by lazy {
		novelFlow.onIO()
	}

	private val _isRefreshing by lazy {
		MutableStateFlow(false)
	}

	override val isRefreshing: Flow<Boolean> = _isRefreshing.onIO()

	private val novelSettingsFlow: Flow<NovelSettingUI?> by lazy {
		novelIDLive.transformLatest { emitAll(getNovelSettingFlowUseCase(it)) }
	}

	private val novelIDLive: MutableStateFlow<Int> by lazy { MutableStateFlow(-1) }


	private val _showOnlyStatusOfFlow: Flow<ReadingStatus?> =
		novelSettingsFlow.mapLatest { it?.showOnlyReadingStatusOf }

	private val _onlyDownloadedFlow: Flow<Boolean> =
		novelSettingsFlow.mapLatest { it?.showOnlyDownloaded ?: false }

	private val _onlyBookmarkedFlow: Flow<Boolean> =
		novelSettingsFlow.mapLatest { it?.showOnlyBookmarked ?: false }

	private val _sortTypeFlow: Flow<ChapterSortType> =
		novelSettingsFlow.mapLatest { it?.sortType ?: SOURCE }

	private val _reversedSortFlow: Flow<Boolean> =
		novelSettingsFlow.mapLatest { it?.reverseOrder ?: false }

	private fun Flow<List<ChapterUI>>.combineBookmarked(): Flow<List<ChapterUI>> =
		combine(_onlyBookmarkedFlow) { result, onlyBookmarked ->
			if (onlyBookmarked)
				result.filter { ui -> ui.bookmarked }
			else result
		}

	private fun Flow<List<ChapterUI>>.combineDownloaded(): Flow<List<ChapterUI>> =
		combine(_onlyDownloadedFlow) { result, onlyDownloaded ->
			if (onlyDownloaded)
				result.filter { it.isSaved }
			else result
		}

	@ExperimentalCoroutinesApi
	private fun Flow<List<ChapterUI>>.combineStatus(): Flow<List<ChapterUI>> =
		combine(_showOnlyStatusOfFlow) { result, readingStatusOf ->
			readingStatusOf?.let { status ->
				if (status != ReadingStatus.UNREAD)
					result.filter { it.readingStatus == status }
				else result.filter {
					it.readingStatus == status || it.readingStatus == ReadingStatus.READING
				}

			} ?: result
		}

	@ExperimentalCoroutinesApi
	private fun Flow<List<ChapterUI>>.combineSort(): Flow<List<ChapterUI>> =
		combine(_sortTypeFlow) { chapters, sortType ->
			when (sortType) {
				SOURCE -> {
					chapters.sortedBy { it.order }
				}
				ChapterSortType.UPLOAD -> {
					chapters.sortedBy { it.releaseDate }
				}
			}
		}

	@ExperimentalCoroutinesApi
	private fun Flow<List<ChapterUI>>.combineReverse(): Flow<List<ChapterUI>> =
		combine(_reversedSortFlow) { result, reverse ->
			if (reverse)
				result.reversed()
			else result
		}


	override fun delete(vararg chapterUI: ChapterUI) {
		launchIO {
			chapterUI.forEach {
				deleteChapterPassageUseCase(it)
			}
		}
	}

	override fun deletePrevious() {
		logI("Deleting previous chapters")
		launchIO {
			loadDeletePreviousChapterUseCase().let { chaptersBackToDelete ->
				if (chaptersBackToDelete != -1) {
					val lastUnread =
						getLastReadChapter(novelFlow.first { it != null }!!.id)

					if (lastUnread == null) {
						logE("Received empty when trying to get lastUnreadResult")
						return@launchIO
					}

					val chapters = chaptersFlow.first().sortedBy { it.order }

					val indexOfLast = chapters.indexOfFirst { it.id == lastUnread.chapterId }

					if (indexOfLast == -1) {
						logE("Index of last read chapter turned up negative")
						return@launchIO
					}

					if (indexOfLast - chaptersBackToDelete < 0) {
						return@launchIO
					}

					deleteChapterPassageUseCase(chapters[indexOfLast - chaptersBackToDelete])
				}
			}

		}
	}

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

	override fun openLastRead(array: List<ChapterUI>): Flow<Int> =
		flow {
			val sortedArray = array.sortedBy { it.order }
			val result = isChaptersResumeFirstUnread()
			val index: Int = if (!result)
				sortedArray.indexOfFirst { it.readingStatus != ReadingStatus.READ }
			else sortedArray.indexOfFirst { it.readingStatus == ReadingStatus.UNREAD }

			emit(
				if (index == -1) {
					index
				} else {
					// Find the original index
					val chapter = sortedArray[index]
					array.indexOf(chapter)
				}
			)
		}.onIO()

	override fun getNovelURL(): Flow<String?> =
		flow {
			emit(getContentURL(novelFlow.first { it != null }!!))
		}.onIO()

	override fun getShareInfo(): Flow<NovelShareInfo?> =
		flow {
			emit(novelFlow.first { it != null }!!.let {
				getContentURL(it)?.let { url ->
					NovelShareInfo(it.title, url)
				}
			})
		}.onIO()

	override fun getChapterURL(chapterUI: ChapterUI): Flow<String?> =
		flow {
			emit(getContentURL(chapterUI))
		}.onIO()

	override fun refresh(): Flow<Unit> =
		flow {
			loadRemoteNovel(novelIDLive.value, true)?.let {
				startDownloadWorkerAfterUpdateUseCase(it.updatedChapters)
			}
			emit(Unit)
		}.onIO()

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

	override fun toggleNovelBookmark() {
		launchIO {
			val novel = novelFlow.first { it != null }!!
			updateNovelUseCase(novel.copy(bookmarked = !novel.bookmarked))
		}
	}

	override fun isBookmarked(): Flow<Boolean> = flow {
		emit(novelFlow.first()?.bookmarked ?: false)
	}.onIO()

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

	override fun downloadNextChapter() {
		launchIO {
			val array = chaptersFlow.first().sortedBy { it.order }
			val r = array.indexOfFirst { it.readingStatus != ReadingStatus.READ }
			if (r != -1) downloadChapter(array[r])
			startDownloadWorkerUseCase()
		}
	}

	override fun downloadNextCustomChapters(max: Int) {
		launchIO {
			val array = chaptersFlow.first().sortedBy { it.order }
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

	override fun downloadNext5Chapters() = downloadNextCustomChapters(5)

	override fun downloadNext10Chapters() = downloadNextCustomChapters(10)

	override fun downloadAllUnreadChapters() {
		launchIO {
			downloadChapter(
				*chaptersFlow.first().filter { it.readingStatus == ReadingStatus.UNREAD }
					.toTypedArray())
			startDownloadWorkerUseCase()
		}
	}

	override fun downloadAllChapters() {
		launchIO {
			downloadChapter(*chaptersFlow.first().toTypedArray())
			startDownloadWorkerUseCase()
		}
	}

	override fun updateNovelSetting(novelSettingUI: NovelSettingUI) {
		logD("Launching update")
		launchIO {
			updateNovelSettingUseCase(novelSettingUI)
		}
	}

	override fun trueDelete(list: List<ChapterUI>) {
		launchIO {
			list.forEach {
				trueDeleteChapter(it)
			}
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

	override var isFromChapterReader: Boolean = false
		get() = if (field) {
			val value = field
			field = !value
			value
		} else field
}