package app.shosetsu.android.view.widget

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView

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
 * 16 / 06 / 2021
 */
class TappingTextView @JvmOverloads constructor(
	context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

	var topTappedListener: (() -> Unit)? = null

	var bottomTappedListener: (() -> Unit)? = null

	var middleTappedListener: (() -> Unit)? = null

	var previousAction = -1

	init {
		setOnTouchListener { v, event ->
			if (previousAction == MotionEvent.ACTION_DOWN && event?.action == MotionEvent.ACTION_UP) {
				val metrics = DisplayMetrics()
				display.getRealMetrics(metrics)
				val height = metrics.heightPixels
				val percentage = event.rawY / height

				// Screen is measured top to bottom, careful
				when {
					percentage <= .25 -> bottomTappedListener?.invoke()
					percentage >= .75 -> topTappedListener?.invoke()
					else -> middleTappedListener?.invoke()
				}
			}
			previousAction = event.action
			v.performClick()
			false
		}
	}
}