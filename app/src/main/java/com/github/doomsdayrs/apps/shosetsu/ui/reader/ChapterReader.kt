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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.Callback
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.unmarkMenuItems
import com.github.doomsdayrs.apps.shosetsu.common.Settings
import com.github.doomsdayrs.apps.shosetsu.common.Settings.MarkingTypes
import com.github.doomsdayrs.apps.shosetsu.common.Settings.TextSizes
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys.BUNDLE_CHAPTER_ID
import com.github.doomsdayrs.apps.shosetsu.common.consts.BundleKeys.BUNDLE_NOVEL_ID
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus.READING
import com.github.doomsdayrs.apps.shosetsu.common.ext.logID
import com.github.doomsdayrs.apps.shosetsu.common.ext.observe
import com.github.doomsdayrs.apps.shosetsu.common.ext.openInBrowser
import com.github.doomsdayrs.apps.shosetsu.common.ext.openInWebView
import com.github.doomsdayrs.apps.shosetsu.ui.reader.adapters.ChapterReaderAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions.*
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ReaderChapterUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IChapterReaderViewModel
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
	: AppCompatActivity(R.layout.chapter_reader), KodeinAware, LifecycleEventObserver {
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
			if (o.bookmarked != n.bookmarked) return false
			return true
		}
	}

	private val demarkActions = arrayOf(
			TextSizeChange(this),
			ParaSpacingChange(this),
			IndentChange(this),
			ReaderChange(this),
			ThemeChange(this)
	)

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
		viewModel.apply {
			setNovelID(intent.getIntExtra(BUNDLE_NOVEL_ID, -1))
			currentChapterID = intent.getIntExtra(BUNDLE_CHAPTER_ID, -1)
		}
		slidingUpPanelLayout.setGravity(Gravity.BOTTOM)
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

	private fun setupBottomMenu() {
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
					Settings.readerTextSize = when (progress) {
						0 -> TextSizes.SMALL.i
						1 -> TextSizes.MEDIUM.i
						2 -> TextSizes.LARGE.i
						else -> TextSizes.MEDIUM.i
					}
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
			@Suppress("CheckedExceptionsKotlin")
			post {
				slidingUpPanelLayout.apply {
					panelState = if (panelState == PanelState.HIDDEN) PanelState.COLLAPSED else PanelState.HIDDEN
				}
			}
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

	override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
		Log.d(logID(), "State changed")
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
				unmarkMenuItems(themes, 0, demarkActions[4])
				true
			}
			R.id.chapter_view_reader_light -> {
				unmarkMenuItems(themes, 1, demarkActions[4])
				true
			}
			R.id.chapter_view_reader_sepia -> {
				unmarkMenuItems(themes, 2, demarkActions[4])
				true
			}
			R.id.chapter_view_reader_dark -> {
				unmarkMenuItems(themes, 3, demarkActions[4])
				true
			}
			R.id.chapter_view_reader_gray -> {
				unmarkMenuItems(themes, 4, demarkActions[4])
				true
			}
			R.id.chapter_view_reader_custom -> {
				unmarkMenuItems(themes, 5, demarkActions[4])
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