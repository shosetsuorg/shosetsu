package app.shosetsu.android.viewmodel.impl

import android.app.Application
import android.graphics.Color
import android.net.Uri
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
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.consts.settings.SettingKey.*
import app.shosetsu.common.domain.model.local.NovelReaderSettingEntity
import app.shosetsu.common.domain.repositories.base.ISettingsRepository
import app.shosetsu.common.enums.MarkingType.ONSCROLL
import app.shosetsu.common.enums.MarkingType.ONVIEW
import app.shosetsu.common.enums.ReadingStatus.READ
import app.shosetsu.common.enums.ReadingStatus.READING
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


	override val ttsPitch: Float
		get() = runBlocking {
			settingsRepo.getFloat(ReaderPitch)
		}

	private val stringMap by lazy { HashMap<Int, Flow<String>>() }

	/**
	 * Trim out the strings present
	 *
	 * Ensures there is only 5 flows at a time in memory
	 */
	private fun cleanStrings(currentIndex: Int) {
		val excluded = arrayListOf<Int>()
		val keys = stringMap.keys.toList()

		excluded.add(keys[currentIndex])

		keys.getOrNull(currentIndex - 1)?.let {
			excluded.add(it)
		}

		keys.getOrNull(currentIndex - 2)?.let {
			excluded.add(it)
		}

		keys.getOrNull(currentIndex + 1)?.let {
			excluded.add(it)
		}

		keys.getOrNull(currentIndex + 2)?.let {
			excluded.add(it)
		}

		keys.filterNot { excluded.contains(it) }.forEach { key ->
			stringMap.remove(key)
		}
	}

	override fun getChapterStringPassage(item: ReaderChapterUI): Flow<String> =
		flow {
			val flow = stringMap.getOrPut(item.id) {
				getChapterPassage(item).transformLatest { bytes ->

					if (bytes != null)
						emitAll(indentSizeFlow.combine(paragraphSpacingFlow) { indentSize, paragraphSpacing ->
							val unformattedText = bytes.decodeToString()

							val replaceSpacing = StringBuilder("\n")
							// Calculate changes to \n
							for (x in 0 until paragraphSpacing.toInt())
								replaceSpacing.append("\n")

							// Calculate changes to \t
							for (x in 0 until indentSize)
								replaceSpacing.append("\t")

							// Set new text formatted
							unformattedText.replace("\n".toRegex(), replaceSpacing.toString())
						})
					else emit("Null")

				}
			}

			cleanStrings(stringMap.keys.indexOf(item.id))

			emitAll(flow)
		}.onIO()

	override fun getChapterHTMLPassage(item: ReaderChapterUI): Flow<String> = flow {
		val flow = stringMap.getOrPut(item.id) {
			getChapterPassage(item).transformLatest { bytes ->
				if (bytes == null) {
					emit("Null")
					return@transformLatest
				}

				val document = Jsoup.parse(bytes.decodeToString())

				emitAll(
					shosetsuCss.combine(userCssFlow) { shoCSS, useCSS ->
						fun update(id: String, css: String) {
							var style: Element? = document.getElementById(id)

							if (style == null) {
								style = document.createElement("style") ?: return

								style.id(id)
								style.attr("type", "text/css")

								document.head().appendChild(style)
							}

							style.text(css)
						}

						update("shosetsu-style", shoCSS)
						update("user-style", useCSS)

						Uri.encode(document.toString())
					}
				)
			}.onIO()
		}

		cleanStrings(stringMap.keys.indexOf(item.id))

		emitAll(flow)
	}.onIO()

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

	override val isMainLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)

	override val chapterType: Flow<Novel.ChapterType?> by lazy {
		novelIDLive.transformLatest { id ->
			emit(null)

			getNovel(id).first()?.let { novel ->
				emit(getExt(novel.extID)?.chapterType)
			}
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
			isMainLoading.emit(true)
			emitAll(
				loadReaderChaptersUseCase(nId)
			)
			isMainLoading.emit(false)
		}
	}

	override val liveData: Flow<List<ReaderUIItem>> by lazy {
		chaptersFlow
			.combineDividers() // Add dividers

			// Invert chapters after all processing has been done
			.combineInvert()
			.onIO()
	}

	override val currentPage: MutableStateFlow<Int> = MutableStateFlow(0)

	private fun Flow<List<ReaderUIItem>>.combineInvert(): Flow<List<ReaderUIItem>> =
		combine(
			// Only invert if horizontalSwipe && invertSwipe are true.
			// Because who will read with an inverted vertical scroll??
			settingsRepo.getBooleanFlow(ReaderIsInvertedSwipe)
				.combine(settingsRepo.getBooleanFlow(ReaderHorizontalPageSwap)) { invertSwipe, horizontalSwipe ->
					horizontalSwipe && invertSwipe
				}
		) { listResult, b ->
			listResult.let { list ->
				if (b) {
					list.reversed()
				} else {
					list
				}
			}
		}

	private fun Flow<List<ReaderChapterUI>>.combineDividers(): Flow<List<ReaderUIItem>> =
		combine(settingsRepo.getBooleanFlow(ReaderShowChapterDivider)) { result, value ->
			result.let {
				if (value) {
					val modified = ArrayList<ReaderUIItem>(it)
					// Adds the "No more chapters" marker
					modified.add(modified.size, ReaderDividerUI(prev = it.last().title))

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
					it
				}
			}
		}

	override fun setCurrentPage(page: Int) {
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

	override val liveVolumeScroll: Flow<Boolean> by lazy {
		settingsRepo.getBooleanFlow(ReaderVolumeScroll).mapLatest {
			_defaultVolumeScroll = it
			it
		}.onIO()
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
			settingsRepo.getBoolean(ReaderMarkReadAsReading).let { markReadAsReading ->
				/*
				 * If marking chapters that are read as reading is disabled
				 * and the chapter's readingStatus is read, return to prevent further IO.
				 */
				if (!markReadAsReading && chapter.readingStatus == READ) return@launchIO

				/*
				 * If marking type is on scroll, record as reading
				 */
				val markingType = getReadingMarkingType()
				if (markingType == ONSCROLL) {
					recordChapterIsReading(chapter)
				}

				updateReaderChapterUseCase(
					chapter.copy(
						readingStatus = if (markingType == ONSCROLL) {
							READING
						} else chapter.readingStatus,
						readingPosition = readingPosition
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

	private var _tapToScroll: Boolean = ReaderIsTapToScroll.default
	private var _userCss: String = ReaderHtmlCss.default

	override val tapToScroll: Boolean
		get() = _tapToScroll

	private val userCssFlow: Flow<String> by lazy {
		settingsRepo.getStringFlow(ReaderHtmlCss).onIO()
	}

	data class ShosetsuCSSBuilder(
		val backgroundColor: Int = Color.WHITE,
		val foregroundColor: Int = Color.BLACK,
		val textSize: Float = SettingKey.ReaderTextSize.default,
		val indentSize: Int = SettingKey.ReaderIndentSize.default,
		val paragraphSpacing: Float = SettingKey.ReaderParagraphSpacing.default
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
			settingsRepo.getBooleanFlow(ReaderIsTapToScroll).collectLatest {
				_tapToScroll = it
			}
		}
		launchIO {
			settingsRepo.getStringFlow(ReaderHtmlCss).collect {
				_userCss = it
			}
		}
	}

	override val liveIsScreenRotationLocked: Flow<Boolean>
		get() = isScreenRotationLockedFlow.onIO()

	override fun toggleScreenRotationLock() {
		isScreenRotationLockedFlow.value = !isScreenRotationLockedFlow.value
	}

	override fun setCurrentChapterID(chapterId: Int, initial: Boolean) {
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
		TODO("Not yet implemented")
	}

	override fun depleteProgress() {
		TODO("Not yet implemented")
	}

	override fun getCurrentChapterURL(): Flow<String> {
		TODO("Not yet implemented")
	}


	companion object {
		const val HTML_SIZE_DIVISION = 1.25
	}
}