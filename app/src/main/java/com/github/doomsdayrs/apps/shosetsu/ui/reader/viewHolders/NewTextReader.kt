package com.github.doomsdayrs.apps.shosetsu.ui.reader.viewHolders

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.common.ShosetsuSettings
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
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
class NewTextReader(itemView: View) : NewReader(itemView), KodeinAware {
	override val kodein: Kodein by kodein(itemView.context)
	private val settings by instance<ShosetsuSettings>()

	/**
	 * Main way of reading in this view
	 */
	val textView: TextView = itemView.findViewById(R.id.textView)
	val scrollView: NestedScrollView = itemView.findViewById(R.id.scrollView)

	private val middleBox: View = itemView.findViewById(R.id.reader_middle_box)
	private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

	// These handle the error view
	private val errorView: View = itemView.findViewById(R.id.error_view)
	private val errorMessage: TextView = itemView.findViewById(R.id.error_message)
	private val errorButton: Button = itemView.findViewById(R.id.error_button)

	var chapterID: Int = -1
	private var unformattedText = ""

	fun showProgress() {
		middleBox.visibility = VISIBLE
		progressBar.visibility = VISIBLE
	}

	fun hideProgress() {
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


	override fun setText(text: String?) {
		unformattedText = text ?: "UNKNOWN"
	}

	override fun bind() {
		val replaceSpacing = StringBuilder("\n")
		for (x in 0 until settings.readerParagraphSpacing)
			replaceSpacing.append("\n")
		for (x in 0 until settings.readerIndentSize)
			replaceSpacing.append("\t")

		textView.text = unformattedText.replace("\n".toRegex(), replaceSpacing.toString())
	}
}