package com.github.doomsdayrs.apps.shosetsu.common.ext

import android.view.View
import android.widget.AdapterView
import app.shosetsu.lib.*
import com.github.doomsdayrs.apps.shosetsu.backend.Settings
import com.github.doomsdayrs.apps.shosetsu.backend.Settings.LISTING_KEY
import com.github.doomsdayrs.apps.shosetsu.ui.secondDrawer.SDViewBuilder

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
 * 01 / 03 / 2020
 *
 * @author github.com/doomsdayrs
 */

val Formatter.defaultListing: Int
	get() = Settings.formatterSettings.getInt("$formatterID:$LISTING_KEY", 0)


fun Formatter.setDefaultListing(int: Int): Boolean = when {
	int >= listings.size || int < 0 -> false
	else -> {
		Settings.formatterSettings.edit().putInt("$formatterID:$LISTING_KEY", int).apply()
		true
	}
}

fun Formatter.getListing(): Formatter.Listing = listings[defaultListing]

fun Filter<*>.build(builder: SDViewBuilder) {
	when (this) {
		is TextFilter -> builder.editText(name).also {
			it.onFocusChangeListener = View.OnFocusChangeListener { _, _ -> state = it.getValue() }
		}
		is SwitchFilter -> builder.switch(name, state).also {
			it.setOnCheckedChangeListener { _, v -> state = v }
		}
		is DropdownFilter -> builder.spinner(name, choices, state).also {
			it.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
				override fun onNothingSelected(_p: AdapterView<*>?) {}
				override fun onItemSelected(_p: AdapterView<*>?, _v: View?, pos: Int, id: Long) {
					state = pos
				}
			}
		}
		is RadioGroupFilter -> builder.radioGroup(name, choices, state).also {
			it.setOnCheckedChangeListener { _, i -> state = i }
		}
	}
}