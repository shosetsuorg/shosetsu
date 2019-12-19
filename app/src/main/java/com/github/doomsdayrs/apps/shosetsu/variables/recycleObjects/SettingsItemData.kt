package com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter


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
 * 18 / 12 / 2019
 *
 * @author github.com/doomsdayrs
 */
class SettingsItemData(val type: SettingsType) {
    enum class SettingsType {
        INFORMATION,
        BUTTON,
        SPINNER
    }

    var titleID: Int = -1
    var descID: Int = -1

    var titleText: String = ""
    var descriptionText: String = ""

    lateinit var buttonOnClickListener: (View) -> Unit
    lateinit var itemViewOnClick: (View) -> Unit

    lateinit var spinnerOnClick: (View) -> Unit
    var spinnerOnItemSelectedListener: AdapterView.OnItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
    lateinit var adapter: ArrayAdapter<*>
    var spinnerSelection: Int = -1

    fun setTitle(titleID: Int): SettingsItemData {
        this.titleID = titleID
        return this
    }

    fun setDescription(descID: Int): SettingsItemData {
        this.descID = descID
        return this
    }

    fun setTitle(title: String): SettingsItemData {
        this.titleText = title
        return this
    }

    fun setDescription(desc: String): SettingsItemData {
        descriptionText = desc
        return this
    }

    fun setOnClickListenerButton(onClickListener: (View) -> Unit): SettingsItemData {
        buttonOnClickListener = (onClickListener)
        return this
    }

    fun setOnClickListenerSpinner(onClickListener: (View) -> Unit): SettingsItemData {
        spinnerOnClick = onClickListener
        return this
    }

    fun setOnClickListener(onClickListener: (View) -> Unit): SettingsItemData {
        itemViewOnClick = onClickListener
        return this
    }

    fun setOnItemSelectedListener(onItemSelectedListener: AdapterView.OnItemSelectedListener): SettingsItemData {
        spinnerOnItemSelectedListener = onItemSelectedListener
        return this
    }

    fun setArrayAdapter(ad: ArrayAdapter<*>): SettingsItemData {
        adapter = ad
        return this
    }

    fun setSpinnerSelection(i: Int): SettingsItemData {
        spinnerSelection = i
        return this
    }
}