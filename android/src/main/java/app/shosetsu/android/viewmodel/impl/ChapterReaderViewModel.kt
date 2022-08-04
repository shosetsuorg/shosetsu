package app.shosetsu.android.viewmodel.impl

import android.database.sqlite.SQLiteException
import android.graphics.Color
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.lifecycle.viewModelScope
import app.shosetsu.android.common.SettingKey.*
import app.shosetsu.android.common.enums.AppThemes
import app.shosetsu.android.common.enums.MarkingType
import app.shosetsu.android.common.enums.MarkingType.ONSCROLL
import app.shosetsu.android.common.enums.MarkingType.ONVIEW
import app.shosetsu.android.common.enums.ReadingStatus.READ
import app.shosetsu.android.common.enums.ReadingStatus.READING
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.common.utils.asHtml
import app.shosetsu.android.common.utils.copy
import app.shosetsu.android.domain.model.local.ColorChoiceData
import app.shosetsu.android.domain.model.local.NovelReaderSettingEntity
import app.shosetsu.android.domain.repository.base.IChaptersRepository
import app.shosetsu.android.domain.repository.base.INovelReaderSettingsRepository
import app.shosetsu.android.domain.repository.base.INovelsRepository
import app.shosetsu.android.domain.repository.base.ISettingsRepository
import app.shosetsu.android.domain.usecases.RecordChapterIsReadUseCase
import app.shosetsu.android.domain.usecases.RecordChapterIsReadingUseCase
import app.shosetsu.android.domain.usecases.get.GetChapterPassageUseCase
import app.shosetsu.android.domain.usecases.get.GetExtensionUseCase
import app.shosetsu.android.domain.usecases.get.GetReaderChaptersUseCase
import app.shosetsu.android.domain.usecases.get.GetReaderSettingUseCase
import app.shosetsu.android.domain.usecases.load.LoadLiveAppThemeUseCase
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem.ReaderChapterUI
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem.ReaderDividerUI
import app.shosetsu.android.viewmodel.abstracted.AChapterReaderViewModel
import app.shosetsu.lib.IExtension
import app.shosetsu.lib.Novel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.plus
import org.acra.ACRA
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

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
@OptIn(ExperimentalCoroutinesApi::class)
class ChapterReaderViewModel(
	override val settingsRepo: ISettingsRepository,
	private val chapterRepository: IChaptersRepository,
	private val novelRepo: INovelsRepository,
	private val readerSettingsRepo: INovelReaderSettingsRepository,
	private var loadLiveAppThemeUseCase: LoadLiveAppThemeUseCase,
	private val loadReaderChaptersUseCase: GetReaderChaptersUseCase,
	private val loadChapterPassageUseCase: GetChapterPassageUseCase,
	private val getReaderSettingsUseCase: GetReaderSettingUseCase,
	private val recordChapterIsReading: RecordChapterIsReadingUseCase,
	private val recordChapterIsRead: RecordChapterIsReadUseCase,
	private val getExt: GetExtensionUseCase
) : AChapterReaderViewModel() {
	override val appThemeLiveData: Flow<AppThemes> by lazy {
		loadLiveAppThemeUseCase()
	}

	private val isHorizontalPageSwapping by lazy {
		settingsRepo.getBooleanFlow(ReaderHorizontalPageSwap)
	}

	private val indentSizeFlow: Flow<Int> by lazy {
		readerSettingsFlow.mapLatest { result ->
			result.paragraphIndentSize
		}
	}

	private val paragraphSpacingFlow: Flow<Float> by lazy {
		readerSettingsFlow.mapLatest { result ->
			result.paragraphSpacingSize
		}
	}

	private val doubleTapSystemFlow: Flow<Boolean> by lazy {
		settingsRepo.getBooleanFlow(ReaderDoubleTapSystem)
	}

	/**
	 * Lets explain what goes on here
	 *
	 * Say the User reads a chapter,
	 * the action that is then taken by the code is to mark the chapter as read and 0 it out.
	 * But when it 0s out the progress, and the user refreshes the UI, the user sees the UI reset.
	 *
	 * The user will view this as an "error" because they expect things to remain the way they left
	 * it while reading. (Object permanence).
	 *
	 * To correct this,
	 */
	private val progressMapFlow by lazy { MutableStateFlow(HashMap<Int, Double>()) }


	override var ttsPitch: Float = 0.0f

	private val stringMap by lazy { HashMap<Int, Flow<ChapterPassage>>() }
	private val refreshMap by lazy { HashMap<Int, MutableStateFlow<Boolean>>() }

	override val isFirstFocusFlow: Flow<Boolean> by lazy {
		settingsRepo.getBooleanFlow(ReaderIsFirstFocus).onIO()
	}

	override val isSwipeInverted: Flow<Boolean> by lazy {
		settingsRepo.getBooleanFlow(ReaderIsInvertedSwipe).onIO()
	}

	override fun onFirstFocus() {
		//logV("")
		launchIO {
			settingsRepo.setBoolean(ReaderIsFirstFocus, false)
		}
	}

	/**
	 * Trim out the strings present around the current page
	 *
	 * Ensures there is only 3~ flows at a time in memory
	 */
	private fun cleanStringMap(currentIndex: Int) {
		val excludedKeys = arrayListOf<Int>()
		val keys = stringMap.keys.toList()

		excludedKeys.add(keys[currentIndex])

		for (i in 1..3) {
			keys.getOrNull(currentIndex - i)?.let {
				excludedKeys.add(it)
			}
			keys.getOrNull(currentIndex + i)?.let {
				excludedKeys.add(it)
			}
		}

		keys.filterNot { excludedKeys.contains(it) }.forEach { key ->
			stringMap.remove(key)
		}
	}

	/**
	 * Clear all maps
	 */
	@Suppress("NOTHING_TO_INLINE") // We need every ns
	private inline fun clearMaps() {
		stringMap.clear()
	}

	@Suppress("NOTHING_TO_INLINE") // We need every ns
	private inline fun getRefreshFlow(item: ReaderChapterUI) =
		refreshMap.getOrPut(item.id) { MutableStateFlow(false) }

	override fun retryChapter(item: ReaderChapterUI) {
		//logV("$item")
		val flow = getRefreshFlow(item)
		flow.value = !flow.value
	}

	private var cleanStringMapJob: Job? = null

	override fun getChapterStringPassage(item: ReaderChapterUI): Flow<ChapterPassage> {
		//logV("$item")
		val mutableFlow = stringMap.getOrPut(item.id) {
			flow {
				emit(ChapterPassage.Loading)
				emitAll(
					getRefreshFlow(item).transformLatest {

						val bytes = try {
							getChapterPassage(item)
						} catch (e: Exception) {
							emit(ChapterPassage.Error(e))
							return@transformLatest
						}

						if (bytes == null) {
							emit(ChapterPassage.Error(Exception("No content received")))
							return@transformLatest
						}

						emitAll(
							indentSizeFlow.combine(
								paragraphSpacingFlow
							) { indentSize, paragraphSpacing ->
								val unformattedText = bytes.decodeToString()

								val replaceSpacing = StringBuilder("\n")
								// Calculate changes to \n
								for (x in 0 until paragraphSpacing.toInt())
									replaceSpacing.append("\n")

								// Calculate changes to \t
								for (x in 0 until indentSize)
									replaceSpacing.append("\t")

								// Set new text formatted
								ChapterPassage.Success(
									unformattedText.replace(
										"\n".toRegex(),
										replaceSpacing.toString()
									)
								)
							}
						)
					}
				)
			}.shareIn(viewModelScope + Dispatchers.IO, SharingStarted.Lazily, 1)
		}

		if (cleanStringMapJob == null && stringMap.size > 10) {
			cleanStringMapJob =
				launchIO {
					cleanStringMap(stringMap.keys.indexOf(item.id))
					cleanStringMapJob = null
				}
		}

		return mutableFlow.onIO()
	}

	override fun getChapterHTMLPassage(item: ReaderChapterUI): Flow<ChapterPassage> {
		val mutableFlow = stringMap.getOrPut(item.id) {
			flow {
				emit(ChapterPassage.Loading)
				emitAll(
					getRefreshFlow(item).transformLatest {
						val bytes = try {
							getChapterPassage(item)
						} catch (e: Exception) {
							emit(ChapterPassage.Error(e))
							return@transformLatest
						}

						if (bytes == null) {
							emit(ChapterPassage.Error(Exception("No content received")))
							return@transformLatest
						}

						var result = bytes.decodeToString()

						@Suppress("DEPRECATION")
						val convert = convertStringToHtml.firstOrNull() ?: false
						val chapterType = extensionChapterTypeFlow.firstOrNull()

						if (chapterType == Novel.ChapterType.STRING && convert) {
							result = asHtml(result, item.title)
						}

						val document = Jsoup.parse(result)

						emitAll(
							shosetsuCss.combine(userCssFlow) { shoCSS, useCSS ->
								fun update(id: String, css: String) {
									var style: Element? = document.getElementById(id)

									if (style == null) {
										style =
											document.createElement("style") ?: return

										style.id(id)
										style.attr("type", "text/css")

										document.head().appendChild(style)
									}

									style.text(css)
								}

								update("shosetsu-style", shoCSS)
								update("user-style", useCSS)

								ChapterPassage.Success(
									document.toString()
								)
							}
						)
					}
				)
			}.shareIn(viewModelScope + Dispatchers.IO, SharingStarted.Lazily, 1)
		}

		if (cleanStringMapJob == null && stringMap.size > 10) {
			cleanStringMapJob =
				launchIO {
					cleanStringMap(stringMap.keys.indexOf(item.id))
					cleanStringMapJob = null
				}
		}

		return mutableFlow.onIO()
	}

	override val isCurrentChapterBookmarked: Flow<Boolean> by lazy {
		currentChapterID.transformLatest { id ->
			emitAll(
				chapterRepository.getChapterBookmarkedFlow(id).map {
					it ?: false
				}
			)
		}.onIO()
	}

	private val extFlow: Flow<IExtension?> by lazy {
		novelIDLive.mapLatest { id ->
			val novel = novelRepo.getNovel(id) ?: return@mapLatest null
			getExt(novel.extensionID)
		}
	}

	private val convertStringToHtml by lazy {
		settingsRepo.getBooleanFlow(ReaderStringToHtml).onIO()
	}

	private val extensionChapterTypeFlow: Flow<Novel.ChapterType?> by lazy {
		extFlow.map { it?.chapterType }
			.shareIn(viewModelScope + Dispatchers.IO, SharingStarted.Lazily, 1)
	}

	/**
	 * Specifies what chapter type the reader should render.
	 *
	 * Upon [ReaderStringToHtml] being true, will clear out any previous strings if the prevType was
	 * not html, causing the content to regenerate.
	 */
	override val chapterType: Flow<Novel.ChapterType?> by lazy {
		extensionChapterTypeFlow.transformLatest { type ->
			emit(null)
			if (type == null) return@transformLatest
			var prevType: Novel.ChapterType? = null

			emitAll(
				convertStringToHtml.transformLatest { convert ->
					@Suppress("DEPRECATION")
					if (convert && type == Novel.ChapterType.STRING) {
						if (prevType != Novel.ChapterType.HTML)
							clearMaps()

						prevType = Novel.ChapterType.HTML
						emit(Novel.ChapterType.HTML)
					} else {
						if (prevType != type)
							clearMaps()

						prevType = type
						emit(type)
					}
				}
			)
		}.shareIn(viewModelScope + Dispatchers.IO, SharingStarted.Lazily, 1)
	}

	override var ttsSpeed: Float = 0.0f

	private val chaptersFlow: Flow<List<ReaderChapterUI>> by lazy {
		novelIDLive.transformLatest { nId ->
			System.gc() // Run GC to try and mitigate OOM
			emitAll(loadReaderChaptersUseCase(nId))
		}.shareIn(viewModelScope + Dispatchers.IO, SharingStarted.Lazily, 1)
	}

	override fun getChapterProgress(chapter: ReaderChapterUI): Flow<Double> =
		flow {
			emitAll(
				progressMapFlow.transformLatest { progressMap ->
					if (progressMap.containsKey(chapter.id))
						emit(progressMap[chapter.id]!!)
					else
						emitAll(chapterRepository.getChapterProgress(chapter.convertTo()))
				}
			)
		}.onIO()

	override val liveData: Flow<List<ReaderUIItem>> by lazy {
		chaptersFlow
			.combineDividers() // Add dividers
			// todo maybe replace with .distinctUntilChanged()
			.shareIn(viewModelScope + Dispatchers.IO, SharingStarted.Lazily, 1)
			.onIO()
	}

	override val currentPage: MutableStateFlow<Int> = MutableStateFlow(0)

	private fun Flow<List<ReaderChapterUI>>.combineDividers(): Flow<List<ReaderUIItem>> =
		combine(settingsRepo.getBooleanFlow(ReaderShowChapterDivider)) { list, value ->
			if (value && list.isNotEmpty()) {
				val modified = ArrayList<ReaderUIItem>(list)
				// Adds the "No more chapters" marker
				modified.add(modified.size, ReaderDividerUI(prev = list.last()))

				/**
				 * Loops down the list, adding in the seperators
				 */
				val startPoint = modified.size - 2
				for (index in startPoint downTo 1)
					modified.add(
						index, ReaderDividerUI(
							(modified[index - 1] as ReaderChapterUI),
							(modified[index] as ReaderChapterUI)
						)
					)

				modified
			} else {
				list
			}
		}

	override fun setCurrentPage(page: Int) {
		//logV("$page")
		currentPage.value = page
	}

	private val readerSettingsFlow: Flow<NovelReaderSettingEntity> by lazy {
		novelIDLive.transformLatest {
			emitAll(getReaderSettingsUseCase(it))
		}
	}

	private val themeFlow: Flow<Pair<Int, Int>> by lazy {
		settingsRepo.getIntFlow(ReaderTheme).transformLatest { id: Int ->
			settingsRepo.getStringSet(ReaderUserThemes)
				.map { ColorChoiceData.fromString(it) }
				.find { it.identifier == id.toLong() }
				?.let { (_, _, textColor, backgroundColor) ->
					emit(textColor to backgroundColor)
				} ?: emit(Color.BLACK to Color.WHITE)

		}
	}

	override val textColor: Flow<Int> by lazy {
		themeFlow.map { it.first }.onIO()
	}

	override val backgroundColor: Flow<Int> by lazy {
		themeFlow.map { it.second }.onIO()
	}

	private val textSizeFlow by lazy {
		settingsRepo.getFloatFlow(ReaderTextSize)
	}

	override val liveTextSize: Flow<Float> by lazy {
		textSizeFlow.onIO()
	}

	override val liveKeepScreenOn: Flow<Boolean> by lazy {
		settingsRepo.getBooleanFlow(ReaderKeepScreenOn).onIO()
	}

	override var currentChapterID: MutableStateFlow<Int> = MutableStateFlow(-1)

	private val novelIDLive: MutableStateFlow<Int> by lazy { MutableStateFlow(-1) }

	private var _defaultVolumeScroll: Boolean = ReaderVolumeScroll.default

	private var _isHorizontalReading: Boolean = ReaderHorizontalPageSwap.default

	override val isVolumeScrollEnabled: Boolean
		get() = _defaultVolumeScroll

	override val isHorizontalReading: Flow<Boolean> by lazy {
		isHorizontalPageSwapping.mapLatest {
			_isHorizontalReading = it
			it
		}.onIO()
	}

	override fun setNovelID(novelID: Int) {
		//logV("novelID=$novelID")
		when {
			novelIDLive.value == -1 -> {
				//logD("Setting NovelID")
			}
			novelIDLive.value != novelID -> {
				//logD("NovelID not equal, resetting")
			}
			novelIDLive.value == novelID -> {
				//logD("NovelID equal, ignoring")
				return
			}
		}
		novelIDLive.value = novelID
	}

	@Suppress("NOTHING_TO_INLINE") // We need every ns
	private suspend inline fun getChapterPassage(readerChapterUI: ReaderChapterUI): ByteArray? =
		loadChapterPassageUseCase(readerChapterUI)

	override fun toggleBookmark() {
		launchIO {
			val id = currentChapterID.first()
			val chapter = chapterRepository.getChapter(id) ?: return@launchIO

			chapterRepository.updateChapter(
				chapter.copy(
					bookmarked = !chapter.bookmarked
				)
			)
		}
	}

	override fun updateChapterAsRead(chapter: ReaderChapterUI) {
		launchIO {
			recordChapterIsRead(chapter)
			try {
				chapterRepository.getChapter(chapter.id)?.let {
					chapterRepository.updateChapter(
						it.copy(
							readingStatus = READ,
							readingPosition = 0.0
						)
					)
				}
			} catch (e: SQLiteException) {
				logE("Failed to update chapter as read", e)
				ACRA.errorReporter.handleSilentException(e)
			}
		}
	}

	private val readingMarkingTypeFlow by lazy {
		settingsRepo.getStringFlow(ReadingMarkingType).map {
			MarkingType.valueOf(it)
		}
	}

	override fun onViewed(chapter: ReaderChapterUI) {
		//logV("$chapter")
		launchIO {
			settingsRepo.getBoolean(ReaderMarkReadAsReading).let { markReadAsReading ->
				val chapterEntity = chapterRepository.getChapter(chapter.id) ?: return@launchIO
				/*
				 * If marking chapters that are read as reading is disabled
				 * and the chapter's readingStatus is read, return to prevent further IO.
				 */
				if (!markReadAsReading && chapterEntity.readingStatus == READ) return@launchIO

				/*
				 * If the reading marking type does not equal on view, then return
				 */
				if (readingMarkingTypeFlow.first() != ONVIEW) return@launchIO

				recordChapterIsReading(chapter)

				chapterRepository.updateChapter(
					chapterEntity.copy(readingStatus = READING)
				)
			}
		}
	}

	override fun onScroll(chapter: ReaderChapterUI, readingPosition: Double) {
		launchIO {
			val chapterEntity = chapterRepository.getChapter(chapter.id) ?: return@launchIO

			// If the chapter reaches 90% read, we can assume the reader already sees it all :P
			if (readingPosition <= 0.90) {
				settingsRepo.getBoolean(ReaderMarkReadAsReading).let { markReadAsReading ->
					/**
					 * If marking chapters that are read as reading is disabled
					 * and the chapter's readingStatus is read, save progress temporarily.
					 */
					if (!markReadAsReading && chapterEntity.readingStatus == READ) {
						progressMapFlow.value = progressMapFlow.value.copy().apply {
							put(chapter.id, readingPosition)
						}
						return@launchIO
					}

					/*
							 * If marking type is on scroll, record as reading
							 */
					val markingType = readingMarkingTypeFlow.first()
					if (markingType == ONSCROLL) {
						recordChapterIsReading(chapter)
					}

					// Remove temp progress
					progressMapFlow.value = progressMapFlow.value.copy().apply {
						remove(chapter.id)
					}

					chapterRepository.updateChapter(
						chapterEntity.copy(
							readingStatus = if (markingType == ONSCROLL) {
								READING
							} else chapterEntity.readingStatus,
							readingPosition = readingPosition
						)
					)
				}
			} else {
				// User probably sees everything at this point

				recordChapterIsRead(chapter)

				// Temp remember the progress
				progressMapFlow.value = progressMapFlow.value.copy().apply {
					put(chapter.id, readingPosition)
				}

				chapterRepository.updateChapter(
					chapterEntity.copy(
						readingStatus = READ,
						readingPosition = 0.0
					)
				)
			}
		}
	}

	override fun loadChapterCss(): Flow<String> =
		settingsRepo.getStringFlow(ReaderHtmlCss)

	override fun updateSetting(novelReaderSettingEntity: NovelReaderSettingEntity) {
		launchIO {
			readerSettingsRepo.update(novelReaderSettingEntity)
		}
	}

	override fun getSettings(): Flow<NovelReaderSettingEntity> =
		readerSettingsFlow.onIO()

	private val isScreenRotationLockedFlow = MutableStateFlow(false)

	override val tapToScroll: Flow<Boolean> by lazy {
		settingsRepo.getBooleanFlow(ReaderIsTapToScroll).onIO()
	}

	private val doubleTapFocus: Flow<Boolean> by lazy {
		settingsRepo.getBooleanFlow(ReaderDoubleTapFocus).onIO()
	}

	override val isFocused: MutableStateFlow<Boolean> = MutableStateFlow(false)
	override val isSystemVisible: MutableStateFlow<Boolean> = MutableStateFlow(true)

	override fun toggleFocus() {
		isFocused.value = !isFocused.value
	}

	override fun toggleSystemVisible() {
		isFocused.value = isSystemVisible.value
		isSystemVisible.value = !isSystemVisible.value
	}

	override fun onReaderClicked() {
		launchIO {
			if (!doubleTapFocus.first()) {
				val newValue = !isFocused.value
				isFocused.value = newValue
				if (newValue)
					isSystemVisible.value = false
			}
		}
	}

	override fun onReaderDoubleClicked() {
		launchIO {
			if (doubleTapFocus.first()) {
				val newValue = !isFocused.value
				isFocused.value = newValue
				if (newValue)
					isSystemVisible.value = false
			} else if (doubleTapSystemFlow.first()) {
				toggleSystemVisible()
			}
		}
	}

	private val userCssFlow: Flow<String> by lazy {
		settingsRepo.getStringFlow(ReaderHtmlCss).onIO()
	}

	data class ShosetsuCSSBuilder(
		val backgroundColor: Int = Color.WHITE,
		val foregroundColor: Int = Color.BLACK,
		val textSize: Float = ReaderTextSize.default,
		val indentSize: Int = ReaderIndentSize.default,
		val paragraphSpacing: Float = ReaderParagraphSpacing.default
	)

	private val shosetsuCss: Flow<String> by lazy {
		themeFlow.combine(liveTextSize) { (fore, back), textSize ->
			ShosetsuCSSBuilder(
				backgroundColor = back,
				foregroundColor = fore,
				textSize = textSize
			)
		}.combine(indentSizeFlow) { builder, indent ->
			builder.copy(
				indentSize = indent
			)
		}.combine(paragraphSpacingFlow) { builder, space ->
			builder.copy(
				paragraphSpacing = space
			)
		}.map {
			val shosetsuStyle: HashMap<String, HashMap<String, String>> = hashMapOf()

			fun setShosetsuStyle(elem: String, action: HashMap<String, String>.() -> Unit) =
				shosetsuStyle.getOrPut(elem) { hashMapOf() }.apply(action)

			fun Int.cssColor(): String = "rgb($red,$green,$blue)"

			setShosetsuStyle("body") {
				this["background-color"] = it.backgroundColor.cssColor()
				this["color"] = it.foregroundColor.cssColor()
				this["font-size"] = "${it.textSize / HTML_SIZE_DIVISION}pt"
				this["scroll-behavior"] = "smooth"
				this["text-indent"] = "${it.indentSize}em"
				this["overflow-wrap"] = "break-word"
			}

			setShosetsuStyle("p") {
				this["margin-top"] = "${it.paragraphSpacing}em"
			}

			setShosetsuStyle("img") {
				this["max-width"] = "100%"
				this["height"] = "initial !important"
			}

			shosetsuStyle.map { elem ->
				"${elem.key} {" + elem.value.map { rule -> "${rule.key}:${rule.value}" }
					.joinToString(";", postfix = ";") + "}"
			}.joinToString("")
		}.onIO()
	}

	init {
		launchIO {
			settingsRepo.getBooleanFlow(ReaderVolumeScroll).collect {
				_defaultVolumeScroll = it
			}
		}
		launchIO {
			settingsRepo.getFloatFlow(ReaderPitch).collect {
				ttsPitch = it
			}
		}
		launchIO {
			settingsRepo.getFloatFlow(ReaderSpeed).collect {
				ttsSpeed = it
			}
		}
	}

	override val liveIsScreenRotationLocked: Flow<Boolean>
		get() = isScreenRotationLockedFlow.onIO()

	override fun toggleScreenRotationLock() {
		isScreenRotationLockedFlow.value = !isScreenRotationLockedFlow.value
	}

	override fun setCurrentChapterID(chapterId: Int, initial: Boolean) {
		//logV("$chapterId, $initial")
		currentChapterID.value = chapterId

		if (initial)
			launchIO {
				val items = liveData.first()
				currentPage.value = items
					.indexOfFirst { it is ReaderChapterUI && it.id == chapterId }
			}
	}

	override fun incrementProgress() {
		launchIO {

			val chapterId = currentChapterID.first()

			val chapter = chaptersFlow.first().find { it.id == chapterId } ?: return@launchIO
			val chapterEntity = chapterRepository.getChapter(chapter.id) ?: return@launchIO

			/*
			 * Increment 5% at a time, let us hope this does not back fire
			 */
			if ((chapterEntity.readingPosition + INCREMENT_PERCENTAGE) < 1)
				onScroll(chapter, chapterEntity.readingPosition + INCREMENT_PERCENTAGE)
		}
	}

	override fun depleteProgress() {
		launchIO {
			val chapterId = currentChapterID.first()

			val chapter = chaptersFlow.first().find { it.id == chapterId } ?: return@launchIO
			val chapterEntity = chapterRepository.getChapter(chapter.id) ?: return@launchIO

			/*
			 * Increment 5% at a time, let us hope this does not back fire
			 */
			if ((chapterEntity.readingPosition - INCREMENT_PERCENTAGE) > 0)
				onScroll(chapter, chapterEntity.readingPosition - INCREMENT_PERCENTAGE)
		}
	}

	override fun clearMemory() {
		logV("Application called to clear memory")
		launchIO {
			run {
				val excludedKeys = arrayListOf<Int>()
				val keys = stringMap.keys.toList()
				val currentChapter = currentChapterID.value

				excludedKeys.add(currentChapter)

				keys.filterNot { excludedKeys.contains(it) }.forEach { key ->
					stringMap.remove(key)
				}
			}

			run {
				val excludedKeys = arrayListOf<Int>()
				val map = progressMapFlow.value
				val keys = map.keys.toList()
				val currentChapter = currentChapterID.value

				excludedKeys.add(currentChapter)

				keys.filterNot { excludedKeys.contains(it) }.forEach { key ->
					map.remove(key)
				}

				progressMapFlow.value = map
			}

			run {
				val excludedKeys = arrayListOf<Int>()
				val map = refreshMap
				val keys = map.keys.toList()
				val currentChapter = currentChapterID.value

				excludedKeys.add(currentChapter)

				keys.filterNot { excludedKeys.contains(it) }.forEach { key ->
					map.remove(key)
				}
			}
		}
	}

	companion object {
		const val HTML_SIZE_DIVISION = 1.25
		const val INCREMENT_PERCENTAGE = 0.05
	}
}