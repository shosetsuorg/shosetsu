package app.shosetsu.android.common.ext

import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView

/**
 * Returns a float between 0 and 100 of the scroll percentage
 */
fun RecyclerView.percentageScrolled(): Float =
	(scrollY / maxY) * 100

fun NestedScrollView.percentageScrolled(): Float =
	(scrollY / maxY) * 100

val RecyclerView.maxY: Float
	get() = (getChildAt(0).height - height).toFloat()


val NestedScrollView.maxY: Float
	get() = (getChildAt(0).height - height).toFloat()
