package app.shosetsu.android.ui.reader.types.model

import android.os.Build
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.setPadding
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LifecycleObserver
import app.shosetsu.android.common.ext.logD
import app.shosetsu.android.common.ext.maxY
import app.shosetsu.android.common.ext.percentageScrolled
import app.shosetsu.android.ui.reader.types.base.ReaderChapterViewHolder
import app.shosetsu.android.view.uimodels.model.reader.ReaderChapterUI
import app.shosetsu.android.view.widget.TappingTextView
import app.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.databinding.ChapterReaderTextViewBinding
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
@Deprecated("No longer supported, moving to WebView")
class StringReader(
	itemView: View
) : ReaderChapterViewHolder(itemView), DIAware, LifecycleObserver {
	override val di: DI by closestDI(itemView.context)
	private val binding = ChapterReaderTextViewBinding.bind(itemView)

	/**
	 * Main way of reading in this view
	 */
	private val textView: TappingTextView = binding.textView
	private val scrollView: NestedScrollView = binding.scrollView
	private val middleBox: View = binding.readerMiddleBox
	private val progressBar: ProgressBar = binding.progressBar

	// These handle the error view
	private val errorView: View = binding.errorView
	private val errorMessage: AppCompatTextView = binding.errorMessage
	private val errorButton: Button = binding.errorButton

	private var unformattedText = ""

	init {
		scrollView.apply {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				setOnScrollChangeListener { _: View?, _: Int, _: Int, _: Int, _: Int ->
					scrollHitBottom()
				}
			} else {
				viewTreeObserver.addOnScrollChangedListener {
					scrollHitBottom()
				}
			}
		}
	}

	override fun showLoadingProgress() {
		middleBox.visibility = VISIBLE
		progressBar.visibility = VISIBLE
	}

	override fun hideLoadingProgress() {
		middleBox.visibility = GONE
		progressBar.visibility = GONE
	}

	fun setError(message: String, errorButtonName: String, action: () -> Unit) {
		middleBox.visibility = VISIBLE
		errorView.visibility = VISIBLE
		progressBar.visibility = GONE

		errorMessage.text = message
		errorButton.text = errorButtonName
		errorButton.setOnClickListener {
			errorView.visibility = GONE
			action()
		}
	}

	fun bind(
		paragraphSpacing: Float = chapterReader.viewModel.defaultParaSpacing,
		paragraphIndent: Int = chapterReader.viewModel.defaultIndentSize
	) {

		// Calculate changes to \n
		val replaceSpacing = StringBuilder("\n")
		for (x in 0 until paragraphSpacing.toInt())
			replaceSpacing.append("\n")
		for (x in 0 until paragraphIndent)
			replaceSpacing.append("\t")

		// Syncs textSize
		syncTextSize()

		// Set color
		textView.setTextColor(chapterReader.viewModel.defaultForeground)
		textView.setBackgroundColor(chapterReader.viewModel.defaultBackground)

		// Set new text formatted
		textView.text = unformattedText.replace("\n".toRegex(), replaceSpacing.toString())
	}

	override fun setData(data: ByteArray) {
		unformattedText = data.decodeToString()
		bind()
	}

	override fun syncTextSize() {
		textView.textSize = chapterReader.viewModel.defaultTextSize
	}

	override fun syncTextPadding() {
		textView.setPadding(16)
	}

	override fun syncParagraphSpacing() {
		bind()
	}

	override fun syncParagraphIndent() {
		bind()
	}

	override fun setProgress(progress: Double) {
		scrollView.scrollTo(0, (scrollView.maxY * (progress / 100)).toInt())
	}

	override fun getFocusTarget(onFocus: () -> Unit) {
//		textView.setOnClickListener { onFocus() }
		textView.middleTappedListener = onFocus
	}

	override fun syncTextColor() {
		textView.setTextColor(chapterReader.viewModel.defaultForeground)
	}

	override fun syncBackgroundColor() {
		textView.setBackgroundColor(chapterReader.viewModel.defaultBackground)
	}

	/**
	 * What to do when scroll hits bottom
	 */
	private fun scrollHitBottom() {
		val yPosition = scrollView.scrollY
		val percentage = scrollView.percentageScrolled()
		if (percentage < 99) {
			if (yPosition % 5 == 0) {
				logD("Percentage: $percentage")
				// Mark as reading if on scroll
				chapterReader.viewModel.markAsReadingOnScroll(chapter, percentage.toDouble())
			}
		} else {
			// Hit bottom
			chapterReader.viewModel.updateChapter(
				chapter.copy(
					readingStatus = ReadingStatus.READ,
					readingPosition = 0.0
				),
			)
		}
	}

	override fun depleteScroll() {
		val currentY = scrollView.scrollY

		if (currentY > scrollStep)
			scrollView.smoothScrollBy(0, -1 * scrollStep, scrollDuration)
		else scrollView.smoothScrollTo(0, 0)
	}

	override fun bindView(item: ReaderChapterUI, payloads: List<Any>) {
		super.bindView(item, payloads)
		chapter = item
		textView.bottomTappedListener = {
			if (tapToScroll)
				depleteScroll()
		}
		textView.topTappedListener = {
			if (tapToScroll)
				incrementScroll()
		}
	}

	override fun unbindView(item: ReaderChapterUI) {
		textView.topTappedListener = null
		textView.middleTappedListener = null
		textView.bottomTappedListener = null
		textView.text = null
	}

	override fun incrementScroll() {
		val currentY = scrollView.scrollY
		val maxY = scrollView.getChildAt(0).height - scrollView.height

		if (currentY < maxY - scrollStep)
			scrollView.smoothScrollBy(0, scrollStep, scrollDuration)
		else scrollView.smoothScrollTo(0, maxY)
	}

	companion object {
		private const val scrollDuration = 750
		private const val scrollStep = 500
	}
}