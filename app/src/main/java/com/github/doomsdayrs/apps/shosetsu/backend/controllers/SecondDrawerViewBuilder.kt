package com.github.doomsdayrs.apps.shosetsu.backend.controllers

import android.content.Context
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
import com.github.doomsdayrs.apps.shosetsu.R.id.*
import com.github.doomsdayrs.apps.shosetsu.R.layout.drawer_item
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
class SecondDrawerViewBuilder(val context: Context, val navigationView: NavigationView, val drawerLayout: DrawerLayout, val secondDrawerController: SecondDrawerController) {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val parentView = inflater.inflate(drawer_layout, null, false)
    private val layout: LinearLayout = parentView.findViewById(linearLayout)

    private fun getNewItem(): View {
        return inflater.inflate(drawer_item, layout, false)
    }

    private fun add(view: View): SecondDrawerViewBuilder {
        layout.addView(view)
        return this
    }

    fun addSwitch(title: String = "UNKNOWN", hint: String = "Not Described", action: (CompoundButton, Boolean) -> Unit): SecondDrawerViewBuilder {
        val item = getNewItem()
        val switch: Switch = item.findViewById(R.id.switchView)
        switch.visibility = VISIBLE
        switch.text = title
        switch.hint = hint
        switch.setOnCheckedChangeListener { buttonView, isChecked -> action(buttonView, isChecked) }
        return add(item)
    }

    fun addEditText(hint: String = "Not Described"): SecondDrawerViewBuilder {
        val item = getNewItem()
        val editText: EditText = item.findViewById(editText)
        editText.visibility = VISIBLE
        editText.hint = hint
        return add(item)
    }

    fun addSpinner(title: String = "Not Described", spinnerAdapter: SpinnerAdapter): SecondDrawerViewBuilder {
        val item = getNewItem()
        val spinner: Spinner = item.findViewById(spinner)
        spinner.visibility = VISIBLE
        spinner.adapter = spinnerAdapter
        val textView = item.findViewById<TextView>(R.id.textView)
        textView.visibility = VISIBLE
        textView.text = title
        return add(item)
    }


    fun addRadioGroup(title: String, array: Array<String>): SecondDrawerViewBuilder {
        val item = getNewItem()

        val radioView = item.findViewById<LinearLayout>(radioGroupView)
        radioView.visibility = VISIBLE

        val bar = (radioView[0] as ConstraintLayout)
        (bar[0] as TextView).text = title

        val image = bar[1] as ImageView
        var first = true

        val divider = radioView[1]

        val radioGroupPar = radioView[2] as LinearLayout
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
        return add(item)
    }

    fun build(): View {
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