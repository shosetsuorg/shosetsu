package app.shosetsu.android.view.widget

/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 * Layout to wrap a scrollable component inside a ViewPager2. Provided as a solution to the problem
 * where pages of ViewPager2 have nested scrollable elements that scroll in the same direction as
 * ViewPager2. The scrollable element needs to be the immediate and only child of this host layout.
 *
 * This solution has limitations when using multiple levels of nested scrollable elements
 * (e.g. a horizontal RecyclerView in a vertical RecyclerView in a horizontal ViewPager2).
 */
class NestedScrollableHost @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

	private var touchSlop = 0
	private var initialX = 0f
	private var initialY = 0f
	private val parentViewPager: ViewPager2?
		get() {
			var v: View? = parent as? View
			while (v != null && v !is ViewPager2) {
				v = v.parent as? View
			}
			return v as? ViewPager2
		}

	private val child: View? get() = if (childCount > 0) getChildAt(0) else null

	init {
		touchSlop = ViewConfiguration.get(context).scaledTouchSlop
	}

	private fun canChildScroll(orientation: Int, delta: Float): Boolean {
		val direction = -delta.sign.toInt()
		return when (orientation) {
			0 -> child?.canScrollHorizontally(direction) ?: false
			1 -> child?.canScrollVertically(direction) ?: false
			else -> throw IllegalArgumentException()
		}
	}

	override fun onInterceptTouchEvent(e: MotionEvent): Boolean =
		handleInterceptTouchEvent(e) || super.onInterceptTouchEvent(e)

	private fun parentTakesControl() {
		parent.requestDisallowInterceptTouchEvent(false)

	}

	private fun childTakesControl() {
		parent.requestDisallowInterceptTouchEvent(true)
	}

	/**
	 * @return The child does not need to scroll.
	 */
	private fun handleInterceptTouchEvent(e: MotionEvent): Boolean {
		val orientation = parentViewPager?.orientation ?: return false

		when (child) {
			is WebView -> {
				when (e.action) {
					MotionEvent.ACTION_DOWN -> {
						initialX = e.x
						initialY = e.y
						childTakesControl()
					}
					MotionEvent.ACTION_MOVE -> {
						val dx = e.x - initialX
						val dy = e.y - initialY

						/**
						 * Is the ViewPager horizontal or not
						 */
						val isVpHorizontal = orientation == ORIENTATION_HORIZONTAL
						if (dx.absoluteValue > touchSlop || dy.absoluteValue > touchSlop) {
							// use two different methods for horizontal and vertical paging
							if (isVpHorizontal == (dy.absoluteValue > dx.absoluteValue)) {
								// Gesture is perpendicular, reject all parents to intercept
								childTakesControl()

								// Return false to prevent parent from taking movement
								return false
							} else {
								// Gesture is parallel, query child if movement in that direction is possible
								if (canChildScroll(orientation, if (isVpHorizontal) dx else dy)) {
									// Child can scroll, disallow all parents to intercept
									childTakesControl()
								} else {
									// Child cannot scroll, allow all parents to intercept
									// Only if the action is greater then a certain amount
									val isDyGreaterThenDx = dy.absoluteValue > dx.absoluteValue
									val isDxLessThenProtection = dx.absoluteValue < 300

									val shouldChildScroll = isDxLessThenProtection && isVpHorizontal
									//	logV("================================")
									//	logV("DX: $dx, $dy")
									//	logV("IsDyGreaterThenDx: $isDyGreaterThenDx")
									//	logV("IsDxLessThenProtection: $isDxLessThenProtection")
									//	logV("Is horizontal: $isVpHorizontal")
									//	logV("ShouldChildScroll: $shouldChildScroll")
									return if (shouldChildScroll) {
										childTakesControl()
										false
									} else {
										parentTakesControl()
										true
									}

								}
							}
						}
					}
				}
			}
			else -> {

				// Early return if child can't scroll in same direction as parent
				if (!canChildScroll(orientation, -1f) && !canChildScroll(orientation, 1f)) {
					return false
				}

				when (e.action) {
					MotionEvent.ACTION_DOWN -> {
						initialX = e.x
						initialY = e.y
						childTakesControl()
					}
					MotionEvent.ACTION_MOVE -> {
						val dx = e.x - initialX
						val dy = e.y - initialY
						val isVpHorizontal = orientation == ORIENTATION_HORIZONTAL

						// Ignore the movement if it is sloppy
						if (dx.absoluteValue > touchSlop || dy.absoluteValue > touchSlop) {
							if (isVpHorizontal == (dy.absoluteValue > dx.absoluteValue)) {

								// Gesture is perpendicular, allow all parents to intercept
								parentTakesControl()

								// If you are the following case:
								// ViewPager2(V) -> NestedScrollView(V) -> RecyclerView(H)
								// return false instead so that the innermost RV(H) can scroll normally.
								// It is not tested for other use case.
								return false
							} else {
								// Gesture is parallel, query child if movement in that direction is possible
								if (canChildScroll(orientation, if (isVpHorizontal) dx else dy)) {
									// Child can scroll, disallow all parents to intercept

									childTakesControl()
								} else {
									// Child cannot scroll, allow all parents to intercept
									parentTakesControl()
									return true
								}
							}
						}
					}
				}
			}
		}
		return false
	}
}