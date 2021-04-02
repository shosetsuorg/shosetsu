package app.shosetsu.android.view.widget.setting

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
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
 * 30 / 01 / 2021
 */
class CheckboxFilterInput @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	override val filterID: Int = -1
) : FilterSettingWidget<Boolean>, AppCompatCheckBox(context, attrs) {
	override var result: Boolean
		get() = super.isChecked()
		set(value) = super.setChecked(value)

	constructor(
		filter: Filter.Checkbox,
		context: Context,
		attrs: AttributeSet? = null
	) : this(
		context,
		attrs,
		filterID = filter.id
	) {
		text = filter.name
		result = filter.state
	}
}