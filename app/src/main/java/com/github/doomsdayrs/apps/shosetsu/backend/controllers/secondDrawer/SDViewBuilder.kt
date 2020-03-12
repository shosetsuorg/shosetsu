package com.github.doomsdayrs.apps.shosetsu.backend.controllers.secondDrawer

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.R.id.spinner
import com.github.doomsdayrs.apps.shosetsu.ui.drawer.ExpandingViewBar


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
open class SDViewBuilder(val viewGroup: ViewGroup, val secondDrawerController: SecondDrawerController) {
    companion object {
        private const val logID = "SDViewBuilder"
    }

    val inflater: LayoutInflater = LayoutInflater.from(viewGroup.context)
    val layout: LinearLayout = inflater.inflate(R.layout.drawer_layout_simple, viewGroup, false) as LinearLayout

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
        spinner.adapter = ArrayAdapter(viewGroup.context, android.R.layout.simple_spinner_item, array)
        spinner.setSelection(selectedInt)

        val textView = item.findViewById<TextView>(R.id.textView)
        textView.visibility = VISIBLE
        textView.text = title
        return add(item)
    }

    fun addRadioGroup(title: String, array: Array<String>): SDViewBuilder {
        Log.d(logID, "Adding RadioGroup\t: $title")
        val expandingViewBar = ExpandingViewBar(viewGroup.context, viewGroup)
        val radioGroup = RadioGroup(expandingViewBar.layout.context)
        array.forEach {
            val r = RadioButton(radioGroup.context)
            r.text = it
            r.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            radioGroup.addView(r)
        }
        expandingViewBar.setChild(radioGroup)
        return add(expandingViewBar.layout)
    }

    open fun build(): View {
        return layout
    }
}