package app.shosetsu.android.common.utils

import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.animation.Animation
import android.view.animation.Transformation
import app.shosetsu.android.common.enums.ProductFlavors
import com.github.doomsdayrs.apps.shosetsu.BuildConfig
import kotlinx.serialization.json.Json


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
 * 20 / 06 / 2020
 */

fun View.expand() {
	val matchParentMeasureSpec: Int =
		View.MeasureSpec.makeMeasureSpec((parent as View).width, View.MeasureSpec.EXACTLY)
	val wrapContentMeasureSpec: Int =
		View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
	measure(matchParentMeasureSpec, wrapContentMeasureSpec)
	val targetHeight: Int = measuredHeight

	// Older versions of android (pre API 21) cancel animations for views with a height of 0.
	layoutParams.height = 1
	visibility = View.VISIBLE
	val a: Animation = object : Animation() {
		override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
			layoutParams.height =
				if (interpolatedTime == 1f) LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
			requestLayout()
		}

		override fun willChangeBounds(): Boolean {
			return true
		}
	}

	// Expansion speed of .25dp/ms
	a.duration = ((targetHeight / context.resources.displayMetrics.density) * 5).toInt().toLong()
	startAnimation(a)
}

fun View.collapse() {
	val initialHeight: Int = measuredHeight
	val a: Animation = object : Animation() {
		override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
			if (interpolatedTime == 1f) {
				visibility = View.GONE
			} else {
				layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
				requestLayout()
			}
		}

		override fun willChangeBounds(): Boolean {
			return true
		}
	}

	// Collapse speed of .25dp/ms
	a.duration = ((initialHeight / context.resources.displayMetrics.density) * 5).toInt().toLong()
	startAnimation(a)
}

fun flavor(): ProductFlavors = ProductFlavors.fromKey(BuildConfig.FLAVOR)


val backupJSON
	get() = Json {
		encodeDefaults = true
		ignoreUnknownKeys = true // Ignore to allow unknown values
	}