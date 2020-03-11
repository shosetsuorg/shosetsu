package com.github.doomsdayrs.apps.shosetsu.backend.controllers.secondDrawer

import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.R.id.linearLayout
import com.github.doomsdayrs.apps.shosetsu.R.layout.drawer_layout
import com.google.android.material.navigation.NavigationView


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
 * ====================================================================
 */

/**
 * shosetsu
 * 06 / 03 / 2020
 *
 * @author github.com/doomsdayrs
 *
 * All added views are
 */
class SDBuilder(navigationView: NavigationView, drawerLayout: DrawerLayout, secondDrawerController: SecondDrawerController) : SDViewBuilder(navigationView, drawerLayout, secondDrawerController) {
    companion object {
        private const val logID = "SDBuilder"
    }

    private val parentView = inflater.inflate(drawer_layout, null, false)
    override val layout: LinearLayout = parentView.findViewById(linearLayout)

    override fun add(view: View): SDBuilder {
        layout.addView(view)
        return this
    }

    fun newInner(): SDViewBuilder {
        return SDViewBuilder(navigationView, drawerLayout, secondDrawerController)
    }

    fun addInner(@StringRes string: Int, builder: SDViewBuilder): SDBuilder {

        val view = LinearLayout(layout.context)
        view.layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT)

        val expandingBar = inflater.inflate(R.layout.drawer_item_expandable, layout, false) as LinearLayout
        val bar = expandingBar[0] as ConstraintLayout
        (bar[0] as TextView).setText(string)
        val image = bar[1] as ImageView

        val divider = expandingBar[1]
        val internalView = builder.build()

        var first = true
        expandingBar.setOnClickListener {
            if (!first) {
                when (internalView.visibility == VISIBLE) {
                    true -> {
                        Log.i("DrawerItem", "Closing RadioView")
                        divider.visibility = GONE
                        image.setImageResource(R.drawable.ic_baseline_expand_more_24)

                        val transition: Transition = Slide(Gravity.BOTTOM)
                        transition.duration = 600
                        transition.addTarget(internalView)
                        TransitionManager.beginDelayedTransition(view, transition)
                    }
                    false -> {
                        Log.i("DrawerItem", "Opening Radio View")
                        divider.visibility = VISIBLE
                        image.setImageResource(R.drawable.ic_baseline_expand_less_24)

                        val transition: Transition = Slide(Gravity.TOP)
                        transition.duration = 600
                        transition.addTarget(internalView)
                        TransitionManager.beginDelayedTransition(view, transition)
                    }
                }
                internalView.visibility = if (internalView.visibility != VISIBLE) VISIBLE else GONE
            } else first = !first
        }
        Log.d(logID, "${layout.childCount}")
        view.addView(expandingBar)
        view.addView(internalView)
        layout.addView(view)
        layout.addView(inflater.inflate(R.layout.drawer_divider, layout, false))
        Log.d(logID, "${layout.childCount}")
        return this
    }

    override fun build(): View {
        parentView.findViewById<Button>(R.id.accept).setOnClickListener {
            secondDrawerController.handleConfirm(layout)
            drawerLayout.closeDrawer(navigationView)
        }

        parentView.findViewById<Button>(R.id.reset).setOnClickListener {
            navigationView.removeAllViews()
            secondDrawerController.createTabs(navigationView, drawerLayout)
            drawerLayout.closeDrawer(navigationView)
        }
        return parentView
    }
}