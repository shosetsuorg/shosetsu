package app.shosetsu.android.view.widget.setting

import android.R
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import app.shosetsu.lib.Filter

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
 */

/**
 * 03 / 03 / 2021
 */
class DropdownFilter @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	override val filterID: Int = -1
) : AppCompatSpinner(context, attrs), FilterSettingWidget<Int> {
	override var result: Int = 0

	constructor(
		filter: Filter.Dropdown,
		context: Context,
		attrs: AttributeSet? = null
	) : this(
		context,
		attrs,
		filterID = filter.id
	) {
		adapter = ArrayAdapter(
			context,
			R.layout.simple_spinner_dropdown_item,
			filter.choices
		)
		prompt = filter.name
		setSelection(filter.state)
		onItemSelectedListener = object : OnItemSelectedListener {
			override fun onItemSelected(
				parent: AdapterView<*>?,
				view: View?,
				position: Int,
				id: Long
			) {
				result = position
			}

			override fun onNothingSelected(parent: AdapterView<*>?) {
			}

		}
	}
}