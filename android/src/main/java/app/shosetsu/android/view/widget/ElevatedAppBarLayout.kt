package app.shosetsu.android.view.widget

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.content.Context
import android.util.AttributeSet
import com.google.android.material.R
import com.google.android.material.appbar.AppBarLayout

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
 * 11 / 09 / 2020
 */
class ElevatedAppBarLayout @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null
) : AppBarLayout(context, attrs) {
	private var origStateAnimator: StateListAnimator? = null

	init {
		origStateAnimator = stateListAnimator
	}

	fun elevate(liftOnScroll: Boolean) {
		stateListAnimator = origStateAnimator
		isLiftOnScroll = liftOnScroll
	}

	fun drop() {
		stateListAnimator = StateListAnimator().apply {
			val objAnimator = ObjectAnimator.ofFloat(this, "elevation", 0f)

			// Enabled and collapsible, but not collapsed means not elevated
			addState(
				intArrayOf(
					android.R.attr.enabled,
					R.attr.state_collapsible,
					-R.attr.state_collapsed
				),
				objAnimator
			)

			// Default enabled state
			addState(intArrayOf(android.R.attr.enabled), objAnimator)

			// Disabled state
			addState(IntArray(0), objAnimator)
		}
	}
}