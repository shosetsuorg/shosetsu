package app.shosetsu.android.ui.reader

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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_CHAPTER_ID
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_NOVEL_ID
import app.shosetsu.android.common.consts.READER_BAR_ALPHA
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.view.compose.DiscreteSlider
import app.shosetsu.android.view.compose.ErrorContent
import app.shosetsu.android.view.compose.setting.GenericBottomSettingLayout
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem.ReaderChapterUI
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem.ReaderDividerUI
import app.shosetsu.android.view.widget.EmptyDataView
import app.shosetsu.android.viewmodel.abstracted.AChapterReaderViewModel
import app.shosetsu.android.viewmodel.impl.settings.*
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.domain.model.local.NovelReaderSettingEntity
import app.shosetsu.lib.Novel.ChapterType
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.android.material.composethemeadapter.MdcTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
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

	override fun onResume() {
		window.hideBar() // resumes fullscreen when returning to the view
		super.onResume()
	}

	/** On Create */
	public override fun onCreate(savedInstanceState: Bundle?) {
		logV("")
		window.hideBar()
		viewModel.apply {
			setNovelID(intent.getIntExtra(BUNDLE_NOVEL_ID, -1))
			viewModel.setCurrentChapterID(intent.getIntExtra(BUNDLE_CHAPTER_ID, -1), true)
		}

		super.onCreate(savedInstanceState)
		setContent {
			val currentTitle by viewModel.currentTitle.collectAsState(null)
			val items by viewModel.liveData.collectAsState(emptyList())
			val isHorizontalReading by viewModel.isHorizontalReading.collectAsState(false)
			val isBookmarked by viewModel.isCurrentChapterBookmarked.collectAsState(false)
			val isRotationLocked by viewModel.liveIsScreenRotationLocked.collectAsState(false)
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
					currentTitle ?: stringResource(R.string.loading),
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
												tts.speak(
													content,
													TextToSpeech.QUEUE_FLUSH,
													null,
													content.hashCode().toString()
												)
											}

									}
									ChapterType.HTML -> {
										viewModel.getChapterStringPassage(item)
											.collectLA(this, catch = {}) { content ->
												tts.speak(
													content,
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
					},
					setting = setting,
					updateSetting = viewModel::updateSetting,
					markChapterAsCurrent = {
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
					onViewed = viewModel::onViewed,

					isFirstFocus = isFirstFocus,
					onFirstFocus = viewModel::onFirstFocus,
					isSwipeInverted = isSwipeInverted,
					getException = viewModel::getChapterException,
					retryChapter = viewModel::retryChapter
					//isTapToScroll = isTapToScroll
				)
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
				if (url.isNotEmpty())
					openInBrowser(url)
			}
			true
		}
		R.id.webview -> {
			viewModel.getCurrentChapterURL().collectLA(this, catch = {}) { url ->
				if (url.isNotEmpty())
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
					viewModel.depleteProgress()
					true
				}
				KeyEvent.KEYCODE_VOLUME_UP -> {
					viewModel.incrementProgress()
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
			title = "Chapter 1",
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
			onViewed = {},
			isFirstFocus = false,
			onFirstFocus = {},
			isSwipeInverted = false,
			getException = { flow { } },
			retryChapter = {}
			//isTapToScroll = false
		)
	}
}

/**
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
 * @param onViewed Called when a chapter has been viewed by the reader
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
@OptIn(ExperimentalMaterialApi::class, com.google.accompanist.pager.ExperimentalPagerApi::class)
@Composable
fun ChapterReaderContent(
	title: String,
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

	getException: (item: ReaderChapterUI) -> Flow<Throwable?>,
	retryChapter: (item: ReaderChapterUI) -> Unit,

	getStringContent: (item: ReaderChapterUI) -> Flow<String>,
	getHTMLContent: (item: ReaderChapterUI) -> Flow<String>,

	onScroll: (item: ReaderChapterUI, perc: Double) -> Unit,
	onViewed: (item: ReaderChapterUI) -> Unit,

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
) {
	var isFocused by remember { mutableStateOf(false) }
	val scaffoldState = rememberScaffoldState()

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

	Scaffold(
		topBar = {
			if (!isFocused)
				TopAppBar(
					navigationIcon = {
						IconButton(onClick = exit) {
							Icon(Icons.Filled.ArrowBack, null)
						}
					},
					title = {
						Text(title, maxLines = 2, modifier = Modifier.padding(end = 16.dp))
					},
					modifier = Modifier.alpha(READER_BAR_ALPHA),
					backgroundColor = MaterialTheme.colors.background
				)
		},
		scaffoldState = scaffoldState
	) { paddingValues ->

		// Do not create the pager if the currentPage has not been set yet
		if (currentPage == null) {
			Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
				Text(stringResource(R.string.loading))
			}
			return@Scaffold
		}

		val pagerState =
			rememberPagerState(currentPage)

		val count = items.size

		var curChapter: ReaderChapterUI? by remember { mutableStateOf(null) }

		if (items.isNotEmpty())
			LaunchedEffect(pagerState) {
				snapshotFlow { pagerState.currentPage }.distinctUntilChanged().collect { newPage ->
					onStopTTS()
					val item = items[newPage]

					if (item is ReaderChapterUI) {
						markChapterAsCurrent(item)
						curChapter = item
					} else {
						curChapter?.let(onChapterRead)
					}
					onPageChanged(newPage)
				}
			}

		@Composable
		fun createPage(page: Int) {
			when (val item = items[page]) {
				is ReaderChapterUI -> {
					when (chapterType) {
						ChapterType.STRING -> {
							val exception by getException(item).collectAsState(null)
							val content by getStringContent(item).collectAsState(null)
							val textSize by textSizeFlow().collectAsState(SettingKey.ReaderTextSize.default)
							val textColor by textColorFlow().collectAsState(Color.Black.toArgb())
							val backgroundColor by backgroundColorFlow().collectAsState(Color.White.toArgb())

							Column {
								if (content == null) {
									LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

									DisposableEffect(Unit) {
										onDispose {
											onViewed(item)
										}
									}
								}

								if (exception == null) {
									StringPageContent(
										content ?: "",
										item.readingPosition,
										textSize = textSize,
										onScroll = {
											onScroll(item, it)
										},
										onFocusToggle = {
											isFocused = !isFocused
										},
										textColor = textColor,
										backgroundColor = backgroundColor
										//	isTapToScroll=isTapToScroll
									)
								} else {
									ErrorContent(
										exception!!.message ?: "Unknown error",
										EmptyDataView.Action(R.string.retry) {
											retryChapter(item)
										}
									)
								}
							}
						}
						ChapterType.HTML -> {
							val exception by getException(item).collectAsState(null)
							val html by getHTMLContent(item).collectAsState(null)

							Column {
								if (html == null) {
									LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

									DisposableEffect(Unit) {
										onDispose {
											onViewed(item)
										}
									}
								}

								if (exception == null) {
									if (html != null) {
										WebViewPageContent(
											html = html!!,
											progress = item.readingPosition,
											onScroll = {
												onScroll(item, it)
											},
											onFocusToggle = {
												isFocused = !isFocused
											},
											onHitBottom = {

											},
										)
									} else {
										Box(
											modifier = Modifier.background(Color.Black)
												.fillMaxSize()
										) {}
									}
								} else {
									ErrorContent(
										exception!!.message ?: "Unknown error",
										EmptyDataView.Action(R.string.retry) {
											retryChapter(item)
										}
									)
								}
							}
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

		if (isHorizontal) {
			HorizontalPager(
				count = count,
				state = pagerState,
				modifier = Modifier.fillMaxSize().padding(
					top = paddingValues.calculateTopPadding(),
					bottom = if (isFocused) 0.dp else 56.dp
				),
				reverseLayout = isSwipeInverted
			) { page ->
				createPage(page)
			}
		} else {
			VerticalPager(
				count = count,
				state = pagerState,
				modifier = Modifier.fillMaxSize().padding(
					top = paddingValues.calculateTopPadding(),
					bottom = if (isFocused) 0.dp else 56.dp
				)
			) { page ->
				createPage(page)
			}
		}

		if (!isFocused) {
			var isExpanded by remember { mutableStateOf(false) }

			Card(
				modifier = Modifier.offset {
					IntOffset(0, if (isExpanded) 0 else 756.dp.toPx().toInt())
				},
				elevation = AppBarDefaults.BottomAppBarElevation,
				shape = MaterialTheme.shapes.large
			) {
				Column {
					Row(
						modifier = Modifier.fillMaxWidth().height(56.dp),
						horizontalArrangement = Arrangement.SpaceBetween,
						verticalAlignment = Alignment.CenterVertically
					) {
						IconButton(onClick = {
							isFocused = !isFocused
						}) {
							Icon(
								painterResource(R.drawable.ic_baseline_visibility_off_24),
								null
							)
						}

						Row {
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

							if (isTTSCapable)
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
						}

						IconButton(onClick = {
							isExpanded = !isExpanded
						}) {
							Icon(
								if (isExpanded)
									painterResource(R.drawable.expand_more)
								else
									painterResource(R.drawable.expand_less),
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
			}
		}
	}
}