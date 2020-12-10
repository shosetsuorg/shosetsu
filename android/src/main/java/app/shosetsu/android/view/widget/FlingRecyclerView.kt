package app.shosetsu.android.view.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.common.consts.FLING_THRESHOLD
import app.shosetsu.android.common.ext.logI
import app.shosetsu.android.common.ext.logV
import kotlin.math.abs

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
 * shosetsu
 * 30 / 09 / 2020
 */
class FlingRecyclerView @JvmOverloads constructor(
		context: Context,
		attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {
	init {
		setItemViewCacheSize(20)
	}

	override fun fling(velocityX: Int, velocityY: Int): Boolean {
		// ignore if under a certain fling
		logV("Velocity is $velocityY")
		if (abs(velocityY) <= FLING_THRESHOLD) return super.fling(velocityX, velocityY)

		val position = if (velocityY > 0) {
			logI("Launching to the bottom")
			(adapter!!.itemCount) - 5
		} else {
			logI("Launching to the top")
			0
		}

		super.scrollToPosition(position)
		return false
	}
}