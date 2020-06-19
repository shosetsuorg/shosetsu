package com.github.doomsdayrs.apps.shosetsu.ui.reader

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.util.set
import androidx.core.view.postDelayed
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.Callback
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.Settings
import com.github.doomsdayrs.apps.shosetsu.common.Settings.MarkingTypes
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys.BUNDLE_CHAPTER_ID
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys.BUNDLE_NOVEL_ID
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus.READING
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.common.ext.observe
import com.github.doomsdayrs.apps.shosetsu.common.ext.openInBrowser
import com.github.doomsdayrs.apps.shosetsu.common.ext.openInWebView
import com.github.doomsdayrs.apps.shosetsu.ui.reader.adapters.ChapterReaderAdapter
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ReaderChapterUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IChapterReaderViewModel
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import com.xw.repo.BubbleSeekBar
import kotlinx.android.synthetic.main.chapter_reader.*
import kotlinx.android.synthetic.main.chapter_reader_bottom.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

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
 * ====================================================================
 */ /**
 * shosetsu
 * 13 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
class ChapterReader
	: AppCompatActivity(R.layout.chapter_reader), KodeinAware {
	private class RecyclerViewDiffer(
			val old: List<ReaderChapterUI>,
			val aNew: List<ReaderChapterUI>
	) : Callback() {
		override fun getOldListSize(): Int = old.size

		override fun getNewListSize(): Int = aNew.size

		override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
				old[oldItemPosition].id == aNew[newItemPosition].id

		override fun areContentsTheSame(
				oldItemPosition: Int,
				newItemPosition: Int
		): Boolean {
			val o: ReaderChapterUI = old[oldItemPosition]
			val n = aNew[newItemPosition]
			if (o.id != n.id) return false
			if (o.link != n.link) return false
			if (o.title != n.title) return false
			return true
		}
	}

	// Order of values. Night, Light, Sepia
	private lateinit var themes: Array<MenuItem>

	override val kodein: Kodein by closestKodein()
	internal val viewModel by instance<IChapterReaderViewModel>()
	private val chapterReaderAdapter: ChapterReaderAdapter = ChapterReaderAdapter(this)
	private val pageChangeCallback: OnPageChangeCallback = object : OnPageChangeCallback() {
		override fun onPageSelected(position: Int) {
			Log.d(logID(), "Page changed to $position ${chapters[position].link}")
			val chapterReaderUI = chapters[position]
			viewModel.currentChapterID = chapterReaderUI.id

			// Mark read if set to onview
			if (Settings.readerMarkingType == MarkingTypes.ONVIEW) {
				Log.d("ChapterReader", "Marking as reading by marking type")
				chapterReaderUI.readingStatus = READING
				viewModel.updateChapter(chapterReaderUI)
			}

			supportActionBar?.title = chapterReaderUI.title

			bookmark.setImageResource(
					if (chapterReaderUI.bookmarked)
						R.drawable.ic_bookmark_24dp
					else R.drawable.ic_bookmark_border_24dp
			)
		}
	}

	/**
	 * Chapters to display owo
	 */
	val chapters: ArrayList<ReaderChapterUI> = arrayListOf()

	public override fun onCreate(savedInstanceState: Bundle?) {
		Log.d(logID(), "On Create")
		window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE
		super.onCreate(savedInstanceState)
		setSupportActionBar(toolbar as Toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		slidingUpPanelLayout.setGravity(Gravity.BOTTOM)

		viewModel.apply {
			setNovelID(intent.getIntExtra(BUNDLE_NOVEL_ID, -1))
			currentChapterID = intent.getIntExtra(BUNDLE_CHAPTER_ID, -1)
		}

		setObservers()
		setupViewPager()
		setupBottomMenu()
	}

	override fun onDestroy() {
		Log.d(logID(), "Destroying")
		super.onDestroy()
		viewpager.unregisterOnPageChangeCallback(pageChangeCallback)
	}

	private fun setObservers() {
		viewModel.liveData.observe(this) {
			when (it) {
				is HResult.Loading -> {
					Log.d(logID(), "Loading")
				}
				is HResult.Empty -> {
				}
				is HResult.Error -> {
				}
				is HResult.Success -> {
					Log.d(logID(), "Loading complete, now displaying")

					val currentSize = chapters.size
					val dif = DiffUtil.calculateDiff(RecyclerViewDiffer(
							chapters,
							it.data
					))
					chapters.clear()
					chapters.addAll(it.data)
					if (currentSize == 0) {
						getCurrentChapter()?.let {
							supportActionBar?.title = it.title
						}
						setupViewPager()
					}
					dif.dispatchUpdatesTo(chapterReaderAdapter)
					//bookmark.setIcon(if (it.data.bookmarked)
					//	R.drawable.ic_bookmark_24dp
					//else
					//	R.drawable.ic_bookmark_border_24dp
					//)
				}
			}
		}
	}

	private fun getCurrentChapter() = chapters.find {
		it.id == viewModel.currentChapterID
	}

	private fun getCurrentChapterIndex() = chapters.indexOfFirst {
		it.id == viewModel.currentChapterID
	}

	private fun setBookmarkIcon(readerChapterUI: ReaderChapterUI) {
		bookmark.setImageResource(
				if (readerChapterUI.bookmarked)
					R.drawable.ic_bookmark_24dp
				else R.drawable.ic_bookmark_border_24dp
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

		text_size_bar?.apply {
			setCustomSectionTextArray { _, array ->
				array.apply {
					clear()
					this[0] = getString(R.string.small)
					this[1] = getString(R.string.medium)
					this[2] = getString(R.string.large)
				}
			}
			onProgressChangedListener = object : BubbleSeekBar.OnProgressChangedListener {
				override fun onProgressChanged(
						bubbleSeekBar: BubbleSeekBar?,
						progress: Int,
						progressFloat: Float,
						fromUser: Boolean
				) {
					if (fromUser) {
						val size = when (progress) {
							0 -> Settings.TextSizes.SMALL
							1 -> Settings.TextSizes.MEDIUM
							2 -> Settings.TextSizes.LARGE
							else -> Settings.TextSizes.MEDIUM
						}
						Log.i(logID(), "TextSize changed to ${size.name}")
						Settings.readerTextSize = size.i
						// Sets current view
						chapterReaderAdapter.textReaders.find {
							it.chapterID == viewModel.currentChapterID
						}?.let {
							it.textView.textSize = size.i
						}
						// Sets other views down
						chapterReaderAdapter.textReaders.filter {
							it.chapterID != viewModel.currentChapterID
						}.forEach {
							it.textView.textSize = size.i
						}
					}
				}

				override fun getProgressOnActionUp(
						bubbleSeekBar: BubbleSeekBar?,
						progress: Int,
						progressFloat: Float
				) {
				}

				override fun getProgressOnFinally(
						bubbleSeekBar: BubbleSeekBar?,
						progress: Int,
						progressFloat: Float,
						fromUser: Boolean
				) {

				}

			}
		}

		para_space_bar?.apply {
			setCustomSectionTextArray { _, array ->
				array.apply {
					clear()
					this[0] = getString(R.string.none)
					this[1] = getString(R.string.small)
					this[2] = getString(R.string.medium)
					this[3] = getString(R.string.large)
				}
			}
			onProgressChangedListener = object : BubbleSeekBar.OnProgressChangedListener {
				override fun onProgressChanged(
						bubbleSeekBar: BubbleSeekBar?,
						progress: Int,
						progressFloat: Float,
						fromUser: Boolean
				) {
					if (fromUser) {
						Log.i(logID(), "ParaSpace changed to $progress")
						Settings.readerParagraphSpacing = progress
						// Sets current view
						chapterReaderAdapter.textReaders.find {
							it.chapterID == viewModel.currentChapterID
						}?.let {
							it.bind()
						}
						// Sets other views down
						chapterReaderAdapter.textReaders.filter {
							it.chapterID != viewModel.currentChapterID
						}.forEach {
							it.bind()
						}
					}
				}

				override fun getProgressOnActionUp(
						bubbleSeekBar: BubbleSeekBar?,
						progress: Int,
						progressFloat: Float
				) {
				}

				override fun getProgressOnFinally(
						bubbleSeekBar: BubbleSeekBar?,
						progress: Int,
						progressFloat: Float,
						fromUser: Boolean
				) {

				}

			}
		}

		para_indent_bar?.apply {
			setCustomSectionTextArray { _, array ->
				array.apply {
					clear()
					this[0] = getString(R.string.none)
					this[1] = getString(R.string.small)
					this[2] = getString(R.string.medium)
					this[3] = getString(R.string.large)
				}
			}
			onProgressChangedListener = object : BubbleSeekBar.OnProgressChangedListener {
				override fun onProgressChanged(
						bubbleSeekBar: BubbleSeekBar?,
						progress: Int,
						progressFloat: Float,
						fromUser: Boolean
				) {
					if (fromUser) {
						Log.i(logID(), "IndentSize changed to $progress")
						Settings.readerIndentSize = progress
						// Sets current view
						chapterReaderAdapter.textReaders.find {
							it.chapterID == viewModel.currentChapterID
						}?.let {
							it.bind()
						}
						// Sets other views down
						chapterReaderAdapter.textReaders.filter {
							it.chapterID != viewModel.currentChapterID
						}.forEach {
							it.bind()
						}
					}
				}

				override fun getProgressOnActionUp(
						bubbleSeekBar: BubbleSeekBar?,
						progress: Int,
						progressFloat: Float
				) {
				}

				override fun getProgressOnFinally(
						bubbleSeekBar: BubbleSeekBar?,
						progress: Int,
						progressFloat: Float,
						fromUser: Boolean
				) {

				}

			}
		}
	}

	private fun setupViewPager() {
		Log.d(logID(), "Setting up ViewPager")
		viewpager.apply {
			adapter = chapterReaderAdapter
			registerOnPageChangeCallback(pageChangeCallback)
			currentItem = getCurrentChapterIndex()
			addItemDecoration(DividerItemDecoration(
					viewpager.context,
					viewpager.orientation
			))
		}
	}

	fun animateBottom() {
		chapter_reader_bottom?.apply {
			post {
				slidingUpPanelLayout.apply {
					val currentState = panelState!!
					Log.d(logID(), "Changing panelState from ${currentState.name} | $panelHeight")
					val state = when (currentState) {
						PanelState.HIDDEN -> {
							Log.d(logID(), "Hidden, making collapsed")
							PanelState.COLLAPSED
						}
						PanelState.ANCHORED -> {
							Log.d(logID(), "ANCHORED, making collapsed")
							PanelState.COLLAPSED
						}
						PanelState.COLLAPSED -> {
							Log.d(logID(), "COLLAPSED, making hidden")
							PanelState.HIDDEN
						}
						PanelState.DRAGGING -> {
							Log.d(logID(), "Dragging, making hidden")
							PanelState.HIDDEN
						}
						PanelState.EXPANDED -> {
							Log.d(logID(), "Expanded, making hidden")
							PanelState.HIDDEN
						}
					}
					panelState = state
					postDelayed(400) { fixHeight() }
				}
			}
		}
	}

	private fun fixHeight(): SlidingUpPanelLayout = slidingUpPanelLayout.apply {
		val state = panelState
		Log.d(logID(), "PanelState is now ${state.name}")
		when (state) {
			PanelState.HIDDEN -> panelHeight = 0
			PanelState.COLLAPSED -> panelHeight = 238
			PanelState.DRAGGING -> postDelayed(100) { fixHeight() }
			else -> Log.d(logID(), "Unknown state: $state")
		}
	}

	fun animateToolbar() {
		toolbar?.let {
			@Suppress("CheckedExceptionsKotlin")
			val animator: Animation = AnimationUtils.loadAnimation(
					it.context,
					if (it.visibility == VISIBLE)
						R.anim.slide_up
					else R.anim.slide_down
			)
			it.startAnimation(animator)
			it.post {
				it.visibility = if (it.visibility == VISIBLE) GONE else VISIBLE
			}
		}

	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.toolbar_chapter_view, menu)
		// Night mode
		run {
			themes = arrayOf(
					menu.findItem(R.id.chapter_view_reader_night),
					menu.findItem(R.id.chapter_view_reader_light),
					menu.findItem(R.id.chapter_view_reader_sepia),
					menu.findItem(R.id.chapter_view_reader_dark),
					menu.findItem(R.id.chapter_view_reader_gray),
					menu.findItem(R.id.chapter_view_reader_custom)

			)
			when (Settings.readerTheme) {
				0 -> themes[0].setChecked(true)
				1 -> themes[1].setChecked(true)
				2 -> themes[2].setChecked(true)
				3 -> themes[3].setChecked(true)
				4 -> themes[4].setChecked(true)
				5 -> themes[5].setChecked(true)
				else -> {
					Settings.readerTheme = 1
					themes[1].setChecked(true)
				}
			}
		}
		return true
	}

	/**
	 * What to do when an menu item is selected
	 *
	 * @param item item selected
	 * @return true if processed
	 */
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		Log.d(logID(), "Selected item: $item")
		return when (item.itemId) {
			android.R.id.home -> {
				finish()
				true
			}
			R.id.chapter_view_reader_night -> {
				true
			}
			R.id.chapter_view_reader_light -> {
				true
			}
			R.id.chapter_view_reader_sepia -> {
				true
			}
			R.id.chapter_view_reader_dark -> {
				true
			}
			R.id.chapter_view_reader_gray -> {
				true
			}
			R.id.chapter_view_reader_custom -> {
				true
			}
			R.id.browser -> {
				val url = chapters[viewModel.currentChapterID].link
				if (url.isNotEmpty())
					openInBrowser(url)
				true
			}
			R.id.webview -> {
				val url = chapters[viewModel.currentChapterID].link
				if (url.isNotEmpty())
					openInWebView(url)
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}
}