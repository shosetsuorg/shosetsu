package com.github.doomsdayrs.apps.shosetsu.backend.controllers.secondDrawer

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.StringRes
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

    fun addSwitch(title: String = "UNKNOWN", int: Int = -1): SDViewBuilder {
        Log.d(logID, "Adding Switch\t: $title")
        val switch = inflater.inflate(R.layout.drawer_item_switch, layout, false) as SDSwitch
        switch.text = title
        switch.sdID = int
        return add(switch)
    }

    fun addEditText(hint: String = "Not Described", int: Int = -1): SDViewBuilder {
        Log.d(logID, "Adding EditText\t: $hint")
        val editText = inflater.inflate(R.layout.drawer_item_edit_text, layout, false) as SDEditText
        editText.hint = hint
        editText.sdID = int
        return add(editText)
    }

    fun addSpinner(title: String = "Not Described", array: Array<String>, selectedInt: Int = 0, int: Int = 0, changeAction: (Int) -> Unit = {}): SDViewBuilder {
        Log.d(logID, "Adding Spinner\t: $title")
        val item = inflater.inflate(R.layout.drawer_item_spinner, layout, false) as LinearLayout
        val spinner: SDSpinner = item.findViewById(spinner)
        spinner.visibility = VISIBLE
        spinner.adapter = ArrayAdapter(viewGroup.context, android.R.layout.simple_spinner_item, array)
        spinner.setSelection(selectedInt)
        spinner.sdID = int
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) = changeAction(position)
        }
        val textView = item.findViewById<TextView>(R.id.textView)
        textView.visibility = VISIBLE
        textView.text = title
        return add(item)
    }

    fun createInner(@StringRes title: Int, builder: (SDViewBuilder) -> SDViewBuilder): SDViewBuilder =
            createInner(viewGroup.context.getString(title), builder)

    fun createInner(title: String, builder: (SDViewBuilder) -> SDViewBuilder): SDViewBuilder {
        val expandingViewBar = ExpandingViewBar(viewGroup.context, viewGroup)
        expandingViewBar.setTitle(title)
        expandingViewBar.bar
        expandingViewBar.setChild(builder(SDViewBuilder(
                expandingViewBar.layout,
                secondDrawerController
        )).build())
        add(expandingViewBar.layout)
        return this
    }

    fun addRadioGroup(title: String, array: Array<String>, int: Int = -1): SDViewBuilder {
        Log.d(logID, "Adding RadioGroup\t: $title")
        val expandingViewBar = ExpandingViewBar(viewGroup.context, viewGroup)
        expandingViewBar.setTitle(title)
        val radioGroup = SDRadioGroup(expandingViewBar.layout.context)
        radioGroup.sdID = int
        array.forEachIndexed { index, it ->
            val r = RadioButton(radioGroup.context)
            r.text = it
            r.id = index
            r.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            radioGroup.addView(r)
        }
        radioGroup.check(0)
        radioGroup.checkedRadioButtonId
        expandingViewBar.setChild(radioGroup)
        return add(expandingViewBar.layout)
    }

    fun removeLast() = layout.removeViewAt(layout.childCount - 1)
    fun removeAll() = layout.removeAllViews()

    open fun build(): View {
        return layout
    }
}