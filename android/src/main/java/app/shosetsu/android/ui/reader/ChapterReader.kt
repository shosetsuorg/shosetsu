package app.shosetsu.android.ui.reader

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
import app.shosetsu.android.viewmodel.abstracted.IChapterReaderViewModel
import app.shosetsu.common.dto.HResult
import app.shosetsu.common.dto.handle
import app.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.ActivityReaderBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil.calculateDiff
import com.skydoves.colorpickerview.ColorPickerDialog
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein

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
	: AppCompatActivity(), KodeinAware {
	override val kodein: Kodein by closestKodein()
	internal val viewModel: IChapterReaderViewModel by viewModel()
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
	private val fastAdapter by lazy { FastAdapter.with(itemAdapter) }
	private val bookmark
		get() = binding.chapterReaderBottom.bookmark
	private val themeSelect
		get() = binding.chapterReaderBottom.themeSelect

	/** Gets chapters from the [itemAdapter] */
	private val chapterItems: List<ReaderChapterUI>
		get() = itemAdapter.itemList.items.filterIsInstance<ReaderChapterUI>()

	/** Gets dividers from the [itemAdapter] */
	val dividerItems: List<ReaderDividerUI>
		get() = itemAdapter.itemList.items.filterIsInstance<ReaderDividerUI>()
	private val bottomSheetBehavior: ChapterReaderBottomBar<LinearLayout> by lazy {
		from(chapterReaderBottom) as ChapterReaderBottomBar
	}

	override fun onResume() {
		window.hideBar() // resumes fullscreen when returning to the view
		super.onResume()
	}

	/** On Create */
	public override fun onCreate(savedInstanceState: Bundle?) {
		logV("On Create")
		window.hideBar()
		viewModel.apply {
			setNovelID(intent.getIntExtra(BUNDLE_NOVEL_ID, -1))
			currentChapterID = intent.getIntExtra(BUNDLE_CHAPTER_ID, -1)
		}

		super.onCreate(savedInstanceState)
		setContentView(ActivityReaderBinding.inflate(layoutInflater).also { binding = it }.root)
		setSupportActionBar(toolbar as Toolbar)
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
		logD("Destroying")
		viewpager.unregisterOnPageChangeCallback(pageChangeCallback)
		super.onDestroy()
	}

	private fun handleChaptersResult(list: List<ReaderUIItem<*, *>>) {
		val oldSize = itemAdapter.itemList.size()
		list.forEach {
			if (it is ReaderChapterUI)
				it.chapterReader = this
		}

		FastAdapterDiffUtil[itemAdapter] =
			calculateDiff(itemAdapter, list)

		if (oldSize == 0)
			viewpager.setCurrentItem(getCurrentChapterIndex(), false)

	}

	private fun setObservers() {
		viewModel.liveData.observe { result ->
			result.handle(
				onLoading = {
					logD("Loading")
				}
			) {
				handleChaptersResult(it)
			}
		}

		viewModel.liveTheme.observe { (t, b) ->
			viewModel.defaultForeground = t
			viewModel.defaultBackground = b

			applyToReaders {
				syncTextColor()
				syncBackgroundColor()
			}
		}

		viewModel.liveIndentSize.observe { i ->
			viewModel.defaultIndentSize = i
			applyToReaders {
				syncParagraphIndent()
			}
		}

		viewModel.liveParagraphSpacing.observe { i ->
			viewModel.defaultParaSpacing = i
			applyToReaders {
				syncParagraphSpacing()
			}
		}

		viewModel.liveTextSize.observe { i ->
			viewModel.defaultTextSize = i
			applyToReaders { syncTextSize() }
		}


		viewModel.liveVolumeScroll.observe {
			viewModel.defaultVolumeScroll = it
		}

		viewModel.liveChapterDirection.observe {
			viewpager.orientation = if (it) ORIENTATION_HORIZONTAL else ORIENTATION_VERTICAL
		}
	}

	private fun applyToReaders(
		onlyCurrent: Boolean = false,
		action: ReaderChapterViewHolder.() -> Unit
	) {
		val textTypedReaders = chapterItems.mapNotNull { it.reader }
		textTypedReaders.find {
			it.chapter.id == viewModel.currentChapterID
		}?.action()

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
		bookmark.setImageResource(
			if (readerChapterUI.bookmarked)
				R.drawable.filled_bookmark
			else R.drawable.empty_bookmark
		)
	}

	private fun setupBottomMenu() {
		bookmark.apply {
			setOnClickListener {
				getCurrentChapter()?.apply {
					bookmarked = !bookmarked
					viewModel.updateChapter(this)
					setBookmarkIcon(this)
				}
			}
		}
		themeSelect.apply {
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
		logV("Setting up ViewPager")
		viewpager.apply {
			adapter = fastAdapter
			registerOnPageChangeCallback(pageChangeCallback)
			orientation = ORIENTATION_VERTICAL
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
		if (viewModel.allowVolumeScroll())
			when (keyCode) {
				KeyEvent.KEYCODE_VOLUME_DOWN -> {
					applyToReaders(true) {
						incrementScroll()
					}
					return true
				}
				KeyEvent.KEYCODE_VOLUME_UP -> {
					applyToReaders(true) {
						depleteScroll()
					}
					return true
				}
			}
		return super.onKeyDown(keyCode, event)
	}

	private fun focusListener(view: View) {

		toolbar.isVisible = if (toolbar.isVisible) {
			toast("hidden")
			chapterReaderBottom.isVisible = false
			false
		} else {
			toast("shown")
			chapterReaderBottom.isVisible = true
			true
		}


		bottomSheetBehavior.state = when (bottomSheetBehavior.state) {
			STATE_HIDDEN -> STATE_COLLAPSED
			else -> STATE_HIDDEN
		}
	}

	fun syncReader(typedReaderViewHolder: ReaderChapterViewHolder) = typedReaderViewHolder.apply {
		chapterReader = this@ChapterReader
		syncBackgroundColor()
		syncTextColor()
		syncTextSize()
		syncTextPadding()
		getFocusTarget()?.setOnClickListener {
			logI("Click")
			focusListener(it)
		} ?: logE("Returned target was null")
	}

	private fun <T> LiveData<T>.observe(observer: (T) -> Unit) =
		observe(this@ChapterReader, observer)

	private inline fun <reified T> LiveData<HResult<T>>.handleObserve(crossinline observer: (T) -> Unit) =
		handleObserve(this@ChapterReader, onSuccess = observer)

	inner class ChapterReaderPageChange : OnPageChangeCallback() {
		override fun onPageSelected(position: Int) {
			when (val item = itemAdapter.getAdapterItem(position)) {
				is ReaderChapterUI -> {
					item.apply {
						logD("Page changed to $position ${this.link}")
						viewModel.currentChapterID = id
						viewModel.markAsReadingOnView(this)    // Mark read if set to onview
						reader?.let { syncReader(it) } ?: logE("Reader is null")
						supportActionBar?.title = title
						setBookmarkIcon(this)
					}
				}
				is ReaderDividerUI -> {
					supportActionBar?.setTitle(R.string.next_chapter)
					val lastChapter = itemAdapter.getAdapterItem(position - 1) as ReaderChapterUI

					// Marks the previous chapter as read when you hit the divider
					// This was implemented due to performance shortcuts taken due to excessive
					// [handleChaptersResult] operation time
					viewModel.updateChapter(
						lastChapter,
						readingStatus = ReadingStatus.READ,
						readingPosition = 0
					)
				}
			}
		}
	}
}

