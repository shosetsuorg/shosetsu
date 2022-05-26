package app.shosetsu.android.ui.reader

import android.content.ComponentCallbacks2
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.KeyEvent
import android.view.MenuItem
import android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import app.shosetsu.android.common.SettingKey
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_CHAPTER_ID
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_NOVEL_ID
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.domain.model.local.NovelReaderSettingEntity
import app.shosetsu.android.view.compose.DiscreteSlider
import app.shosetsu.android.view.compose.ErrorAction
import app.shosetsu.android.view.compose.ErrorContent
import app.shosetsu.android.view.compose.setting.GenericBottomSettingLayout
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem.ReaderChapterUI
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem.ReaderDividerUI
import app.shosetsu.android.viewmodel.abstracted.AChapterReaderViewModel
import app.shosetsu.android.viewmodel.abstracted.AChapterReaderViewModel.ChapterPassage
import app.shosetsu.android.viewmodel.impl.settings.*
import app.shosetsu.lib.Novel.ChapterType
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.accompanist.pager.*
import com.google.android.material.composethemeadapter.MdcTheme
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import java.util.*
import kotlin.math.roundToInt


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
 * 13 / 12 / 2019
 */
class ChapterReader
	: AppCompatActivity(), DIAware {
	override val di: DI by closestDI()
	internal val viewModel: AChapterReaderViewModel by viewModel()

	private val isTTSCapable = MutableStateFlow(false)
	private val isTTSPlaying = MutableStateFlow(false)

	private val ttsInitListener: TextToSpeech.OnInitListener by lazy {
		TextToSpeech.OnInitListener {
			when (it) {
				TextToSpeech.SUCCESS -> {
					val result = tts.setLanguage(Locale.getDefault())

					if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
						logE("Language not supported for TTS")
						isTTSCapable.tryEmit(false)
					} else {
						isTTSCapable.tryEmit(true)
					}
				}
				else -> {
					logE("TTS Initialization failed")
					isTTSCapable.tryEmit(false)
				}
			}
		}
	}

	private val tts: TextToSpeech by lazy {
		TextToSpeech(this, ttsInitListener)
	}

	override fun onTrimMemory(level: Int) {
		super.onTrimMemory(level)
		// Determine which lifecycle or system event was raised.
		when (level) {
			ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN -> {
				/*
				   Release any UI objects that currently hold memory.

				   The user interface has moved to the background.
				*/
			}

			ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE,
			ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
			ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL -> {
				/*
				   Release any memory that your app doesn't need to run.

				   The device is running low on memory while the app is running.
				   The event raised indicates the severity of the memory-related event.
				   If the event is TRIM_MEMORY_RUNNING_CRITICAL, then the system will
				   begin killing background processes.
				*/
			}

			ComponentCallbacks2.TRIM_MEMORY_BACKGROUND,
			ComponentCallbacks2.TRIM_MEMORY_MODERATE,
			ComponentCallbacks2.TRIM_MEMORY_COMPLETE -> {
				/*
				   Release as much memory as the process can.

				   The app is on the LRU list and the system is running low on memory.
				   The event raised indicates where the app sits within the LRU list.
				   If the event is TRIM_MEMORY_COMPLETE, the process will be one of
				   the first to be terminated.
				*/
			}

			else -> {
				/*
				  Release any non-critical data structures.

				  The app received an unrecognized memory level value
				  from the system. Treat this as a generic low-memory message.
				*/
				viewModel.clearMemory()
			}
		}

	}

	/** On Create */
	public override fun onCreate(savedInstanceState: Bundle?) {
		logV("")
		viewModel.apply {
			setNovelID(intent.getIntExtra(BUNDLE_NOVEL_ID, -1))
			viewModel.setCurrentChapterID(intent.getIntExtra(BUNDLE_CHAPTER_ID, -1), true)
		}
		runBlocking {
			setTheme(viewModel.appThemeLiveData.first())
		}
		viewModel.appThemeLiveData.collectLA(this, catch = {}) {
			setTheme(it)
		}
		super.onCreate(savedInstanceState)
		val insetsController = WindowInsetsControllerCompat(window, window.decorView)
		insetsController.systemBarsBehavior = BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

		setContent {
			val items by viewModel.liveData.collectAsState(emptyList())
			val isHorizontalReading by viewModel.isHorizontalReading.collectAsState(false)
			val isBookmarked by viewModel.isCurrentChapterBookmarked.collectAsState(false)
			val isRotationLocked by viewModel.liveIsScreenRotationLocked.collectAsState(false)
			val isFocused by viewModel.isFocused.collectAsState(false)
			val chapterType by viewModel.chapterType.collectAsState(null)
			val currentChapterID by viewModel.currentChapterID.collectAsState(-1)
			val isTTSCapable by isTTSCapable.collectAsState(false)
			val isTTSPlaying by isTTSPlaying.collectAsState(false)
			val setting by viewModel.getSettings()
				.collectAsState(NovelReaderSettingEntity(-1, 0, 0.0F))
			val currentPage by viewModel.currentPage.collectAsState(null)

			val isFirstFocus by viewModel.isFirstFocusFlow.collectAsState(false)
			val isSwipeInverted by viewModel.isSwipeInverted.collectAsState(false)
			//val isTapToScroll by viewModel.tapToScroll.collectAsState(false)

			MdcTheme {
				ChapterReaderContent(
					exit = {
						finish()
					},
					items = items,
					isHorizontal = isHorizontalReading,
					chapterType = chapterType,
					getStringContent = viewModel::getChapterStringPassage,
					getHTMLContent = viewModel::getChapterHTMLPassage,
					onScroll = viewModel::onScroll,
					onPlayTTS = {
						if (chapterType == null) return@ChapterReaderContent
						items
							.filterIsInstance<ReaderChapterUI>()
							.find { it.id == currentChapterID }
							?.let { item ->
								tts.setPitch(viewModel.ttsPitch)
								tts.setSpeechRate(viewModel.ttsSpeed)
								when (chapterType!!) {
									ChapterType.STRING -> {
										viewModel.getChapterStringPassage(item)
											.collectLA(this, catch = {}) { content ->
												if (content is ChapterPassage.Success)
													tts.speak(
														content.content,
														TextToSpeech.QUEUE_FLUSH,
														null,
														content.hashCode().toString()
													)
											}

									}
									ChapterType.HTML -> {
										viewModel.getChapterStringPassage(item)
											.collectLA(this, catch = {}) { content ->
												if (content is ChapterPassage.Success)
													tts.speak(
														content.content,
														TextToSpeech.QUEUE_FLUSH,
														null,
														content.hashCode().toString()
													)
											}
									}
									else -> {}
								}
							}
					},
					onStopTTS = {
						tts.stop()
					},
					toggleBookmark = viewModel::toggleBookmark,
					toggleRotationLock = viewModel::toggleScreenRotationLock,
					isTTSPlaying = isTTSPlaying,
					isTTSCapable = isTTSCapable,
					lowerSheet = {
						item { viewModel.textSizeOption() }
						//item { viewModel.tapToScrollOption() }
						item { viewModel.volumeScrollingOption() }
						item { viewModel.horizontalSwitchOption() }
						item { viewModel.invertChapterSwipeOption() }
						item { viewModel.readerKeepScreenOnOption() }
						item { viewModel.showReaderDivider() }
						item { viewModel.stringAsHtmlOption() }
						item { viewModel.doubleTapFocus() }
					},
					setting = setting,
					updateSetting = viewModel::updateSetting,
					markChapterAsCurrent = {
						viewModel.onViewed(it)
						viewModel.setCurrentChapterID(it.id)
					},
					onChapterRead = viewModel::updateChapterAsRead,
					currentPage = currentPage,
					isBookmarked = isBookmarked,
					isRotationLocked = isRotationLocked,
					onPageChanged = viewModel::setCurrentPage,
					textSizeFlow = { viewModel.liveTextSize },
					textColorFlow = { viewModel.textColor },
					backgroundColorFlow = { viewModel.backgroundColor },

					isFirstFocus = isFirstFocus,
					onFirstFocus = viewModel::onFirstFocus,
					isSwipeInverted = isSwipeInverted,
					retryChapter = viewModel::retryChapter,
					isFocused = isFocused,
					toggleFocus = viewModel::toggleFocus,
					onFocusClick = viewModel::onFocusClick,
					onFocusDoubleClick = viewModel::onFocusDoubleClick,
					onShowNavigation = {
						insetsController.show(Type.systemBars())
					}
					//isTapToScroll = isTapToScroll
				)
			}
		}

		viewModel.isFocused.collectLA(this, catch = {
		}) {
			if (it) {
				insetsController.hide(Type.systemBars())
			}
		}
		viewModel.liveIsScreenRotationLocked.collectLA(this, catch = {}) {
			if (it)
				lockRotation()
			else unlockRotation()
		}

		viewModel.liveKeepScreenOn.collectLA(this, catch = {}) {
			if (it) {
				window.addFlags(FLAG_KEEP_SCREEN_ON)
			} else {
				window.clearFlags(FLAG_KEEP_SCREEN_ON)
			}
		}
	}

	/** On Destroy */
	override fun onDestroy() {
		logV("")
		tts.stop()
		tts.shutdown()
		super.onDestroy()
	}

	/**
	 * What to do when an menu item is selected
	 *
	 * @param item item selected
	 * @return true if processed
	 */
	override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
		android.R.id.home -> {
			finish()
			true
		}
		R.id.browser -> {
			viewModel.getCurrentChapterURL().collectLA(this, catch = {}) { url ->
				if (!url.isNullOrEmpty())
					openInBrowser(url)
			}
			true
		}
		R.id.webview -> {
			viewModel.getCurrentChapterURL().collectLA(this, catch = {}) { url ->
				if (!url.isNullOrEmpty())
					openInWebView(url)
			}
			true
		}
		else -> super.onOptionsItemSelected(item)
	}

	/**
	 * Adds the
	 */
	override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
		return if (viewModel.isVolumeScrollEnabled)
			when (keyCode) {
				KeyEvent.KEYCODE_VOLUME_DOWN -> {
					viewModel.incrementProgress()
					true
				}
				KeyEvent.KEYCODE_VOLUME_UP -> {
					viewModel.depleteProgress()
					true
				}
				else -> super.onKeyDown(keyCode, event)
			}
		else super.onKeyDown(keyCode, event)
	}

	private fun lockRotation() {
		val currentOrientation = resources.configuration.orientation
		requestedOrientation = if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
		} else {
			ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
		}
	}

	private fun unlockRotation() {
		//window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
		requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_USER
	}
}

@Preview
@Composable
fun PreviewChapterReaderContent() {
	MdcTheme {
		ChapterReaderContent(
			exit = {},
			items = emptyList(),
			isHorizontal = false,
			chapterType = ChapterType.HTML,
			getStringContent = { flow { } },
			getHTMLContent = { flow { } },
			onScroll = { a, b ->
			},
			onStopTTS = {},
			onPlayTTS = {},
			isTTSCapable = false,
			isTTSPlaying = false,
			toggleRotationLock = {},
			toggleBookmark = {},
			setting = NovelReaderSettingEntity(-1, 0, 0f),
			updateSetting = {},
			lowerSheet = {},
			markChapterAsCurrent = {},
			onChapterRead = {},
			currentPage = 0,
			isBookmarked = false,
			isRotationLocked = false,
			onPageChanged = {},
			textSizeFlow = { flow { } },
			textColorFlow = { flow { } },
			backgroundColorFlow = { flow { } },
			isFirstFocus = false,
			onFirstFocus = {},
			isSwipeInverted = false,
			retryChapter = {},
			isFocused = false,
			onFocusClick = {},
			onFocusDoubleClick = {},
			toggleFocus = {},
			onShowNavigation = {}
			//isTapToScroll = false
		)
	}
}

/**
 * Main reader content
 *
 * @param title Title to display on top, either chapter title or what is currently occuring.
 * @param exit called to leave the chapter reader
 * @param items Chapters to display
 * @param isHorizontal If the chapters should be displayed horizontally or not
 * @param chapterType Chapter type to load into
 * @param currentPage Current page to open up to, should be null initially for loading purposes
 * @param onPageChanged Called when the page is changed, with the new page passed in
 * @param markChapterAsCurrent Mark a chapter as the current chapter
 * @param onChapterRead Chapter has been completely read
 * @param getHTMLContent Get HTML content for web
 * @param getStringContent Get string content for text
 * @param onScroll Called when the content is scrolled
 * @param isTTSCapable Is the system capable of TTS
 * @param isTTSPlaying Is TTS playing currently
 * @param onPlayTTS Play TTS of the current content
 * @param onStopTTS Stop TTS
 * @param isBookmarked Is the current chapter bookmarked
 * @param toggleBookmark Toggle the bookmark of the current chapter
 * @param isRotationLocked Is rotation prohibited
 * @param toggleRotationLock Toggle the lock of rotation
 * @param setting Settings of the current novel
 * @param updateSetting Update settings of the current novel
 * @param lowerSheet Define any other settings below [setting]
 * @param textSizeFlow Get a text size flow, useful for text
 * @param textColorFlow Get text color flow, useful for text
 * @param backgroundColorFlow Get background color flow, useful for text
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun ChapterReaderContent(
	exit: () -> Unit,
	items: List<ReaderUIItem>,
	isHorizontal: Boolean,
	chapterType: ChapterType?,

	//isTapToScroll: Boolean,
	isSwipeInverted: Boolean,

	isFirstFocus: Boolean,
	onFirstFocus: () -> Unit,

	currentPage: Int?,
	onPageChanged: (Int) -> Unit,

	markChapterAsCurrent: (item: ReaderChapterUI) -> Unit,
	onChapterRead: (item: ReaderChapterUI) -> Unit,

	retryChapter: (item: ReaderChapterUI) -> Unit,

	getStringContent: (item: ReaderChapterUI) -> Flow<ChapterPassage>,
	getHTMLContent: (item: ReaderChapterUI) -> Flow<ChapterPassage>,

	onScroll: (item: ReaderChapterUI, perc: Double) -> Unit,

	isTTSCapable: Boolean,
	isTTSPlaying: Boolean,
	onPlayTTS: () -> Unit,
	onStopTTS: () -> Unit,

	isBookmarked: Boolean,
	toggleBookmark: () -> Unit,

	isRotationLocked: Boolean,
	toggleRotationLock: () -> Unit,

	setting: NovelReaderSettingEntity,
	updateSetting: (NovelReaderSettingEntity) -> Unit,
	lowerSheet: LazyListScope.() -> Unit,

	textSizeFlow: () -> Flow<Float>,
	textColorFlow: () -> Flow<Int>,
	backgroundColorFlow: () -> Flow<Int>,

	isFocused: Boolean,
	toggleFocus: () -> Unit,
	onFocusClick: () -> Unit,
	onFocusDoubleClick: () -> Unit,
	onShowNavigation: () -> Unit
) {

	val scaffoldState = rememberBottomSheetScaffoldState()
	val coroutineScope = rememberCoroutineScope()

	if (isFocused && isFirstFocus) {
		val string = stringResource(R.string.reader_first_focus)
		val dismiss = stringResource(R.string.reader_first_focus_dismiss)
		LaunchedEffect(scaffoldState.snackbarHostState) {
			launch {
				when (scaffoldState.snackbarHostState.showSnackbar(string, dismiss)) {
					SnackbarResult.Dismissed -> onFirstFocus()
					SnackbarResult.ActionPerformed -> onFirstFocus()
				}
			}
		}
	}

	BottomSheetScaffold(
		scaffoldState = scaffoldState,
		sheetContent = {
			Column {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.height(56.dp),
					horizontalArrangement = Arrangement.SpaceBetween,
					verticalAlignment = Alignment.CenterVertically
				) {
					IconButton(onClick = exit) {
						Icon(Icons.Filled.ArrowBack, null)
					}

					Row {
						IconButton(onClick = toggleFocus) {
							Icon(
								painterResource(R.drawable.ic_baseline_visibility_off_24),
								null
							)
						}
						IconButton(onClick = toggleBookmark) {
							Icon(
								painterResource(
									if (!isBookmarked) {
										R.drawable.empty_bookmark
									} else {
										R.drawable.filled_bookmark
									}
								),
								null
							)
						}

						IconButton(onClick = toggleRotationLock) {
							Icon(
								painterResource(
									if (!isRotationLocked)
										R.drawable.ic_baseline_screen_rotation_24
									else R.drawable.ic_baseline_screen_lock_rotation_24
								),
								null
							)
						}

						if (isTTSCapable && !isTTSPlaying)
							IconButton(onClick = onPlayTTS) {
								Icon(
									painterResource(R.drawable.ic_baseline_audiotrack_24),
									null
								)
							}

						if (isTTSPlaying)
							IconButton(onClick = onStopTTS) {
								Icon(
									painterResource(R.drawable.ic_baseline_stop_circle_24),
									null
								)
							}

						IconButton(onClick = onShowNavigation) {
							Icon(
								painterResource(R.drawable.unfold_less),
								null
							)
						}
					}

					IconButton(onClick = {
						coroutineScope.launch {
							if (!scaffoldState.bottomSheetState.isExpanded)
								scaffoldState.bottomSheetState.expand()
							else scaffoldState.bottomSheetState.collapse()
						}
					}) {
						Icon(
							if (scaffoldState.bottomSheetState.isExpanded) {
								painterResource(R.drawable.expand_more)
							} else {
								painterResource(R.drawable.expand_less)
							},
							null
						)
					}
				}

				LazyColumn(
					contentPadding = PaddingValues(16.dp)
				) {
					item {
						GenericBottomSettingLayout(
							stringResource(R.string.paragraph_spacing),
							"",
						) {
							DiscreteSlider(
								setting.paragraphSpacingSize,
								"${setting.paragraphSpacingSize}",
								{ it, a ->
									updateSetting(
										setting.copy(
											paragraphSpacingSize = if (!a)
												it.roundToInt().toFloat()
											else it
										)
									)
								},
								0..10,
							)
						}

					}

					item {
						GenericBottomSettingLayout(
							stringResource(R.string.paragraph_indent),
							"",
						) {
							DiscreteSlider(
								setting.paragraphIndentSize,
								"${setting.paragraphIndentSize}",
								{ it, _ ->
									updateSetting(setting.copy(paragraphIndentSize = it))
								},
								0..10,
							)
						}
					}
					lowerSheet()
				}
			}
		},
		sheetPeekHeight = if (!isFocused) BottomSheetScaffoldDefaults.SheetPeekHeight else 0.dp
	) { paddingValues ->
		ChapterReaderPagerContent(
			paddingValues = paddingValues,
			items = items,
			isHorizontal = isHorizontal,
			isSwipeInverted = isSwipeInverted,
			currentPage = currentPage,
			onPageChanged = onPageChanged,
			markChapterAsCurrent = markChapterAsCurrent,
			onChapterRead = onChapterRead,
			onStopTTS = onStopTTS,
			createPage = { page ->
				when (val item = items[page]) {
					is ReaderChapterUI -> {
						when (chapterType) {
							ChapterType.STRING -> {
								ChapterReaderStringContent(
									item = item,
									getStringContent = getStringContent,
									retryChapter = retryChapter,
									textSizeFlow = textSizeFlow,
									textColorFlow = textColorFlow,
									backgroundColorFlow = backgroundColorFlow,
									onScroll = onScroll,
									onClick = onFocusClick,
									onDoubleClick = onFocusDoubleClick
								)
							}
							ChapterType.HTML -> {
								ChapterReaderHTMLContent(
									item = item,
									getHTMLContent = getHTMLContent,
									retryChapter = retryChapter,
									onScroll = onScroll,
									onClick = onFocusClick,
									onDoubleClick = onFocusDoubleClick
								)
							}
							else -> {
							}
						}
					}
					is ReaderDividerUI -> {
						DividierPageContent(
							item.prev,
							item.next
						)
					}
				}
			}
		)
	}
}

/**
 * Creates the string page
 */
@Suppress("FunctionName")
@Composable
inline fun ChapterReaderStringContent(
	item: ReaderChapterUI,
	getStringContent: (item: ReaderChapterUI) -> Flow<ChapterPassage>,
	crossinline retryChapter: (item: ReaderChapterUI) -> Unit,
	textSizeFlow: () -> Flow<Float>,
	textColorFlow: () -> Flow<Int>,
	backgroundColorFlow: () -> Flow<Int>,
	crossinline onScroll: (item: ReaderChapterUI, perc: Double) -> Unit,
	crossinline onClick: () -> Unit,
	crossinline onDoubleClick: () -> Unit
) {
	val content by getStringContent(item).collectAsState(ChapterPassage.Loading)

	when (content) {
		is ChapterPassage.Error -> {
			ErrorContent(
				(content as? ChapterPassage.Error)?.throwable!!.message
					?: "Unknown error",
				ErrorAction(R.string.retry) {
					retryChapter(item)
				}
			)
		}
		is ChapterPassage.Loading -> {
			val backgroundColor by backgroundColorFlow().collectAsState(
				Color.Gray.toArgb()
			)

			Column {
				LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

				Box(
					modifier = Modifier
						.background(Color(backgroundColor))
						.fillMaxSize()
				) { }
			}
		}
		is ChapterPassage.Success -> {
			val textSize by textSizeFlow().collectAsState(SettingKey.ReaderTextSize.default)
			val textColor by textColorFlow().collectAsState(Color.White.toArgb())
			val backgroundColor by backgroundColorFlow().collectAsState(
				Color.Gray.toArgb()
			)


			StringPageContent(
				(content as? ChapterPassage.Success)?.content ?: "",
				item.readingPosition,
				textSize = textSize,
				onScroll = {
					onScroll(item, it)
				},
				textColor = textColor,
				backgroundColor = backgroundColor,
				onClick = {
					onClick()
				},
				onDoubleClick = {
					onDoubleClick()
				}
				//	isTapToScroll=isTapToScroll
			)
		}

	}
}

/**
 * Creates the HTML page
 */
@Suppress("FunctionName")
@Composable
inline fun ChapterReaderHTMLContent(
	item: ReaderChapterUI,
	getHTMLContent: (item: ReaderChapterUI) -> Flow<ChapterPassage>,
	crossinline retryChapter: (item: ReaderChapterUI) -> Unit,
	crossinline onScroll: (item: ReaderChapterUI, perc: Double) -> Unit,
	crossinline onClick: () -> Unit,
	crossinline onDoubleClick: () -> Unit
) {
	val html by getHTMLContent(item).collectAsState(ChapterPassage.Loading)

	when (html) {
		is ChapterPassage.Error -> {
			ErrorContent(
				(html as? ChapterPassage.Error)?.throwable?.message
					?: "Unknown error",
				ErrorAction(R.string.retry) {
					retryChapter(item)
				}
			)
		}
		ChapterPassage.Loading -> {
			Column {
				LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
				Box(
					modifier = Modifier
						.background(Color.Black)
						.fillMaxSize()
				) {}
			}
		}
		is ChapterPassage.Success -> {
			WebViewPageContent(
				html = (html as ChapterPassage.Success).content,
				progress = item.readingPosition,
				onScroll = {
					onScroll(item, it)
				},
				onClick = {
					onClick()
				},
				onDoubleClick = {
					onDoubleClick()
				}
			)
		}
	}
}

/**
 * Content of pager itself
 */
@Suppress("FunctionName", "DEPRECATION")
@OptIn(ExperimentalPagerApi::class)
@Composable
inline fun ChapterReaderPagerContent(
	paddingValues: PaddingValues,

	items: List<ReaderUIItem>,
	isHorizontal: Boolean,

	isSwipeInverted: Boolean,

	currentPage: Int?,
	crossinline onPageChanged: (Int) -> Unit,

	crossinline markChapterAsCurrent: (item: ReaderChapterUI) -> Unit,
	crossinline onChapterRead: (item: ReaderChapterUI) -> Unit,

	crossinline onStopTTS: () -> Unit,

	crossinline createPage: @Composable PagerScope.(page: Int) -> Unit
) {
	// Do not create the pager if the currentPage has not been set yet
	if (currentPage == null) {
		Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
			Text(stringResource(R.string.loading))
		}
		return
	}

	val pagerState = rememberPagerState(currentPage)

	var curChapter: ReaderChapterUI? by remember { mutableStateOf(null) }

	if (items.isNotEmpty())
		LaunchedEffect(pagerState) {
			snapshotFlow { pagerState.currentPage }.distinctUntilChanged().collect { newPage ->
				onStopTTS()
				val item = items.getOrNull(newPage) ?: return@collect

				if (item is ReaderChapterUI) {
					markChapterAsCurrent(item)
					curChapter = item
				} else {
					curChapter?.let(onChapterRead)
				}
				onPageChanged(newPage)
			}
		}

	if (isHorizontal) {
		HorizontalPager(
			count = items.size,
			state = pagerState,
			modifier = Modifier
				.fillMaxSize()
				.padding(
					top = paddingValues.calculateTopPadding(),
					bottom = paddingValues.calculateBottomPadding()
				),
			reverseLayout = isSwipeInverted,
			content = {
				createPage(this, it)
			}
		)
	} else {
		VerticalPager(
			count = items.size,
			state = pagerState,
			modifier = Modifier
				.fillMaxSize()
				.padding(
					top = paddingValues.calculateTopPadding(),
					bottom = paddingValues.calculateBottomPadding()
				),
			content = {
				createPage(this, it)
			}
		)
	}
}