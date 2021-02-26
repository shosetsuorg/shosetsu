package app.shosetsu.android.viewmodel.impl

import android.app.Application
import android.graphics.Color
import android.widget.ArrayAdapter
import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import app.shosetsu.android.common.dto.*
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.ReportExceptionUseCase
import app.shosetsu.android.domain.model.local.ColorChoiceData
import app.shosetsu.android.domain.usecases.get.GetChapterPassageUseCase
import app.shosetsu.android.domain.usecases.get.GetReaderChaptersUseCase
import app.shosetsu.android.domain.usecases.get.GetReaderSettingUseCase
import app.shosetsu.android.domain.usecases.load.LoadReaderThemes
import app.shosetsu.android.domain.usecases.update.UpdateReaderChapterUseCase
import app.shosetsu.android.domain.usecases.update.UpdateReaderSettingUseCase
import app.shosetsu.android.view.uimodels.model.ColorChoiceUI
import app.shosetsu.android.view.uimodels.model.reader.ReaderChapterUI
import app.shosetsu.android.view.uimodels.model.reader.ReaderDividerUI
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.view.uimodels.settings.dsl.*
import app.shosetsu.android.viewmodel.abstracted.IChapterReaderViewModel
import app.shosetsu.android.viewmodel.base.spinnerValue
import app.shosetsu.android.viewmodel.impl.settings.*
import app.shosetsu.common.consts.settings.SettingKey.*
import app.shosetsu.common.domain.model.local.NovelReaderSettingEntity
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.domain.repositories.base.getStringSetOrDefault
import app.shosetsu.common.dto.*
import app.shosetsu.common.enums.MarkingTypes
import app.shosetsu.common.enums.MarkingTypes.ONSCROLL
import app.shosetsu.common.enums.MarkingTypes.ONVIEW
import app.shosetsu.common.enums.ReadingStatus
import app.shosetsu.common.enums.ReadingStatus.READ
import app.shosetsu.common.enums.ReadingStatus.READING
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

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
 *
 * TODO delete previous chapter
 */
class ChapterReaderViewModel(
	private val application: Application,
	override val settingsRepo: ISettingsRepository,
	private val loadReaderChaptersUseCase: GetReaderChaptersUseCase,
	private val loadChapterPassageUseCase: GetChapterPassageUseCase,
	private val updateReaderChapterUseCase: UpdateReaderChapterUseCase,
	private val loadReadersThemes: LoadReaderThemes,
	private val reportExceptionUseCase: ReportExceptionUseCase,
	private val getReaderSettingsUseCase: GetReaderSettingUseCase,
	private val updateReaderSettingUseCase: UpdateReaderSettingUseCase
) : IChapterReaderViewModel() {
	private val isReaderContinuousScrollFlow = settingsRepo.getBooleanFlow(ReaderContinuousScroll)
	private val convertStringToHtml = settingsRepo.getBooleanFlow(ReaderStringToHtml)
	private val isHorizontalPageSwapping = settingsRepo.getBooleanFlow(ReaderHorizontalPageSwap)

	/**
	 * TODO Memory management here
	 *
	 * ChapterID to the data flow for it
	 */
	private val hashMap: HashMap<Int, Flow<HResult<String>>> = hashMapOf()

	@ExperimentalCoroutinesApi
	override val liveData: LiveData<HList<ReaderUIItem<*, *>>> by lazy {
		novelIDLive.transformLatest { nId ->
			loadReaderChaptersUseCase(nId).mapLatestResult {
				withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
					successResult(ArrayList<ReaderUIItem<*, *>>(it).apply {
						// Adds the "No more chapters" marker
						add(size, ReaderDividerUI(prev = it.last().title))

						/**
						 * Loops down the list, adding in betweens
						 */
						val startPoint = size - 2
						for (index in startPoint downTo 1)
							add(
								index, ReaderDividerUI(
									(this[index - 1] as ReaderChapterUI).title,
									(this[index] as ReaderChapterUI).title
								)
							)

					})
				}
			}.let { emitAll(it) }
		}.asIOLiveData()
	}

	@ExperimentalCoroutinesApi
	private val readerSettingsFlow: Flow<HResult<NovelReaderSettingEntity>> by lazy {
		novelIDLive.transformLatest {
			emitAll(getReaderSettingsUseCase(it))
		}
	}

	@ExperimentalCoroutinesApi
	override val liveTheme: LiveData<Pair<Int, Int>> by lazy {
		settingsRepo.getIntFlow(ReaderTheme).transformLatest { id: Int ->
			settingsRepo.getStringSetOrDefault(ReaderUserThemes)
				.map { ColorChoiceData.fromString(it) }
				.find { it.identifier == id.toLong() }
				?.let { (_, _, textColor, backgroundColor) ->
					super.defaultForeground = textColor
					super.defaultBackground = backgroundColor

					emit(textColor to backgroundColor)
				} ?: emit(Color.BLACK to Color.WHITE)

		}.asIOLiveData()
	}
	override val liveMarkingTypes: LiveData<MarkingTypes> by lazy {
		settingsRepo.getStringFlow(ReadingMarkingType)
			.asIOLiveData()
			.map { MarkingTypes.valueOf(it) }
	}

	@ExperimentalCoroutinesApi
	override val liveThemes: LiveData<List<ColorChoiceUI>> by lazy {
		loadReadersThemes().asIOLiveData()
	}
	override val liveIndentSize: LiveData<Int> by lazy {
		settingsRepo.getIntFlow(ReaderIndentSize).asIOLiveData()
	}
	override val liveParagraphSpacing: LiveData<Float> by lazy {
		settingsRepo.getFloatFlow(ReaderParagraphSpacing).asIOLiveData()
	}
	override val liveTextSize: LiveData<Float> by lazy {
		settingsRepo.getFloatFlow(ReaderTextSize).asIOLiveData()
	}
	override val liveVolumeScroll: LiveData<Boolean> by lazy {
		settingsRepo.getBooleanFlow(ReaderVolumeScroll).asIOLiveData()
	}
	override var currentChapterID: Int = -1
	private val novelIDLive: MutableStateFlow<Int> by lazy { MutableStateFlow(novelIDValue) }
	private var novelIDValue = -1
	override val liveChapterDirection: LiveData<Boolean> =
		settingsRepo.getBooleanFlow(ReaderHorizontalPageSwap).asIOLiveData()

	init {
		launchIO {
			isReaderContinuousScrollFlow.collectLatest {
			}
			convertStringToHtml.collectLatest {
				super.convertStringAsHtml = it
			}
			isHorizontalPageSwapping.collectLatest {
				super.isHorizontalReading = it
			}
		}
	}

	override fun reportError(error: HResult.Error, isSilent: Boolean) {
		reportExceptionUseCase(error)
	}

	override fun setReaderTheme(value: Int) {
		launchIO { settingsRepo.setInt(ReaderTheme, value) }
	}

	override fun setReaderIndentSize(value: Int) {
		launchIO {
			logI("setting")
			settingsRepo.setInt(ReaderIndentSize, value)
		}
	}

	override fun setReaderParaSpacing(value: Float) {
		launchIO { settingsRepo.setFloat(ReaderParagraphSpacing, value) }
	}

	override fun setReaderTextSize(value: Float) {
		launchIO { settingsRepo.setFloat(ReaderTextSize, value) }
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

	@WorkerThread
	override fun getChapterPassage(readerChapterUI: ReaderChapterUI): LiveData<HResult<String>> {
		if (hashMap.containsKey(readerChapterUI.id))
			return hashMap[readerChapterUI.id]!!.asIOLiveData() as LiveData<HResult<String>>

		return flow {
			emit(loading())
			emit(loadChapterPassageUseCase(readerChapterUI))
		}.also {
			hashMap[readerChapterUI.id] = it
		}.asIOLiveData()
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
			updateReaderChapterUseCase(
				readerChapterUI.copy(
					readingPosition = readingPosition,
					readingStatus = readingStatus,
					bookmarked = bookmarked
				)
			)
		}
	}

	private fun markAsReading(
		chapterUI: ReaderChapterUI,
		markingTypes: MarkingTypes,
		readingPosition: Int = chapterUI.readingPosition
	) {
		launchIO {
			settingsRepo.getBoolean(ReaderMarkReadAsReading).handle { markReadAsReading ->
				if (!markReadAsReading && chapterUI.readingStatus == READ) return@launchIO
				settingsRepo.getString(ReadingMarkingType).handle {
					if (MarkingTypes.valueOf(it) == markingTypes) updateChapter(
						chapterUI.copy(
							readingStatus = READING, readingPosition = readingPosition
						)
					)
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

	override fun setOnVolumeScroll(checked: Boolean) {
		launchIO {
			settingsRepo.setBoolean(ReaderVolumeScroll, checked)
		}
	}

	override fun loadChapterCss(): LiveData<String> =
		settingsRepo.getStringFlow(ReaderHtmlCss).asIOLiveData()

	override fun updateConvertStringAsHtml(checked: Boolean) {
		launchIO {
			settingsRepo.setBoolean(ReaderStringToHtml, checked)
		}
	}

	override fun updateHorizontalReading(checked: Boolean) {
		launchIO {
			settingsRepo.setBoolean(ReaderHorizontalPageSwap, checked)
		}
	}

	@ExperimentalCoroutinesApi
	override fun getSettings(): LiveData<HResult<List<SettingsItemData>>> =
		flow {
			// First build the universal setting interface
			emit(loading)
			emit(successResult(settings()))
		}.combine(readerSettingsFlow) { settingsListResult, readerSettingsResult ->
			/*
			 * Combining the universal setting flow and the readerSettingFlow
			 * Handle both results together, then transform the result, adding UI for reader specific settings
			 */
			(settingsListResult thenAlso readerSettingsResult).transformToSuccess { (settingsList, settingEntity) ->
				ArrayList(settingsList).apply {
					add(floatButtonSettingData(1) {
						title { R.string.paragraph_spacing }
						minWhole = 0

						settingEntity.paragraphSpacingSize.let { settingValue ->
							initialWhole =
								wholeSteps.indexOfFirst { it == settingValue.toInt() }.orZero()
							val decimal: Int = ((settingValue % 1) * 100).toInt()
							initialDecimal = decimalSteps.indexOfFirst { it == decimal }.orZero()
						}
						onValueSelected { selected ->
							launchIO {
								updateReaderSettingUseCase(
									settingEntity.copy(
										paragraphSpacingSize = selected.toFloat()
									)
								)
							}
						}
					})
					add(spinnerSettingData(2) {
						val context = application.applicationContext
						title { R.string.paragraph_indent }
						arrayAdapter = ArrayAdapter(
							context,
							android.R.layout.simple_spinner_dropdown_item,
							context.resources!!.getStringArray(R.array.sizes_with_none)
						)

						spinnerValue { settingEntity.paragraphIndentSize }
						onSpinnerItemSelected { _, _, position, _ ->
							launchIO {
								updateReaderSettingUseCase(
									settingEntity.copy(
										paragraphIndentSize = position
									)
								)
							}
						}
					})

					// Sort so the result will be ordered properly
					sortBy { it.id }
				}
			}
		}.asIOLiveData()

	suspend fun settings(): List<SettingsItemData> = listOf(
		// Quick settings
		textSizeOption(0),
		//==paragraph indent and spacing here

		// Minor Behavior settings, these wont effect the UI too much
		tapToScrollOption(3),
		volumeScrollingOption(4),
		horizontalSwitchOption(5),
		continuousScrollOption(6),
		invertChapterSwipeOption(8),

		// Major changes
		stringAsHtmlOption(7),
	)
}