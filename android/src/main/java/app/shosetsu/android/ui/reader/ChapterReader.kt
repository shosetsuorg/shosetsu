package app.shosetsu.android.ui.reader

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.KeyEvent
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_CHAPTER_ID
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_NOVEL_ID
import app.shosetsu.android.common.consts.READER_BAR_ALPHA
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.reader.types.model.ShosetsuScript
import app.shosetsu.android.ui.reader.types.model.getMaxJson
import app.shosetsu.android.view.compose.DiscreteSlider
import app.shosetsu.android.view.compose.setting.GenericBottomSettingLayout
import app.shosetsu.android.view.uimodels.model.reader.ReaderChapterUI
import app.shosetsu.android.view.uimodels.model.reader.ReaderDividerUI
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem
import app.shosetsu.android.viewmodel.abstracted.AChapterReaderViewModel
import app.shosetsu.android.viewmodel.impl.settings.*
import app.shosetsu.common.consts.settings.SettingKey
import app.shosetsu.common.domain.model.local.NovelReaderSettingEntity
import app.shosetsu.lib.Novel.ChapterType
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.VerticalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewStateWithHTMLData
import com.google.android.material.composethemeadapter.MdcTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
			val isLoading by viewModel.isMainLoading.collectAsState(true)
			val currentChapterID by viewModel.currentChapterID.collectAsState(-1)
			val isTTSCapable by isTTSCapable.collectAsState(false)
			val isTTSPlaying by isTTSPlaying.collectAsState(false)
			val setting by viewModel.getSettings()
				.collectAsState(NovelReaderSettingEntity(-1, 0, 0.0F))
			val currentPage by viewModel.currentPage.collectAsState(null)
			MdcTheme {
				ChapterReaderContent(
					currentTitle ?: stringResource(R.string.loading),
					exit = {
						finish()
					},
					items = items,
					isHorizontal = isHorizontalReading,
					chapterType = chapterType,
					isInitialLoading = isLoading,
					getStringContent = viewModel::getChapterStringPassage,
					getHTMLContent = viewModel::getChapterHTMLPassage,
					onScroll = { item, percentage ->
						viewModel.markAsReadingOnScroll(item, percentage)
					},
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
						viewModel.textSizeOption()
						viewModel.tapToScrollOption()
						viewModel.volumeScrollingOption()
						viewModel.horizontalSwitchOption()
						viewModel.continuousScrollOption()
						viewModel.invertChapterSwipeOption()
						viewModel.readerKeepScreenOnOption()
						viewModel.showReaderDivider()
						viewModel.stringAsHtmlOption()
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
					paragraphSpacingFlow = viewModel.liveParagraphSpacing,
					textSizeFlow = viewModel.liveTextSize
				)
			}
		}

		viewModel.liveIsScreenRotationLocked.collectLA(this, catch = {}) {
			if (it)
				lockRotation()
			else unlockRotation()
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
		return if (viewModel.defaultVolumeScroll)
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
			title = "Chapter 1",
			exit = {},
			items = emptyList(),
			isHorizontal = false,
			chapterType = ChapterType.HTML,
			getStringContent = { flow { } },
			getHTMLContent = { flow { } },
			onScroll = { a, b ->
			},
			isInitialLoading = true,
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
			paragraphSpacingFlow = flow { },
			textSizeFlow = flow { },
		)
	}
}

@OptIn(ExperimentalMaterialApi::class, com.google.accompanist.pager.ExperimentalPagerApi::class)
@Composable
fun ChapterReaderContent(
	title: String,
	exit: () -> Unit,
	items: List<ReaderUIItem<*, *>>,
	isHorizontal: Boolean,
	isInitialLoading: Boolean,
	chapterType: ChapterType?,

	currentPage: Int?,
	onPageChanged: (Int) -> Unit,

	markChapterAsCurrent: (item: ReaderChapterUI) -> Unit,
	onChapterRead: (item: ReaderChapterUI) -> Unit,

	getStringContent: (item: ReaderChapterUI) -> Flow<String>,
	getHTMLContent: (item: ReaderChapterUI) -> Flow<String>,

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
	lowerSheet: @Composable () -> Unit,

	textSizeFlow: Flow<Float>,
	paragraphSpacingFlow: Flow<Float>,
) {
	var isFocused by remember { mutableStateOf(false) }
	val scaffoldState = rememberBottomSheetScaffoldState()
	val coroutineScope = rememberCoroutineScope()

	BottomSheetScaffold(
		topBar = {
			if (!isFocused)
				Row {
					TopAppBar(
						navigationIcon = {
							IconButton(onClick = exit) {
								Icon(Icons.Filled.ArrowBack, null)
							}
						},
						title = {
							Text(title, maxLines = 2, modifier = Modifier.padding(end = 16.dp))
						},
						modifier = Modifier.alpha(READER_BAR_ALPHA)
					)

				}
		},
		sheetContent = {
			if (!isFocused) {
				Card(
					shape = MaterialTheme.shapes.large
				) {
					Column(
						modifier = Modifier.padding(16.dp)
					) {
						Row(
							modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
							horizontalArrangement = Arrangement.SpaceBetween
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
							}

							IconButton(onClick = {
								coroutineScope.launch {
									if (scaffoldState.bottomSheetState.isCollapsed)
										scaffoldState.bottomSheetState.expand()
									else scaffoldState.bottomSheetState.collapse()
								}
							}) {
								Icon(
									painterResource(R.drawable.expand_less),
									null
								)
							}
						}
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
						lowerSheet()
					}
				}
			}
		},
		scaffoldState = scaffoldState,
		sheetGesturesEnabled = !isFocused,
		sheetPeekHeight = 82.dp
	) {
		// Do not create the pager if the currentPage has not been set yet
		if (currentPage == null) return@BottomSheetScaffold

		val pagerState =
			rememberPagerState(currentPage)

		val count = items.size

		var curChapter: ReaderChapterUI? by remember { mutableStateOf(null) }

		if (pagerState.isScrollInProgress)
			DisposableEffect(Unit) {
				onDispose {
					onStopTTS()
					val item = items[pagerState.currentPage]

					if (item is ReaderChapterUI) {
						markChapterAsCurrent(item)
						curChapter = item
					} else {
						curChapter?.let(onChapterRead)
					}
					onPageChanged(pagerState.currentPage)
				}
			}

		@Composable
		fun createPage(page: Int) {
			when (val item = items[page]) {
				is ReaderChapterUI -> {
					when (chapterType) {
						ChapterType.STRING -> {
							val content by getStringContent(item).collectAsState("")
							val textSize by textSizeFlow.collectAsState(SettingKey.ReaderTextSize.default)
							val paragraphSpacing by paragraphSpacingFlow.collectAsState(SettingKey.ReaderParagraphSpacing.default)

							StringPageContent(
								content,
								item.readingPosition,
								textSize = textSize,
								paragraphSpacing = paragraphSpacing,
								onScroll = {
									onScroll(item, it)
								},
								onFocusToggle = {
									isFocused = !isFocused
								},
								isHorizontal = isHorizontal
							)
						}
						ChapterType.HTML -> {
							val html by getHTMLContent(item).collectAsState("")

							WebViewPageContent(
								html = html,
								progress = item.readingPosition,
								onScroll = {
									onScroll(item, it)
								},
								onFocusToggle = {
									isFocused = !isFocused
								},
								onHitBottom = {

								}
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

		if (isHorizontal) {
			HorizontalPager(
				count = count,
				state = pagerState,
				modifier = Modifier.fillMaxSize(),
			) { page ->
				createPage(page)
			}
		} else {
			VerticalPager(
				count = count,
				state = pagerState,
				modifier = Modifier.fillMaxSize()
			) { page ->
				createPage(page)
			}
		}
	}
}

@Preview
@Composable
fun PreviewDividerPageContent() {
	DividierPageContent(
		"The first",
		"The second"
	)
}

@Composable
fun DividierPageContent(
	previous: String,
	next: String?
) {
	Box(
		modifier = Modifier.fillMaxSize().background(Color.Black)
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center,
			modifier = Modifier.fillMaxSize().padding(16.dp)
		) {

			if (next != null) {
				Text(stringResource(R.string.reader_last_chapter, previous))
				Text(stringResource(R.string.reader_next_chapter, next))
			} else {
				Text(stringResource(R.string.no_more_chapters))
			}
		}
	}
}

@Composable
fun StringPageContent(
	content: String,
	progress: Double,
	textSize: Float,
	paragraphSpacing: Float,
	onScroll: (perc: Double) -> Unit,
	onFocusToggle: () -> Unit,
	isHorizontal: Boolean
) {
	val state = rememberScrollState()

	if (state.isScrollInProgress)
		DisposableEffect(Unit) {
			onDispose {
				if (state.value != 0)
					onScroll((state.maxValue / state.value).toDouble())
				else onScroll(0.0)
			}
		}

	SelectionContainer(
		modifier = Modifier.clickable {
			onFocusToggle()
		}
	) {
		Text(
			content,
			fontSize = textSize.sp,
			modifier = Modifier.fillMaxSize().verticalScroll(state),
		)
	}

	LaunchedEffect(Unit) {
		launch {
			state.scrollTo((state.maxValue * progress).toInt())
		}
	}
}

@Composable
fun WebViewPageContent(
	html: String,
	progress: Double,
	onScroll: (perc: Double) -> Unit,
	onFocusToggle: () -> Unit,
	onHitBottom: () -> Unit
) {
	val state = rememberWebViewStateWithHTMLData(html)
	val blackColor = colorResource(android.R.color.black)

	Box {
		WebView(
			state,
			captureBackPresses = false,
			onCreated = { webView ->
				webView.setBackgroundColor(blackColor.toArgb())
				@SuppressLint("SetJavaScriptEnabled")
				webView.settings.javaScriptEnabled = true

				val inter = ShosetsuScript(
					webView,
					onHitBottom = onHitBottom,
					onScroll = onScroll
				)

				inter.onClickMethod = onFocusToggle

				webView.addJavascriptInterface(inter, "shosetsuScript")

				webView.webViewClient = object : WebViewClient() {
					override fun onPageFinished(view: WebView?, url: String?) {
						super.onPageFinished(view, url)
						webView.evaluateJavascript(
							"""
						window.addEventListener("scroll",(event)=>{ shosetsuScript.onScroll(); });
						window.addEventListener("click",(event)=>{ shosetsuScript.onClick(); });
					""".trimIndent(), null
						)

						webView.evaluateJavascript(getMaxJson) { maxString ->
							maxString.toDoubleOrNull()?.let { maxY ->
								webView.evaluateJavascript(
									"window.scrollTo(0,${(maxY * (progress / 100)).toInt()})",
									null
								)
							}
						}
					}
				}
			},
			modifier = Modifier.fillMaxSize()
		)
	}

}