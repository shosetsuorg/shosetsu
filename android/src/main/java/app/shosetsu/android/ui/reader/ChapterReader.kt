package app.shosetsu.android.ui.reader

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.*
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_CHAPTER_ID
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_NOVEL_ID
import app.shosetsu.android.common.consts.READER_BAR_ALPHA
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.reader.types.base.ReaderChapterViewHolder
import app.shosetsu.android.view.compose.DiscreteSlider
import app.shosetsu.android.view.compose.setting.GenericBottomSettingLayout
import app.shosetsu.android.view.uimodels.model.reader.ReaderChapterUI
import app.shosetsu.android.view.uimodels.model.reader.ReaderDividerUI
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem
import app.shosetsu.android.viewmodel.abstracted.AChapterReaderViewModel
import app.shosetsu.android.viewmodel.impl.settings.*
import app.shosetsu.common.domain.model.local.NovelReaderSettingEntity
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ActivityReaderBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.google.android.material.composethemeadapter.MdcTheme
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItemVHFactory
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil.calculateDiff
import com.mikepenz.fastadapter.listeners.OnCreateViewHolderListenerImpl
import kotlinx.coroutines.delay
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
	private lateinit var binding: ActivityReaderBinding

	private val ttsInitListener: TextToSpeech.OnInitListener by lazy {
		TextToSpeech.OnInitListener {
			when (it) {
				TextToSpeech.SUCCESS -> {
					val result = tts.setLanguage(Locale.getDefault())

					if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
						logE("Language not supported for TTS")
						binding.chapterReaderBottom.ttsPlay.isEnabled = false
					} else {
						binding.chapterReaderBottom.ttsPlay.isEnabled = true
					}
				}
				else -> {
					logE("TTS Initialization failed")
					binding.chapterReaderBottom.ttsPlay.isEnabled = false
				}
			}
		}
	}

	private val tts: TextToSpeech by lazy {
		TextToSpeech(this, ttsInitListener)
	}

	private val toolbar: MaterialToolbar
		get() = binding.toolbar

	private val chapterReaderBottom: LinearLayout
		get() = binding.chapterReaderBottom.chapterReaderBottom

	private val viewpager: ViewPager2
		get() = binding.viewpager

	private val drawerToggle: AppCompatImageButton
		get() = binding.chapterReaderBottom.drawerToggle


	private val pageChangeCallback: OnPageChangeCallback by lazy { ChapterReaderPageChange() }
	private val itemAdapter by lazy { ItemAdapter<ReaderUIItem<*, *>>() }

	private var chapterViewHolders = ArrayList<ReaderChapterViewHolder?>()

	private val viewHolderListener = object : OnCreateViewHolderListenerImpl<ReaderUIItem<*, *>>() {
		override fun onPostCreateViewHolder(
			fastAdapter: FastAdapter<ReaderUIItem<*, *>>,
			viewHolder: RecyclerView.ViewHolder,
			itemVHFactory: IItemVHFactory<*>
		): RecyclerView.ViewHolder {
			chapterViewHolders.removeAll { it == null }
			if (viewHolder is ReaderChapterViewHolder) chapterViewHolders.add(viewHolder)

			return super.onPostCreateViewHolder(fastAdapter, viewHolder, itemVHFactory)
		}
	}
	private val fastAdapter by lazy {
		FastAdapter.with(itemAdapter).apply {
			this.onCreateViewHolderListener = viewHolderListener
		}
	}

	private val bookmarkButton
		get() = binding.chapterReaderBottom.bookmark

	private val rotationLockButton
		get() = binding.chapterReaderBottom.rotationLockButton

	private val themeSelectButton
		get() = binding.chapterReaderBottom.themeSelect

	/** Gets chapters from the [itemAdapter] */
	private val chapterItems: List<ReaderChapterUI>
		get() = itemAdapter.adapterItems.filterIsInstance<ReaderChapterUI>()

	/** Gets dividers from the [itemAdapter] */
	val dividerItems: List<ReaderDividerUI>
		get() = itemAdapter.adapterItems.filterIsInstance<ReaderDividerUI>()

	private val bottomSheetBehavior: ChapterReaderBottomBar<LinearLayout> by lazy {
		from(chapterReaderBottom) as ChapterReaderBottomBar
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
			currentChapterID = intent.getIntExtra(BUNDLE_CHAPTER_ID, -1)
		}

		super.onCreate(savedInstanceState)
		setContentView(ActivityReaderBinding.inflate(layoutInflater).also { binding = it }.root)
		setSupportActionBar(toolbar as Toolbar)

		// Show back button
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		//slidingUpPanelLayout.setGravity(Gravity.BOTTOM)

		setupViewPager()
		setupBottomMenu()
		setObservers()

		toolbar.alpha = READER_BAR_ALPHA
		chapterReaderBottom.alpha = READER_BAR_ALPHA

		binding.toggleVisibility.setOnClickListener {
			focusListener()
		}
	}

	/** On Destroy */
	override fun onDestroy() {
		logV("")
		viewpager.unregisterOnPageChangeCallback(pageChangeCallback)
		tts.stop()
		tts.shutdown()
		super.onDestroy()
	}

	private fun handleChaptersResult(list: List<ReaderUIItem<*, *>>) {
		val oldSize = itemAdapter.itemList.size()

		// Provide each entity with self
		list.filterIsInstance<ReaderChapterUI>().forEach {
			it.chapterReader = this
		}

		// Update the UI
		FastAdapterDiffUtil[itemAdapter] =
			calculateDiff(itemAdapter, list)

		// Go to the current chapter
		if (oldSize == 0)
			viewpager.setCurrentItem(getCurrentChapterIndex(), false)
	}

	private fun setObservers() {
		logD("Loading chapters")
		viewModel.liveData.collectLA(this, catch = {
			logE("Error occured while loading chapters", it)
		}) { result ->
			handleChaptersResult(result)
		}

		viewModel.liveTheme.collectLA(this, catch = {}) {
			applyToChapterViews {
				syncTextColor()
				syncBackgroundColor()
			}
		}

		viewModel.liveIndentSize.collectLA(this, catch = {}) {
			applyToChapterViews {
				syncParagraphIndent()
			}
		}

		viewModel.liveParagraphSpacing.collectLA(this, catch = {}) {
			logD("Updating paragraph spacing to reader UI")
			applyToChapterViews { syncParagraphSpacing() }
		}

		viewModel.liveTextSize.collectLA(this, catch = {}) {
			applyToChapterViews { syncTextSize() }
		}

		viewModel.liveVolumeScroll.collectLA(this, catch = {}) {
		}

		viewModel.liveChapterDirection.collectLA(this, catch = {}) {
			viewpager.orientation = if (it) ORIENTATION_HORIZONTAL else ORIENTATION_VERTICAL
		}

		viewModel.liveKeepScreenOn.collectLA(this, catch = {}) {
			binding.root.keepScreenOn = it
		}
		viewModel.liveIsScreenRotationLocked.collectLA(this, catch = {}) {
			if (it) {
				lockRotation()
				rotationLockButton.setImageResource(R.drawable.ic_baseline_screen_lock_rotation_24)
			} else {
				unlockRotation()
				rotationLockButton.setImageResource(R.drawable.ic_baseline_screen_rotation_24)
			}
		}
	}

	private fun applyToChapterViews(
		onlyCurrent: Boolean = false,
		action: ReaderChapterViewHolder.() -> Unit
	) {
		val textTypedReaders = chapterViewHolders.filterNotNull()
		// Apply to the current chapter first
		logD("Found ${textTypedReaders.map { it.chapter.id }}")
		textTypedReaders.find {
			it.chapter.id == viewModel.currentChapterID
		}?.action() ?: logE("Did not find current chapter: ${viewModel.currentChapterID}")

		// Sets other views down
		if (!onlyCurrent)
			textTypedReaders.filter {
				it.chapter.id != viewModel.currentChapterID
			}.forEach {
				it.action()
			}
	}

	private fun getCurrentChapter(): ReaderChapterUI? =
		chapterItems.find {
			it.id == viewModel.currentChapterID
		}

	private fun getCurrentChapterIndex(): Int = itemAdapter.itemList.items.indexOfFirst {
		if (it is ReaderChapterUI)
			it.id == viewModel.currentChapterID
		else false
	}

	private fun setBookmarkIcon(readerChapterUI: ReaderChapterUI) {
		bookmarkButton.setImageResource(
			if (readerChapterUI.bookmarked)
				R.drawable.filled_bookmark
			else R.drawable.empty_bookmark
		)
	}

	private fun setupBottomMenu() {
		bookmarkButton.apply {
			setOnClickListener {
				getCurrentChapter()?.apply {
					bookmarked = !bookmarked
					viewModel.updateChapter(this)
					setBookmarkIcon(this)
				}
			}
		}

		rotationLockButton.apply {
			setOnClickListener {
				viewModel.toggleScreenRotationLock()
			}
		}

		themeSelectButton.apply {
			setOnClickListener {
				/*
				ColorPickerDialog.Builder(context)
					.setPositiveButton("") { _, _ ->
						logD("Clicked")
					}
					.show()
				*/
			}
		}

		bottomSheetBehavior.apply bsb@{
			isHideable = true
			isDraggable = false
			addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
				override fun onStateChanged(bottomSheet: View, newState: Int) {
					when (newState) {
						STATE_COLLAPSED -> {
							drawerToggle.setImageResource(R.drawable.expand_less)
						}
						STATE_EXPANDED -> {
							drawerToggle.setImageResource(R.drawable.expand_more)
						}
						else -> {
						}
					}
				}

				override fun onSlide(bottomSheet: View, slideOffset: Float) {
					drawerToggle.setImageResource(R.drawable.ic_baseline_drag_handle_24)
				}

			})
		}
		drawerToggle.apply {
			setOnClickListener {
				bottomSheetBehavior.state = when (bottomSheetBehavior.state) {
					STATE_EXPANDED -> STATE_COLLAPSED
					else -> STATE_EXPANDED
				}
			}
		}

		binding.chapterReaderBottom.recyclerView.setContent {
			val settings by viewModel.getSettings().collectAsState(
				NovelReaderSettingEntity(0, 0, 0f)
			)

			MdcTheme {
				Column {
					GenericBottomSettingLayout(
						stringResource(R.string.paragraph_spacing),
						"",
					) {
						DiscreteSlider(
							settings.paragraphSpacingSize,
							"${settings.paragraphSpacingSize}",
							{ it, a ->
								viewModel.updateSetting(
									settings.copy(
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
							settings.paragraphIndentSize,
							"${settings.paragraphIndentSize}",
							{
								viewModel.updateSetting(settings.copy(paragraphIndentSize = it))
							},
							0..10,
						)
					}
					viewModel.textSizeOption()
					viewModel.tapToScrollOption()
					viewModel.volumeScrollingOption()
					viewModel.horizontalSwitchOption()
					viewModel.continuousScrollOption()
					viewModel.invertChapterSwipeOption()
					viewModel.readerKeepScreenOnOption()
					viewModel.showReaderDivider()
					viewModel.stringAsHtmlOption()
				}
			}
		}

		binding.chapterReaderBottom.ttsPlay.setOnClickListener {
			applyToChapterViews(true) {
				tts.setPitch(viewModel.ttsPitch)
				tts.setSpeechRate(viewModel.ttsSpeed)
				this.playTTS(tts)
				binding.stopTts.isVisible = true
			}
		}
		binding.stopTts.setOnClickListener {
			stopTTS()
		}


		binding.chapterReaderBottom.drawerToggleVisibility.setOnClickListener {
			focusListener()
		}
	}

	private fun setupViewPager() {
		logV("")
		viewpager.apply {
			adapter = fastAdapter
			registerOnPageChangeCallback(pageChangeCallback)
			orientation =
				if (viewModel.isHorizontalReading) ORIENTATION_HORIZONTAL else ORIENTATION_VERTICAL
			isNestedScrollingEnabled = true
		}
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
			val url = chapterItems[viewModel.currentChapterID].link
			if (url.isNotEmpty())
				openInBrowser(url)
			true
		}
		R.id.webview -> {
			val url = chapterItems[viewModel.currentChapterID].link
			if (url.isNotEmpty())
				openInWebView(url)
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
					applyToChapterViews(true) { incrementScroll() }
					true
				}
				KeyEvent.KEYCODE_VOLUME_UP -> {
					applyToChapterViews(true) { depleteScroll() }
					true
				}
				else -> false
			}
		else super.onKeyDown(keyCode, event)
	}

	private fun focusListener() {
		binding.toggleVisibility.isVisible = !binding.toggleVisibility.isVisible

		toolbar.isVisible = if (toolbar.isVisible) {
			logV("hidden")
			chapterReaderBottom.isVisible = false
			false
		} else {
			logV("shown")
			chapterReaderBottom.isVisible = true
			true
		}

		bottomSheetBehavior.state = when (bottomSheetBehavior.state) {
			STATE_HIDDEN -> STATE_COLLAPSED
			else -> STATE_HIDDEN
		}
	}

	fun syncReader(typedReaderViewHolder: ReaderChapterViewHolder) = typedReaderViewHolder.apply {
		syncBackgroundColor()
		syncTextColor()
		syncTextSize()
		syncTextPadding()
		getFocusTarget {
			focusListener()
		}
	}

	private fun stopTTS() {
		binding.stopTts.isVisible = false
		tts.stop()
	}

	inner class ChapterReaderPageChange : OnPageChangeCallback() {
		override fun onPageSelected(position: Int) {
			onPageSelected(position, false)
		}

		private fun onPageSelected(position: Int, retry: Boolean) {
			logV("New position: $position")
			stopTTS()
			when (val item = itemAdapter.getAdapterItem(position)) {
				is ReaderChapterUI -> {
					logV("New is a Chapter")
					viewModel.currentChapterID = item.id
					viewModel.markAsReadingOnView(item)    // Mark read if set to onview
					chapterViewHolders.filterNotNull().find { it.chapter.id == item.id }
						?.let { syncReader(it) } ?: run {

						if (!retry) {
							logE("Reader is null, retry in 200ms")
						} else {
							logE("Reader is still null, aborting")
							return@run
						}
						launchIO {
							delay(200)
							launchUI { onPageSelected(position, true) }
						}
					}
					supportActionBar?.title = item.title
					setBookmarkIcon(item)
				}
				is ReaderDividerUI -> {
					logV("New is a Divider")
					viewModel.currentChapterID = -1
					supportActionBar?.setTitle(R.string.next_chapter)
					// Marks the previous chapter as read when you hit the divider
					// This was implemented due to performance shortcuts taken due to excessive
					// [handleChaptersResult] operation time
					(itemAdapter.getAdapterItem(position - 1) as? ReaderChapterUI)?.let { lastChapter ->
						viewModel.updateChapterAsRead(lastChapter)
					}

				}
			}
		}
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

