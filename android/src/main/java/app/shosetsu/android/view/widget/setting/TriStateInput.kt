package app.shosetsu.android.view.widget.setting

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import app.shosetsu.android.view.widget.TriStateButton
import app.shosetsu.android.view.widget.TriStateButton.State
import app.shosetsu.lib.Filter
import com.github.doomsdayrs.apps.shosetsu.databinding.TriStateCheckboxBinding

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
class TriStateInput @JvmOverloads constructor(
	context: Context,
	attrs: AttributeSet? = null,
	override val filterID: Int = -1
) : FilterSettingWidget<Int>, FrameLayout(context, attrs) {
	private val triStateButton: TriStateButton =
		TriStateCheckboxBinding.inflate(LayoutInflater.from(context), this, true).root

	override var result: Int
		get() = triStateButton.state.toFilter()
		set(value) {
			triStateButton.state = when (value) {
				Filter.TriState.STATE_IGNORED -> State.IGNORED
				Filter.TriState.STATE_INCLUDE -> State.CHECKED
				Filter.TriState.STATE_EXCLUDE -> State.UNCHECKED
				else -> State.IGNORED
			}
		}

	private fun State.toFilter() = when (this) {
		State.IGNORED -> Filter.TriState.STATE_IGNORED
		State.CHECKED -> Filter.TriState.STATE_INCLUDE
		State.UNCHECKED -> Filter.TriState.STATE_EXCLUDE
	}

	constructor(
		filter: Filter.TriState,
		context: Context,
		attrs: AttributeSet? = null
	) : this(
		context,
		attrs,
		filterID = filter.id
	) {
		triStateButton.setText(filter.name)
		result = filter.state
	}
}
