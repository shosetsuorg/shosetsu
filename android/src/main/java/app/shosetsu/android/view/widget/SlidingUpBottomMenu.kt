package app.shosetsu.android.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.SlideUpBottomMenuBinding

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
 * 22 / 11 / 2020
 */
class SlidingUpBottomMenu @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
	private val onShowListeners = ArrayList<() -> Unit>()
	private val onHideListeners = ArrayList<() -> Unit>()

	fun addOnShowListener(onShow: () -> Unit) =
		onShowListeners.add(onShow)

	fun addOnHideListener(onHide: () -> Unit) =
		onHideListeners.add(onHide)

	fun clearOnShowListeners() = onShowListeners.clear()
	fun clearOnHideListeners() = onHideListeners.clear()


	private val binding by lazy {
		SlideUpBottomMenuBinding.inflate(
			LayoutInflater.from(context),
			this,
			true
		).also {
			it.background.setOnClickListener {
				hide()
			}
		}
	}

	fun addChildView(view: View) {
		binding.childFrameLayout.addView(view)
	}

	fun clearChildren() {
		binding.childFrameLayout.removeAllViews()
	}

	fun show() {
		onShowListeners.forEach { it.invoke() }
		binding.root.isVisible = true

		binding.root.startAnimation(loadAnimation(context, R.anim.bottom_slide_up).apply {
			duration = 300
			interpolator = AccelerateDecelerateInterpolator()
			setAnimationListener(object : SimpleAnimationListener() {
				override fun onAnimationStart(animation: Animation) {
					binding.background.isVisible = false
					binding.cardView.isVisible = true
					binding.root.isVisible = true
				}

				override fun onAnimationEnd(animation: Animation) {
					binding.background.startAnimation(loadAnimation(context, R.anim.fade_in).apply {
						duration = 100
						setAnimationListener(object : SimpleAnimationListener() {
							override fun onAnimationEnd(animation: Animation) {
								binding.background.isVisible = true
							}
						})
					})
				}
			})
		})

	}

	fun hide() {
		binding.background.startAnimation(loadAnimation(context, R.anim.fade_out).apply {
			duration = 50
			setAnimationListener(object : SimpleAnimationListener() {
				override fun onAnimationEnd(animation: Animation) {
					binding.background.isVisible = false

					binding.cardView.startAnimation(
						loadAnimation(
							context,
							R.anim.bottom_slide_down
						).apply {
							duration = 300
							interpolator = AccelerateDecelerateInterpolator()
							setAnimationListener(object : SimpleAnimationListener() {
								override fun onAnimationStart(animation: Animation) {

								}

								override fun onAnimationEnd(animation: Animation) {
									binding.cardView.isVisible = false
									binding.root.isVisible = false
									onHideListeners.forEach { it.invoke() }
								}
							})
						})
				}
			})
		})
	}
}