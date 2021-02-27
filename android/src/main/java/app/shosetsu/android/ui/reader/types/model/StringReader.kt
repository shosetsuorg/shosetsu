package app.shosetsu.android.ui.reader.types.model

import android.os.Build
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.setPadding
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LifecycleObserver
import app.shosetsu.android.common.ext.logD
import app.shosetsu.android.common.ext.percentageScrolled
import app.shosetsu.android.ui.reader.types.base.ReaderChapterViewHolder
import app.shosetsu.android.view.uimodels.model.reader.ReaderChapterUI
import app.shosetsu.common.enums.ReadingStatus
import com.github.doomsdayrs.apps.shosetsu.R
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein

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
) : ReaderChapterViewHolder(itemView), KodeinAware, LifecycleObserver {
	override val kodein: Kodein by kodein(itemView.context)

	/**
	 * Main way of reading in this view
	 */
	private val textView: AppCompatTextView = itemView.findViewById(R.id.textView)
	private val scrollView: NestedScrollView = itemView.findViewById(R.id.scrollView)

	private val middleBox: View = itemView.findViewById(R.id.reader_middle_box)
	private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

	// These handle the error view
	private val errorView: View = itemView.findViewById(R.id.error_view)
	private val errorMessage: TextView = itemView.findViewById(R.id.error_message)
	private val errorButton: Button = itemView.findViewById(R.id.error_button)

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

	override fun setData(data: String) {
		unformattedText = data
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
		scrollView.scrollTo(0, progress.toInt())
	}

	override fun getFocusTarget(): View = textView

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

		if (currentY > scrollSpeed)
			scrollView.smoothScrollTo(0, currentY - scrollSpeed)
	}

	override fun bindView(item: ReaderChapterUI, payloads: List<Any>) {
	}

	override fun unbindView(item: ReaderChapterUI) {
		textView.setOnClickListener(null)
	}

	override fun incrementScroll() {
		val currentY = scrollView.scrollY
		val maxY = scrollView.getChildAt(0).height - scrollView.height

		if (currentY < maxY - scrollSpeed)
			scrollView.smoothScrollTo(0, currentY + scrollSpeed)
		else scrollView.smoothScrollTo(0, maxY)
	}

	companion object {
		private const val scrollSpeed = 300
	}
}