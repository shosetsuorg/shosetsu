package app.shosetsu.android.viewmodel.model.novel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import app.shosetsu.android.common.dto.*
import app.shosetsu.android.common.enums.ChapterSortType
import app.shosetsu.android.common.enums.ReadingStatus
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.usecases.DownloadChapterPassageUseCase
import app.shosetsu.android.domain.usecases.IsOnlineUseCase
import app.shosetsu.android.domain.usecases.ShareUseCase
import app.shosetsu.android.domain.usecases.delete.DeleteChapterPassageUseCase
import app.shosetsu.android.domain.usecases.load.LoadChapterUIsUseCase
import app.shosetsu.android.domain.usecases.load.LoadFormatterNameUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUIUseCase
import app.shosetsu.android.domain.usecases.load.LoadNovelUseCase
import app.shosetsu.android.domain.usecases.open.OpenInBrowserUseCase
import app.shosetsu.android.domain.usecases.open.OpenInWebviewUseCase
import app.shosetsu.android.domain.usecases.settings.LoadChaptersResumeFirstUnreadUseCase
import app.shosetsu.android.domain.usecases.update.UpdateChapterUseCase
import app.shosetsu.android.domain.usecases.update.UpdateNovelUseCase
import app.shosetsu.android.view.uimodels.model.ChapterUI
import app.shosetsu.android.view.uimodels.model.NovelUI
import app.shosetsu.android.viewmodel.abstracted.INovelViewModel
import com.mikepenz.fastadapter.items.AbstractItem

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
		private val getFormatterNameUseCase: LoadFormatterNameUseCase,
		private val getChapterUIsUseCase: LoadChapterUIsUseCase,
		private val loadNovelUIUseCase: LoadNovelUIUseCase,

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
	private val chapters = ArrayList<ChapterUI>()

	/**
	 * This class manages how chapters are filtered, sorted, and displayed
	 */
	private inner class ChaptersManagement() {
		var showOnlyReadingStatusOf: ReadingStatus? = null
			set(value) {
				launchIO { showOnlyReadingStatusOfLive.postValue(value) }
				field = value
			}
		var onlyDownloaded: Boolean = false
			set(value) {
				launchIO { onlyDownloadedLive.postValue(value) }
				field = value
			}
		var onlyBookmarked: Boolean = false
			set(value) {
				launchIO { onlyBookmarkedLive.postValue(value) }
				field = value
			}
		var sortType: ChapterSortType = ChapterSortType.SOURCE
			set(value) {
				launchIO { sortTypeLive.postValue(value) }
				field = value
			}
		var reversedSort: Boolean = false
			set(value) {
				launchIO { reversedSortLive.postValue(value) }
				field = value
			}


		val showOnlyReadingStatusOfLive: MutableLiveData<ReadingStatus?> = MutableLiveData(showOnlyReadingStatusOf)
		val onlyDownloadedLive: MutableLiveData<Boolean> = MutableLiveData(onlyDownloaded)
		val onlyBookmarkedLive: MutableLiveData<Boolean> = MutableLiveData(onlyBookmarked)

		val sortTypeLive: MutableLiveData<ChapterSortType> = MutableLiveData(sortType)
		val reversedSortLive: MutableLiveData<Boolean> = MutableLiveData(reversedSort)

		fun toggleOnlyDownloaded() = this::onlyDownloaded.toggle()
		fun toggleOnlyBookMarked() = this::onlyBookmarked.toggle()
		fun toggleReverse() = this::reversedSort.toggle()
	}

	/** Adds sources for chapterManagment */
	private fun addChapterManageSource(v: MediatorLiveData<HResult<List<AbstractItem<*>>>> = this._uiLive) {
		v.addSource(chaptersManagement.showOnlyReadingStatusOfLive) {
			uiHasNewData()
		}
		v.addSource(chaptersManagement.onlyDownloadedLive) {
			uiHasNewData()
		}
		v.addSource(chaptersManagement.onlyBookmarkedLive) {
			uiHasNewData()
		}
		v.addSource(chaptersManagement.sortTypeLive) {
			uiHasNewData()
		}
		v.addSource(chaptersManagement.reversedSortLive) {
			uiHasNewData()
		}
	}

	private var chaptersManagement = ChaptersManagement()
		set(value) {
			_uiLive.removeSource(field.showOnlyReadingStatusOfLive)
			_uiLive.removeSource(field.onlyDownloadedLive)
			_uiLive.removeSource(field.onlyBookmarkedLive)
			_uiLive.removeSource(field.sortTypeLive)
			_uiLive.removeSource(field.reversedSortLive)
			field = value
			addChapterManageSource()
		}

	@Deprecated("No longer needed")
	private val _uiLive: MediatorLiveData<HResult<List<AbstractItem<*>>>> by lazy {
		val v = MediatorLiveData<HResult<List<AbstractItem<*>>>>()
		v.addSource(chaptersLive) { result ->
			result.handle(onError = { v.postValue(it) }) {
				chapters.clear()
				chapters.addAll(it)
				uiHasNewData()
			}
		}
		addChapterManageSource(v)
		v
	}

	override val chaptersLive: LiveData<HResult<List<ChapterUI>>> by lazy {
		novelIDLive.switchMap { id ->
			liveDataIO {
				emitSource(getChapterUIsUseCase(id))
			}
		}
	}
	override val formatterName: LiveData<HResult<String>> by lazy {
		novelIDLive.switchMap {
			liveDataIO {
				emit(successResult(""))
				emitSource(getFormatterNameUseCase(it))
			}
		}
	}
	override val novelLive: LiveData<HResult<NovelUI>> by lazy {
		novelIDLive.switchMap {
			liveDataIO {
				emitSource(loadNovelUIUseCase(it))
			}
		}
	}
	private val novelIDLive: MutableLiveData<Int> by lazy { MutableLiveData() }
	private var novelIDValue = -1

	/** Constructs the UI */
	private fun uiHasNewData() {
		logV("uiHasNewData")
		_uiLive.postValue(successResult(ArrayList<AbstractItem<*>>().apply {
			addAll(chapters.handleFilters())
		}))
	}

	private fun List<ChapterUI>.handleFilters(): List<ChapterUI> {
		var result: List<ChapterUI> = this
		if (chaptersManagement.onlyBookmarked)
			result = result.filter { it.bookmarked }

		if (chaptersManagement.onlyDownloaded)
			result = result.filter { it.isSaved }

		chaptersManagement.showOnlyReadingStatusOf?.let { status ->
			result = result.filter { it.readingStatus == status }
		}

		when (chaptersManagement.sortType) {
			ChapterSortType.SOURCE -> {
				if (chaptersManagement.reversedSort)
					result = result.reversed()
			}
			ChapterSortType.UPLOAD -> {

			}
		}

		return result
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
		chaptersManagement = ChaptersManagement()
		launchIO {
			novelIDLive.postValue(-1)
			uiHasNewData()
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

	override fun setNovelID(novelID: Int) {
		when {
			novelIDValue == -1 -> logI("Setting NovelID")
			novelIDValue != novelID -> logI("NovelID not equal, resetting")
			novelIDValue == novelID -> {
				logI("NovelID equal, ignoring")
				return
			}
		}
		novelIDLive.postValue(novelID)
		novelIDValue = novelID
	}

	override fun share() {
		launchIO { novelLive.value?.handle { shareUseCase(it) } }
	}

	override fun toggleBookmark() {
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
}