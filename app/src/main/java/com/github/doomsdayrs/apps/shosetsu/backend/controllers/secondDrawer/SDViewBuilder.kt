package com.github.doomsdayrs.apps.shosetsu.backend.controllers.secondDrawer

import android.annotation.SuppressLint
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.drawerlayout.widget.DrawerLayout
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.R.id.spinner
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
open class SDViewBuilder(val navigationView: NavigationView, val drawerLayout: DrawerLayout, val secondDrawerController: SecondDrawerController) {
    companion object {
        private const val logID = "SDViewBuilder"
    }

    val inflater: LayoutInflater = LayoutInflater.from(navigationView.context)
    val layout: LinearLayout = inflater.inflate(R.layout.drawer_layout_simple, navigationView, false) as LinearLayout

    @SuppressLint("ResourceType")
    open fun add(view: View): SDViewBuilder {
        layout.addView(view)
        layout.addView(inflater.inflate(R.layout.drawer_divider, layout, false))
        return this
    }

    fun addSwitch(title: String = "UNKNOWN"): SDViewBuilder {
        Log.d(logID, "Adding Switch\t: $title")
        val switch = inflater.inflate(R.layout.drawer_item_switch, layout, false) as Switch
        switch.text = title
        return add(switch)
    }

    fun addEditText(hint: String = "Not Described"): SDViewBuilder {
        Log.d(logID, "Adding EditText\t: $hint")
        val editText = inflater.inflate(R.layout.drawer_item_edit_text, layout, false) as EditText
        editText.hint = hint
        return add(editText)
    }

    fun addSpinner(title: String = "Not Described", array: Array<String>, selectedInt: Int = 0): SDViewBuilder {
        Log.d(logID, "Adding Spinner\t: $title")
        val item = inflater.inflate(R.layout.drawer_item_spinner, layout, false) as LinearLayout
        val spinner: Spinner = item.findViewById(spinner)
        spinner.visibility = VISIBLE
        spinner.adapter = ArrayAdapter(navigationView.context, android.R.layout.simple_spinner_item, array)
        spinner.setSelection(selectedInt)

        val textView = item.findViewById<TextView>(R.id.textView)
        textView.visibility = VISIBLE
        textView.text = title
        return add(item)
    }

    fun addRadioGroup(title: String, array: Array<String>): SDViewBuilder {
        Log.d(logID, "Adding RadioGroup\t: $title")
        val radioView = inflater.inflate(R.layout.drawer_item_radio_group, layout, false) as LinearLayout
        val expandableView = (radioView[0] as LinearLayout)
        val divider = expandableView[1]

        val bar = expandableView[0] as ConstraintLayout
        (bar[0] as TextView).text = title
        val image = bar[1] as ImageView

        var first = true

        val radioGroupPar = radioView[1] as LinearLayout
        (radioGroupPar[0] as RadioGroup).let { radioGroup ->
            array.forEach {
                val r = RadioButton(radioGroup.context)
                r.text = it
                r.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                radioGroup.addView(r)
            }
            radioView.setOnClickListener {
                if (!first) {
                    when (radioGroup.visibility == VISIBLE) {
                        true -> {
                            Log.i("DrawerItem", "Closing RadioView")
                            divider.visibility = GONE
                            image.setImageResource(R.drawable.ic_baseline_expand_more_24)

                            val transition: Transition = Slide(Gravity.BOTTOM)
                            transition.duration = 600
                            transition.addTarget(radioGroup)
                            TransitionManager.beginDelayedTransition(radioGroupPar, transition)
                        }
                        false -> {
                            Log.i("DrawerItem", "Opening Radio View")
                            divider.visibility = VISIBLE
                            image.setImageResource(R.drawable.ic_baseline_expand_less_24)

                            val transition: Transition = Slide(Gravity.TOP)
                            transition.duration = 600
                            transition.addTarget(radioGroup)
                            TransitionManager.beginDelayedTransition(radioGroupPar, transition)
                        }
                    }
                    radioGroup.visibility = if (radioGroup.visibility != VISIBLE) VISIBLE else GONE
                } else first = !first
            }
        }
        return add(radioView)
    }

    open fun build(): View {
        return layout
    }
}