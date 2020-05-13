package com.github.doomsdayrs.apps.shosetsu.ui.reader

import android.os.Build
import android.os.Bundle
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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.Callback
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.backend.unmarkMenuItems
import com.github.doomsdayrs.apps.shosetsu.common.dto.HResult
import com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus.READ
import com.github.doomsdayrs.apps.shosetsu.common.enums.ReadingStatus.READING
import com.github.doomsdayrs.apps.shosetsu.common.ext.openInBrowser
import com.github.doomsdayrs.apps.shosetsu.common.ext.openInWebView
import com.github.doomsdayrs.apps.shosetsu.common.ext.regret
import com.github.doomsdayrs.apps.shosetsu.ui.reader.adapters.ChapterReaderAdapter
import com.github.doomsdayrs.apps.shosetsu.ui.reader.demarkActions.*
import com.github.doomsdayrs.apps.shosetsu.view.uimodels.ChapterReaderUI
import com.github.doomsdayrs.apps.shosetsu.viewmodel.base.IChapterReaderViewModel
import kotlinx.android.synthetic.main.chapter_reader.*
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
class ChapterReader : AppCompatActivity(R.layout.chapter_reader), KodeinAware {
	private class RecyclerViewDiffer(
			val old: List<ChapterReaderUI>,
			val new: List<ChapterReaderUI>
	) : Callback() {
		override fun getOldListSize(): Int = old.size

		override fun getNewListSize(): Int = new.size

		override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
				old[oldItemPosition].id == new[newItemPosition].id

		override fun areContentsTheSame(
				oldItemPosition: Int,
				newItemPosition: Int
		): Boolean {
			val o: ChapterReaderUI = old[oldItemPosition]
			val n = new[newItemPosition]
			if (o.id != n.id) return false
			if (o.link != n.link) return false
			if (o.title != n.title) return false
			if (o.readingReadingStatus != n.readingReadingStatus) return false
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

	// Order of values. Small,Medium,Large
	private lateinit var textSizes: Array<MenuItem>

	// Order of values. Non,Small,Medium,Large
	private lateinit var paragraphSpaces: Array<MenuItem>

	// Order of values. Non,Small,Medium,Large
	private lateinit var indentSpaces: Array<MenuItem>
	private lateinit var bookmark: MenuItem
	private var tapToScroll: MenuItem? = null

	override val kodein: Kodein by closestKodein()
	val viewModel: IChapterReaderViewModel by instance<IChapterReaderViewModel>()
	private val chapterReaderAdapter: ChapterReaderAdapter = ChapterReaderAdapter(this)

	val chapters: ArrayList<ChapterReaderUI> = arrayListOf()

	public override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setSupportActionBar(toolbar as Toolbar)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		if (savedInstanceState == null) {
			TODO("LOAD DATA TO VIEW MODEL")
		} else {
		}
		setupViewPager()
		viewpager.setOnClickListener {
			toolbar?.let {
				@Suppress("CheckedExceptionsKotlin")
				val animator: Animation = AnimationUtils.loadAnimation(
						this,
						if (it.visibility == VISIBLE)
							R.anim.slide_down
						else R.anim.slide_up
				)
				it.startAnimation(animator)
				it.visibility = if (it.visibility == VISIBLE) GONE else VISIBLE
			}
		}
	}

	fun handleChapterReaderUIChange(hResult: HResult<List<ChapterReaderUI>>) {
		when (hResult) {
			is HResult.Loading -> {
			}
			is HResult.Empty -> {
			}
			is HResult.Error -> {
			}
			is HResult.Success -> {
				val dif = DiffUtil.calculateDiff(RecyclerViewDiffer(
						chapters,
						hResult.data
				))
				chapters.clear()
				chapters.addAll(hResult.data)
				dif.dispatchUpdatesTo(chapterReaderAdapter)

				//bookmark.setIcon(if (it.data.bookmarked)
				//	R.drawable.ic_bookmark_24dp
				//else
				//	R.drawable.ic_bookmark_border_24dp
				//)
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
		//  Bookmark
		run {
			menu.findItem(R.id.chapter_view_bookmark)?.let {
				bookmark = it
				/*
				viewModel.getCurrentChapter().observe(this, Observer {
					when (it) {
						is HResult.Loading -> {
						}
						is HResult.Empty -> {
						}
						is HResult.Error -> {
						}
						is HResult.Success -> {
							bookmark.setIcon(if (it.data.bookmarked)
								R.drawable.ic_bookmark_24dp
							else
								R.drawable.ic_bookmark_border_24dp
							)
						}
					}
				})
*/
			}

		}
		// Tap To Scroll
		run {
			tapToScroll = menu.findItem(R.id.tap_to_scroll)
			tapToScroll?.setChecked(Settings.isTapToScroll)
		}
		// Text size
		run {
			textSizes = arrayOf(
					menu.findItem(R.id.chapter_view_textSize_small),
					menu.findItem(R.id.chapter_view_textSize_medium),
					menu.findItem(R.id.chapter_view_textSize_large)
			)
			when (Settings.readerTextSize) {
				Settings.TextSizes.SMALL.i -> textSizes[0].setChecked(true)
				Settings.TextSizes.MEDIUM.i -> textSizes[1].setChecked(true)
				Settings.TextSizes.LARGE.i -> textSizes[2].setChecked(true)
				else -> {
					Settings.readerTextSize = Settings.TextSizes.SMALL.i
					textSizes[0].setChecked(true)
				}
			}
		}
		// Paragraph Space
		run {
			paragraphSpaces = arrayOf(
					menu.findItem(R.id.chapter_view_paragraphSpace_none),
					menu.findItem(R.id.chapter_view_paragraphSpace_small),
					menu.findItem(R.id.chapter_view_paragraphSpace_medium),
					menu.findItem(R.id.chapter_view_paragraphSpace_large)
			)
			paragraphSpaces[Settings.readerParagraphSpacing].setChecked(true)
		}
		// Indent Space
		run {
			indentSpaces = arrayOf(
					menu.findItem(R.id.chapter_view_indent_none),
					menu.findItem(R.id.chapter_view_indent_small),
					menu.findItem(R.id.chapter_view_indent_medium),
					menu.findItem(R.id.chapter_view_indent_large)
			)
			indentSpaces[Settings.ReaderIndentSize].setChecked(true)
		}
		/* Reader
		{

			readers[0] = menu.findItem(R.id.reader_0);
			readers[1] = menu.findItem(R.id.reader_1);
			readerType = getReaderType(novelID);

			switch (readerType) {
				case 1:
					demarkMenuItems(readers, 1, null);
					break;
				case 0:
				case -1:
					demarkMenuItems(readers, 0, null);
					break;
				case -2:
				default:
					throw new RuntimeException("Invalid chapter?!? How are you reading this without the novel loaded in");
			}
		}*/
		return true
	}

	/**
	 * What to do when an menu item is selected
	 *
	 * @param item item selected
	 * @return true if processed
	 */
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		Log.d("item", item.toString())
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
			R.id.tap_to_scroll -> {
				this.regret()
				// tapToScroll!!.isChecked = Utilities.toggleTapToScroll()
				true
			}
			R.id.chapter_view_bookmark -> {
				this.viewModel.bookmark()
				true
			}
			R.id.chapter_view_textSize_small -> {
				unmarkMenuItems(indentSpaces, 0, demarkActions[0])
				true
			}
			R.id.chapter_view_textSize_medium -> {
				unmarkMenuItems(textSizes, 1, demarkActions[0])
				true
			}
			R.id.chapter_view_textSize_large -> {
				unmarkMenuItems(textSizes, 2, demarkActions[0])
				true
			}
			R.id.chapter_view_paragraphSpace_none -> {
				unmarkMenuItems(paragraphSpaces, 0, demarkActions[1])
				true
			}
			R.id.chapter_view_paragraphSpace_small -> {
				unmarkMenuItems(paragraphSpaces, 1, demarkActions[1])
				true
			}
			R.id.chapter_view_paragraphSpace_medium -> {
				unmarkMenuItems(paragraphSpaces, 2, demarkActions[1])
				true
			}
			R.id.chapter_view_paragraphSpace_large -> {
				unmarkMenuItems(paragraphSpaces, 3, demarkActions[1])
				true
			}
			R.id.chapter_view_indent_none -> {
				unmarkMenuItems(indentSpaces, 0, demarkActions[2])
				true
			}
			R.id.chapter_view_indent_small -> {
				unmarkMenuItems(indentSpaces, 1, demarkActions[2])
				true
			}
			R.id.chapter_view_indent_medium -> {
				unmarkMenuItems(indentSpaces, 2, demarkActions[2])
				true
			}
			R.id.chapter_view_indent_large -> {
				unmarkMenuItems(indentSpaces, 3, demarkActions[2])
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
			R.id.reader_0 -> {
				regret()
				//unmarkMenuItems(readers, 0, demarkActions[3])
				true
			}
			R.id.reader_1 -> {
				regret()
				//unmarkMenuItems(readers, 1, demarkActions[3])
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		viewpager.unregisterOnPageChangeCallback(pageChangeCallback)
	}

	private fun setupViewPager() {
		viewpager.adapter = chapterReaderAdapter
		viewpager.registerOnPageChangeCallback(pageChangeCallback)
		viewpager.currentItem = chapters.indexOfFirst { it.id == viewModel.currentChapterID }
	}

	/**
	 * What to do when scroll hits bottom
	 */
	private fun scrollHitBottom() {
		val total = viewpager.getChildAt(0).height - viewpager.height
		val cUI = chapters[viewModel.currentChapterID]
		if (viewpager.scrollY / total.toFloat() < .99) {
			// Inital mark of reading
			/*
			if (!marked && Settings.readerMarkingType == Settings.MarkingTypes.ONSCROLL.i) {
				Log.d("ChapterView", "Marking as Reading")
				cUI.readingReadingStatus = READING
				viewModel.updateChapter(cUI)
				marked = !marked
			}
			*/
			val y = viewpager!!.scrollY
			if (y % 5 == 0)
				if (cUI.readingReadingStatus != READ) {
					cUI.readingPosition = y
				}
		} else {
			Log.i("Scroll", "Marking chapter as READ${viewModel.appendID(cUI)}")
			cUI.readingReadingStatus = READING
			viewModel.updateChapter(cUI)
		}
	}

	/**
	 * Sets up the hitting bottom listener
	 */
	private fun addBottomListener() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			viewpager.setOnScrollChangeListener { _: View?, _: Int, _: Int, _: Int, _: Int ->
				scrollHitBottom()
			}
		} else {
			viewpager.viewTreeObserver.addOnScrollChangedListener { scrollHitBottom() }
		}
	}

	internal val pageChangeCallback: OnPageChangeCallback = object : OnPageChangeCallback() {
		override fun onPageSelected(position: Int) {
			if (Settings.readerMarkingType == Settings.MarkingTypes.ONVIEW.i) {
				Log.d("ChapterReader", "Marking as Reading")
				val chapterReaderUI = chapters[viewModel.currentChapterID]
				chapterReaderUI.readingReadingStatus = READING
				viewModel.updateChapter(chapterReaderUI)
			}
		}
	}
}