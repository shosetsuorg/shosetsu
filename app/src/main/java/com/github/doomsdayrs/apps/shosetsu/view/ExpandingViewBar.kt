package com.github.doomsdayrs.apps.shosetsu.view

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.github.doomsdayrs.apps.shosetsu.R

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
 * ====================================================================
 */


/**
 * Shosetsu
 * 9 / June / 2019
 *
 * @author github.com/doomsdayrs
 */
class ExpandingViewBar(context: Context, viewGroup: ViewGroup) {

	val bar: LinearLayout
	private val barTitle: TextView
	val layout: LinearLayout
	private val frameLayout: FrameLayout

	init {
		val inflater = LayoutInflater.from(context)

		layout = inflater.inflate(R.layout.drawer_layout_expandable, viewGroup, false) as LinearLayout
		bar = layout.findViewById(R.id.expandable_layout)
		barTitle = bar.findViewById(R.id.title)
		barTitle.setText(R.string.unknown)
		val image = bar.findViewById<ImageView>(R.id.imageView)
		val expandableLayoutDivider = bar.findViewById<View>(R.id.expand_divider)

		val holder = layout.findViewById<LinearLayout>(R.id.radioGroupHolder)
		frameLayout = layout.findViewById(R.id.frameLayout)
		frameLayout.visibility = View.GONE

		var first = true
		bar.setOnClickListener {
			if (!first) {
				val transition: Transition = when (frameLayout.visibility == View.VISIBLE) {
					true -> {
						Log.i("DrawerItem", "Closing RadioView")
						image.setImageResource(R.drawable.ic_baseline_expand_more_24)
						Slide(Gravity.BOTTOM)
					}
					false -> {
						Log.i("DrawerItem", "Opening Radio View")
						image.setImageResource(R.drawable.ic_baseline_expand_less_24)
						Slide(Gravity.TOP)
					}
				}
				transition.duration = 600
				transition.addTarget(frameLayout)
				TransitionManager.beginDelayedTransition(holder, transition)

				expandableLayoutDivider.visibility = if (expandableLayoutDivider.visibility != View.VISIBLE) View.VISIBLE else View.GONE
				frameLayout.visibility = if (frameLayout.visibility != View.VISIBLE) View.VISIBLE else View.GONE
			} else first = !first
		}
	}

	fun setTitle(string: String) {
		barTitle.text = string
	}

	fun setTitle(@StringRes string: Int) {
		barTitle.setText(string)
	}

	fun setChild(view: View) {
		view.layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
		frameLayout.addView(view)
	}
}