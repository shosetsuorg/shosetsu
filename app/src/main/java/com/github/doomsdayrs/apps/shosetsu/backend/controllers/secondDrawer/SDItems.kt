package com.github.doomsdayrs.apps.shosetsu.backend.controllers.secondDrawer

import android.content.Context
import android.util.AttributeSet
import android.widget.RadioGroup
import android.widget.Switch
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatSpinner

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
 * 12 / 03 / 2020
 *
 * @author github.com/doomsdayrs
 */
interface SDItem<T> {
    var sdID: Int
    fun getValue(): T
    override fun toString(): String
}

class SDRadioGroup(context: Context, attributeSet: AttributeSet?) : RadioGroup(context, attributeSet), SDItem<Int> {
    constructor(context: Context) : this(context, null)
    override var sdID: Int = -1
    override fun getValue(): Int = checkedRadioButtonId
    override fun toString(): String = "$sdID\t| ${getValue()}"
}

class SDEditText(context: Context, attributeSet: AttributeSet) : AppCompatEditText(context, attributeSet), SDItem<String> {
    override var sdID: Int = -1
    override fun getValue(): String = text.toString()
    override fun toString(): String = "$sdID\t| ${getValue()}"
}

class SDSpinner(context: Context, attributeSet: AttributeSet) : AppCompatSpinner(context, attributeSet), SDItem<Int> {
    override var sdID: Int = -1
    override fun getValue(): Int = selectedItemPosition
    override fun toString(): String = "$sdID\t| ${getValue()}"
}

class SDSwitch(context: Context, attributeSet: AttributeSet) : Switch(context, attributeSet), SDItem<Boolean> {
    override var sdID: Int = -1
    override fun getValue(): Boolean = isChecked
    override fun toString(): String = "$sdID\t| ${getValue()}"
}