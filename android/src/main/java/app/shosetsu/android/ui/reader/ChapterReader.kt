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
			viewModel.setCurrentChapterID(intent.getIntExtra(BUNDLE_CHAPTER_ID, -1))
		}

		super.onCreate(savedInstanceState)
		setContent {
			val currentChapterTitle by viewModel.currentChapterTitle.collectAsState(null)
			val items by viewModel.liveData.collectAsState(emptyList())
			val isHorizontalReading by viewModel.isHorizontalReading.collectAsState(false)
			val chapterType by viewModel.chapterType.collectAsState(null)
			val isLoading by viewModel.isMainLoading.collectAsState(true)
			val currentChapterID by viewModel.currentChapterID.collectAsState(-1)
			val isTTSCapable by isTTSCapable.collectAsState(false)
			val isTTSPlaying by isTTSPlaying.collectAsState(false)
			val setting by viewModel.getSettings()
				.collectAsState(NovelReaderSettingEntity(-1, 0, 0.0F))

			MdcTheme {
				ChapterReaderContent(
					currentChapterTitle ?: stringResource(R.string.loading),
					exit = {
						finish()
					},
					items = items,
					isHorizontal = isHorizontalReading,
					chapterType = chapterType,
					isInitalLoading = isLoading,
					currentChapterId = currentChapterID,
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
					onChapterRead = viewModel::updateChapterAsRead
				)
			}
		}

		// Show back button
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		//slidingUpPanelLayout.setGravity(Gravity.BOTTOM)
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
			currentChapterTitle = "Chapter 1",
			exit = {},
			items = emptyList(),
			isHorizontal = false,
			chapterType = ChapterType.HTML,
			currentChapterId = 0,
			getStringContent = { flow { } },
			getHTMLContent = { flow { } },
			onScroll = { a, b ->
			},
			isInitalLoading = false,
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
			onChapterRead = {}
		)
	}
}

@OptIn(ExperimentalMaterialApi::class, com.google.accompanist.pager.ExperimentalPagerApi::class)
@Composable
fun ChapterReaderContent(
	currentChapterTitle: String,
	exit: () -> Unit,
	items: List<ReaderUIItem<*, *>>,
	isHorizontal: Boolean,
	isInitalLoading: Boolean,
	chapterType: ChapterType?,
	currentChapterId: Int,
	markChapterAsCurrent: (item: ReaderChapterUI) -> Unit,
	onChapterRead: (item: ReaderChapterUI) -> Unit,
	getStringContent: (item: ReaderChapterUI) -> Flow<String>,
	getHTMLContent: (item: ReaderChapterUI) -> Flow<String>,
	onScroll: (item: ReaderChapterUI, perc: Double) -> Unit,
	onPlayTTS: () -> Unit,
	onStopTTS: () -> Unit,
	isTTSPlaying: Boolean,
	isTTSCapable: Boolean,
	toggleBookmark: (item: ReaderChapterUI) -> Unit,
	toggleRotationLock: () -> Unit,
	setting: NovelReaderSettingEntity,
	updateSetting: (NovelReaderSettingEntity) -> Unit,
	lowerSheet: @Composable () -> Unit
) {
	var isFocused by remember { mutableStateOf(false) }
	val scaffoldState = rememberBottomSheetScaffoldState()

	BottomSheetScaffold(
		topBar = {
			if (!isFocused)
				TopAppBar(
					navigationIcon = {
						IconButton(onClick = exit) {
							Icon(Icons.Filled.ArrowBack, null)
						}
					},
					title = {
						Text(currentChapterTitle, maxLines = 2)
					},
					modifier = Modifier.alpha(READER_BAR_ALPHA)
				)
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
								//TODO toggle focus
							}) {
								Icon(
									painterResource(R.drawable.ic_baseline_visibility_off_24),
									null
								)
							}

							Row {

								IconButton(onClick = {
									//TODO toggle bookmark
								}) {
									Icon(
										painterResource(R.drawable.empty_bookmark),
										null
									)
								}

								IconButton(onClick = {
									//TODO toggle rotation
								}) {
									Icon(
										painterResource(R.drawable.ic_baseline_screen_rotation_24),
										null
									)
								}

								IconButton(onClick = {
									//TODO start tts
								}) {
									Icon(
										painterResource(R.drawable.ic_baseline_audiotrack_24),
										null
									)
								}
							}


							IconButton(onClick = {
								//TODO toggle drawer
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
				// TODO Fill
				// Use READER_BAR_ALPHA
				items
					.filterIsInstance<ReaderChapterUI>()
					.find { it.id == currentChapterId }
					?.let {
						if (it.bookmarked)
							R.drawable.filled_bookmark
						else R.drawable.empty_bookmark
					}
			}
		},
		scaffoldState = scaffoldState,
		sheetGesturesEnabled = !isFocused,
		sheetPeekHeight = 82.dp
	) {
		val pagerState =
			rememberPagerState(items.indexOfFirst { it.identifier == currentChapterId.toLong() }
				.takeIf { it != -1 } ?: 0)
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
				}
			}

		@Composable
		fun createPage(page: Int) {
			when (val item = items[page]) {
				is ReaderChapterUI -> {
					when (chapterType) {
						ChapterType.STRING -> {

						}
						ChapterType.HTML -> {
							val html by getHTMLContent(item).collectAsState("")
							WebViewPageContent(html, item.readingPosition, onScroll = {
								onScroll(item, it)
							}, onFocusToggle = {
								isFocused = !isFocused
							},
								onHitBottom = {

								})
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
				onScroll((state.maxValue / state.value).toDouble())
			}
		}

	Text(
		content,
		fontSize = textSize.sp,
		lineHeight = paragraphSpacing.sp,
		modifier = Modifier.fillMaxSize().verticalScroll(state).clickable {
			onFocusToggle()
		}
	)

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