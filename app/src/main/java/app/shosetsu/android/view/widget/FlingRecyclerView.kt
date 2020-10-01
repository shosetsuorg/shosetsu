package app.shosetsu.android.view.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import app.shosetsu.android.common.consts.FLING_DIVIDE
import app.shosetsu.android.common.consts.FLING_SPEED
import app.shosetsu.android.common.consts.FLING_THRESHOLD
import app.shosetsu.android.common.ext.logD
import app.shosetsu.android.common.ext.logV

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
class FlingRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : RecyclerView(context, attrs) {
	init {
		setItemViewCacheSize(20)
	}

	override fun fling(velocityX: Int, velocityY: Int): Boolean {
		// ignore if under a certain fling
		if (velocityY < FLING_THRESHOLD) return super.fling(velocityX, velocityY)

		logV("Fling")
		val size = adapter?.itemCount ?: 1
		logD("Size: $size")
		val extraFling = (size / FLING_DIVIDE) + 1
		val multiplier = (FLING_SPEED + extraFling)
		logD("Extra by $extraFling, final multiplier $multiplier")
		val finalVelocity = velocityY * multiplier
		logD("Final velocity: $finalVelocity")
		return super.fling(velocityX, finalVelocity)
	}
}