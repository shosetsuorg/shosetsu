package app.shosetsu.android.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.appcompat.view.ActionMode
import androidx.core.view.isVisible
import com.github.doomsdayrs.apps.shosetsu.R
import kotlinx.android.synthetic.main.bottom_action_bar.view.*

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
	init {
		inflate(context, R.layout.bottom_action_bar, this)
	}

	/**
	 * Removes all items
	 */
	fun clear() {
		bottom_action_menu.menu.clear()
		bottom_action_menu.setOnMenuItemClickListener(null)
	}

	fun findItem(@IdRes id: Int): MenuItem? = bottom_action_menu.menu.findItem(id)

	fun show(mode: ActionMode, @MenuRes menuRes: Int, listener: (item: MenuItem?) -> Boolean) {
		// Avoid re-inflating the menu
		if (bottom_action_menu.menu.size() == 0) {
			mode.menuInflater.inflate(menuRes, bottom_action_menu.menu)
			bottom_action_menu.setOnMenuItemClickListener { listener(it) }
		}

		bottom_action_bar.isVisible = true
		val bottomAnimation = AnimationUtils.loadAnimation(context, R.anim.bottom_slide_down)
		bottom_action_bar.startAnimation(bottomAnimation)
	}

	fun hide() {
		val bottomAnimation = AnimationUtils.loadAnimation(context, R.anim.bottom_slide_up)
		bottomAnimation.setAnimationListener(object : SimpleAnimationListener() {
			override fun onAnimationEnd(animation: Animation) {
				bottom_action_bar.isVisible = false
			}
		})
		bottom_action_bar.startAnimation(bottomAnimation)
	}
}