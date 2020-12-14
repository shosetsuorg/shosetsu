package app.shosetsu.android.ui.reader

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.github.doomsdayrs.apps.shosetsu.R
import com.google.android.material.behavior.HideBottomViewOnScrollBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior

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
 * 25 / 09 / 2020
 */
class ChapterReaderBottomBar<T : View> @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null
) : BottomSheetBehavior<T>(context, attrs) {
	private val slideBehavior: SlideBehavior by lazy {
		SlideBehavior(context, attrs)
	}

	fun isHidden(): Boolean = slideBehavior.isHidden

	override fun onLayoutChild(parent: CoordinatorLayout, child: T, layoutDirection: Int): Boolean {
		slideBehavior.onLayoutChild(parent, child, layoutDirection)
		return super.onLayoutChild(parent, child, layoutDirection)
	}


	override fun onStartNestedScroll(
		coordinatorLayout: CoordinatorLayout,
		child: T,
		directTargetChild: View,
		target: View,
		axes: Int,
		type: Int
	): Boolean {
		slideBehavior.onStartNestedScroll(
			coordinatorLayout,
			child,
			directTargetChild,
			target,
			axes,
			type
		)
		return super.onStartNestedScroll(
			coordinatorLayout,
			child,
			directTargetChild,
			target,
			axes,
			type
		)
	}

	override fun onNestedScroll(
		coordinatorLayout: CoordinatorLayout,
		child: T,
		target: View,
		dxConsumed: Int,
		dyConsumed: Int,
		dxUnconsumed: Int,
		dyUnconsumed: Int,
		type: Int,
		consumed: IntArray
	) {
		slideBehavior.onNestedScroll(
			coordinatorLayout,
			child,
			target,
			dxConsumed,
			dyConsumed,
			dxUnconsumed,
			dyUnconsumed,
			type,
			consumed
		)
	}

	fun slideUp(view: T) {
		slideBehavior.slideUp(view)
	}

	fun slideDown(view: T) {
		slideBehavior.slideDown(view)
	}


	inner class SlideBehavior(
		context: Context,
		attrs: AttributeSet?
	) : HideBottomViewOnScrollBehavior<T>(context, attrs) {
		/** If the view is hidden or not */
		var isHidden = false

		override fun slideDown(child: T) {
			super.slideDown(child)
			this@ChapterReaderBottomBar.state = STATE_COLLAPSED
			isHidden = true
		}

		override fun onNestedScroll(
			coordinatorLayout: CoordinatorLayout,
			child: T,
			target: View,
			dxConsumed: Int,
			dyConsumed: Int,
			dxUnconsumed: Int,
			dyUnconsumed: Int,
			type: Int,
			consumed: IntArray
		) {
			if (dyConsumed > 0 && target.id != R.id.chapter_reader_bottom_scroll) {
				slideDown(child)
			} else if (dyConsumed < 0) {
				slideUp(child)
			}
		}


		override fun slideUp(child: T) {
			super.slideUp(child)
			isHidden = false
		}
	}
}