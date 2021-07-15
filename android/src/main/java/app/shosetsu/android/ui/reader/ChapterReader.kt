package app.shosetsu.android.ui.reader

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.*
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_CHAPTER_ID
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_NOVEL_ID
import app.shosetsu.android.common.consts.READER_BAR_ALPHA
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.reader.types.base.ReaderChapterViewHolder
import app.shosetsu.android.view.uimodels.model.reader.ReaderChapterUI
import app.shosetsu.android.view.uimodels.model.reader.ReaderDividerUI
import app.shosetsu.android.view.uimodels.model.reader.ReaderUIItem
import app.shosetsu.android.view.uimodels.settings.base.SettingsItemData
import app.shosetsu.android.viewmodel.abstracted.AChapterReaderViewModel
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.handle
import app.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ActivityReaderBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItemVHFactory
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil.calculateDiff
import com.mikepenz.fastadapter.listeners.OnCreateViewHolderListenerImpl
import com.skydoves.colorpickerview.ColorPickerDialog
import kotlinx.coroutines.delay
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI


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

	private val toolbar: MaterialToolbar
		get() = binding.toolbar

	private val chapterReaderBottom: LinearLayout
		get() = binding.chapterReaderBottom.chapterReaderBottom

	private val viewpager: ViewPager2
		get() = binding.viewpager

	private val drawerToggle: AppCompatImageButton
		get() = binding.chapterReaderBottom.drawerToggle

	private val bottomMenuRecycler: RecyclerView
		get() = binding.chapterReaderBottom.recyclerView


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
	}

	/** On Destroy */
	override fun onDestroy() {
		logV("")
		viewpager.unregisterOnPageChangeCallback(pageChangeCallback)
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
		viewModel.liveData.observe { result ->
			result.handle(
				onLoading = {
					logD("Loading chapters")
				},
				onError = {
					logE("Error occured while loading chapters", it.exception)
				},
				onEmpty = {
					logD("Recieved an empty result")
				}
			) {
				handleChaptersResult(it)
			}
		}

		viewModel.liveTheme.observe {
			applyToChapterViews {
				syncTextColor()
				syncBackgroundColor()
			}
		}

		viewModel.liveIndentSize.observe {
			applyToChapterViews {
				syncParagraphIndent()
			}
		}

		viewModel.liveParagraphSpacing.observe {
			logD("Updating paragraph spacing to reader UI")
			applyToChapterViews { syncParagraphSpacing() }
		}

		viewModel.liveTextSize.observe {
			applyToChapterViews { syncTextSize() }
		}

		viewModel.liveVolumeScroll.observe {
		}

		viewModel.liveChapterDirection.observe {
			viewpager.orientation = if (it) ORIENTATION_HORIZONTAL else ORIENTATION_VERTICAL
		}

		viewModel.liveKeepScreenOn.observe {
			binding.root.keepScreenOn = it
		}
		viewModel.liveIsScreenRotationLocked.observe {
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
				ColorPickerDialog.Builder(context)
					.setPositiveButton("") { _, _ ->
						logD("Clicked")
					}
					.show()
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

		bottomMenuRecycler.apply {
			val itemAdapter = ItemAdapter<SettingsItemData>()
			adapter = FastAdapter.with(itemAdapter)
			viewModel.getSettings().handleObserve { newList ->
				FastAdapterDiffUtil[itemAdapter] = calculateDiff(itemAdapter, newList)
			}
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

		toolbar.isVisible = if (toolbar.isVisible) {
			toast("hidden")
			logV("hidden")
			chapterReaderBottom.isVisible = false
			false
		} else {
			toast("shown")
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

	private fun <T> LiveData<T>.observe(observer: (T) -> Unit) =
		observe(this@ChapterReader, observer)

	private inline fun <reified T> LiveData<HResult<T>>.handleObserve(crossinline observer: (T) -> Unit) =
		handleObserve(this@ChapterReader, onSuccess = observer)

	inner class ChapterReaderPageChange : OnPageChangeCallback() {
		override fun onPageSelected(position: Int) {
			onPageSelected(position, false)
		}

		private fun onPageSelected(position: Int, retry: Boolean) {
			logV("New position: $position")
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
						viewModel.updateChapter(
							lastChapter.copy(
								readingStatus = ReadingStatus.READ,
								readingPosition = 0.0
							)
						)
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

