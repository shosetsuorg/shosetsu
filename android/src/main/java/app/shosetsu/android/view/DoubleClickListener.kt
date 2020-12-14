package app.shosetsu.android.view

import android.os.SystemClock.elapsedRealtime
import android.view.View

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
 * 18 / 08 / 2020
 *
 * Is an onClickListener that handles doubleClickListener
 *
 * @param doubleClickQualificationTime The time in which the second tap should be done
 * in order to qualify as a double click. Time in MS
 */
abstract class DoubleClickListener(
	private val doubleClickQualificationTime: Long = 200,
) : View.OnClickListener {

	private var timestampLastClick = 0L

	/***/
	override fun onClick(v: View) {
		if ((elapsedRealtime() - timestampLastClick) < doubleClickQualificationTime)
			onDoubleClick(v)
		timestampLastClick = elapsedRealtime()
	}

	/** When the view is double clicked */
	abstract fun onDoubleClick(v: View)
}

/**
 * Creates a [DoubleClickListener] and applies it to a view
 */
inline fun View.setOnDoubleClickListener(
	crossinline onDoubleClick: (View) -> Unit,
) {
	setOnClickListener(object : DoubleClickListener() {
		override fun onDoubleClick(v: View) {
			onDoubleClick(v)
		}
	})
}