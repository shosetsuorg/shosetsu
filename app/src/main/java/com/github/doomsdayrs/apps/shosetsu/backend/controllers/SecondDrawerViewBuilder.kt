package com.github.doomsdayrs.apps.shosetsu.backend.controllers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.widget.*
import androidx.drawerlayout.widget.DrawerLayout
import com.github.doomsdayrs.apps.shosetsu.R
import com.github.doomsdayrs.apps.shosetsu.R.id.editText
import com.github.doomsdayrs.apps.shosetsu.R.id.linearLayout
import com.github.doomsdayrs.apps.shosetsu.R.layout.*
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
    private val scrollView: ScrollView = inflater.inflate(R.layout.drawer_layout, null, false) as ScrollView
    private val layout: LinearLayout = scrollView.findViewById(linearLayout)

    init {
        layout.addView(inflater.inflate(drawer_header, layout, false))
    }

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

    fun addEditText(hint: String = "Not Described", action: (String) -> Unit): SecondDrawerViewBuilder {
        val item = getNewItem()
        val editText: EditText = item.findViewById(editText)
        editText.visibility = VISIBLE
        editText.hint = hint
        return add(item)
    }

    fun build(): View {
        val ender = inflater.inflate(drawer_bottom, layout, false)
        ender.findViewById<Button>(R.id.accept).setOnClickListener {
            drawerLayout.closeDrawer(navigationView)
        }
        ender.findViewById<Button>(R.id.reset).setOnClickListener {
            navigationView.removeAllViews()
            secondDrawerController.createTabs(navigationView, drawerLayout)
            drawerLayout.closeDrawer(navigationView)
        }
        add(ender)
        return scrollView
    }
}