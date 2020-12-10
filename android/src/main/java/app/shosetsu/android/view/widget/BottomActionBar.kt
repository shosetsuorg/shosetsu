package app.shosetsu.android.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ActionMode
import android.view.LayoutInflater.from
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.databinding.BottomActionBarBinding

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
 * 01 / 10 / 2020
 */
class BottomActionBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
	: FrameLayout(context, attrs) {

	private val binding: BottomActionBarBinding =
			inflate(from(context), this, true)

	/**
	 * Removes all items
	 */
	fun clear() {
		binding.bottomActionMenu.menu.clear()
		binding.bottomActionMenu.setOnMenuItemClickListener(null)
	}

	fun findItem(@IdRes id: Int): MenuItem? = binding.bottomActionMenu.menu.findItem(id)

	fun show(mode: ActionMode, @MenuRes menuRes: Int, listener: (item: MenuItem) -> Boolean) {
		// Avoid re-inflating the menu
		if (binding.bottomActionMenu.menu.size() == 0) {
			mode.menuInflater.inflate(menuRes, binding.bottomActionMenu.menu)
			binding.bottomActionMenu.setOnMenuItemClickListener { listener(it) }
		}

		binding.bottomActionBar.visibility = View.VISIBLE
		val bottomAnimation = AnimationUtils.loadAnimation(context, R.anim.bottom_slide_down)
		binding.bottomActionBar.startAnimation(bottomAnimation)
	}

	fun hide() {
		val bottomAnimation = AnimationUtils.loadAnimation(context, R.anim.bottom_slide_up)
		bottomAnimation.setAnimationListener(object : SimpleAnimationListener() {
			override fun onAnimationEnd(animation: Animation) {
				binding.bottomActionBar.visibility = View.GONE
			}
		})
		binding.bottomActionBar.startAnimation(bottomAnimation)
	}
}