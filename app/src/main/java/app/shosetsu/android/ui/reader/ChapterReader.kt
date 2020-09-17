package app.shosetsu.android.ui.reader

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
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
import app.shosetsu.android.common.ShosetsuSettings
import app.shosetsu.android.common.ShosetsuSettings.MarkingTypes
import app.shosetsu.android.common.ShosetsuSettings.TextSizes
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_CHAPTER_ID
import app.shosetsu.android.common.consts.BundleKeys.BUNDLE_NOVEL_ID
import app.shosetsu.android.common.dto.HResult
import app.shosetsu.android.common.dto.handle
import app.shosetsu.android.common.enums.ReadingStatus.READING
import app.shosetsu.android.common.ext.*
import app.shosetsu.android.ui.reader.adapters.ChapterReaderAdapter
import app.shosetsu.android.view.uimodels.model.ReaderChapterUI
import app.shosetsu.android.viewmodel.abstracted.IChapterReaderViewModel
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.*
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.select.selectExtension
import com.skydoves.colorpickerview.ColorPickerDialog
import com.xw.repo.BubbleSeekBar
import kotlinx.android.synthetic.main.activity_chapter_reader.*
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
 */

/**
 * shosetsu
 * 13 / 12 / 2019
 */
class ChapterReader
	: AppCompatActivity(R.layout.activity_chapter_reader), KodeinAware {
	private class RecyclerViewDiffer(
			val old: List<ReaderChapterUI>,
			val aNew: List<ReaderChapterUI>,
	) : Callback() {
		override fun getOldListSize(): Int = old.size

		override fun getNewListSize(): Int = aNew.size

		override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
				old[oldItemPosition].id == aNew[newItemPosition].id

		override fun areContentsTheSame(
				oldItemPosition: Int,
				newItemPosition: Int,
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
	internal val shosetsuSettings: ShosetsuSettings by instance()

	private val chapterReaderAdapter: ChapterReaderAdapter = ChapterReaderAdapter(this)
	private val pageChangeCallback: OnPageChangeCallback = object : OnPageChangeCallback() {
		override fun onPageSelected(position: Int) {
			Log.d(logID(), "Page changed to $position ${chapters[position].link}")
			val chapterReaderUI = chapters[position]
			viewModel.currentChapterID = chapterReaderUI.id

			// Mark read if set to onview
			if (shosetsuSettings.readerMarkingType == MarkingTypes.ONVIEW) {
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

	private val bottomSheetBehavior by lazy {
		from(chapter_reader_bottom)
	}

	private val colorItemAdapter: ItemAdapter<ShosetsuSettings.ColorChoice> by lazy {
		ItemAdapter<ShosetsuSettings.ColorChoice>()
	}

	private val colorFastAdapter: FastAdapter<ShosetsuSettings.ColorChoice> by lazy {
		FastAdapter.with(colorItemAdapter)
	}

	override fun onResume() {
		window.hideBar()
		super.onResume()
	}

	/** On Create */
	public override fun onCreate(savedInstanceState: Bundle?) {
		Log.d(logID(), "On Create")
		window.hideBar()
		super.onCreate(savedInstanceState)
		setSupportActionBar(toolbar as Toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		//slidingUpPanelLayout.setGravity(Gravity.BOTTOM)

		viewModel.apply {
			setNovelID(intent.getIntExtra(BUNDLE_NOVEL_ID, -1))
			currentChapterID = intent.getIntExtra(BUNDLE_CHAPTER_ID, -1)
		}

		setObservers()
		setupViewPager()
		setupBottomMenu()
	}

	/** On Destroy */
	override fun onDestroy() {
		Log.d(logID(), "Destroying")
		super.onDestroy()
		viewpager.unregisterOnPageChangeCallback(pageChangeCallback)
	}

	private fun setObservers() {
		viewModel.liveData.observe(this) { result ->
			result.handle(
					onLoading = {
						Log.d(logID(), "Loading")
					}
			) {
				Log.d(logID(), "Loading complete, now displaying")

				val currentSize = chapters.size
				val dif = DiffUtil.calculateDiff(RecyclerViewDiffer(
						chapters,
						it
				))
				chapters.clear()
				chapters.addAll(it)
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

		shosetsuSettings.readerTextSizeLive.observe(this) { size ->
			// Sets current view
			chapterReaderAdapter.textReaders.find {
				it.chapter.id == viewModel.currentChapterID
			}?.let {
				it.setTextSize(size)
			}
			// Sets other views down
			chapterReaderAdapter.textReaders.filter {
				it.chapter.id != viewModel.currentChapterID
			}.forEach {
				it.setTextSize(size)
			}
		}

		shosetsuSettings.readerParagraphSpacingLive.observe(this) { pSpacing: Int ->
			// Sets current view
			chapterReaderAdapter.textReaders.find {
				it.chapter.id == viewModel.currentChapterID
			}?.setParagraphSpacing(pSpacing)

			// Sets other views down
			chapterReaderAdapter.textReaders.filter {
				it.chapter.id != viewModel.currentChapterID
			}.forEach {
				it.setParagraphSpacing(pSpacing)
			}
		}

		shosetsuSettings.readerIndentSizeLive.observe(this) { indentSize: Int ->
			// Sets current view
			chapterReaderAdapter.textReaders.find {
				it.chapter.id == viewModel.currentChapterID
			}?.setParagraphIndent(indentSize)

			// Sets other views down
			chapterReaderAdapter.textReaders.filter {
				it.chapter.id != viewModel.currentChapterID
			}.forEach {
				it.setParagraphIndent(indentSize)
			}
		}

		shosetsuSettings.readerUserThemeSelectionLive.observe(this) { theme ->
			val b = shosetsuSettings.getReaderBackgroundColor(theme.toLong())
			val t = shosetsuSettings.getReaderTextColor(theme.toLong())

			// Sets current view
			chapterReaderAdapter.textReaders.find {
				it.chapter.id == viewModel.currentChapterID
			}?.let {
				it.setTextColor(t)
				it.setBackgroundColor(b)
			}

			// Sets other views down
			chapterReaderAdapter.textReaders.filter {
				it.chapter.id != viewModel.currentChapterID
			}.forEach {
				it.setTextColor(t)
				it.setBackgroundColor(b)
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
		theme_select.apply {
			setOnClickListener {
				ColorPickerDialog.Builder(context)
						.setPositiveButton("") { _, _ ->
							Log.d(logID(), "Clicked")
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
							drawer_toggle.setImageResource(R.drawable.ic_baseline_expand_less_24)
						}
						STATE_EXPANDED -> {
							drawer_toggle.setImageResource(R.drawable.ic_baseline_expand_more_24)
						}
						else -> {
						}
					}
				}

				override fun onSlide(bottomSheet: View, slideOffset: Float) {
					drawer_toggle.setImageResource(R.drawable.ic_baseline_drag_handle_24)
				}

			})
		}
		drawer_toggle.apply {
			setOnClickListener {
				bottomSheetBehavior.state = when (bottomSheetBehavior.state) {
					STATE_EXPANDED -> STATE_COLLAPSED
					else -> STATE_EXPANDED
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
			bubbleOnProgressChanged { _, progress, _, fromUser ->
				if (fromUser) {
					val size = when (progress) {
						0 -> TextSizes.SMALL
						1 -> TextSizes.MEDIUM
						2 -> TextSizes.LARGE
						else -> TextSizes.MEDIUM
					}
					shosetsuSettings.readerTextSize = size.i
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
			bubbleOnProgressChanged { _, progress, _, fromUser ->
				if (fromUser) shosetsuSettings.readerParagraphSpacing = progress
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
			bubbleOnProgressChanged { _, progress, _, fromUser ->
				if (fromUser) shosetsuSettings.readerIndentSize = progress
			}
		}

		color_picker_options?.apply {
			colorFastAdapter.selectExtension {
				isSelectable = true
				setSelectionListener { item, _ ->
					colorFastAdapter.notifyItemChanged(colorFastAdapter.getPosition(item))
				}
			}
			this.adapter = colorFastAdapter
			colorFastAdapter.setOnClickListener { _, _, item, _ ->
				shosetsuSettings.readerTheme = item.identifier.toInt()
				item.isSelected = true

				run {
					val count = colorFastAdapter.itemCount
					for (i in 0 until count)
						colorFastAdapter.getItem(i)?.takeIf {
							it.identifier != item.identifier
						}?.isSelected = false
				}

				colorFastAdapter.notifyDataSetChanged()
				true
			}
			colorItemAdapter.add(shosetsuSettings.readerUserThemes.apply {
				find { it.identifier == shosetsuSettings.readerTheme.toLong() }?.isSelected = true
				forEach { it.inReader = true }
			})
		}
	}

	private fun setupViewPager() {
		Log.d(logID(), "Setting up ViewPager")
		viewpager.apply {
			adapter = chapterReaderAdapter
			registerOnPageChangeCallback(pageChangeCallback)
			setCurrentItem(getCurrentChapterIndex(), false)
			addItemDecoration(DividerItemDecoration(
					viewpager.context,
					viewpager.orientation
			))
		}
	}

	/**
	 * Moves bottom up and down
	 */
	fun animateBottom() {
		chapter_reader_bottom?.apply {
			post {
				bottomSheetBehavior.apply {
					//Log.d(logID(), "Changing panelState from $state | ${this.peekHeight}")
					val state = when (state) {
						STATE_HIDDEN -> {
							Log.d(logID(), "Hidden, making collapsed")
							STATE_COLLAPSED
						}
						STATE_COLLAPSED -> {
							Log.d(logID(), "COLLAPSED, making hidden")
							STATE_HIDDEN
						}
						STATE_DRAGGING -> {
							Log.d(logID(), "Dragging, making hidden")
							STATE_HIDDEN
						}
						STATE_EXPANDED -> {
							Log.d(logID(), "Expanded, making hidden")
							STATE_HIDDEN
						}
						STATE_HALF_EXPANDED -> {
							Log.d(logID(), "Half Expanded, making hidden")
							STATE_HIDDEN
						}
						STATE_SETTLING -> {
							Log.d(logID(), "Settling, making hidden")
							STATE_HIDDEN
						}
						else -> if (toolbar!!.visibility == VISIBLE) STATE_HIDDEN else STATE_COLLAPSED
					}
					this.state = state
					postDelayed(400) { fixHeight() }
				}
			}
		}
	}

	/**
	 * Moves top up and down
	 */
	fun animateToolbar() {
		toolbar?.let {
			@Suppress("CheckedExceptionsKotlin")
			val animator: Animation = AnimationUtils.loadAnimation(
					it.context,
					if (it.visibility == VISIBLE)
						R.anim.slide_up
					else R.anim.slide_down
			).apply {
				duration = 250
			}
			it.startAnimation(animator)
			it.post {
				it.visibility = if (it.visibility == VISIBLE) GONE else VISIBLE
			}
		}
	}

	/** Syncs [bottomSheetBehavior] with [toolbar] */
	private fun fixHeight() {
		bottomSheetBehavior.apply {
			state = if (toolbar.visibility == VISIBLE) {
				STATE_COLLAPSED
			} else STATE_HIDDEN
		}
		/*
		slidingUpPanelLayout.apply {
		val state = panelState
		Log.d(logID(), "PanelState is now ${state.name}")
		when (state) {
			PanelState.HIDDEN -> panelHeight = 0
			PanelState.COLLAPSED -> panelHeight = 238
			PanelState.DRAGGING -> postDelayed(100) { fixHeight() }
			else -> Log.d(logID(), "Unknown state: $state")
			}
		*/
	}

	/** Creates the option menu */
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
			when (shosetsuSettings.readerTheme) {
				0 -> themes[0].setChecked(true)
				1 -> themes[1].setChecked(true)
				2 -> themes[2].setChecked(true)
				3 -> themes[3].setChecked(true)
				4 -> themes[4].setChecked(true)
				5 -> themes[5].setChecked(true)
				else -> {
					shosetsuSettings.readerTheme = 1
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
		//Log.d(logID(), "Selected item: $item")
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

	private fun BubbleSeekBar.bubbleOnProgressChanged(
			onProgressChangedFun: (
					@ParameterName("bubbleSeekBar") BubbleSeekBar?,
					@ParameterName("progress") Int,
					@ParameterName("progressFloat") Float,
					@ParameterName("fromUser") Boolean,
			) -> Unit,
	) {
		this.onProgressChangedListener =
				object : BubbleSeekBar.OnProgressChangedListener {
					override fun onProgressChanged(
							bubbleSeekBar: BubbleSeekBar?,
							progress: Int,
							progressFloat: Float,
							fromUser: Boolean,
					) {
						onProgressChangedFun(bubbleSeekBar, progress, progressFloat, fromUser)
					}

					override fun getProgressOnActionUp(
							bubbleSeekBar: BubbleSeekBar?,
							progress: Int,
							progressFloat: Float,
					) {
					}

					override fun getProgressOnFinally(
							bubbleSeekBar: BubbleSeekBar?,
							progress: Int,
							progressFloat: Float,
							fromUser: Boolean,
					) {
					}
				}
	}
}