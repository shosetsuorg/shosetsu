package app.shosetsu.android.viewmodel.model

import android.graphics.Color
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import app.shosetsu.android.common.consts.settings.SettingKey.*
import app.shosetsu.android.common.dto.*
import app.shosetsu.android.common.enums.MarkingTypes
import app.shosetsu.android.common.enums.MarkingTypes.ONSCROLL
import app.shosetsu.android.common.enums.MarkingTypes.ONVIEW
import app.shosetsu.android.common.enums.ReadingStatus
import app.shosetsu.android.common.enums.ReadingStatus.READ
import app.shosetsu.android.common.enums.ReadingStatus.READING
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logD
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.logID
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.model.local.ColorChoiceData
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.domain.usecases.load.LoadChapterPassageUseCase
import app.shosetsu.android.domain.usecases.load.LoadReaderChaptersUseCase
import app.shosetsu.android.domain.usecases.load.LoadReaderThemes
import app.shosetsu.android.domain.usecases.update.UpdateReaderChapterUseCase
import app.shosetsu.android.view.uimodels.model.ColorChoiceUI
import app.shosetsu.android.view.uimodels.model.reader.ReaderChapterUI
import app.shosetsu.android.view.uimodels.model.reader.ReaderDividerUI
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem
import app.shosetsu.android.viewmodel.abstracted.IChapterReaderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.mapLatest

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
 * 06 / 05 / 2020
 */
class ChapterReaderViewModel(
		private val iSettingsRepository: ISettingsRepository,
		private val loadReaderChaptersUseCase: LoadReaderChaptersUseCase,
		private val loadChapterPassageUseCase: LoadChapterPassageUseCase,
		private val updateReaderChapterUseCase: UpdateReaderChapterUseCase,
		private val loadReadersThemes: LoadReaderThemes,
		private val reportExceptionUseCase: ReportExceptionUseCase
) : IChapterReaderViewModel() {

	private val hashMap: HashMap<Int, MutableLiveData<*>> = hashMapOf()

	override val liveData: LiveData<HResult<List<ReaderUIItem<*, *>>>> by lazy {
		loadReaderChaptersUseCase(nID).mapLatest {
			it.handleReturn {
				val array = ArrayList<ReaderUIItem<*, *>>(it)

				// Adds the "No more chapters" marker
				array.add(array.size, ReaderDividerUI(prev = it.last().title))

				/**
				 * Loops down the list, adding inbetweens
				 */
				var index = array.size - 2
				while (index > 1) {
					val next = array[index] as ReaderChapterUI
					val prev = array[index - 1] as ReaderChapterUI
					array.add(index, ReaderDividerUI(prev.title, next.title))
					index -= 2
				}

				successResult(array)
			}
		}.asIOLiveData()
	}

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}

	override val liveTheme: LiveData<Pair<Int, Int>> by lazy {
		liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
			emitSource(iSettingsRepository.observeInt(ReaderTheme).asIOLiveData().switchMap { id: Int ->
				logD("Loading theme for $id")
				liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
					val s = iSettingsRepository.getStringSet(ReaderUserThemes)
					if (s is HResult.Success) {
						val selected = s.data.map { ColorChoiceData.fromString(it) }.find { it.identifier == id.toLong() }
						selected?.let {
							emit(it.textColor to it.backgroundColor)
						} ?: emit(Color.BLACK to Color.WHITE)
					} else emit(Color.BLACK to Color.WHITE)
				}
			})
		}
	}

	override val liveMarkingTypes: LiveData<MarkingTypes> by lazy {
		iSettingsRepository.observeString(ReadingMarkingType)
				.asIOLiveData()
				.map { MarkingTypes.valueOf(it) }
	}

	override val liveThemes: LiveData<List<ColorChoiceUI>> by lazy {
		loadReadersThemes().asIOLiveData()
	}

	override val liveIndentSize: LiveData<Int> by lazy {
		iSettingsRepository.observeInt(ReaderIndentSize).asIOLiveData()
	}

	override val liveParagraphSpacing: LiveData<Int> by lazy {
		iSettingsRepository.observeInt(ReaderParagraphSpacing).asIOLiveData()
	}

	override val liveTextSize: LiveData<Float> by lazy {
		iSettingsRepository.observeFloat(ReaderTextSize).asIOLiveData()
	}
	override val liveVolumeScroll: LiveData<Boolean> by lazy {
		iSettingsRepository.observeBoolean(ReaderVolumeScroll).asIOLiveData()
	}

	override var currentChapterID: Int = -1

	private var nID = -1

	override fun setReaderTheme(value: Int) {
		launchIO { iSettingsRepository.setInt(ReaderTheme, value) }
	}

	override fun setReaderIndentSize(value: Int) {
		launchIO {
			logI("setting")
			iSettingsRepository.setInt(ReaderIndentSize, value)
		}
	}

	override fun setReaderParaSpacing(value: Int) {
		launchIO { iSettingsRepository.setInt(ReaderParagraphSpacing, value) }
	}

	override fun setReaderTextSize(value: Float) {
		launchIO { iSettingsRepository.setFloat(ReaderTextSize, value) }
	}

	override fun setNovelID(novelID: Int) {
		if (nID == -1)
			nID = novelID
	}

	@WorkerThread
	override fun getChapterPassage(readerChapterUI: ReaderChapterUI): LiveData<HResult<String>> {
		if (hashMap.containsKey(readerChapterUI.id)) {
			Log.d(logID(), "Loading existing live data for ${readerChapterUI.id}")
			return hashMap[readerChapterUI.id] as LiveData<HResult<String>>
		}

		Log.d(logID(), "Creating a new live data for ${readerChapterUI.id}")
		val data = MutableLiveData<HResult<String>>()
		hashMap[readerChapterUI.id] = data
		launchIO {
			data.postValue(loading())
			Log.d(logID(), "Loading ${readerChapterUI.link}")
			val v = loadChapterPassageUseCase(readerChapterUI)
			Log.d(logID(), "I got a ${v.javaClass.simpleName}")
			data.postValue(v)
		}
		return data
	}

	override fun appendID(readerChapterUI: ReaderChapterUI): String =
			"${readerChapterUI.id}|${readerChapterUI.link}"

	override fun toggleBookmark(readerChapterUI: ReaderChapterUI) {
		updateChapter(readerChapterUI, bookmarked = !readerChapterUI.bookmarked)
	}

	override fun updateChapter(
			readerChapterUI: ReaderChapterUI,
			readingPosition: Int,
			readingStatus: ReadingStatus,
			bookmarked: Boolean,
	) {
		launchIO {
			updateReaderChapterUseCase(readerChapterUI.copy(
					readingPosition = readingPosition,
					readingStatus = readingStatus,
					bookmarked = bookmarked
			))
		}
	}

	private fun markAsReading(
			chapterUI: ReaderChapterUI,
			markingTypes: MarkingTypes,
			readingPosition: Int = chapterUI.readingPosition
	) {
		launchIO {
			iSettingsRepository.getBoolean(ReaderMarkReadAsReading).handle { markReadAsReading ->
				if (!markReadAsReading && chapterUI.readingStatus == READ) return@launchIO
				iSettingsRepository.getString(ReadingMarkingType).handle {
					if (MarkingTypes.valueOf(it) == markingTypes) updateChapter(chapterUI.copy(
							readingStatus = READING, readingPosition = readingPosition
					))
				}
			}
		}
	}

	override fun markAsReadingOnView(readerChapterUI: ReaderChapterUI) {
		markAsReading(readerChapterUI, ONVIEW)
	}

	override fun markAsReadingOnScroll(readerChapterUI: ReaderChapterUI, yAswell: Int) {
		markAsReading(readerChapterUI, ONSCROLL, yAswell)
	}

	override fun allowVolumeScroll(): Boolean = volumeScroll

	override fun setOnVolumeScroll(checked: Boolean) {
		volumeScroll = checked
		launchIO {
			iSettingsRepository.setBoolean(ReaderVolumeScroll, checked)
		}
	}
}