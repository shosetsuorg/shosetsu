package app.shosetsu.android.viewmodel.impl

import android.app.Application
import android.graphics.Color
import androidx.annotation.WorkerThread
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import app.shosetsu.android.common.ext.launchIO
import app.shosetsu.android.common.ext.logD
import app.shosetsu.android.common.ext.logV
import app.shosetsu.android.domain.model.local.ColorChoiceData
import app.shosetsu.android.domain.usecases.RecordChapterIsReadUseCase
import app.shosetsu.android.domain.usecases.RecordChapterIsReadingUseCase
import app.shosetsu.android.domain.usecases.get.*
import app.shosetsu.android.domain.usecases.update.UpdateReaderChapterUseCase
import app.shosetsu.android.domain.usecases.update.UpdateReaderSettingUseCase
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem.ReaderChapterUI
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem.ReaderDividerUI
import app.shosetsu.android.viewmodel.abstracted.AChapterReaderViewModel
import app.shosetsu.common.consts.settings.SettingKey.*
import app.shosetsu.common.domain.model.local.NovelReaderSettingEntity
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.enums.MarkingType.ONSCROLL
import app.shosetsu.common.enums.MarkingType.ONVIEW
import app.shosetsu.common.enums.ReadingStatus.READ
import app.shosetsu.common.enums.ReadingStatus.READING
import app.shosetsu.common.utils.asHtml
import app.shosetsu.common.utils.copy
import app.shosetsu.lib.Novel
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
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
	private val application: Application,
	override val settingsRepo: ISettingsRepository,
	private val loadReaderChaptersUseCase: GetReaderChaptersUseCase,
	private val loadChapterPassageUseCase: GetChapterPassageUseCase,
	private val updateReaderChapterUseCase: UpdateReaderChapterUseCase,
	private val getReaderSettingsUseCase: GetReaderSettingUseCase,
	private val updateReaderSettingUseCase: UpdateReaderSettingUseCase,
	private val getReadingMarkingType: GetReadingMarkingTypeUseCase,
	private val recordChapterIsReading: RecordChapterIsReadingUseCase,
	private val recordChapterIsRead: RecordChapterIsReadUseCase,
	private val getNovel: GetNovelUIUseCase,
	private val getExt: GetExtensionUseCase
) : AChapterReaderViewModel() {
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
	private val progressMap by lazy { MutableStateFlow(HashMap<Int, Double>()) }

	override val ttsPitch: Float
		get() = runBlocking {
			settingsRepo.getFloat(ReaderPitch)
		}

	private val stringMap by lazy { HashMap<Int, MutableStateFlow<ChapterPassage>>() }
	private val refreshMap by lazy { HashMap<Int, MutableStateFlow<Boolean>>() }

	override val isFirstFocusFlow: Flow<Boolean> by lazy {
		settingsRepo.getBooleanFlow(ReaderIsFirstFocus).onIO()
	}

	override val isSwipeInverted: Flow<Boolean> by lazy {
		settingsRepo.getBooleanFlow(ReaderIsInvertedSwipe).onIO()
	}

	override fun onFirstFocus() {
		logV("")
		launchIO {
			settingsRepo.setBoolean(ReaderIsFirstFocus, false)
		}
	}

	/**
	 * Trim out the strings present around the current page
	 *
	 * Ensures there is only 10~ flows at a time in memory
	 */
	private fun cleanMap(map: HashMap<Int, *>, currentIndex: Int) {
		val excludedKeys = arrayListOf<Int>()
		val keys = map.keys.toList()

		excludedKeys.add(keys[currentIndex])

		for (i in 1..5) {
			keys.getOrNull(currentIndex - i)?.let {
				excludedKeys.add(it)
			}
			keys.getOrNull(currentIndex + i)?.let {
				excludedKeys.add(it)
			}
		}

		keys.filterNot { excludedKeys.contains(it) }.forEach { key ->
			map.remove(key)
		}
	}

	/**
	 * Clear all maps
	 */
	private fun clearMaps() {
		stringMap.clear()
	}

	private fun getRefreshFlow(item: ReaderChapterUI) =
		refreshMap.getOrPut(item.id) { MutableStateFlow(false) }


	override fun retryChapter(item: ReaderChapterUI) {
		logV("$item")
		val flow = getRefreshFlow(item)
		flow.tryEmit(!flow.value)
	}


	override fun getChapterStringPassage(item: ReaderChapterUI): Flow<ChapterPassage> {
		logV("$item")
		val mutableFlow = stringMap.getOrPut(item.id) {
			MutableStateFlow<ChapterPassage>(ChapterPassage.Loading).also { mutableFlow ->
				launchIO {
					mutableFlow.emitAll(
						getRefreshFlow(item).transformLatest {
							emitAll(
								getChapterPassage(item).transformLatest transformLatest2@{ bytes ->
									if (bytes == null) {
										emit(ChapterPassage.Error(Exception("No content received")))
										return@transformLatest2
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
								}.catch {
									emit(ChapterPassage.Error(it))
								}
							)
						}

					)
				}
			}
		}

		cleanMap(stringMap, stringMap.keys.indexOf(item.id))

		return flow { emitAll(mutableFlow) }.onIO()
	}


	override fun getChapterHTMLPassage(item: ReaderChapterUI): Flow<ChapterPassage> {
		logV("$item")
		val mutableFlow = stringMap.getOrPut(item.id) {
			MutableStateFlow<ChapterPassage>(ChapterPassage.Loading).also { mutableFlow ->
				launchIO {
					mutableFlow.emitAll(
						getRefreshFlow(item).transformLatest {
							emitAll(
								getChapterPassage(item).transformLatest transformLatest2@{ bytes ->
									if (bytes == null) {
										emit(ChapterPassage.Error(Exception("No content received")))
										return@transformLatest2
									}

									var result = bytes.decodeToString()

									@Suppress("DEPRECATION")
									if (item.chapterType == Novel.ChapterType.STRING && item.convertStringToHtml) {
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
								}.catch catch@{
									emit(ChapterPassage.Error(it))
								})
						}
					)
				}
			}
		}

		cleanMap(stringMap, stringMap.keys.indexOf(item.id))

		return flow { emitAll(mutableFlow) }.onIO()
	}

	override val isCurrentChapterBookmarked: Flow<Boolean> by lazy {
		chaptersFlow.transformLatest { items ->
			emitAll(
				currentChapterID.transformLatest { id ->
					items.find { it.id == id }?.let {
						emit(it.bookmarked)
					}
				}
			)
		}.onIO()
	}


	/**
	 * Specifies what chapter type the reader should render.
	 *
	 * Upon [ReaderStringToHtml] being true, will clear out any previous strings if the prevType was
	 * not html, causing the content to regenerate.
	 */
	override val chapterType: Flow<Novel.ChapterType?> by lazy {
		novelIDLive.transformLatest { id ->
			emit(null)


			val novel = getNovel(id).first() ?: return@transformLatest

			val type = getExt(novel.extID)?.chapterType ?: return@transformLatest
			var prevType: Novel.ChapterType? = null

			emitAll(
				settingsRepo.getBooleanFlow(ReaderStringToHtml).transformLatest { convert ->
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
		}.onIO()
	}

	override val currentTitle: Flow<String?> by lazy {
		flow {
			emit(null)
			emitAll(
				currentPage.transformLatest { page ->
					liveData.first()[page].let {
						if (it is ReaderChapterUI)
							emit(it.title)
						else if (it is ReaderDividerUI) {
							emit(application.getString(R.string.next_chapter))
						}
					}
				}
			)
		}.onIO()
	}

	override val ttsSpeed: Float
		get() = runBlocking {
			settingsRepo.getFloat(ReaderSpeed)
		}

	private val chaptersFlow: Flow<List<ReaderChapterUI>> by lazy {
		novelIDLive.transformLatest { nId ->
			emitAll(
				loadReaderChaptersUseCase(nId)
			)
		}
	}

	override val liveData: Flow<List<ReaderUIItem>> by lazy {
		chaptersFlow
			.combineTempProgress()
			.combineDividers() // Add dividers

			.onIO()
	}

	override val currentPage: MutableStateFlow<Int> = MutableStateFlow(0)

	private fun Flow<List<ReaderChapterUI>>.combineDividers(): Flow<List<ReaderUIItem>> =
		combine(settingsRepo.getBooleanFlow(ReaderShowChapterDivider)) { list, value ->
			if (value) {
				val modified = ArrayList<ReaderUIItem>(list)
				// Adds the "No more chapters" marker
				modified.add(modified.size, ReaderDividerUI(prev = list.last().title))

				/**
				 * Loops down the list, adding in the seperators
				 */
				val startPoint = modified.size - 2
				for (index in startPoint downTo 1)
					modified.add(
						index, ReaderDividerUI(
							(modified[index - 1] as ReaderChapterUI).title,
							(modified[index] as ReaderChapterUI).title
						)
					)

				modified
			} else {
				list
			}
		}

	private fun Flow<List<ReaderChapterUI>>.combineTempProgress(): Flow<List<ReaderChapterUI>> =
		combine(progressMap) { list, progress ->
			list.map {
				if (progress.containsKey(it.id)) {
					val progress = progress[it.id]
					val copy = it.copy(
						readingPosition = progress!!
					)
					logD("Progress map contains temp key for $it=$progress, result: $copy")
					copy
				} else {
					it
				}
			}
		}

	override fun setCurrentPage(page: Int) {
		logV("$page")
		currentPage.tryEmit(page)
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
		logV("novelID=$novelID")
		when {
			novelIDLive.value == -1 ->
				logD("Setting NovelID")
			novelIDLive.value != novelID ->
				logD("NovelID not equal, resetting")
			novelIDLive.value == novelID -> {
				logD("NovelID equal, ignoring")
				return
			}
		}
		novelIDLive.tryEmit(novelID)
	}

	@WorkerThread
	private fun getChapterPassage(readerChapterUI: ReaderChapterUI): Flow<ByteArray?> =
		flow {
			emit(loadChapterPassageUseCase(readerChapterUI))
		}.onIO()

	override fun toggleBookmark() {
		launchIO {
			val id = currentChapterID.first()
			val items = chaptersFlow.first()
			items.find { it.id == id }?.let {
				updateChapter(
					it.copy(
						bookmarked = !it.bookmarked
					)
				)
			}
		}
	}

	override fun updateChapter(
		chapter: ReaderChapterUI,
	) {
		launchIO {
			updateReaderChapterUseCase(chapter)
		}
	}

	override fun updateChapterAsRead(chapter: ReaderChapterUI) {
		launchIO {
			recordChapterIsRead(chapter)
			updateReaderChapterUseCase(
				chapter.copy(
					readingStatus = READ,
					readingPosition = 0.0
				)
			)
		}
	}

	override fun onViewed(chapter: ReaderChapterUI) {
		logV("$chapter")
		launchIO {
			settingsRepo.getBoolean(ReaderMarkReadAsReading).let { markReadAsReading ->
				/*
				 * If marking chapters that are read as reading is disabled
				 * and the chapter's readingStatus is read, return to prevent further IO.
				 */
				if (!markReadAsReading && chapter.readingStatus == READ) return@launchIO

				/*
				 * If the reading marking type does not equal on view, then return
				 */
				if (getReadingMarkingType() != ONVIEW) return@launchIO

				recordChapterIsReading(chapter)

				updateReaderChapterUseCase(chapter.copy(readingStatus = READING))
			}
		}
	}

	override fun onScroll(chapter: ReaderChapterUI, readingPosition: Double) {
		logV("$chapter , $readingPosition")
		launchIO {
			// If the chapter reaches 95% read, we can assume the reader already sees it all :P
			if (readingPosition < 0.95) {
				settingsRepo.getBoolean(ReaderMarkReadAsReading).let { markReadAsReading ->
					/**
					 * If marking chapters that are read as reading is disabled
					 * and the chapter's readingStatus is read, save progress temporarily.
					 */
					if (!markReadAsReading && chapter.readingStatus == READ) {
						progressMap.emit(progressMap.value.copy().apply {
							put(chapter.id, readingPosition)
						})
						return@launchIO
					}

					/*
							 * If marking type is on scroll, record as reading
							 */
					val markingType = getReadingMarkingType()
					if (markingType == ONSCROLL) {
						recordChapterIsReading(chapter)
					}

					// Remove temp progress
					progressMap.emit(progressMap.value.copy().apply {
						remove(chapter.id)
					})

					updateReaderChapterUseCase(
						chapter.copy(
							readingStatus = if (markingType == ONSCROLL) {
								READING
							} else chapter.readingStatus,
							readingPosition = readingPosition
						)
					)
				}
			} else {
				// User probably sees everything at this point

				recordChapterIsRead(chapter)

				// Temp remember the progress
				progressMap.emit(progressMap.value.copy().apply {
					put(chapter.id, readingPosition)
				})

				updateReaderChapterUseCase(
					chapter.copy(
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
			updateReaderSettingUseCase(novelReaderSettingEntity)
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

	override fun toggleFocus() {
		isFocused.tryEmit(!isFocused.value)
	}

	override fun onFocusClick() {
		launchIO {
			if (!doubleTapFocus.first())
				isFocused.emit(!isFocused.value)
		}
	}

	override fun onFocusDoubleClick() {
		launchIO {
			if (doubleTapFocus.first())
				isFocused.emit(!isFocused.value)
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
	}

	override val liveIsScreenRotationLocked: Flow<Boolean>
		get() = isScreenRotationLockedFlow.onIO()

	override fun toggleScreenRotationLock() {
		isScreenRotationLockedFlow.value = !isScreenRotationLockedFlow.value
	}

	override fun setCurrentChapterID(chapterId: Int, initial: Boolean) {
		logV("$chapterId, $initial")
		currentChapterID.tryEmit(chapterId)

		if (initial)
			launchIO {
				val items = liveData.first()
				currentPage.emit(
					items
						.indexOfFirst { it is ReaderChapterUI && it.id == chapterId }
				)
			}
	}

	override fun incrementProgress() {
		launchIO {
			val chapterId = currentChapterID.first()

			val chapter = chaptersFlow.first().find { it.id == chapterId } ?: return@launchIO

			/*
			 * Increment 5% at a time, let us hope this does not back fire
			 */
			if ((chapter.readingPosition + INCREMENT_PERCENTAGE) < 1)
				onScroll(chapter, chapter.readingPosition + INCREMENT_PERCENTAGE)
		}
	}

	override fun depleteProgress() {
		launchIO {
			val chapterId = currentChapterID.first()

			val chapter = chaptersFlow.first().find { it.id == chapterId } ?: return@launchIO

			/*
			 * Increment 5% at a time, let us hope this does not back fire
			 */
			if ((chapter.readingPosition - INCREMENT_PERCENTAGE) > 0)
				onScroll(chapter, chapter.readingPosition - INCREMENT_PERCENTAGE)
		}
	}

	override fun getCurrentChapterURL(): Flow<String> {
		TODO("Not yet implemented")
	}

	companion object {
		const val HTML_SIZE_DIVISION = 1.25
		const val INCREMENT_PERCENTAGE = 0.05
	}
}