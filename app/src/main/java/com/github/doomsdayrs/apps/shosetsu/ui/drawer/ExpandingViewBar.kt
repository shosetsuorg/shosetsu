package com.github.doomsdayrs.apps.shosetsu.ui.drawer

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
class ExpandingViewBar(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    constructor(context: Context) : this(context, null)

    private val frameLayout: FrameLayout

    init {
        layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL

        val inflater = LayoutInflater.from(context)

        val expandableLayoutDivider = inflater.inflate(R.layout.drawer_divider, this, false)

        val bar = inflater.inflate(R.layout.drawer_item_expandable, this, false)

        bar.findViewById<TextView>(R.id.textView).setText(R.string.unknown)
        val image = bar.findViewById<ImageView>(R.id.imageView)

        frameLayout = FrameLayout(context)
        frameLayout.visibility = View.GONE

        var first = true
        bar.setOnClickListener {
            if (!first) {
                when (frameLayout.visibility == View.VISIBLE) {
                    true -> {
                        Log.i("DrawerItem", "Closing RadioView")
                        expandableLayoutDivider.visibility = View.GONE
                        image.setImageResource(R.drawable.ic_baseline_expand_more_24)

                        val transition: Transition = Slide(Gravity.BOTTOM)
                        transition.duration = 600
                        transition.addTarget(frameLayout)
                        TransitionManager.beginDelayedTransition(this, transition)
                    }
                    false -> {
                        Log.i("DrawerItem", "Opening Radio View")
                        expandableLayoutDivider.visibility = View.VISIBLE
                        image.setImageResource(R.drawable.ic_baseline_expand_less_24)

                        val transition: Transition = Slide(Gravity.TOP)
                        transition.duration = 600
                        transition.addTarget(frameLayout)
                        TransitionManager.beginDelayedTransition(this, transition)
                    }
                }
                frameLayout.visibility = if (frameLayout.visibility != View.VISIBLE) View.VISIBLE else View.GONE
            } else first = !first
        }

        this.addView(bar)
        this.addView(frameLayout)
    }

    fun setChild(view: View) {
        view.layoutParams = ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        view.visibility = View.GONE
        frameLayout.addView(view)
    }
}