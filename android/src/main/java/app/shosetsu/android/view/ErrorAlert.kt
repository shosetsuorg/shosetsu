package app.shosetsu.android.view

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.widget.AppCompatTextView
import com.github.doomsdayrs.apps.shosetsu.R

/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 */

/**
 * Shosetsu
 * 9 / June / 2019
 */
class ErrorAlert(
	val activity: Activity,
	retryAction: (dialog: DialogInterface?, which: Int) -> Unit =
		{ dialog, _: Int -> dialog?.dismiss() },
) : AlertDialog.Builder(activity) {
	private val view = activity.layoutInflater.inflate(R.layout.error_view, null)!!
	private val messageView: AppCompatTextView = view.findViewById(R.id.error_message)
	private var e: Exception? = null

	init {
		setPositiveButton(R.string.retry, retryAction)
		setView(view)
	}

	fun setError(e: Exception): ErrorAlert {
		this.e = e
		return this
	}

	override fun setMessage(message: CharSequence?): ErrorAlert {
		messageView.text = message
		return this
	}

	fun runOnUI() {
		activity.runOnUiThread { show() }
	}
}